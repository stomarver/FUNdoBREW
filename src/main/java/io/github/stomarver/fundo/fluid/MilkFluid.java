package io.github.stomarver.fundo.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public abstract class MilkFluid extends FlowingFluid {
	@Override
	public Fluid getFlowing() {
		return FundoFluids.FLOWING_MILK;
	}

	@Override
	public Fluid getSource() {
		return FundoFluids.MILK;
	}

	@Override
	public Item getBucket() {

		return Items.MILK_BUCKET;
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return Optional.of(SoundEvents.BUCKET_FILL);
	}

	@Override
	protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
		return false;
	}

	@Override
	protected boolean canConvertToSource(ServerLevel level) {
		return false;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {

	}

	@Override
	protected int getSlopeFindDistance(LevelReader level) {
		return 4;
	}

	@Override
	protected int getDropOff(LevelReader level) {
		return 1;
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return 5;
	}

	@Override
	public float getExplosionResistance() {
		return 100.0F;
	}

	@Override
	public float getOwnHeight(FluidState state) {
		return state.getAmount() / 9.0F;
	}

	@Override
	protected BlockState createLegacyBlock(FluidState state) {

		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public VoxelShape getShape(FluidState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	public static class Still extends MilkFluid {
		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}
	}

	public static class Flowing extends MilkFluid {
		public Flowing() {
			registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL) > 0 ? 0 : 8;
		}
	}
}
