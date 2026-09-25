package io.github.stomarver.fundo.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import io.github.stomarver.fundo.hitbox.PotionHitboxes;

final class MilkWaterEffects {

	private MilkWaterEffects() {
	}

	static void applySplash(ServerLevel level, AbstractThrownPotion source, HitResult hitResult,
			PotionHitboxes.Sphere sphere) {
		douseBlocks(level, source, hitResult);
		DamageSource waterImpact = source.damageSources().indirectMagic(source, source.getOwner());
		for (LivingEntity target : PotionHitboxes.collect(level, sphere)) {
			applyToLiving(level, target, waterImpact);
		}
	}

	static void applyLingering(ServerLevel level, AbstractThrownPotion source, HitResult hitResult,
			PotionHitboxes.Cylinder cylinder) {
		douseBlocks(level, source, hitResult);
		DamageSource waterImpact = source.damageSources().indirectMagic(source, source.getOwner());
		for (LivingEntity target : PotionHitboxes.collect(level, cylinder)) {
			applyToLiving(level, target, waterImpact);
		}
	}

	private static void applyToLiving(ServerLevel level, LivingEntity target, DamageSource waterImpact) {
		if (target.isSensitiveToWater()) {
			target.hurtServer(level, waterImpact, 1.0F);
		}
		if (target.isOnFire() && target.isAlive()) {
			target.extinguishFire();
		}
		if (target instanceof Axolotl axolotl) {
			axolotl.rehydrate();
		}
	}

	private static void douseBlocks(ServerLevel level, AbstractThrownPotion source, HitResult hitResult) {
		if (!(hitResult instanceof BlockHitResult blockHit)) {
			return;
		}
		BlockPos exterior = blockHit.getBlockPos().relative(blockHit.getDirection());
		douseFire(level, source, exterior);
		douseFire(level, source, exterior.relative(blockHit.getDirection().getOpposite()));
		for (Direction horizontal : Direction.Plane.HORIZONTAL) {
			douseFire(level, source, exterior.relative(horizontal));
		}
	}

	private static void douseFire(ServerLevel level, AbstractThrownPotion source, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.is(BlockTags.FIRE)) {
			level.destroyBlock(pos, false, source);
		} else if (AbstractCandleBlock.isLit(state)) {
			AbstractCandleBlock.extinguish(null, state, level, pos);
		} else if (CampfireBlock.isLitCampfire(state)) {
			level.levelEvent(null, 1009, pos, 0);
			CampfireBlock.douse(source.getOwner(), level, pos, state);
			level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, false));
		}
	}
}
