package io.github.stomarver.fundo.entity;

import io.github.stomarver.fundo.advancement.FundoAdvancements;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.item.FundoItems;
import io.github.stomarver.fundo.network.PotionHitboxImpactNetwork;
import io.github.stomarver.fundo.network.PotionHitboxImpactPayload;

public class ThrownLingeringMilkBottle extends ThrownLingeringPotion {

	public ThrownLingeringMilkBottle(EntityType<? extends ThrownLingeringMilkBottle> type, Level level) {
		super(type, level);
	}

	public ThrownLingeringMilkBottle(Level level, LivingEntity owner, ItemStack stack) {
		super(level, owner, stack);
	}

	public ThrownLingeringMilkBottle(Level level, double x, double y, double z, ItemStack stack) {
		super(level, x, y, z, stack);
	}

	@Override
	protected Item getDefaultItem() {
		return FundoItems.LINGERING_MILK_BOTTLE;
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

		double cloudX = this.getX();
		double cloudY = this.getY();
		double cloudZ = this.getZ();
		if (hitResult instanceof EntityHitResult entityHit) {
			cloudX = entityHit.getEntity().getX();
			cloudY = entityHit.getEntity().getY();
			cloudZ = entityHit.getEntity().getZ();
		}
		MilkCleansingCloud cloud = new MilkCleansingCloud(level, cloudX, cloudY, cloudZ);
		if (this.getOwner() instanceof LivingEntity living) {
			cloud.setOwner(living);
		}

		PotionHitboxes.Cylinder impact = PotionHitboxes.cloudCylinder(cloud);
		MilkWaterEffects.applyLingering(level, this, hitResult, impact);

		PotionHitboxImpactNetwork.sendToLevelPlayers(level, PotionHitboxImpactPayload.cylinder(impact));
		level.addFreshEntity(cloud);
		if (ActionLogs.potionsEnabled()) {
			ActionLogs.potions(level, "entity -> cloud | " + PotionActionLog.projectile(this) + " | "
					+ PotionActionLog.hit(hitResult) + " shape=cylinder radius=" + impact.radius()
					+ " height=" + impact.height() + " cloud=" + ActionLogs.subject(cloud)
					+ " cloud-pos=" + PotionActionLog.position(cloud.position()) + " | "
					+ PotionActionLog.describe(stack));
		}

		PotionImpactEffects.milkBottle(level, hitResult.getLocation());
		FundoAdvancements.grantSpilledItAgain(this);
	}
}
