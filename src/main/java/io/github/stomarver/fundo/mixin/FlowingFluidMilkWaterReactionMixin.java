package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.milklogged.MilkloggedState;
import io.github.stomarver.fundo.milklogged.MilkWaterReaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 



@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMilkWaterReactionMixin {
	@Inject(method = "spread", at = @At("HEAD"), cancellable = true)
	private void fundo$reactMilkAndWater(ServerLevel level, BlockPos pos, BlockState state,
			FluidState fluidState, CallbackInfo ci) {
		if (FundoFluids.MILK == null) {
			return;
		}

		Fluid activeFluid = fluidState.getType();
		if (activeFluid.isSame(FundoFluids.MILK)) {
			 
			 
			 
			 
			if (MilkloggedState.isMilklogged(state)) {
				for (Direction direction : Direction.values()) {
					BlockPos waterPos = pos.relative(direction);
					BlockState waterState = level.getBlockState(waterPos);
					if (isOrdinaryWater(waterState.getFluidState().getType()) && !isWaterloggedHost(waterState)) {
						createMilkIce(level, waterPos);
						ci.cancel();
						return;
					}
				}
				return;
			}

			 
			 
			 
			 
			for (Direction direction : Direction.values()) {
				if (direction == Direction.DOWN) {
					continue;
				}
				if (isOrdinaryWater(level.getFluidState(pos.relative(direction)).getType())) {
					createMilkIce(level, pos);
					ci.cancel();
					return;
				}
			}

			 
			 
			 
			BlockPos below = pos.below();
			if (isOrdinaryWater(level.getFluidState(below).getType())) {
				createMilkIce(level, below);
			}
			return;
		}

		if (!isOrdinaryWater(activeFluid)) {
			return;
		}

		for (Direction direction : Direction.values()) {
			BlockPos milkPos = pos.relative(direction);
			BlockState milkState = level.getBlockState(milkPos);
			if (!milkState.getFluidState().getType().isSame(FundoFluids.MILK)) {
				continue;
			}

			if (MilkloggedState.isMilklogged(milkState)) {
				 
				 
				 
				if (!isWaterloggedHost(state)) {
					createMilkIce(level, pos);
					ci.cancel();
					return;
				}
				continue;
			}

			 
			 
			 
			if (direction == Direction.UP && !hasSideOrUpperWater(level, milkPos)) {
				createMilkIce(level, pos);
			} else {
				createMilkIce(level, milkPos);
			}
			ci.cancel();
			return;
		}
	}

	private static boolean hasSideOrUpperWater(ServerLevel level, BlockPos milkPos) {
		return MilkWaterReaction.hasSideOrUpperWater(level, milkPos);
	}

	private static boolean isOrdinaryWater(Fluid fluid) {
		return MilkWaterReaction.isOrdinaryWater(fluid);
	}

	private static boolean isWaterloggedHost(BlockState state) {
		return state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED)
				&& state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED);
	}

	private static void createMilkIce(ServerLevel level, BlockPos pos) {
		MilkWaterReaction.freeze(level, pos);
	}
}
