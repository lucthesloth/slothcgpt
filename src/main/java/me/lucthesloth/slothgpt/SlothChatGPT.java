package me.lucthesloth.slothgpt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SlothChatGPT implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sloth-chatgpt");
    public static SlothChatGPT instance;
    public static String lastPlayer = "";
    public static List<String> lastPlayers;
    public static boolean ready = false;
    public static Gson gson = new GsonBuilder().create();

    public static SCGPTConfig _Config;

    private KeyBinding requestLastPlayer;

    ExecutorService asyncExecute = Executors.newFixedThreadPool(5);

    @Override
    public void onInitialize() {
        instance = this;
        AutoConfig.register(SCGPTConfig.class, GsonConfigSerializer::new).getConfig();
        _Config = AutoConfig.getConfigHolder(SCGPTConfig.class).getConfig();

        // register keybind
        this.requestLastPlayer = KeyBindingHelper
                .registerKeyBinding(new KeyBinding("me.lucthesloth.scgpt.requestLastPlayer", InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_LEFT_BRACKET, "category.scgpt.binds"));

        registerCommands();
        // handle ticks
        ClientTickEvents.END_CLIENT_TICK.register(this::HandleTick);
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            asyncExecute.execute(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ready = true;
            });
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ready = false;
            lastPlayers = null;
            lastPlayer = "";
        });
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            LOGGER.info(String.format("%s - %s", sender.getName().getString(), message.getContent().getString()));
        });
    }

    public static List<String> responses(@Nullable String prompt, String player,
            @Nullable ClientPlayerEntity requester, @Nullable Integer max_tokens) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + _Config.api.token);
        headers.put("Content-Type", "application/json");
        requester.sendMessage(((MutableText) Text.of(
                String.format("Sent prompt: %s", prompt != null ? prompt : _Config.prompt.replace("%player%", player))))
                .setStyle(Style.EMPTY.withColor(Formatting.GOLD).withItalic(true)));
        ChatRequestData data = new ChatRequestData(_Config.request.model,
                List.of(new ChatRequestData.Message("user",
                        prompt != null ? prompt : _Config.prompt.replace("%player%", player))),
                max_tokens != null ? max_tokens : _Config.request.max_tokens, _Config.request.n);
        try {
            String response = HTTPRequest.sendPostRequest("https://api.openai.com/v1/chat/completions", headers,
                    SlothChatGPT.gson.toJson(data));
            if (response.contains("POST request failed")) {
                requester.sendMessage(((MutableText) Text.of(
                response))
                .setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(true)));
                return null;
            }
            ChatRequestData.Response responseData = SlothChatGPT.gson.fromJson(response,
                    ChatRequestData.Response.class);
            return responseData.getChoices().stream()
                    .map(choice -> choice.getMessage().getContent().replace("\n", "").replace("\"", "")).toList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendResponseList(@Nullable List<String> responses) {
        if (responses == null || responses.isEmpty())
            return;

        MutableText text;
        for (String response : responses) {
            text = (MutableText) Text.of(response);
            MinecraftClient.getInstance().player.sendMessage(text
                    .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, response))
                            .withColor(Formatting.DARK_AQUA)
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to send")))));
        }
    }

    public void HandleTick(MinecraftClient client) {
        asyncExecute.execute(() -> {
            while (this.requestLastPlayer.wasPressed()) {

                if (lastPlayer != "" && ready) {
                    List<String> responses = responses(null, lastPlayer, client.player, null);
                    if (responses != null)
                        sendResponseList(responses);
                }

            }
        });
    }
    public static boolean registerPlayerJoin(String player) {
        if (lastPlayers == null)
            lastPlayers = new LinkedList<>();
        if (lastPlayers.contains(player))
            return false;
        lastPlayers.add(player);
        if (lastPlayers.size() >= _Config.autoWelcome.lookBehind){
            lastPlayers.remove(0);
        }
        return true;
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("scgpt").then(ClientCommandManager.argument("player", StringArgumentType.word()).suggests(new PlayersSuggestionProvider()).executes(ctx -> {
                        String target = StringArgumentType.getString(ctx, "player");
                        if (target != null) {
                            asyncExecute.execute(() -> {
                                List<String> responses = responses(null, StringArgumentType.getString(ctx, "player"), ctx.getSource().getPlayer(), null);
                                if (responses != null)
                                sendResponseList(responses);
                            });
                        } else {
                            ctx.getSource().sendError(Text.of("Player not found"));
                        }
                        return 1;
                    })));
            dispatcher.register(ClientCommandManager.literal("cgpt").then(ClientCommandManager.argument("prompt", StringArgumentType.greedyString()).executes(ctx -> {
                asyncExecute.execute(() -> {
                    List<String> responses = responses(StringArgumentType.getString(ctx, "prompt"), null, ctx.getSource().getPlayer(), null);
                    if (responses != null)
                    sendResponseList(responses);
                });
                return 1;
            })));                            
        });
    }
}
