package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.milklogged.MilkloggedState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 





@Mixin(StateHolder.class)
public abstract class StateHolderMilkloggedExclusionMixin<O, S> {
	@Inject(method = "setValue", at = @At("RETURN"), cancellable = true)
	@SuppressWarnings("unchecked")
	private <T extends Comparable<T>, V extends T> void fundo$keepLoggedFluidsExclusive(
			Property<T> property, V value, CallbackInfoReturnable<S> cir) {
		if (!((Object) cir.getReturnValue() instanceof BlockState result)
				|| !MilkloggedState.isMilklogged(result)
				|| !result.hasProperty(BlockStateProperties.WATERLOGGED)
				|| !result.getValue(BlockStateProperties.WATERLOGGED)) {
			return;
		}

		 
		 
		 
		BlockState normalised = property == BlockStateProperties.WATERLOGGED && Boolean.TRUE.equals(value)
				? result.setValue(MilkloggedState.MILKLOGGED, false)
				: result.setValue(BlockStateProperties.WATERLOGGED, false);
		cir.setReturnValue((S) normalised);
	}
}
