package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.milklogged.MilkloggedState;
import io.github.stomarver.fundo.milklogged.MilkloggedWaterReaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 







@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateMilkloggedFluidMixin {
	@Shadow
	protected abstract BlockState asState();

	@Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
	private void fundo$exposeMilkSourceFromMilkloggedState(CallbackInfoReturnable<FluidState> cir) {
		if (FundoFluids.MILK != null && MilkloggedState.isMilklogged(asState())) {
			cir.setReturnValue(FundoFluids.MILK.defaultFluidState());
		}
	}

	 




	@Inject(method = "onPlace", at = @At("TAIL"))
	private void fundo$freezeWaterWhenHostBecomesMilklogged(Level level, BlockPos pos,
			BlockState oldState, boolean movedByPiston, CallbackInfo ci) {
		if (!level.isClientSide()
				&& level instanceof ServerLevel server
				&& FundoFluids.MILK != null
				&& MilkloggedState.isMilklogged(asState())
				&& !MilkloggedState.isMilklogged(oldState)) {
			MilkloggedWaterReaction.freezeAdjacentFreeWater(server, pos);
		}
	}

	@Inject(method = "updateShape", at = @At("HEAD"))
	private void fundo$reactAndScheduleMilkAfterNeighbourUpdates(LevelReader level, ScheduledTickAccess tickAccess,
			BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState,
			RandomSource random, CallbackInfoReturnable<BlockState> cir) {
		if (FundoFluids.MILK != null && MilkloggedState.isMilklogged(asState())) {
			 
			 
			 
			if (level instanceof ServerLevel server) {
				MilkloggedWaterReaction.freezeFreeWater(server, neighbourPos, neighbourState);
			}
			tickAccess.scheduleTick(pos, FundoFluids.MILK, FundoFluids.MILK.getTickDelay(level));
		}
	}
}
