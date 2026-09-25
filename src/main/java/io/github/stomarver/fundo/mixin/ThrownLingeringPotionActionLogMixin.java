package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;
import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(ThrownLingeringPotion.class)
public abstract class ThrownLingeringPotionActionLogMixin {

	@Inject(method = "onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V",
			at = @At("TAIL"))
	private void fundo$logLingeringCloudCreate(ServerLevel level, ItemStack stack, HitResult hit, CallbackInfo ci) {
		if (!ActionLogs.potionsEnabled() || !PotionActionLog.tracks(stack)) {
			return;
		}
		ThrownLingeringPotion self = (ThrownLingeringPotion) (Object) this;
		Vec3 position = self.position();
		if (hit instanceof EntityHitResult entityHit) {
			Entity target = entityHit.getEntity();
			position = target.position();
		}
		String event = "entity -> cloud | " + PotionActionLog.projectile(self) + " | "
				+ PotionActionLog.hit(hit) + " shape=cylinder radius=3.0 height=" + PotionHitboxes.CLOUD_HEIGHT
				+ " cloud-pos=" + PotionActionLog.position(position) + " | " + PotionActionLog.describe(stack);
		ActionLogs.potions(level, event);
		if (InfinitePotionMark.isMarked(stack)) {
			ActionLogs.infinite(level, "lifecycle | " + event);
		}
	}
}
