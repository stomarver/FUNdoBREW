package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.milklogged.MilkloggedState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 





@Mixin(BucketItem.class)
public abstract class WaterBucketMilkloggedMixin {
	@Shadow @Final
	protected Fluid content;

	@Inject(method = "emptyContents", at = @At("HEAD"), cancellable = true)
	private void fundo$replaceMilkloggedWithWater(LivingEntity entity, Level level, BlockPos pos,
			BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
		if (content != Fluids.WATER) {
			return;
		}

		BlockState state = level.getBlockState(pos);
		if (MilkloggedState.isMilklogged(state)) {
			cir.setReturnValue(MilkloggedState.tryWaterlog(level, pos, state));
		}
	}
}
