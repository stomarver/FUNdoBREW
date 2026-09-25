package io.github.stomarver.fundo.fluid;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;

public final class FundoFluids {
	public static FlowingFluid MILK;
	public static FlowingFluid FLOWING_MILK;

	private FundoFluids() {
	}

	public static void register() {
		if (!FundoConfig.milk_changes) {
			return;
		}
		MILK = Registry.register(BuiltInRegistries.FLUID, Fundo.id("milk"), new MilkFluid.Still());
		FLOWING_MILK = Registry.register(BuiltInRegistries.FLUID, Fundo.id("flowing_milk"), new MilkFluid.Flowing());
	}
}
