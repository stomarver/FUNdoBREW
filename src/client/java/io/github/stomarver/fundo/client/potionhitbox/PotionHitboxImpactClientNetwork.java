package io.github.stomarver.fundo.client.potionhitbox;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import io.github.stomarver.fundo.network.PotionHitboxImpactPayload;

public final class PotionHitboxImpactClientNetwork {

	private PotionHitboxImpactClientNetwork() {
	}

	public static void registerReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(PotionHitboxImpactPayload.TYPE,
				(payload, context) -> context.client().execute(() -> PotionHitboxGizmos.recordServerImpact(payload)));
	}
}
