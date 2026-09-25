package io.github.stomarver.fundo.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;

import io.github.stomarver.fundo.brewing.ExtendedPotionIndex;

public final class EchoCreativeVariantsNetwork {

	private EchoCreativeVariantsNetwork() {
	}

	public static void registerPayload() {
		PayloadTypeRegistry.clientboundPlay().register(EchoCreativeVariantsPayload.TYPE, EchoCreativeVariantsPayload.CODEC);
	}

	public static void sync(ServerPlayer player) {
		if (ServerPlayNetworking.canSend(player, EchoCreativeVariantsPayload.TYPE)) {
			ServerPlayNetworking.send(player, new EchoCreativeVariantsPayload(ExtendedPotionIndex.creativeSources()));
		}
	}
}
