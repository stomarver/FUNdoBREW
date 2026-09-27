package io.github.stomarver.fundo.block;

import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.milklogged.MilkWaterReaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.redstone.Orientation;

 





public final class MilkLiquidBlock extends LiquidBlock {
	public MilkLiquidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
		super(fluid, properties);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
			InsideBlockEffectApplier applier, boolean flag) {
		super.entityInside(state, level, pos, entity, applier, flag);
		if (level.isClientSide() || !(level instanceof ServerLevel serverLevel) || !entity.mayInteract(serverLevel, pos)) {
			return;
		}

		boolean changed = false;
		if (entity.isOnFire()) {
			entity.extinguishFire();
			changed = true;
		}
		if (entity instanceof LivingEntity living && !living.getActiveEffects().isEmpty()) {
			living.removeAllEffects();
			changed = true;
		}
		 
		 
		if (changed) {
			level.gameEvent(entity, GameEvent.FLUID_PICKUP, pos);
		}
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState,
			boolean movedByPiston) {
		if (!fundo$freezeForNormalWaterContact(level, pos)) {
			super.onPlace(state, level, pos, oldState, movedByPiston);
		}
	}

	@Override
	protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock,
			Orientation orientation, boolean movedByPiston) {
		if (!fundo$freezeForNormalWaterContact(level, pos)) {
			super.neighborChanged(state, level, pos, neighbourBlock, orientation, movedByPiston);
		}
	}

	private static boolean fundo$freezeForNormalWaterContact(Level level, BlockPos pos) {
		if (level.isClientSide() || !(level instanceof ServerLevel server)) {
			return false;
		}

		for (Direction direction : Direction.values()) {
			BlockPos waterPos = pos.relative(direction);
			Fluid fluid = level.getFluidState(waterPos).getType();
			if (!MilkWaterReaction.isOrdinaryWater(fluid)) {
				continue;
			}

			 
			 
			 
			 
			 
			 
			 
			if (direction == Direction.DOWN) {
				return MilkWaterReaction.freeze(server, waterPos);
			}
			return MilkWaterReaction.freeze(server, pos);
		}
		return false;
	}
}
