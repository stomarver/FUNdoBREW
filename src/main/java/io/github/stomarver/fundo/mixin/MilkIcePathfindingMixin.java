package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.block.FundoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 




@Mixin(WalkNodeEvaluator.class)
public abstract class MilkIcePathfindingMixin {
	@Inject(method = "getPathTypeFromState", at = @At("HEAD"), cancellable = true)
	private static void fundo$treatMilkIceAsHoneyForMobNavigation(BlockGetter level, BlockPos pos,
			CallbackInfoReturnable<PathType> cir) {
		if (FundoBlocks.MILK_ICE != null && level.getBlockState(pos).is(FundoBlocks.MILK_ICE)) {
			cir.setReturnValue(PathType.STICKY_HONEY);
		}
	}
}
