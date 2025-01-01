package com.mallardlabs.matscraft;

import com.mallardlabs.matscraft.client.hud.MatsBalanceOverlay;
import com.mallardlabs.matscraft.client.networking.ClientNetworkingHandler;
import com.mallardlabs.matscraft.util.CustomNotification;
import net.fabricmc.api.ClientModInitializer;


public class MatsCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MatsBalanceOverlay matsBalanceOverlay = new MatsBalanceOverlay();
        matsBalanceOverlay.initialize();
        CustomNotification.register();
        ClientNetworkingHandler.registerReceivers();
    }
}
