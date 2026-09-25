package io.github.stomarver.fundo.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.compat.MilkBottleCompatibility;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.RegistryOps;

public record FundoFeatureCondition(Feature feature) implements ResourceCondition {

	public static final ResourceConditionType<FundoFeatureCondition> TYPE = ResourceConditionType.create(
			Fundo.id("feature_enabled"),
			Codec.STRING.fieldOf("feature")
					.xmap(Feature::fromSerializedName, Feature::serializedName)
					.xmap(FundoFeatureCondition::new, FundoFeatureCondition::feature));

	public static void register() {
		ResourceConditions.register(TYPE);
	}

	@Override
	public ResourceConditionType<?> getType() {
		return TYPE;
	}

	@Override
	public boolean test(RegistryOps.RegistryInfoLookup registries) {
		return this.feature.isEnabled();
	}

	public enum Feature {
		MILK_CHANGES("milk_changes") {
			@Override
			boolean isEnabled() {
				return FundoConfig.milk_changes;
			}
		},
		NATIVE_MILK_BOTTLE("native_milk_bottle") {
			@Override
			boolean isEnabled() {
				return FundoConfig.milk_changes && !MilkBottleCompatibility.usesFarmersDelightBottle();
			}
		},
		FARMERS_DELIGHT_MILK_BOTTLE("farmers_delight_milk_bottle") {
			@Override
			boolean isEnabled() {
				return FundoConfig.milk_changes && MilkBottleCompatibility.usesFarmersDelightBottle();
			}
		},
		INFINITE_POTIONS("infinite_potions") {
			@Override
			boolean isEnabled() {
				return FundoConfig.infinite_potions;
			}
		};

		private final String serializedName;

		Feature(String serializedName) {
			this.serializedName = serializedName;
		}

		abstract boolean isEnabled();

		String serializedName() {
			return this.serializedName;
		}

		static Feature fromSerializedName(String name) {
			for (Feature feature : values()) {
				if (feature.serializedName.equals(name)) {
					return feature;
				}
			}
			throw new IllegalArgumentException("Unknown FUNdoBREW feature condition: " + name);
		}
	}
}
