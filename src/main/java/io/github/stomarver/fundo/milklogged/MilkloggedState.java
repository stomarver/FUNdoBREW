package io.github.stomarver.fundo.milklogged;

import io.github.stomarver.fundo.fluid.FundoFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;

 








public final class MilkloggedState {
	public static final BooleanProperty MILKLOGGED = BooleanProperty.create("milklogged");

	private MilkloggedState() {
	}

	 
	public static boolean hasMilkloggedProperty(BlockState state) {
		return state.hasProperty(MILKLOGGED);
	}

	 



	public static boolean canMilklog(BlockState state) {
		return state.hasProperty(MILKLOGGED)
				&& state.hasProperty(BlockStateProperties.WATERLOGGED)
				&& !state.getValue(MILKLOGGED);
	}

	public static boolean isMilklogged(BlockState state) {
		return state.hasProperty(MILKLOGGED) && state.getValue(MILKLOGGED);
	}

	 




	public static boolean tryMilklog(LevelAccessor level, BlockPos pos, BlockState state) {
		if (FundoFluids.MILK == null || !canMilklog(state)) {
			return false;
		}

		BlockState milklogged = state
				.setValue(BlockStateProperties.WATERLOGGED, false)
				.setValue(MILKLOGGED, true);
		if (!level.setBlock(pos, milklogged, 11)) {
			return false;
		}

		 
		 
		level.scheduleTick(pos, FundoFluids.MILK, FundoFluids.MILK.getTickDelay(level));
		return true;
	}

	 




	public static boolean tryWaterlog(LevelAccessor level, BlockPos pos, BlockState state) {
		if (!isMilklogged(state)) {
			return false;
		}

		BlockState waterlogged = state
				.setValue(MILKLOGGED, false)
				.setValue(BlockStateProperties.WATERLOGGED, true);
		if (!level.setBlock(pos, waterlogged, 11)) {
			return false;
		}

		level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		return true;
	}

	 
	public static BlockState clearMilklogged(BlockState state) {
		return isMilklogged(state) ? state.setValue(MILKLOGGED, false) : state;
	}
}
