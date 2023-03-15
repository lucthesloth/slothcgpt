package me.lucthesloth.slothgpt.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.lucthesloth.slothgpt.SlothChatGPT;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Inject(at = @At("HEAD"), method = "onPlayerList")
	private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
		if (MinecraftClient.getInstance().player != null && !packet.getEntries().isEmpty()) {
			if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
				if (!packet.getEntries().get(0).profile().getId().equals(MinecraftClient.getInstance().player
						.getGameProfile()
						.getId()) && !SlothChatGPT.lastPlayer.equals(packet.getEntries().get(0).profile().getName())) {					
					if (SlothChatGPT._Config.generateOnJoin) {
						List<String> responses = SlothChatGPT.responses(packet.getEntries().get(0).profile().getName());
						if (responses != null) {
							MutableText text;
							for (String response : responses) {
								text = (MutableText) Text.of(response);
								MinecraftClient.getInstance().player.sendMessage(text.setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, response)).withColor(Formatting.AQUA).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to send")))));
							}
						}
					}
					SlothChatGPT.lastPlayer = packet.getEntries().get(0).profile().getName();
				}
			}
		}

	}
}