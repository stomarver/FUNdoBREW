package io.github.stomarver.fundo;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.world.level.Level;

import io.github.stomarver.fundo.block.FundoBlocks;
import io.github.stomarver.fundo.block.FundoCauldronInteractions;
import io.github.stomarver.fundo.brewing.ExtendedPotionIndex;
import io.github.stomarver.fundo.brewing.FundoBrewing;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.config.FundoFeatureCondition;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.migration.FundoContentMigration;
import io.github.stomarver.fundo.entity.FundoEntityTypes;
import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.item.FundoItems;
import io.github.stomarver.fundo.network.EchoCreativeVariantsNetwork;
import io.github.stomarver.fundo.network.PotionHitboxImpactNetwork;
import io.github.stomarver.fundo.network.PotionStackingNetwork;
import io.github.stomarver.fundo.particle.FundoParticles;

import eu.midnightdust.lib.config.MidnightConfig;

public class Fundo implements ModInitializer {
	public static final String MOD_ID = "fundo";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		MidnightConfig.init(MOD_ID, FundoConfig.class);

		FundoFeatureCondition.register();

		PotionHitboxImpactNetwork.registerPayload();
		PotionStackingNetwork.registerPayload();
		EchoCreativeVariantsNetwork.registerPayload();

		FundoFluids.register();
		FundoBlocks.register();
		FundoItems.register();

		FundoBrewing.register();
		FundoParticles.register();
		FundoEntityTypes.register();
		FundoContentMigration.register();

		ServerLevelEvents.LOAD.register((server, world) -> {
			if (world.dimension() == Level.OVERWORLD) {
				ActionLogs.sessionOpened(world, server.getWorldData().getLevelName());
			}
		});
		ServerLevelEvents.UNLOAD.register((server, world) -> {
			if (world.dimension() == Level.OVERWORLD) {
				ActionLogs.sessionClosed(world, server.getWorldData().getLevelName());
			}
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ActionLogs.infinite(handler.player.level(), "player | join " + ActionLogs.subject(handler.player));
			ActionLogs.potions(handler.player.level(), "player | join " + ActionLogs.subject(handler.player));
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ActionLogs.infinite(handler.player.level(), "player | leave " + ActionLogs.subject(handler.player));
			ActionLogs.potions(handler.player.level(), "player | leave " + ActionLogs.subject(handler.player));
		});
		ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) -> {
			ActionLogs.infinite(server.overworld(), "world | saved (flush=" + flush + ")");
			ActionLogs.potions(server.overworld(), "world | saved (flush=" + flush + ")");
		});

		// Deferred registry lookup keeps optional Farmer's Delight integration independent of initializer order.
		ServerLifecycleEvents.SERVER_STARTING.register(server -> FundoCauldronInteractions.register());

		ServerLifecycleEvents.SERVER_STARTED.register(server ->
				ExtendedPotionIndex.refresh(server.getRecipeManager()));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) ->
				ExtendedPotionIndex.refresh(server.getRecipeManager()));

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			PotionStackingNetwork.sync(player);
			EchoCreativeVariantsNetwork.sync(player);
		});

		String version = FabricLoader.getInstance().getModContainer(MOD_ID)
				.map(container -> container.getMetadata().getVersion().getFriendlyString())
				.orElse("dev");
		LOGGER.info("FUNdoBREW {} initialized", version);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
