package io.github.stomarver.fundo.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.block.FundoCauldronInteractions;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.client.debug.EffectInfiniteTagListEntry;
import io.github.stomarver.fundo.client.debug.EffectInfiniteTagListOnEntry;
import io.github.stomarver.fundo.client.debug.PotionHitboxesEntry;
import io.github.stomarver.fundo.client.mixin.DebugScreenEntriesInvoker;
import io.github.stomarver.fundo.client.particle.MilkMistParticle;
import io.github.stomarver.fundo.client.particle.MilkSplashParticle;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxDebug;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxGizmos;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxImpactClientNetwork;
import io.github.stomarver.fundo.entity.FundoEntityTypes;
import io.github.stomarver.fundo.particle.FundoParticles;

public class FundoClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		// Common entrypoints from optional mods have completed by this lifecycle phase.
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> FundoCauldronInteractions.register());

		if (FundoEntityTypes.SPLASH_MILK_BOTTLE != null) {
			EntityRendererRegistry.register(FundoEntityTypes.SPLASH_MILK_BOTTLE, ThrownItemRenderer::new);
		}
		if (FundoEntityTypes.LINGERING_MILK_BOTTLE != null) {
			EntityRendererRegistry.register(FundoEntityTypes.LINGERING_MILK_BOTTLE, ThrownItemRenderer::new);
		}
		if (FundoEntityTypes.MILK_CLEANSING_CLOUD != null) {
			EntityRendererRegistry.register(FundoEntityTypes.MILK_CLEANSING_CLOUD, NoopRenderer::new);
		}

		if (FundoParticles.MILK_MIST != null) {
			ParticleProviderRegistry.getInstance().register(FundoParticles.MILK_MIST, MilkMistParticle.Provider::new);
		}
		if (FundoParticles.MILK_SPLASH != null) {
			ParticleProviderRegistry.getInstance().register(FundoParticles.MILK_SPLASH, MilkSplashParticle.Provider::new);
		}

		if (FundoConfig.infinite_potions) {
			DebugScreenEntriesInvoker.fundo$register(
					EffectInfiniteTagListEntry.ID, new EffectInfiniteTagListEntry());
			DebugScreenEntriesInvoker.fundo$register(
					EffectInfiniteTagListOnEntry.ID, new EffectInfiniteTagListOnEntry());
		}

		DebugScreenEntriesInvoker.fundo$register(
				PotionHitboxesEntry.ID, new PotionHitboxesEntry());

		PotionHitboxImpactClientNetwork.registerReceiver();
		PotionHitboxDebug.register();
		PotionHitboxGizmos.init();

		Fundo.LOGGER.info("Registered debug screen entries: {}, {}, and {} (Debug Modifier Key + configurable key)",
				EffectInfiniteTagListEntry.ID, EffectInfiniteTagListOnEntry.ID, PotionHitboxesEntry.ID);

		PotionStackingClientNetwork.registerReceiver();
		EchoCreativeVariantsClientNetwork.registerReceiver();
	}
}

