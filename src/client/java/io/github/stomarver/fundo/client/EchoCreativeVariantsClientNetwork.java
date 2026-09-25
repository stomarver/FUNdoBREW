package io.github.stomarver.fundo.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.brewing.ExtendedPotionIndex;
import io.github.stomarver.fundo.client.mixin.CreativeModeTabsAccessor;
import io.github.stomarver.fundo.network.EchoCreativeVariantsPayload;

public final class EchoCreativeVariantsClientNetwork {

	private EchoCreativeVariantsClientNetwork() {
	}

	public static void registerReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(EchoCreativeVariantsPayload.TYPE, (payload, context) ->
				context.client().execute(() -> {
					ExtendedPotionIndex.installCreativeSources(payload.sources());
					fundo$rebuildTabs(context.client());

					if (FabricLoader.getInstance().isModLoaded("jei")) {
						fundo$refreshJeiContractRows();
					}
				}));
	}

	private static void fundo$refreshJeiContractRows() {
		try {
			Class.forName("io.github.stomarver.fundo.compat.jei.FundoJeiPlugin")
					.getMethod("refreshAfterCreativeContract").invoke(null);
		} catch (ReflectiveOperationException exception) {
			Fundo.LOGGER.warn("Could not refresh optional JEI Echo rows: {}", exception.toString());
		}
	}

	private static void fundo$rebuildTabs(Minecraft minecraft) {
		if (minecraft.player == null || minecraft.getConnection() == null) {
			return;
		}

		CreativeModeTabsAccessor.fundo$setCachedParameters(null);
		CreativeModeTabs.tryRebuildTabContents(
				minecraft.getConnection().enabledFeatures(),
				minecraft.player.canUseGameMasterBlocks(),
				minecraft.getConnection().registryAccess());
	}
}
