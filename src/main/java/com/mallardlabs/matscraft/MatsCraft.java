package com.mallardlabs.matscraft;

import com.mallardlabs.matscraft.block.ModBlocks;
import com.mallardlabs.matscraft.events.ItemPickupEvent;
import com.mallardlabs.matscraft.events.PlayerSpawnEvent;
import com.mallardlabs.matscraft.item.ModItemGroups;
import com.mallardlabs.matscraft.item.ModItems;
import com.mallardlabs.matscraft.sound.ModSounds;
import com.mallardlabs.matscraft.util.CustomNotification;
import com.mallardlabs.matscraft.world.gen.ModWorldGeneration;
import com.mallardlabs.matscraft.events.BlockBreak;
import com.mallardlabs.matscraft.commands.LinkAccount;
import com.mallardlabs.matscraft.gui.MatsBalanceOverlay;
import com.mallardlabs.matscraft.ws.WebSocketClientHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MatsCraft implements ModInitializer{
	public static final String MOD_ID = "matscraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize(){
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModSounds.registerSounds();
		ModWorldGeneration.generateModWorldGeneration();
		BlockBreak.register();
		ItemPickupEvent.register();
		PlayerSpawnEvent.register();


		// Initialize WebSocket connection
		//WebSocketClientHandler.initialize("ws://localhost:8080");

		// Add shutdown hook for clean WebSocket closure
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			WebSocketClientHandler client = WebSocketClientHandler.getInstance();
			if (client != null) {
				client.close();
			}
		}));
		WebSocketClientHandler.sendMessage("Pesan dari Minecraft mod!");


		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
			LinkAccount.register(dispatcher);
		});
	}

}