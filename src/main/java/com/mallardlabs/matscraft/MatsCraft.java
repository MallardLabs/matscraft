package com.mallardlabs.matscraft;

import com.mallardlabs.matscraft.block.ModBlocks;
import com.mallardlabs.matscraft.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatsCraft implements ModInitializer {
	public static final String MOD_ID = "matscraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
	}
}