package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.milklogged.MilkloggedState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

 





@Mixin(Block.class)
public abstract class BlockDefaultMilkloggedStateMixin {
	@ModifyVariable(method = "registerDefaultState", at = @At("HEAD"), argsOnly = true)
	private BlockState fundo$defaultGeneratedMilkloggedStateToEmpty(BlockState state) {
		return state.hasProperty(MilkloggedState.MILKLOGGED)
				? state.setValue(MilkloggedState.MILKLOGGED, false)
				: state;
	}
}
