package com.mallardlabs.matscraft.networking.Payload;

import com.mallardlabs.matscraft.networking.ModNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncAccount(String json) implements CustomPayload {

    // Defines a static final identifier for this type of custom payload.
    public static final CustomPayload.Id<SyncAccount> ID = new CustomPayload.Id<>(ModNetworking.json_payload);
    
    // Codec for encoding and decoding JSON as a String.
    // This codec specifies how to convert between SyncAccount and its string representation.
    public static final PacketCodec<RegistryByteBuf, SyncAccount> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SyncAccount::json, SyncAccount::new
    );

    /**
     * Returns the identifier of this custom payload.
     *
     * @return The unique identifier of this type of custom payload.
     */
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    /**
     * Getter for obtaining the JSON contained in the payload.
     *
     * @return The JSON string contained in this packet.
     */
    public String json() {
        return json;
    }
}
