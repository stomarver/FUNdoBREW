package io.github.stomarver.fundo.milklogged;

import io.github.stomarver.fundo.fluid.FundoFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

 




public final class MilkloggedWaterReaction {
	private MilkloggedWaterReaction() {
	}

	 



	public static boolean freezeFreeWater(ServerLevel level, BlockPos waterPos, BlockState waterState) {
		if (!MilkWaterReaction.isFreeOrdinaryWater(waterState)) {
			return false;
		}

		 
		return MilkWaterReaction.freeze(level, waterPos);
	}

	 




	public static void freezeAdjacentFreeWater(ServerLevel level, BlockPos hostPos) {
		for (Direction direction : Direction.values()) {
			BlockPos waterPos = hostPos.relative(direction);
			freezeFreeWater(level, waterPos, level.getBlockState(waterPos));
		}
	}

}
