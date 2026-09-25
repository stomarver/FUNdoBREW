package io.github.stomarver.fundo.block;

import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class MilkCauldronBlock extends LayeredCauldronBlock {
	public MilkCauldronBlock(Properties properties) {
		super(Biome.Precipitation.NONE, FundoCauldronInteractions.MILK, properties);
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
		return new ItemStack(Blocks.CAULDRON);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean flag) {
		if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!FundoConfig.milk_changes) {

			level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
			return;
		}
		boolean mayInteract = entity.mayInteract(serverLevel, pos);
		boolean onFire = entity.isOnFire() && mayInteract;
		int effects = entity instanceof LivingEntity living && mayInteract
				? living.getActiveEffects().size()
				: 0;
		int charges = effects + (onFire ? 1 : 0);
		if (charges == 0) {
			return;
		}
		int fill = state.getValue(LEVEL);

		int spent = Math.min(fill, charges);
		if (onFire) {
			entity.extinguishFire();
		}
		if (effects > 0) {
			((LivingEntity) entity).removeAllEffects();
		}
		level.setBlock(pos,
				spent == fill
						? Blocks.CAULDRON.defaultBlockState()
						: state.setValue(LEVEL, fill - spent),
				Block.UPDATE_ALL);
		level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
	}
}
