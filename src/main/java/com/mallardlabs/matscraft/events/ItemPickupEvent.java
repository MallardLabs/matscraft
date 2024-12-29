package com.mallardlabs.matscraft.events;

import com.mallardlabs.matscraft.ws.WebSocketClientHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ItemPickupEvent {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getPlayerManager().getPlayerList().forEach(player -> {
                if (player != null) {
                    checkAndRemoveMats(player);
                }
            });
        });
    }

    private static void checkAndRemoveMats(ServerPlayerEntity player) {
        var inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem().getTranslationKey().equals("item.matscraft.mats")) {
                int count = stack.getCount();
                inventory.removeStack(i);

                // Kirim ke WebSockets
                String message = String.format(
                    "{\"type\":\"item_pickup\",\"player\":\"%s\",\"uuid\":\"%s\",\"item\":\"mats\",\"count\":%d}",
                    player.getName().getString(),
                    player.getUuidAsString(),
                    count
                );

                if (WebSocketClientHandler.getInstance() != null && WebSocketClientHandler.getInstance().isOpen()) {
                    WebSocketClientHandler client = WebSocketClientHandler.getInstance();
                    client.setMessageCallback(response -> {
                        try {
                            // Tampilkan pesan ke pemain berdasarkan response
                            System.out.println("Response: " + response);
                            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                            boolean success = jsonResponse.get("success").getAsBoolean();
                            player.sendMessage(
                                Text.literal(success ? 
                                    "+ " + count + " Mats" : 
                                    jsonResponse.get("message").getAsString())
                                    .formatted(success ? Formatting.GREEN : Formatting.RED),
                                true
                            );
                        } catch (Exception e) {
                            player.sendMessage(
                                Text.literal("Error processing server response")
                                    .formatted(Formatting.RED),
                                true
                            );
                        }
                    });
                    
                    WebSocketClientHandler.sendMessage(message);
                } else {
                    player.sendMessage(
                        Text.literal("Failed to process Mats pickup - Connection error")
                            .formatted(Formatting.RED),
                        true
                    );
                }
            }
        }
    }
}
