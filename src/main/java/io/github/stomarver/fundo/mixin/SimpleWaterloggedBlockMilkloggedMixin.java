package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.milklogged.MilkloggedState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 




@Mixin(SimpleWaterloggedBlock.class)
public interface SimpleWaterloggedBlockMilkloggedMixin {
	@Inject(method = "canPlaceLiquid", at = @At("HEAD"), cancellable = true)
	private void fundo$doNotMixWaterOrFlowingMilk(LivingEntity entity, BlockGetter level, BlockPos pos,
			BlockState state, Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
		if (MilkloggedState.isMilklogged(state)
				|| (FundoFluids.MILK != null && fluid.isSame(FundoFluids.MILK))) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "placeLiquid", at = @At("HEAD"), cancellable = true)
	private void fundo$rejectFlowButLetSourceWaterReplaceMilk(LevelAccessor level, BlockPos pos,
			BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
		if (FundoFluids.MILK != null && fluidState.getType().isSame(FundoFluids.MILK)) {
			 
			 
			cir.setReturnValue(false);
			return;
		}

		if (!MilkloggedState.isMilklogged(state)) {
			return;
		}

		 
		 
		if (fluidState.is(Fluids.WATER) && fluidState.isSource()) {
			cir.setReturnValue(MilkloggedState.tryWaterlog(level, pos, state));
		} else {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "pickupBlock", at = @At("HEAD"), cancellable = true)
	private void fundo$pickUpMilkloggedSource(LivingEntity entity, LevelAccessor level, BlockPos pos,
			BlockState state, CallbackInfoReturnable<ItemStack> cir) {
		if (FundoFluids.MILK == null || !MilkloggedState.isMilklogged(state)) {
			return;
		}

		BlockState emptied = MilkloggedState.clearMilklogged(state);
		level.setBlockAndUpdate(pos, emptied);
		if (!emptied.canSurvive(level, pos)) {
			level.destroyBlock(pos, true);
		}
		cir.setReturnValue(new ItemStack(Items.MILK_BUCKET));
	}
}
