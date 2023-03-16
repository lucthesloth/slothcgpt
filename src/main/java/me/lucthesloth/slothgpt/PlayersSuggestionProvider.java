package me.lucthesloth.slothgpt;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PlayersSuggestionProvider implements SuggestionProvider<FabricClientCommandSource>{

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context,
            SuggestionsBuilder builder) throws CommandSyntaxException {
        context.getSource().getWorld().getPlayers().forEach(player -> {
            builder.suggest(player.getName().getString());
        });
        return builder.buildFuture();
    }
    
}
