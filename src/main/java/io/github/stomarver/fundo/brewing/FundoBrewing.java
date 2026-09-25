package io.github.stomarver.fundo.brewing;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class FundoBrewing {

	private FundoBrewing() {
	}

	public static void register() {
		if (!FundoConfig.infinite_potions) {
			return;
		}
		EchoBrewingRecipe recipe = EchoBrewingRecipe.createRecipe();
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
				Fundo.id("echo_brewing"), EchoBrewingRecipe.createSerializer(recipe));
		Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
				Fundo.id("infinite_conversion"), InfiniteConversionRecipe.SERIALIZER);
	}
}
