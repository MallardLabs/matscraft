package com.mallardlabs.matscraft.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mallardlabs.matscraft.ws.WebSocketClientHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class LinkAccount {
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

            context.getSource().sendFeedback(() -> Text.of("Verifying token... Please wait."), false);

            // Format pesan untuk WebSocket
            String message = String.format(
                "{\"type\":\"link_account\",\"token\":\"%s\",\"player\":\"%s\",\"uuid\":\"%s\"}",
                token, playerName, userId
            );

            if (WebSocketClientHandler.getInstance() != null && WebSocketClientHandler.getInstance().isOpen()) {
                WebSocketClientHandler client = WebSocketClientHandler.getInstance();
                client.setMessageCallback(response -> {
                    try {
                        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                        System.out.print(jsonResponse);
                        boolean success = jsonResponse.get("success").getAsBoolean();
                        context.getSource().sendFeedback(
                            () -> Text.of(success ? "Account successfully linked!" : 
                                        jsonResponse.get("message").getAsString()),
                            false
                        );
                    } catch (Exception e) {
                        context.getSource().sendFeedback(
                            () -> Text.of("Error processing server response: " + e.getMessage()),
                            false
                        );
                    }
                });
                
                WebSocketClientHandler.sendMessage(message);
            } else {
                context.getSource().sendFeedback(
                    () -> Text.of("Failed to connect to server. Please try again later."),
                    false
                );
            }

            return 1;
        } catch (Exception e) {
            context.getSource().sendFeedback(() -> Text.of("Command Error: " + e.getMessage()), false);
            e.printStackTrace();
            return 0;
        }
    }
}
