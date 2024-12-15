package com.mallardlabs.matscraft.block;

import com.mallardlabs.matscraft.commands.LinkAccount;
import com.mallardlabs.matscraft.ws.WebSocketManager;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import com.mallardlabs.matscraft.config.ConfigManager;

import java.net.URI;

public class BlockBreakEvent {

    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            String blockType = state.getBlock().getTranslationKey();

            if (world instanceof ServerWorld && isTrackedBlock(blockType)) {
                handleBlockBreak((ServerWorld) world, player.getUuidAsString(), blockType, pos);
            }

            return true; // Allow block break
        });
    }

    private static boolean isTrackedBlock(String blockType) {
        return blockType.equals("block.matscraft.common_mats_ore") ||
                blockType.equals("block.matscraft.epic_mats_ore") ||
                blockType.equals("block.matscraft.legendary_mats_ore") ||
                blockType.equals("block.matscraft.rare_mats_ore") ||
                blockType.equals("block.matscraft.uncommon_mats_ore");
    }

    private static void handleBlockBreak(ServerWorld world, String playerUuid, String blockType, BlockPos pos) {
        // Construct the data to send
        String eventData = String.format("{\"uuid\":\"%s\", \"block\":\"%s\", \"position\":\"%s\"}",
                playerUuid, blockType, pos.toString());
        sendWebSocketMessage(eventData);
        // Optional: Log to the console for debugging
        System.out.printf("Block %s at %s was broken by %s%n", blockType, pos, playerUuid);

    }
    private static void sendWebSocketMessage(String data) {
        // Initialize WebSocket connection if not already done
        WebSocketManager.initializeWebSocket(ConfigManager.getWebSocketUrl());
        WebSocketManager.sendMessage("BlockBreak", data, message -> {
            // This is the callback that gets invoked when the response is received
            System.out.printf("Response", message);

        });


        System.out.printf("Send To", data);
    }
}