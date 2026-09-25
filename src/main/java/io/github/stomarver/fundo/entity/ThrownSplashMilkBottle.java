package io.github.stomarver.fundo.entity;

import io.github.stomarver.fundo.advancement.FundoAdvancements;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.item.FundoItems;
import io.github.stomarver.fundo.network.PotionHitboxImpactNetwork;
import io.github.stomarver.fundo.network.PotionHitboxImpactPayload;

public class ThrownSplashMilkBottle extends ThrownSplashPotion {

	public ThrownSplashMilkBottle(EntityType<? extends ThrownSplashMilkBottle> type, Level level) {
		super(type, level);
	}

	public ThrownSplashMilkBottle(Level level, LivingEntity owner, ItemStack stack) {
		super(level, owner, stack);
	}

	public ThrownSplashMilkBottle(Level level, double x, double y, double z, ItemStack stack) {
		super(level, x, y, z, stack);
	}

	@Override
	protected Item getDefaultItem() {
		return FundoItems.SPLASH_MILK_BOTTLE;
	}

	@Override
	protected void onHit(HitResult hitResult) {
		if (this.level() instanceof ServerLevel serverLevel) {
			this.onHitAsPotion(serverLevel, this.getItem(), hitResult);
		}
		this.discard();
	}

	@Override
	public void onHitAsPotion(ServerLevel level, ItemStack stack, HitResult hitResult) {
		if (!FundoConfig.milk_changes) {
			return;
		}

		AABB impactBox = this.getBoundingBox().move(hitResult.getLocation().subtract(this.position()));
		PotionHitboxes.Sphere impact = PotionHitboxes.Sphere.ofCenter(
				impactBox.getCenter(), PotionHitboxes.MILK_SPLASH_WIDTH);

		MilkWaterEffects.applySplash(level, this, hitResult, impact);
		PotionHitboxImpactNetwork.sendToLevelPlayers(level, PotionHitboxImpactPayload.sphere(impact));
		var victims = PotionHitboxes.collect(level, impact);
		if (ActionLogs.potionsEnabled()) {
			ActionLogs.potions(level, "entity -> hitbox | " + PotionActionLog.projectile(this) + " | "
					+ PotionActionLog.hit(hitResult) + " shape=sphere radius=" + impact.radius()
					+ " victims=" + victims.size() + " | " + PotionActionLog.describe(stack));
		}
		for (LivingEntity target : victims) {
			target.removeAllEffects();
		}

		PotionImpactEffects.milkBottle(level, hitResult.getLocation());
		FundoAdvancements.grantSpilledItAgain(this);
	}
}
