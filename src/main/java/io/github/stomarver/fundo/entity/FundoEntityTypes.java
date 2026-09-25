package io.github.stomarver.fundo.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.FundoConfig;

public final class FundoEntityTypes {

	public static EntityType<ThrownSplashMilkBottle> SPLASH_MILK_BOTTLE = null;
	public static EntityType<ThrownLingeringMilkBottle> LINGERING_MILK_BOTTLE = null;
	public static EntityType<MilkCleansingCloud> MILK_CLEANSING_CLOUD = null;

	private FundoEntityTypes() {
	}

	public static void register() {
		if (FundoConfig.milk_changes) {
			SPLASH_MILK_BOTTLE = Registry.register(
					BuiltInRegistries.ENTITY_TYPE,
					Fundo.id("splash_milk_bottle"),
					EntityType.Builder.<ThrownSplashMilkBottle>of(ThrownSplashMilkBottle::new, MobCategory.MISC)
							.sized(0.25F, 0.25F)
							.clientTrackingRange(4)
							.updateInterval(10)
							.build(ResourceKey.create(Registries.ENTITY_TYPE, Fundo.id("splash_milk_bottle"))));
		}
		if (FundoConfig.milk_changes) {
			LINGERING_MILK_BOTTLE = Registry.register(
					BuiltInRegistries.ENTITY_TYPE,
					Fundo.id("lingering_milk_bottle"),
					EntityType.Builder.<ThrownLingeringMilkBottle>of(ThrownLingeringMilkBottle::new, MobCategory.MISC)
							.sized(0.25F, 0.25F)
							.clientTrackingRange(4)
							.updateInterval(10)
							.build(ResourceKey.create(Registries.ENTITY_TYPE, Fundo.id("lingering_milk_bottle"))));
			MILK_CLEANSING_CLOUD = Registry.register(
					BuiltInRegistries.ENTITY_TYPE,
					Fundo.id("milk_cleansing_cloud"),
					EntityType.Builder.<MilkCleansingCloud>of(MilkCleansingCloud::new, MobCategory.MISC)
							.sized(6.0F, 0.5F)
							.clientTrackingRange(10)
							.build(ResourceKey.create(Registries.ENTITY_TYPE, Fundo.id("milk_cleansing_cloud"))));
		}
	}
}
