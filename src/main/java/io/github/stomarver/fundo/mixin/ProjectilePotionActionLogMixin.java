package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.item.ItemStack;

import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(Projectile.class)
public abstract class ProjectilePotionActionLogMixin {

	@Inject(method = "applyOnProjectileSpawned", at = @At("TAIL"))
	private void fundo$logPotionEntitySpawn(ServerLevel level, ItemStack stack, CallbackInfo ci) {
		Projectile self = (Projectile) (Object) this;
		if (!ActionLogs.potionsEnabled() || !(self instanceof AbstractThrownPotion) || !PotionActionLog.tracks(stack)) {
			return;
		}
		Entity owner = self.getOwner();
		String event = (owner == null ? "dispenser" : "hand") + " -> entity | "
				+ PotionActionLog.projectile(self) + " owner="
				+ (owner == null ? "dispenser" : ActionLogs.subject(owner)) + " | " + PotionActionLog.describe(stack);
		ActionLogs.potions(level, event);
		if (InfinitePotionMark.isMarked(stack)) {
			ActionLogs.infinite(level, "lifecycle | " + event);
		}
	}
}
