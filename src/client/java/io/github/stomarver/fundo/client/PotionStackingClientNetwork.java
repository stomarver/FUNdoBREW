package io.github.stomarver.fundo.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import io.github.stomarver.fundo.item.PotionStacking;
import io.github.stomarver.fundo.network.PotionStackingPayload;

public final class PotionStackingClientNetwork {

	private PotionStackingClientNetwork() {
	}

	public static void registerReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(PotionStackingPayload.TYPE,
				(payload, context) -> context.client().execute(() -> PotionStacking.installServerValue(payload.enabled())));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> PotionStacking.clearServerValue());
	}
}
