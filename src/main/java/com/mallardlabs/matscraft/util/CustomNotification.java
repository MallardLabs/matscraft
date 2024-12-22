package com.mallardlabs.matscraft.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class CustomNotification {
    private static int notificationTimer = 0; // Timer to control notification display duration
    private static boolean showNotification = false; // Flag to indicate if the notification should be shown
    private static String notificationText = ""; // The notification text to display
    private static String notificationType = "normal"; // Type of notification ("normal", "warning", "danger")

    // Colors for each notification type
    private static final int BACKGROUND_COLOR_NORMAL = 0x80000000; // Semi-transparent black background
    private static final int BACKGROUND_COLOR_WARNING = 0x80FFFF00; // Semi-transparent yellow
    private static final int BACKGROUND_COLOR_DANGER = 0x80FF0000; // Semi-transparent red
    private static final int BACKGROUND_COLOR_SUCCESS= 0x802FFF00;

    private static final int TEXT_COLOR_NORMAL = 0xFFFFFF; // White text color
    private static final int TEXT_COLOR_WARNING = 0xFFFF00; // Yellow text color for warning
    private static final int TEXT_COLOR_DANGER = 0xFF0000; // Red text color for danger

    /**
     * Registers the tick and HUD render events.
     */
    public static void register() {
        // Handle client tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (showNotification && notificationTimer > 0) {
                notificationTimer--;

                if (notificationTimer <= 0) {
                    // Reset notification state when the timer ends
                    showNotification = false;
                    notificationText = "";
                }
            }
        });

        // Handle HUD rendering
        HudRenderCallback.EVENT.register(CustomNotification::renderNotification);
    }

    /**
     * Displays a custom notification with a given message, duration, and type.
     *
     * @param text The notification text to display.
     * @param duration The duration (in ticks) for which the notification should be shown.
     * @param type The type of notification ("normal", "warning", "danger").
     */
    public static void showNotification(String text, int duration, String type) {
        notificationText = text;
        notificationTimer = duration;
        notificationType = type;
        showNotification = true;
    }

    /**
     * Renders the notification on the HUD.
     *
     * @param context The draw context for rendering.
     * @param tickDelta The partial tick time (not used in this implementation).
     */
    private static void renderNotification(DrawContext context, RenderTickCounter tickDelta) {
        if (showNotification && !notificationText.isEmpty()) {
            MinecraftClient client = MinecraftClient.getInstance();
            int screenWidth = client.getWindow().getScaledWidth();
            int screenHeight = client.getWindow().getScaledHeight();

            // Calculate position (centered at the top)
            int x = screenWidth / 2;
            int y = 10;

            // Maximum width for wrapping text
            int maxWidth = screenWidth - 300; // Leave some margin

            // Wrap the notification text into multiple lines
            List<OrderedText> wrappedLines = client.textRenderer.wrapLines(
                    Text.literal(notificationText).formatted(getFormatting(notificationType)), maxWidth);

            // Calculate text box height based on the number of wrapped lines
            int textHeight = wrappedLines.size() * client.textRenderer.fontHeight;
            int textWidth = 0;

            // Find the maximum width of the wrapped lines
            for (OrderedText line : wrappedLines) {
                int lineWidth = client.textRenderer.getWidth(line);
                if (lineWidth > textWidth) {
                    textWidth = lineWidth;
                }
            }

            // Choose background color based on notification type
            int backgroundColor = getBackgroundColor(notificationType);

            // Draw background (semi-transparent rectangle)
            context.fill(x - textWidth / 2 - 5, y - 5, x + textWidth / 2 + 5, y + textHeight + 5, backgroundColor);

            // Render each wrapped line of text
            int currentY = y;
            for (OrderedText line : wrappedLines) {
                context.drawTextWithShadow(
                        client.textRenderer,
                        line,
                        x - client.textRenderer.getWidth(line) / 2, // Center the text horizontally
                        currentY,
                        getTextColor(notificationType)
                );
                currentY += client.textRenderer.fontHeight; // Move down for the next line
            }
        }
    }

    private static Formatting getFormatting(String type) {
        return switch (type) {
            case "warning" -> Formatting.WHITE;
            case "danger" -> Formatting.WHITE;
            default -> Formatting.WHITE;
        };
    }

    private static int getBackgroundColor(String type) {
        return switch (type) {
            case "warning" -> BACKGROUND_COLOR_WARNING;
            case "danger" -> BACKGROUND_COLOR_DANGER;
            case "success" -> BACKGROUND_COLOR_SUCCESS;
            default -> BACKGROUND_COLOR_NORMAL;
        };
    }

    private static int getTextColor(String type) {
        return switch (type) {
            case "warning" -> TEXT_COLOR_WARNING;
            case "danger" -> TEXT_COLOR_DANGER;
            default -> TEXT_COLOR_NORMAL;
        };
    }
}
