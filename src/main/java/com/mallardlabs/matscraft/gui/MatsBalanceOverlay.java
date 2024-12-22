package com.mallardlabs.matscraft.gui;

import com.mallardlabs.matscraft.config.ConfigManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The MatsBalanceOverlay class manages and displays the player's Mats balance on the HUD.
 * It also synchronizes the player's balance with the database when the player spawns for the first time.
 */
public class MatsBalanceOverlay {
    // Stores the player's balance globally for HUD rendering
    public static int playerBalance = 0;

    // Flag to track if the player has spawned for the first time
    private boolean isFirstSpawn = true;

    /**
     * Initializes the MatsBalanceOverlay by registering event listeners.
     * Should be called manually during mod initialization.
     */
    public void initialize() {
        // Register HUD rendering callback
        HudRenderCallback.EVENT.register(this::renderMatsBalance);

        // Register client tick event for player spawn detection
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && isFirstSpawn) {
                isFirstSpawn = false; // Mark first spawn as handled
                syncAccount(client.player.getUuidAsString()); // Sync account with the database
            }
        });
    }

    /**
     * Renders the Mats balance overlay on the player's HUD.
     *
     * @param context     The rendering context.
     * @param tickCounter The tick counter for rendering.
     */
    private void renderMatsBalance(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null && !isFirstSpawn) {
            TextRenderer textRenderer = client.textRenderer;

            int x = 10; // X position of the overlay
            int y = 10; // Y position of the overlay
            int width = 80; // Width of the background
            int height = 20; // Height of the background

            // Draw a semi-transparent background for better readability
            context.fill(x - 5, y - 5, x + width, y + height, 0x80000000);

            // Render "Your Balance" label
            context.drawText(textRenderer, "Your Balance", x, y, 0xFFFFFF, true);

            // Render the player's Mats balance
            context.drawText(textRenderer, playerBalance + " Mats", x, y + 10, 0xFFFF00, true);
        }
    }

    /**
     * Synchronizes the player's account by fetching their balance from the database.
     *
     * @param playerUuid The UUID of the player.
     */
    private void syncAccount(String playerUuid) {
        String dbUrl = ConfigManager.PG_URL;
        String dbUser = ConfigManager.PG_USER;
        String dbPassword = ConfigManager.PG_PW;

        String userCheckQuery = "SELECT minecraft_id FROM users WHERE minecraft_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            try (PreparedStatement stmt = conn.prepareStatement(userCheckQuery)) {
                stmt.setString(1, playerUuid);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Player found in users table, fetch their balance
                        updateBalanceFromDatabase(playerUuid, conn);
                    } else {
                        // Player not found, display error message
                        sendPlayerMessage("Sync Failed, Account Not Linked", Formatting.RED);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendPlayerMessage("Error syncing account: " + e.getMessage(), Formatting.RED);
        }
    }

    /**
     * Fetches the player's balance from the database and updates the global balance.
     *
     * @param playerUuid The UUID of the player.
     * @param conn       The active database connection.
     */
    private void updateBalanceFromDatabase(String playerUuid, Connection conn) {
        String query = "SELECT balance FROM mats_balance WHERE minecraft_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerUuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int balance = rs.getInt("balance");
                    playerBalance = balance; // Update the global balance

                    sendPlayerMessage("Your balance has been updated: " + balance, Formatting.GREEN);
                    sendPlayerMessage("Welcome back, " + getPlayerName() + "!", Formatting.GOLD);
                } else {
                    sendPlayerMessage("No balance found for your account.", Formatting.RED);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendPlayerMessage("Error fetching balance from the database.", Formatting.RED);
        }
    }

    /**
     * Sends a message to the player in-game.
     *
     * @param message  The message text.
     * @param color    The color formatting for the message.
     */
    private void sendPlayerMessage(String message, Formatting color) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message).formatted(color), false);
        }
    }

    /**
     * Retrieves the player's name from the Minecraft client.
     *
     * @return The player's name as a string.
     */
    private String getPlayerName() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player != null ? client.player.getName().getString() : "Player";
    }
}
