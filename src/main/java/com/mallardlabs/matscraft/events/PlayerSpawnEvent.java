package com.mallardlabs.matscraft.events;

import com.mallardlabs.matscraft.networking.Payload.SyncAccount;
import com.mallardlabs.matscraft.util.CustomNotification;
import com.mallardlabs.matscraft.ws.WebSocketClientHandler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerSpawnEvent {

    /**
     * Register event to handle player spawn.
     */
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayerEntity player) {
                notifyPlayerJoin(player);
            }
        });
    }

    /**
     * Handles the logic for when a player spawns.
     *
     * @param player the player entity that spawned
     */
    private static void handlePlayerSpawn(ServerPlayerEntity player) {
        notifyPlayerJoin(player);
    }

    /**
     * Notifies the server about a player's join event via WebSocket.
     *
     * @param player the player entity
     */
    private static void notifyPlayerJoin(ServerPlayerEntity player) {
        String playerName = player.getName().getString();
        String uuid = player.getUuidAsString();
        String message = String.format("{\"type\":\"player_join\",\"player\":\"%s\",\"uuid\":\"%s\"}",
                playerName, uuid);

        syncAccountWithServer(player,message);
    }

    /**
     * Syncs the player's account with the server.
     *
     * @param message the JSON message to send via WebSocket
     */
    private static void syncAccountWithServer(ServerPlayerEntity player,String message) {
        WebSocketClientHandler webSocket = WebSocketClientHandler.getInstance();
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.setMessageCallback(response -> handleWebSocketResponse(player,response));
            webSocket.sendMessage(message);
        } else {
            CustomNotification.showNotification("WebSocket connection is not open!", 200, "danger");
        }
    }

    /**
     * Handles the response from the WebSocket server.
     *
     * @param response the response message
     */
    private static void handleWebSocketResponse(ServerPlayerEntity player,String response) {
        ServerPlayNetworking.send(player, new SyncAccount(response));
    }

}
