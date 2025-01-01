package com.mallardlabs.matscraft.client.hud;

import com.mallardlabs.matscraft.MatsCraft;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

public class MatsBalanceOverlay {
    private static final Identifier MATS_ICON = Identifier.of(MatsCraft.MOD_ID, "textures/item/mats.png");
    private static final Identifier DISCORD_ICON = Identifier.of(MatsCraft.MOD_ID, "textures/gui/discord.png");
    private static final Identifier PLAYER_ICON = Identifier.of(MatsCraft.MOD_ID, "textures/gui/minecraft.png");
    private static final Identifier COORDIANTE_ICON = Identifier.of(MatsCraft.MOD_ID, "textures/gui/compas.png");
    public static int playerBalance = 0;
    public static String DiscordUsername = "Not Linked";
    private boolean isFirstSpawn = true;

    private static final int MARGIN_X = 10;
    private static final int MARGIN_Y = 10;
    private static final int MARGIN_BETWEEN_ELEMENTS = 5;

    private static final int BACKGROUND_WIDTH = 110;
    private static final int BACKGROUND_HEIGHT = 12;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int BORDER_COLOR = 0x66000000; // White border color

    public void initialize() {
        HudRenderCallback.EVENT.register(this::renderOverlay);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && isFirstSpawn) {
                isFirstSpawn = false;
            }
        });
    }

    private void renderOverlay(DrawContext context, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && !isFirstSpawn) {
            TextRenderer textRenderer = client.textRenderer;
            int x = MARGIN_X;
            int y = MARGIN_Y;

            // Draw player name
            drawSection(context, textRenderer, client.player.getName().getString(), x, y, BACKGROUND_HEIGHT);
            y += BACKGROUND_HEIGHT + MARGIN_BETWEEN_ELEMENTS;

            drawDiscordUsername(context ,textRenderer,DiscordUsername,x,y);
            y += BACKGROUND_HEIGHT + MARGIN_BETWEEN_ELEMENTS;
            // Draw Mats Balance
            drawMatsBalance(context, textRenderer, playerBalance, x, y);
            y += BACKGROUND_HEIGHT + MARGIN_BETWEEN_ELEMENTS;

            // Draw coordinates
            drawCoordinates(context, textRenderer, client.player.getX(), client.player.getY(), client.player.getZ(), x, y);
        }
    }

    private void drawSection(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int height) {
        // Draw background, text, and border for sections
        drawBackground(context, x, y, BACKGROUND_WIDTH, height);
        drawPlayerIcon(context,x,y-3);
        context.drawText(textRenderer, text, x + 14, y + 2, TEXT_COLOR, false);
        drawBorder(context, x, y, BACKGROUND_WIDTH, height);
    }
    private void drawDiscordUsername(DrawContext context, TextRenderer textRenderer, String DiscordUsername, int x, int y) {
        drawBackground(context, x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        drawDiscordIcon(context, x, y);
        context.drawText(textRenderer, DiscordUsername, x + 14, y + 3, TEXT_COLOR, false);
        drawBorder(context, x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }
    private void drawMatsBalance(DrawContext context, TextRenderer textRenderer, int balance, int x, int y) {
        // Draw background, mats icon, balance text, and border
        drawBackground(context, x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        drawMatsIcon(context, x, y);
        context.drawText(textRenderer, String.valueOf(balance), x + 14, y + 2, TEXT_COLOR, false);
        drawBorder(context, x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    private void drawCoordinates(DrawContext context, TextRenderer textRenderer, double x, double y, double z, int drawX, int drawY) {
        // Draw background, coordinates text, and border
        drawBackground(context, drawX, drawY, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        drawCoordinateIcon(context,drawX,drawY);
        String coordinates = String.format("%.1f, %.1f, %.1f", x, y, z);
        context.drawText(textRenderer, coordinates, drawX + 14, drawY + 3, TEXT_COLOR, false);
        drawBorder(context, drawX, drawY, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    }

    private void drawBackground(DrawContext context, int x, int y, int width, int height) {
        // Draw the background with dark gray color
        context.fill(x, y , x + width, y + height, 0x66000000); // Dark gray background
    }

    private void drawMatsIcon(DrawContext context, int x, int y) {
        int iconSize = 9;
        context.drawTexture(MATS_ICON, x + 2, y + 1, 0, 0, iconSize, iconSize, iconSize, iconSize);
    }
    private void drawDiscordIcon(DrawContext context, int x, int y){
        int iconSize = 9;
        context.drawTexture(DISCORD_ICON, x + 2, y + 2, 0, 0, iconSize, iconSize, iconSize, iconSize);
    }
    private void drawPlayerIcon(DrawContext context, int x, int y){
        int iconSize = 9;
        context.drawTexture(PLAYER_ICON, x + 2, y + 4, 0, 0, iconSize, iconSize, iconSize, iconSize);
    }
    private void drawCoordinateIcon(DrawContext context, int x, int y){
        int iconSize = 9;
        context.drawTexture(COORDIANTE_ICON, x + 2, y + 2, 0, 0, iconSize, iconSize, iconSize, iconSize);
    }
    private void drawBorder(DrawContext context, int x, int y, int width, int height) {
        // Draw border around the background with a white color
        context.fill(x, y - 2, x + width, y, BORDER_COLOR); // Top border
        context.fill(x, y + height, x + width, y + height + 2, BORDER_COLOR); // Bottom border
        context.fill(x - 2, y, x, y + height, BORDER_COLOR); // Left border
        context.fill(x + width, y, x + width + 2, y + height, BORDER_COLOR); // Right border
    }
}
