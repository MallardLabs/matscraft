package com.mallardlabs.matscraft;

import com.mallardlabs.matscraft.block.ModBlocks;
import com.mallardlabs.matscraft.config.ConfigManager;
import com.mallardlabs.matscraft.item.ModItemGroups;
import com.mallardlabs.matscraft.item.ModItems;
import com.mallardlabs.matscraft.sound.ModSounds;
import com.mallardlabs.matscraft.world.gen.ModWorldGeneration;
import com.mallardlabs.matscraft.block.BlockBreakEvent;
import com.mallardlabs.matscraft.commands.LinkAccount;
import com.mallardlabs.matscraft.ws.WebSocketManager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatsCraft implements ModInitializer {
	public static final String MOD_ID = "matscraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModSounds.registerSounds();
		ModWorldGeneration.generateModWorldGeneration();
		BlockBreakEvent.register();
		WebSocketManager.initializeWebSocket(ConfigManager.getWebSocketUrl());

        // Listener For Link Account Command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
			LinkAccount.register(dispatcher);
		});
	}
}