package com.mallardlabs.matscraft;

import com.mallardlabs.matscraft.block.ModBlocks;
import com.mallardlabs.matscraft.gui.MatsBalanceOverlay;
import com.mallardlabs.matscraft.item.ModItemGroups;
import com.mallardlabs.matscraft.item.ModItems;
import com.mallardlabs.matscraft.sound.ModSounds;
import com.mallardlabs.matscraft.util.CustomNotification;
import com.mallardlabs.matscraft.world.gen.ModWorldGeneration;
import net.fabricmc.api.ClientModInitializer;

import com.mallardlabs.matscraft.client.CustomHudRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class MatsCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CustomHudRenderer hudRenderer = new CustomHudRenderer();
        HudRenderCallback.EVENT.register(hudRenderer);
        MatsBalanceOverlay matsBalanceOverlay = new MatsBalanceOverlay();
        matsBalanceOverlay.initialize();
        CustomNotification.register();

    }
}
