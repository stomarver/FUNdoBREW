package io.github.stomarver.fundo.block;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.sound.FundoSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import io.github.stomarver.fundo.fluid.FundoFluids;

import java.util.function.Function;

public final class FundoBlocks {

	public static Block MILK_CAULDRON;
	public static Block MILK;
	public static Block MILK_ICE;

	private FundoBlocks() {
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory,
			BlockBehaviour.Properties settings) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Fundo.id(name));
		Block block = blockFactory.apply(settings.setId(blockKey));
		return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
	}

	public static void register() {
		MILK_CAULDRON = register(
				"milk_cauldron",
				MilkCauldronBlock::new,
				BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));

		if (FundoFluids.MILK != null) {
			MILK = register(
					"milk",
					properties -> new MilkLiquidBlock(FundoFluids.MILK, properties),
					BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));
			MILK_ICE = register(
					"milk_ice",
					MilkIceBlock::new,
					 
					 
					BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).sound(FundoSounds.MILK_ICE));
		}
	}
}
