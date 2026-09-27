package io.github.stomarver.fundo.item;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.config.FundoFeaturePolicy;
import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.milklogged.MilkloggedState;
import io.github.stomarver.fundo.sound.FundoSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket.RandomizationType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

 




public final class MilkBucketDispenseBehavior extends DefaultDispenseItemBehavior {
	private final DefaultDispenseItemBehavior fallback = new DefaultDispenseItemBehavior();

	@Override
	protected ItemStack execute(BlockSource source, ItemStack stack) {
		if (!FundoFeaturePolicy.milkBucketPouring() || FundoFluids.MILK == null) {
			return fallback.dispense(source, stack);
		}

		Direction facing = source.state().getValue(DispenserBlock.FACING);
		BlockPos target = source.pos().relative(facing);
		if (!fundo$emptyMilk(source.level(), target)) {
			return fallback.dispense(source, stack);
		}

		return consumeWithRemainder(source, stack, new ItemStack(Items.BUCKET));
	}

	private static boolean fundo$emptyMilk(ServerLevel level, BlockPos target) {
		BlockState replaced = level.getBlockState(target);
		if (MilkloggedState.tryMilklog(level, target, replaced)) {
			level.playSound(null, target, FundoSounds.MILK_BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(null, GameEvent.FLUID_PLACE, target);
			return true;
		}
		 
		 
		 
		if (!replaced.getFluidState().isEmpty() || !replaced.canBeReplaced(FundoFluids.MILK)) {
			return false;
		}

		if (level.environmentAttributes().getValue(
				net.minecraft.world.attribute.EnvironmentAttributes.WATER_EVAPORATES, target)
				&& FundoFluids.MILK.is(FluidTags.WATER)) {
			RandomSource random = level.getRandom();
			level.playSound(null, target, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
					2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
			level.sendParticles(ParticleTypes.LARGE_SMOKE, target.getX(), target.getY(), target.getZ(),
					8, 1.0D, 1.0D, 1.0D, 0.0D, RandomizationType.ALTERNATIVE);
			return true;
		}

		if (!replaced.isAir() && !replaced.liquid()) {
			level.destroyBlock(target, true);
		}
		if (!level.setBlock(target, FundoFluids.MILK.defaultFluidState().createLegacyBlock(), 11)) {
			return false;
		}
		level.playSound(null, target, FundoSounds.MILK_BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.gameEvent(null, GameEvent.FLUID_PLACE, target);
		return true;
	}

}
