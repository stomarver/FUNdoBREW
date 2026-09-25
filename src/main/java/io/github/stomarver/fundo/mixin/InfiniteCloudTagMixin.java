package io.github.stomarver.fundo.mixin;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.effect.InfiniteEffects;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(AreaEffectCloud.class)
public abstract class InfiniteCloudTagMixin {

	@Inject(method = "tick", at = @At("HEAD"))
	private void fundo$retireLegacyCloudPayloadBeforeVanilla(CallbackInfo ci) {
		if (!FundoConfig.infinite_potions) {
			InfinitePotionMark.sanitizeIfDisabled((AreaEffectCloud) (Object) this);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void fundo$tagInfiniteCloudVictims(CallbackInfo ci) {
		AreaEffectCloud self = (AreaEffectCloud) (Object) this;
		if (!FundoConfig.infinite_potions) {
			InfinitePotionMark.sanitizeIfDisabled(self);
			return;
		}
		if (self.level().isClientSide() || !InfinitePotionMark.isMarked(self)) {
			return;
		}
		PotionContents contents = self.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return;
		}
		List<MobEffectInstance> endless = InfiniteEffects.storedEndless(contents);
		if (endless.isEmpty()) {
			return;
		}
		Map<Entity, Integer> victims = ((AreaEffectCloudAccessor) self).fundo$getVictims();
		for (Entity victim : victims.keySet()) {
			if (!(victim instanceof LivingEntity living) || !living.isAffectedByPotions()) {
				continue;
			}
			for (MobEffectInstance stored : endless) {
				boolean hadRow = InfiniteEffects.hasAnyRow(living, stored.getEffect());
				boolean tagged = InfiniteEffects.mirrorArrival(living, stored);
				if (tagged && !hadRow) {
					ActionLogs.infinite(self.level(), "cloud | " + ActionLogs.subject(living)
							+ " fundo:infinite cloud landed: " + InfiniteEffects.describe(stored) + " -> tag");
				} else if (!tagged) {
					ActionLogs.infinite(self.level(), "cloud | " + ActionLogs.subject(living)
							+ " fundo:infinite cloud carried " + InfiniteEffects.describe(stored)
							+ " but vanilla merged it away — no tag");
				}
			}
		}
	}
}
