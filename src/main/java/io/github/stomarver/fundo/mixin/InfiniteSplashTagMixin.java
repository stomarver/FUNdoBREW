package io.github.stomarver.fundo.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.effect.InfiniteEffects;
import io.github.stomarver.fundo.effect.InfinitePotionMark;
import io.github.stomarver.fundo.hitbox.PotionHitboxes;

@Mixin(ThrownSplashPotion.class)
public abstract class InfiniteSplashTagMixin {

	@Inject(method = "onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V",
			at = @At("TAIL"))
	private void fundo$tagInfiniteSplashVictims(ServerLevel level, ItemStack potionItem, HitResult hitResult, CallbackInfo ci) {
		if (!FundoConfig.infinite_potions) {
			return;
		}
		PotionContents contents = potionItem.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return;
		}
		List<MobEffectInstance> endless = InfiniteEffects.storedEndless(contents);
		ThrownSplashPotion self = (ThrownSplashPotion) (Object) this;
		AABB potionAabb = self.getBoundingBox().move(hitResult.getLocation().subtract(self.position()));
		float margin = ProjectileUtil.computeMargin(self);
		boolean marked = InfinitePotionMark.isMarked(potionItem);
		int victims = 0;
		StringBuilder victimLog = new StringBuilder();

		for (LivingEntity entity : PotionHitboxes.collect(level, PotionHitboxes.Sphere.ofCenter(
				potionAabb.getCenter(), PotionHitboxes.SPLASH_WIDTH + 2.0D * margin))) {
			if (!entity.isAffectedByPotions()
					|| potionAabb.distanceToSqr(entity.getBoundingBox().inflate(margin)) >= 16.0) {
				continue;
			}
			victims++;
			if (marked) {
				for (MobEffectInstance stored : endless) {
					boolean hadRow = InfiniteEffects.hasAnyRow(entity, stored.getEffect());
					boolean tagged = InfiniteEffects.mirrorArrival(entity, stored);
					if (tagged && !hadRow) {
						victimLog.append(" [tag ").append(InfiniteEffects.describe(stored))
								.append(" -> ").append(ActionLogs.subject(entity)).append(']');
					}
				}
			}
		}
		ActionLogs.infinite(level, "splash | " + BuiltInRegistries.ITEM.getKey(potionItem.getItem())
				+ " hit | victims=" + victims
				+ " | endless=" + endless.size()
				+ " | mark=" + (marked ? "fundo:infinite" : "none")
				+ (marked && endless.isEmpty() ? " (marked, but no endless payload)" : "")
				+ victimLog);
	}
}
