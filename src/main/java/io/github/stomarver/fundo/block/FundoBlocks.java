package io.github.stomarver.fundo.block;

import io.github.stomarver.fundo.Fundo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class FundoBlocks {

	public static Block MILK_CAULDRON;

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
	}
}
