package io.github.stomarver.fundo.entity;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.particle.FundoParticles;

public class MilkCleansingCloud extends AreaEffectCloud {

	private float maxRadius = -1.0F;

	public MilkCleansingCloud(EntityType<? extends MilkCleansingCloud> type, Level level) {
		super(type, level);
	}

	public MilkCleansingCloud(Level level, double x, double y, double z) {
		this(FundoEntityTypes.MILK_CLEANSING_CLOUD, level);
		this.setPos(x, y, z);

		this.setRadius(3.0F);
		this.maxRadius = 3.0F;
		this.setDuration(600);

		this.setWaitTime(0);
		this.setRadiusPerTick((1.0F - 3.0F) / 600.0F);

		this.setCustomParticle(FundoParticles.MILK_MIST);
	}

	@Override
	public void tick() {
		super.tick();
		Level level = this.level();
		if (!FundoConfig.milk_changes) {
			this.discard();
			return;
		}
		if (level.isClientSide() || !this.isAlive() || this.isWaiting()) {
			return;
		}
		int effectsRemoved = 0;

		for (LivingEntity target : PotionHitboxes.collect(level, PotionHitboxes.cloudCylinder(this))) {
			int active = target.getActiveEffects().size();
			if (active > 0 && target.removeAllEffects()) {
				effectsRemoved += active;
			}
		}
		if (effectsRemoved > 0) {

			float delta = this.maxRadius > 0.0F
					? this.maxRadius * 0.15F * effectsRemoved
					: 0.25F * effectsRemoved;
			float oldRadius = this.getRadius();
			float newRadius = oldRadius - delta;
			this.setRadius(newRadius);
			if (ActionLogs.potionsEnabled()) {
				ActionLogs.potions(level, "cloud radius | cloud=" + ActionLogs.subject(this)
						+ " shape=cylinder radius=" + oldRadius + " -> " + newRadius
						+ " height=" + PotionHitboxes.CLOUD_HEIGHT + " cause=milk-cleansing"
						+ " effects-removed=" + effectsRemoved + " pos=" + PotionActionLog.position(this.position()));
			}
			if (newRadius < 0.5F) {
				this.discard();
			}
		}
	}
}
