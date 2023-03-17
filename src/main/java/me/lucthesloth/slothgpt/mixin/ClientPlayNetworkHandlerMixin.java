package me.lucthesloth.slothgpt.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
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
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null && !packet.getEntries().isEmpty()) {
			if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
				//Slight workaround bungee/waterfall
				String playerName = packet.getEntries().get(0).profile().getName();
				if (!playerName.startsWith("~")) {
					if (!packet.getEntries().get(0).profile().getId().equals(player
							.getGameProfile()
							.getId())
							&& !SlothChatGPT.lastPlayer.equals(playerName)) {
						if (SlothChatGPT._Config.generateOnJoin && SlothChatGPT.ready) {
							List<String> responses = SlothChatGPT.responses(null,
									playerName, player, null);
							SlothChatGPT.sendResponseList(responses);
						}
						SlothChatGPT.lastPlayer = playerName;
						if (!SlothChatGPT.registerPlayerJoin(playerName) && SlothChatGPT._Config.autoWelcome.enabled){

						}
					}
				}
			}
		}

	}
}