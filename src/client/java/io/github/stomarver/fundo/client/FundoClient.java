package io.github.stomarver.fundo.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.chat.Component;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.block.FundoBlocks;
import io.github.stomarver.fundo.block.FundoCauldronInteractions;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.config.FundoFeaturePolicy;
import io.github.stomarver.fundo.client.debug.EffectInfiniteTagListEntry;
import io.github.stomarver.fundo.client.debug.EffectInfiniteTagListOnEntry;
import io.github.stomarver.fundo.client.debug.PotionHitboxesEntry;
import io.github.stomarver.fundo.client.mixin.DebugScreenEntriesInvoker;
import io.github.stomarver.fundo.client.particle.MilkDripParticle;
import io.github.stomarver.fundo.client.particle.MilkMistParticle;
import io.github.stomarver.fundo.client.particle.MilkSplashParticle;
import io.github.stomarver.fundo.client.particle.UndermilkParticle;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxDebug;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxGizmos;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxImpactClientNetwork;
import io.github.stomarver.fundo.entity.FundoEntityTypes;
import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.particle.FundoParticles;

public class FundoClient implements ClientModInitializer {
	private static final Identifier ENHANCED_MILK_VISION_PACK = Fundo.id("enhanced_milk_vision");

	@Override
	public void onInitializeClient() {
		registerEnhancedMilkVisionPack();

		 
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> FundoCauldronInteractions.register());

		if (FundoFluids.MILK != null && FundoFluids.FLOWING_MILK != null && FundoBlocks.MILK != null) {
			 
			 
			 
			 
			FluidRenderingRegistry.register(FundoFluids.MILK, FundoFluids.FLOWING_MILK,
					new FluidModel.Unbaked(
							new Material(Fundo.id("block/milk_still")),
							new Material(Fundo.id("block/milk_flow")),
							null,
							null));
			FluidRenderingRegistry.setBlockTransparency(FundoBlocks.MILK, false);
		}
		if (FundoBlocks.MILK_ICE != null) {
			 
			FluidRenderingRegistry.setBlockTransparency(FundoBlocks.MILK_ICE, true);
		}

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
		if (FundoParticles.UNDERMILK != null) {
			ParticleProviderRegistry.getInstance().register(FundoParticles.UNDERMILK, UndermilkParticle.Provider::new);
		}
		if (FundoParticles.MILK_DRIPPING != null) {
			ParticleProviderRegistry.getInstance().register(FundoParticles.MILK_DRIPPING, MilkDripParticle.HangingProvider::new);
		}
		if (FundoParticles.MILK_FALLING != null) {
			ParticleProviderRegistry.getInstance().register(FundoParticles.MILK_FALLING, MilkDripParticle.FallingProvider::new);
		}
		if (FundoParticles.MILK_DRIP_SPLASH != null) {
			ParticleProviderRegistry.getInstance().register(FundoParticles.MILK_DRIP_SPLASH, MilkDripParticle.SplashProvider::new);
		}

		if (FundoFeaturePolicy.brewingAdditions()) {
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

		FundoFeaturePolicyClientNetwork.registerReceiver();
		EchoCreativeVariantsClientNetwork.registerReceiver();
	}

	private static void registerEnhancedMilkVisionPack() {
		boolean registered = FabricLoader.getInstance().getModContainer(Fundo.MOD_ID)
				.map(container -> ResourceManagerHelper.registerBuiltinResourcePack(
						ENHANCED_MILK_VISION_PACK,
						container,
						Component.literal("Enhanced Milk Vision"),
						ResourcePackActivationType.NORMAL))
				.orElse(false);

		if (!registered) {
			Fundo.LOGGER.error("Could not register optional built-in resource pack {}", ENHANCED_MILK_VISION_PACK);
		} else {
			Fundo.LOGGER.info("Registered optional built-in resource pack {} (disabled by default)", ENHANCED_MILK_VISION_PACK);
		}
	}
}

