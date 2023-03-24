package me.lucthesloth.slothgpt.mixin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.lucthesloth.slothgpt.SlothChatGPT;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {
    String oldPattern = SlothChatGPT._Config.autoWelcome.chatCaptureRegex;
    Pattern regex = Pattern.compile(SlothChatGPT._Config.autoWelcome.chatCaptureRegex);
    Matcher matcher = regex.matcher("");

    @Inject(at = @At("HEAD"), method = "onGameMessage")
    private void onGameMessage(Text message, boolean overlay, CallbackInfo info) {
        if (!oldPattern.equals(SlothChatGPT._Config.autoWelcome.chatCaptureRegex)) {
            oldPattern = SlothChatGPT._Config.autoWelcome.chatCaptureRegex;
            regex = Pattern.compile(SlothChatGPT._Config.autoWelcome.chatCaptureRegex);
        }
        if (SlothChatGPT._Config.autoWelcome.useChatCapture) {
            matcher = regex.matcher(message.getString());
            if (matcher.find()) {
                String playerName = matcher.group(1).trim();
                if (!playerName.toLowerCase()
                        .contains(MinecraftClient.getInstance().player.getName().getString().toLowerCase())) {
                    if (SlothChatGPT.registerPlayerJoin(playerName) && SlothChatGPT._Config.autoWelcome.enabled) {
                        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(
                                SlothChatGPT._Config.autoWelcome.message.replace("%player%", playerName));
                    }
                }
            }
        }
    }
}
