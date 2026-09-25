package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AreaEffectCloud;

import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;
import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudPotionActionLogMixin {

	@Unique private boolean fundo$potionLogSeen;
	@Unique private boolean fundo$potionLogTick;
	@Unique private float fundo$potionLogRadius;
	@Unique private int fundo$potionLogDuration;
	@Unique private boolean fundo$potionLogWaiting;

	@Inject(method = "tick", at = @At("HEAD"))
	private void fundo$observeCloudBeforeTick(CallbackInfo ci) {
		AreaEffectCloud self = (AreaEffectCloud) (Object) this;
		if (!(self.level() instanceof ServerLevel level) || !ActionLogs.potionsEnabled()) {
			fundo$potionLogTick = false;
			return;
		}
		if (!fundo$potionLogSeen) {
			fundo$potionLogSeen = true;
			String event = "cloud spawned/observed | cloud=" + ActionLogs.subject(self)
					+ " shape=cylinder radius=" + self.getRadius() + " height=" + PotionHitboxes.CLOUD_HEIGHT
					+ " wait=" + self.getWaitTime() + " duration=" + self.getDuration()
					+ " radius-per-tick=" + self.getRadiusPerTick()
					+ " radius-on-use=" + self.getRadiusOnUse()
					+ " duration-on-use=" + self.getDurationOnUse()
					+ " pos=" + PotionActionLog.position(self.position());
			ActionLogs.potions(level, event);
			fundo$mirrorInfiniteLifecycle(level, self, event);
		}
		fundo$potionLogTick = true;
		fundo$potionLogRadius = self.getRadius();
		fundo$potionLogDuration = self.getDuration();
		fundo$potionLogWaiting = self.isWaiting();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void fundo$observeCloudAfterTick(CallbackInfo ci) {
		if (!fundo$potionLogTick) {
			return;
		}
		AreaEffectCloud self = (AreaEffectCloud) (Object) this;
		if (!(self.level() instanceof ServerLevel level)) {
			return;
		}
		if (fundo$potionLogWaiting != self.isWaiting()) {
			String event = "cloud state | cloud=" + ActionLogs.subject(self)
					+ " waiting=" + fundo$potionLogWaiting + " -> " + self.isWaiting()
					+ " shape=cylinder radius=" + self.getRadius() + " height=" + PotionHitboxes.CLOUD_HEIGHT;
			ActionLogs.potions(level, event);
			fundo$mirrorInfiniteLifecycle(level, self, event);
		}
		if (Float.compare(fundo$potionLogRadius, self.getRadius()) != 0) {
			String event = "cloud radius | cloud=" + ActionLogs.subject(self)
					+ " shape=cylinder radius=" + fundo$potionLogRadius + " -> " + self.getRadius()
					+ " height=" + PotionHitboxes.CLOUD_HEIGHT + " duration="
					+ fundo$potionLogDuration + " -> " + self.getDuration();
			ActionLogs.potions(level, event);
			fundo$mirrorInfiniteLifecycle(level, self, event);
		}
		if (self.isRemoved()) {
			String event = "cloud removed | cloud=" + ActionLogs.subject(self)
					+ " last-radius=" + self.getRadius() + " duration=" + self.getDuration();
			ActionLogs.potions(level, event);
			fundo$mirrorInfiniteLifecycle(level, self, event);
		}
	}

	@Unique
	private static void fundo$mirrorInfiniteLifecycle(ServerLevel level, AreaEffectCloud cloud, String event) {
		if (InfinitePotionMark.isMarked(cloud)) {
			ActionLogs.infinite(level, "lifecycle | " + event);
		}
	}
}
