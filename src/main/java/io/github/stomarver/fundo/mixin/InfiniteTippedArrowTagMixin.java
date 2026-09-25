package io.github.stomarver.fundo.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.effect.InfiniteEffects;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

/** Applies Fundo infinite provenance after vanilla has applied a tipped arrow's effects. */
@Mixin(Arrow.class)
public abstract class InfiniteTippedArrowTagMixin {

	/**
	 * A legacy marked arrow can still be in flight when Infinite Potions is
	 * disabled. Remove its endless payload before Arrow's vanilla effect loop.
	 */
	@Inject(method = "doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"))
	private void fundo$retireDisabledInfiniteArrow(LivingEntity victim, CallbackInfo ci) {
		if (!FundoConfig.infinite_potions) {
			InfinitePotionMark.sanitizeIfDisabled(((Arrow) (Object) this).getPickupItemStackOrigin());
		}
	}

	@Inject(method = "doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("TAIL"))
	private void fundo$tagInfiniteArrowVictim(LivingEntity victim, CallbackInfo ci) {
		Arrow self = (Arrow) (Object) this;
		if (!FundoConfig.infinite_potions || self.level().isClientSide()) {
			return;
		}

		ItemStack arrowStack = self.getPickupItemStackOrigin();
		if (!InfinitePotionMark.isMarked(arrowStack)) {
			return;
		}
		PotionContents contents = arrowStack.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return;
		}
		List<MobEffectInstance> endless = InfiniteEffects.storedEndless(contents);
		for (MobEffectInstance stored : endless) {
			boolean hadRow = InfiniteEffects.hasAnyRow(victim, stored.getEffect());
			boolean tagged = InfiniteEffects.mirrorArrival(victim, stored);
			if (tagged && !hadRow) {
				ActionLogs.infinite(self.level(), "arrow | " + ActionLogs.subject(victim)
						+ " fundo:infinite landed: " + InfiniteEffects.describe(stored) + " -> tag");
			} else if (!tagged) {
				ActionLogs.infinite(self.level(), "arrow | " + ActionLogs.subject(victim)
						+ " fundo:infinite carried " + InfiniteEffects.describe(stored)
						+ " but vanilla merged it away (stronger active infinite) — no tag");
			}
		}
	}
}
