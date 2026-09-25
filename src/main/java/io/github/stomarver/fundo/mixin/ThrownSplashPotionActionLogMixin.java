package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;
import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(ThrownSplashPotion.class)
public abstract class ThrownSplashPotionActionLogMixin {

	@Inject(method = "onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V",
			at = @At("TAIL"))
	private void fundo$logSplashHit(ServerLevel level, ItemStack stack, HitResult hit, CallbackInfo ci) {
		if (!ActionLogs.potionsEnabled() || !PotionActionLog.tracks(stack)) {
			return;
		}
		ThrownSplashPotion self = (ThrownSplashPotion) (Object) this;
		AABB impactBox = self.getBoundingBox().move(hit.getLocation().subtract(self.position()));
		PotionHitboxes.Sphere sphere = PotionHitboxes.Sphere.ofCenter(impactBox.getCenter(), PotionHitboxes.SPLASH_WIDTH);
		String event = "entity -> hitbox | " + PotionActionLog.projectile(self) + " | "
				+ PotionActionLog.hit(hit) + " shape=sphere radius=" + sphere.radius()
				+ " victims=" + PotionHitboxes.collect(level, sphere).size() + " | " + PotionActionLog.describe(stack);
		ActionLogs.potions(level, event);
		if (InfinitePotionMark.isMarked(stack)) {
			ActionLogs.infinite(level, "lifecycle | " + event);
		}
	}
}
