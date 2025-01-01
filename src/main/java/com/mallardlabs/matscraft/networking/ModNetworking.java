package com.mallardlabs.matscraft.networking;

import com.mallardlabs.matscraft.MatsCraft;
import com.mallardlabs.matscraft.networking.Payload.SyncAccount;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier json_payload = Identifier.of(MatsCraft.MOD_ID,"json_payload");
    public static void register() {
        PayloadTypeRegistry.playS2C().register(SyncAccount.ID, SyncAccount.CODEC);
    }

}