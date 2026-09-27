package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.milklogged.MilkloggedState;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 





@Mixin(StateDefinition.Builder.class)
public abstract class StateDefinitionBuilderMilkloggedMixin<O, S extends StateHolder<O, S>> {
	@Shadow @Final private O owner;
	@Shadow @Final private Map<String, Property<?>> properties;

	@Shadow
	public abstract StateDefinition.Builder<O, S> add(Property<?>... properties);

	@Inject(method = "create", at = @At("HEAD"))
	private void fundo$addMilkloggedToWaterloggedBlockStates(
			java.util.function.Function<O, S> ownerToState,
			StateDefinition.Factory<O, S> factory,
			CallbackInfoReturnable<StateDefinition<O, S>> cir) {
		if (!(owner instanceof Block)
				|| properties.get(BlockStateProperties.WATERLOGGED.getName()) != BlockStateProperties.WATERLOGGED
				|| properties.containsKey(MilkloggedState.MILKLOGGED.getName())) {
			return;
		}

		add(MilkloggedState.MILKLOGGED);
	}
}
