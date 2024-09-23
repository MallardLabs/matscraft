package com.mallardlabs.matscraft.item;

import com.mallardlabs.matscraft.MatsCraft;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item MATS = registerItem("mats", new Item(new Item.Settings()));
    public static final Item MATT_LUONGO = registerItem("matt_luongo", new Item(new Item.Settings()));

    private static Item registerItem (String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MatsCraft.MOD_ID, name), item);
    }

    public static void registerModItems () {
        MatsCraft.LOGGER.info("Registering Mod Items for " + MatsCraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(MATS);
            entries.add(MATT_LUONGO);
        });
    }
}
