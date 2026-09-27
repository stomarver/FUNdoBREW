package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.milklogged.MilkloggedState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 




@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMilkLavaReactionMixin {
	@Inject(method = "spread", at = @At("HEAD"))
	private void fundo$lavaAboveMilkTurnsTheLowerCellToStone(ServerLevel level, BlockPos lavaPos,
			BlockState lavaBlockState, FluidState lavaState, CallbackInfo ci) {
		if (FundoFluids.MILK == null
				|| !((FlowingFluid) (Object) this).is(FluidTags.LAVA)
				|| !level.getFluidState(lavaPos.below()).getType().isSame(FundoFluids.MILK)) {
			return;
		}

		BlockPos milkPos = lavaPos.below();
		 
		 
		if (MilkloggedState.isMilklogged(level.getBlockState(milkPos))) {
			return;
		}
		level.setBlock(milkPos, Blocks.STONE.defaultBlockState(), 3);
		level.levelEvent(1501, milkPos, 0);
	}
}
