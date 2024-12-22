package com.mallardlabs.matscraft.events;

import com.mallardlabs.matscraft.gui.MatsBalanceOverlay; // Import MatsBalanceOverlay to update balance
import com.mallardlabs.matscraft.config.ConfigManager;
import com.mallardlabs.matscraft.util.CustomNotification;
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
        // Notify player to sync their account
        CustomNotification.showNotification("Sync Your Account...", 100,"warning");
        // Attempt to sync account and update balance
        syncAccount(player);
    }

    /**
     * Method to sync the player's account by checking if the UUID exists in the users table
     * and then fetching the player's balance from the mats_balance table.
     */
    private static void syncAccount(ServerPlayerEntity player) {
        // Database connection details from ConfigManager
        String dbUrl = ConfigManager.PG_URL;
        String dbUser = ConfigManager.PG_USER;
        String dbPassword = ConfigManager.PG_PW;

        // SQL query to check if the player's UUID exists in the users table
        String userCheckQuery = "SELECT minecraft_id FROM users WHERE minecraft_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // Check if the player's UUID exists in the users table
            try (PreparedStatement stmt = conn.prepareStatement(userCheckQuery)) {
                stmt.setString(1, player.getUuidAsString()); // Set the player's UUID

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Player found in users table, now update balance from mats_balance
                        updateBalanceFromDatabase(player, conn);
                    } else {
                        CustomNotification.showNotification("Sync Failed, Account Not Linked. Link Your Account First! Go To discord.gg/mezo ", 200,"danger");

                    }
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
            CustomNotification.showNotification("Error syncing account", 200,"danger");

        }
    }

    /**
     * Method to fetch the player's balance from the database using their Minecraft UUID
     * and update the MatsBalanceOverlay with the fetched balance.
     */
    private static void updateBalanceFromDatabase(ServerPlayerEntity player, Connection conn) {
        // SQL query to get the balance for the player's Minecraft UUID from mats_balance
        String query = "SELECT balance FROM mats_balance WHERE minecraft_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, player.getUuidAsString()); // Set the player's UUID

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Get the balance from the result set
                    int balance = rs.getInt("balance");

                    // Update the player balance in MatsBalanceOverlay
                    MatsBalanceOverlay.playerBalance = balance;

                    CustomNotification.showNotification("Sync Success!", 200,"sucess");
                    // Send a welcome back message
                    player.sendMessage(
                            Text.literal("Welcome back, " + player.getName().getString() + "!")
                                    .formatted(Formatting.GOLD), // Gold color for success
                            false
                    );
                } else {
                    // If no balance is found, inform the player
                    player.sendMessage(
                            Text.literal("No balance found for your account.")
                                    .formatted(Formatting.RED), // Red color for error
                            false
                    );
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
            player.sendMessage(
                    Text.literal("Error fetching balance from the database.")
                            .formatted(Formatting.RED), // Red color for error
                    false
            );
        }
    }
}
