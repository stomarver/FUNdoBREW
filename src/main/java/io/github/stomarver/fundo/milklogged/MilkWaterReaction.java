package io.github.stomarver.fundo.milklogged;

import io.github.stomarver.fundo.block.FundoBlocks;
import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.sound.FundoSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;

 








public final class MilkWaterReaction {

	private MilkWaterReaction() {
	}

	public static boolean isOrdinaryWater(Fluid fluid) {
		return fluid.is(FluidTags.WATER) && !fluid.isSame(FundoFluids.MILK);
	}

	public static boolean isFreeOrdinaryWater(BlockState state) {
		return isOrdinaryWater(state.getFluidState().getType())
				&& !(state.hasProperty(BlockStateProperties.WATERLOGGED)
						&& state.getValue(BlockStateProperties.WATERLOGGED));
	}

	 





	public static boolean freeze(ServerLevel level, BlockPos pos) {
		if (FundoBlocks.MILK_ICE == null || level.getBlockState(pos).is(FundoBlocks.MILK_ICE)) {
			return false;
		}
		if (!level.setBlock(pos, FundoBlocks.MILK_ICE.defaultBlockState(), Block.UPDATE_ALL)) {
			return false;
		}
		level.playSound(null, pos, FundoSounds.MILK_ICE_FORM, SoundSource.BLOCKS, 0.8F, 1.0F);
		return true;
	}

	public static boolean hasSideOrUpperWater(Level level, BlockPos milkPos) {
		for (Direction direction : Direction.values()) {
			if (direction != Direction.DOWN
					&& isOrdinaryWater(level.getFluidState(milkPos.relative(direction)).getType())) {
				return true;
			}
		}
		return false;
	}
}
