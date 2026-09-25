package io.github.stomarver.fundo.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.server.level.ServerPlayer;

import io.github.stomarver.fundo.config.FundoConfig;

public final class PotionStackingNetwork {

	private PotionStackingNetwork() {
	}

	public static void registerPayload() {
		PayloadTypeRegistry.clientboundPlay().register(PotionStackingPayload.TYPE, PotionStackingPayload.CODEC);
	}

	public static void sync(ServerPlayer player) {
		if (ServerPlayNetworking.canSend(player, PotionStackingPayload.TYPE)) {
			ServerPlayNetworking.send(player, new PotionStackingPayload(FundoConfig.increased_potion_stacking));
		}
	}
}
