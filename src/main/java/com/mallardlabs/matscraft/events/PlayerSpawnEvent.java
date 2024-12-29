package com.mallardlabs.matscraft.events;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mallardlabs.matscraft.gui.MatsBalanceOverlay; // Import MatsBalanceOverlay to update balance
import com.mallardlabs.matscraft.config.ConfigManager;
import com.mallardlabs.matscraft.util.CustomNotification;
import com.mallardlabs.matscraft.ws.WebSocketClientHandler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerSpawnEvent {

    // Register event to handle player spawn
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                onPlayerSpawn(player);
            }
        });
    }

    // This function is triggered when the player spawns
    private static void onPlayerSpawn(ServerPlayerEntity player) {
        WebSocketClientHandler instance = WebSocketClientHandler.getInstance();
        String playerName = player.getName().getString();
        String uuid = player.getUuidAsString();
        String message = String.format("{\"type\":\"player_join\",\"player\":\"%s\",\"uuid\":\"%s\"}",
                playerName, uuid);
        syncAccount(message);
    }

    /**
     * Method to sync the player's account by checking if the UUID exists in the users table
     * and then fetching the player's balance from the mats_balance table.
     */
    private static void syncAccount(String message) {
        if (WebSocketClientHandler.getInstance() != null && WebSocketClientHandler.getInstance().isOpen()) {
            WebSocketClientHandler client = WebSocketClientHandler.getInstance();
            client.setMessageCallback(response -> {
                try {
                    JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                    System.out.print(jsonResponse);
                    boolean success = jsonResponse.get("success").getAsBoolean();
                    if (!success) {
                        CustomNotification.showNotification( jsonResponse.get("message").getAsString(), 200, "warning");
                        return;
                    }
                    CustomNotification.showNotification("Sync Success", 200, "sucess");
                    MatsBalanceOverlay.playerBalance = jsonResponse.get("data").getAsInt();
                } catch (Exception e) {
                    CustomNotification.showNotification("Something When Wrong!", 200, "danger");
                }
            });

            WebSocketClientHandler.sendMessage(message);
        } else {
            CustomNotification.showNotification("Something When Wrong!", 200, "danger");
        }
    }

    /**
     * Method to fetch the player's balance from the database using their Minecraft UUID
     * and update the MatsBalanceOverlay with the fetched balance.
     */
    private static void updateBalanceFromDatabase(ServerPlayerEntity player, Connection conn) {
        String query = "SELECT balance FROM mats_balance WHERE minecraft_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getUuidAsString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int balance = rs.getInt("balance");
                    MatsBalanceOverlay.playerBalance = balance;

                    CustomNotification.showNotification("Sync Success!", 200, "success");
                    player.sendMessage(
                            Text.literal("Sync Success! Welcome back, " + player.getName().getString() + "!")
                                    .formatted(Formatting.GOLD),
                            false
                    );
                } else {
                    CustomNotification.showNotification("No balance found", 200, "warning");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            CustomNotification.showNotification("Database Error", 200, "danger");
        }
    }
}
