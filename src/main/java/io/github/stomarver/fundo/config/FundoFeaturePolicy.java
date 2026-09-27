package io.github.stomarver.fundo.config;

import java.util.List;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.block.FundoCauldronInteractions;
import io.github.stomarver.fundo.network.FundoFeaturePolicyPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

 





public final class FundoFeaturePolicy {

	private static volatile FundoFeaturePolicyPayload serverPolicy;
	private static volatile MinecraftServer activeServer;
	 
	 
	 
	private static boolean dataPackReloadInProgress;
	private static boolean followUpDataPackReload;

	private FundoFeaturePolicy() {
	}

	public static void registerPayload() {
		PayloadTypeRegistry.clientboundPlay().register(FundoFeaturePolicyPayload.TYPE, FundoFeaturePolicyPayload.CODEC);
	}

	public static boolean milkAdditions() {
		FundoFeaturePolicyPayload policy = serverPolicy;
		return policy != null ? policy.milkAdditions() : FundoConfig.milk_additions;
	}

	public static boolean brewingAdditions() {
		FundoFeaturePolicyPayload policy = serverPolicy;
		return policy != null ? policy.brewingAdditions() : FundoConfig.brewing_additions;
	}

	public static boolean farmersDelightCompatibility() {
		FundoFeaturePolicyPayload policy = serverPolicy;
		return policy != null ? policy.farmersDelightCompatibility() : FundoConfig.farmers_delight;
	}

	public static boolean increasedPotionStacking() {
		FundoFeaturePolicyPayload policy = serverPolicy;
		return policy != null ? policy.increasedPotionStacking() : FundoConfig.increased_potion_stacking;
	}

	public static boolean milkBucketPouring() {
		FundoFeaturePolicyPayload policy = serverPolicy;
		return policy != null ? policy.milkBucketPouring() : FundoConfig.milk_bucket_pouring;
	}

	public static boolean milkBucketDrinking() {
		FundoFeaturePolicyPayload policy = serverPolicy;
		return policy != null ? policy.milkBucketDrinking() : FundoConfig.milk_bucket_drinking;
	}

	public static int brewingSpeedMultiplier() {
		FundoFeaturePolicyPayload policy = serverPolicy;
		return policy != null ? Math.max(1, policy.brewingSpeedMultiplier()) : FundoConfig.brewingSpeedMultiplier();
	}

	public static FundoFeaturePolicyPayload localPolicy() {
		return new FundoFeaturePolicyPayload(FundoConfig.milk_additions, FundoConfig.brewing_additions,
				FundoConfig.farmers_delight, FundoConfig.increased_potion_stacking,
				FundoConfig.milk_bucket_pouring, FundoConfig.milk_bucket_drinking,
				FundoConfig.brewingSpeedMultiplier());
	}

	 
	public static void installServerPolicy(FundoFeaturePolicyPayload policy) {
		serverPolicy = policy;
		FundoCauldronInteractions.refresh();
	}

	 
	public static void clearServerPolicy() {
		serverPolicy = null;
		FundoCauldronInteractions.refresh();
	}

	public static void serverStarted(MinecraftServer server) {
		activeServer = server;
		dataPackReloadInProgress = false;
		followUpDataPackReload = false;
	}

	public static void serverStopped(MinecraftServer server) {
		if (activeServer == server) {
			activeServer = null;
		}
		dataPackReloadInProgress = false;
		followUpDataPackReload = false;
	}

	public static void sync(ServerPlayer player) {
		if (ServerPlayNetworking.canSend(player, FundoFeaturePolicyPayload.TYPE)) {
			ServerPlayNetworking.send(player, localPolicy());
		}
	}

	 





	public static void configSaved() {
		MinecraftServer server = activeServer;
		if (server == null) {
			if (serverPolicy != null) {
				Fundo.LOGGER.info("FUNdoBREW feature settings are controlled by the connected server; local values will apply after disconnecting");
			}
			return;
		}
		server.execute(() -> {
			FundoCauldronInteractions.refresh();
			broadcast(server);
			requestDataPackReload(server);
		});
	}

	private static void requestDataPackReload(MinecraftServer server) {
		if (dataPackReloadInProgress) {
			followUpDataPackReload = true;
			return;
		}
		dataPackReloadInProgress = true;
		List<String> selectedPacks = List.copyOf(server.getPackRepository().getSelectedIds());
		server.reloadResources(selectedPacks).whenComplete((unused, error) -> {
			 
			 
			if (activeServer != server) {
				return;
			}
			server.execute(() -> {
				if (activeServer != server) {
					return;
				}
				dataPackReloadInProgress = false;
				if (error != null) {
					Fundo.LOGGER.error("Could not reload data packs after FUNdoBREW feature settings changed", error);
				} else {
					FundoCauldronInteractions.refresh();
					broadcast(server);
					Fundo.LOGGER.info("Reloaded data packs after FUNdoBREW feature settings changed");
				}
				if (followUpDataPackReload) {
					followUpDataPackReload = false;
					requestDataPackReload(server);
				}
			});
		});
	}

	private static void broadcast(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			sync(player);
		}
	}
}
