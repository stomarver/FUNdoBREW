package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.milklogged.MilkloggedState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

 





@Mixin(LevelChunk.class)
public abstract class LevelChunkMilkloggedExclusionMixin {
	@ModifyArg(
			method = "setBlockState",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"),
			index = 3
	)
	private BlockState fundo$normaliseLoggedFluidStateBeforeChunkWrite(BlockState state) {
		if (MilkloggedState.isMilklogged(state)
				&& state.hasProperty(BlockStateProperties.WATERLOGGED)
				&& state.getValue(BlockStateProperties.WATERLOGGED)) {
			 
			 
			 
			return state.setValue(BlockStateProperties.WATERLOGGED, false);
		}
		return state;
	}
}
