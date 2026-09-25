package io.github.stomarver.fundo.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class PotionHitboxImpactNetwork {

	private PotionHitboxImpactNetwork() {
	}

	public static void registerPayload() {
		PayloadTypeRegistry.clientboundPlay().register(PotionHitboxImpactPayload.TYPE, PotionHitboxImpactPayload.CODEC);
	}

	public static void sendToLevelPlayers(ServerLevel level, PotionHitboxImpactPayload payload) {
		for (ServerPlayer player : level.players()) {
			if (ServerPlayNetworking.canSend(player, PotionHitboxImpactPayload.TYPE)) {
				ServerPlayNetworking.send(player, payload);
			}
		}
	}
}
