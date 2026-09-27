package io.github.stomarver.fundo.fluid;

import io.github.stomarver.fundo.block.FundoBlocks;
import io.github.stomarver.fundo.particle.FundoParticles;
import io.github.stomarver.fundo.sound.FundoSounds;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;

 



public abstract class MilkFluid extends WaterFluid {
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
	public boolean isSame(Fluid other) {
		 
		 
		 
		return other instanceof MilkFluid;
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return Optional.of(FundoSounds.MILK_BUCKET_FILL);
	}

	@Override
	public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		 
		 
		if (!state.isSource() && !state.getValue(FALLING)) {
			if (random.nextInt(64) == 0) {
				level.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
						FundoSounds.MILK_AMBIENT, SoundSource.AMBIENT,
						random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
			}
		} else if (random.nextInt(10) == 0) {
			ParticleOptions particle = FundoParticles.UNDERMILK != null
					? FundoParticles.UNDERMILK
					: ParticleTypes.UNDERWATER;
			level.addParticle(particle, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(),
					pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public ParticleOptions getDripParticle() {
		return FundoParticles.MILK_DRIPPING != null
				? FundoParticles.MILK_DRIPPING
				: ParticleTypes.DRIPPING_WATER;
	}

	@Override
	protected boolean canConvertToSource(ServerLevel level) {
		return false;
	}


	@Override
	public BlockState createLegacyBlock(FluidState state) {
		return FundoBlocks.MILK.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
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
			 
			 
			 
			return state.getValue(LEVEL);
		}
	}
}
