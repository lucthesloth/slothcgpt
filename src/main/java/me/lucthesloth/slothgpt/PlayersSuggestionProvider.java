package me.lucthesloth.slothgpt;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class PlayersSuggestionProvider implements SuggestionProvider<FabricClientCommandSource>{

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {
                MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().forEach(player -> {
                    builder.suggest(player.getProfile().getName());
                });

        return builder.buildFuture();
    }
    
}
