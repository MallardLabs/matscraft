package com.mallardlabs.matscraft.commands;

import com.google.gson.Gson;
import com.mallardlabs.matscraft.config.ConfigManager;
import com.mallardlabs.matscraft.ws.WebSocketManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class LinkAccount {

    private static final Gson gson = new Gson();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("link")
                .then(CommandManager.argument("token", StringArgumentType.word())
                        .executes(LinkAccount::executeCommand)));
    }

    private static int executeCommand(CommandContext<ServerCommandSource> context) {
        try {
            String token = StringArgumentType.getString(context, "token");
            String playerName = context.getSource().getPlayer().getName().getString();
            String userId = context.getSource().getPlayer().getUuidAsString();

            context.getSource().sendFeedback(() -> Text.of("Verifying Tokens...Please Wait"), false);

            sendWebSocketMessage(playerName, userId, token, context);

            return 1; // Success
        } catch (Exception e) {
            context.getSource().sendFeedback(() -> Text.of("Command Error: " + e.getMessage()), false);
            e.printStackTrace();
            return 0; // Failure
        }
    }

    private static void sendWebSocketMessage(String playerName, String userId, String token, CommandContext<ServerCommandSource> context) {
        // Initialize WebSocket connection if not already done
        WebSocketManager.initializeWebSocket(ConfigManager.getWebSocketUrl());

        // Construct the data payload
        PlayerData playerData = new PlayerData(playerName, userId, token);

        // Send the message through WebSocket with the "linkAccount" event name
        WebSocketManager.sendMessage("linkAccount", playerData, message -> {
            // This is the callback that gets invoked when the response is received
            context.getSource().sendFeedback(() -> Text.of("Response from WebSocket: " + message), false);
        });
    }

    // Data class for JSON serialization
    private static class PlayerData {
        private final String playerName;
        private final String userId;
        private final String token;

        public PlayerData(String playerName, String userId, String token) {
            this.playerName = playerName;
            this.userId = userId;
            this.token = token;
        }
    }
}