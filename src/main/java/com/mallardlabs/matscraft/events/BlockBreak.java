package com.mallardlabs.matscraft.events;

import com.mallardlabs.matscraft.ws.WebSocketClientHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import java.time.Instant;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;


import java.util.List;
import java.util.ArrayList;

public class BlockBreak {

    private static final List<BlockBreakData> blockBreakDataList = new ArrayList<>();

    // Register the event
    public static void register() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world instanceof ServerWorld serverWorld) {
                String blockType = state.getBlock().getTranslationKey();
                if (isTrackedBlock(blockType)) {
                    handleBlockBreak(player.getUuidAsString(), 
                                   player.getName().getString(),
                                   blockType.replace("block.matscraft.", ""), 
                                   pos);
                }
            }
            return true;
        });
    }

    // Check if the block is tracked
    private static boolean isTrackedBlock(String blockType) {
        return switch (blockType) {
            case "block.matscraft.common_mats_ore",
                 "block.matscraft.epic_mats_ore",
                 "block.matscraft.legendary_mats_ore",
                 "block.matscraft.rare_mats_ore",
                 "block.matscraft.uncommon_mats_ore" -> true;
            default -> false;
        };
    }

    // Handle the block break event
    private static void handleBlockBreak(String playerUuid, String playerName, String blockType, BlockPos pos) {
        // Format pesan untuk WebSocket
        String message = String.format(
            "{\"type\":\"block_break\",\"player\":\"%s\",\"uuid\":\"%s\",\"block\":\"%s\",\"position\":{\"x\":%d,\"y\":%d,\"z\":%d}}",
            playerName,
            playerUuid,
            blockType,
            pos.getX(),
            pos.getY(),
            pos.getZ()
        );
        
        // Kirim ke WebSocket
        if (WebSocketClientHandler.getInstance() != null && WebSocketClientHandler.getInstance().isOpen()) {
            WebSocketClientHandler.sendMessage(message);
        }
    }

    // Data container for block break events
    private static class BlockBreakData {
        String playerUuid;
        String blockType;
        BlockPos pos;
        Instant minedAt;

        public BlockBreakData(String playerUuid, String blockType, BlockPos pos, Instant minedAt) {
            this.playerUuid = playerUuid;
            this.blockType = blockType;
            this.pos = pos;
            this.minedAt = minedAt;
        }
    }
}
