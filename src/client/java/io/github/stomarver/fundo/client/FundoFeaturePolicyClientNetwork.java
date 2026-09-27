package io.github.stomarver.fundo.client;

import io.github.stomarver.fundo.config.FundoFeaturePolicy;
import io.github.stomarver.fundo.network.FundoFeaturePolicyPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

 
public final class FundoFeaturePolicyClientNetwork {

	private FundoFeaturePolicyClientNetwork() {
	}

	public static void registerReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(FundoFeaturePolicyPayload.TYPE, (payload, context) ->
				context.client().execute(() -> {
					FundoFeaturePolicy.installServerPolicy(payload);
					EchoCreativeVariantsClientNetwork.refreshPresentation(context.client());
				}));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(() -> {
			FundoFeaturePolicy.clearServerPolicy();
			EchoCreativeVariantsClientNetwork.refreshPresentation(client);
		}));
	}
}
