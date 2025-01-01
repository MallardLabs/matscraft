package com.mallardlabs.matscraft.client.networking;

import com.google.gson.JsonObject;
import com.mallardlabs.matscraft.client.hud.MatsBalanceOverlay;
import com.mallardlabs.matscraft.networking.Payload.SyncAccount;
import com.mallardlabs.matscraft.util.CustomNotification;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.google.gson.JsonParser;

public class ClientNetworkingHandler {
    public static void registerReceivers() {
        // Existing listener
        ClientPlayNetworking.registerGlobalReceiver(SyncAccount.ID, (payload, context) -> {
            context.client().execute(() -> {
                try {
                    // Konversi payload ke string
                    String payloadString = payload.toString();

                    // Ekstrak bagian JSON (format sesuai dengan output yang diterima)
                    int jsonStartIndex = payloadString.indexOf("json=") + 5; // Cari index setelah "json="
                    String jsonString = payloadString.substring(jsonStartIndex, payloadString.length() - 1);

                    // Parse JSON string menjadi JsonObject
                    JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                    // Akses elemen dalam JSON
                    boolean success = jsonObject.get("success").getAsBoolean();
                    String message = jsonObject.get("message").getAsString();

                    if (!success){
                        CustomNotification.showNotification(message, 200, "danger");
                        return;
                    }
                    JsonObject data = (JsonObject) jsonObject.get("data");
                    MatsBalanceOverlay.DiscordUsername = data.get("discordUsername").getAsString();
                    MatsBalanceOverlay.playerBalance = data.get("balance").getAsInt();
                    CustomNotification.showNotification("Sync Sucess", 200, "success");
                    System.out.println("Success: " + data);
                    System.out.println("Message: " + message);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Failed to parse JSON payload: " + payload);
                }
            });
        });

    }
}
