package io.github.stomarver.fundo.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.effect.InfiniteEffects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityInfiniteEffectsMixin {

	private LivingEntity fundo$self() {
		return (LivingEntity) (Object) this;
	}

	@Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
			at = @At("TAIL"))
	private void fundo$logArrivalAndRazeUntaggedShelter(MobEffectInstance incoming,
			net.minecraft.world.entity.Entity source, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = fundo$self();
		if (self.level().isClientSide()) {
			return;
		}
		if (!FundoConfig.infinite_potions) {
			InfiniteEffects.purgeRetiredRows(self);
			return;
		}
		if (incoming.isInfiniteDuration()) {
			MobEffectInstance active = self.getEffect(incoming.getEffect());
			MobEffectInstance chainInf = InfiniteEffects.firstInfiniteInChain(active);
			ActionLogs.infinite(self.level(), "effect | endless " + InfiniteEffects.describe(incoming)
					+ " -> " + ActionLogs.subject(self)
					+ " | applied=" + cir.getReturnValue()
					+ (chainInf == null ? " | chain: no endless layer"
							: chainInf == active ? " | chain: active" : " | chain: covered"));
		}
		var razed = InfiniteEffects.stripUntaggedInfiniteShelters(self);
		if (!razed.isEmpty()) {
			ActionLogs.infinite(self.level(), "purity | untagged endless shelter razed on "
					+ ActionLogs.subject(self) + ": " + razed
					+ " (no tag — no cover/return; vanilla would have parked ANY endless instance)");
		}
	}

	@Inject(method = "onEffectsRemoved", at = @At("TAIL"))
	private void fundo$stripRowsOfRemoved(Collection<MobEffectInstance> removed, CallbackInfo ci) {
		LivingEntity self = fundo$self();
		if (self.level().isClientSide() || !FundoConfig.infinite_potions) {
			return;
		}
		for (MobEffectInstance instance : removed) {
			if (InfiniteEffects.hasAnyRow(self, instance.getEffect())) {
				InfiniteEffects.stripTagsForEffect(self, instance.getEffect());
				ActionLogs.infinite(self.level(), "clear | rows of " + InfiniteEffects.effectId(instance.getEffect())
						+ " stripped from " + ActionLogs.subject(self) + " (effect removed)");
			}
		}
	}

	@Inject(method = "removeAllEffects", at = @At("RETURN"))
	private void fundo$stripAllRowsOnCleared(CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = fundo$self();
		if (self.level().isClientSide() || !FundoConfig.infinite_potions) {
			return;
		}
		var rows = InfiniteEffects.infiniteTagsOf(self);
		if (!rows.isEmpty()) {
			for (InfiniteEffects.TaggedEffect row : rows) {
				InfiniteEffects.stripTagsForEffect(self, row.holder());
			}
			ActionLogs.infinite(self.level(), "clear | " + rows.size() + " row(s) stripped from "
					+ ActionLogs.subject(self) + " (effects cleared)");
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void fundo$sanitizeLoadedRetiredRows(net.minecraft.world.level.storage.ValueInput input, CallbackInfo ci) {
		LivingEntity self = fundo$self();
		if (!self.level().isClientSide() && !FundoConfig.infinite_potions) {
			InfiniteEffects.purgeRetiredRows(self);
		}
	}

	@Inject(method = "tickEffects", at = @At("TAIL"))
	private void fundo$syncInfiniteEffects(CallbackInfo ci) {
		LivingEntity self = fundo$self();
		if (!self.level().isClientSide()) {
			if (!FundoConfig.infinite_potions) {
				InfiniteEffects.purgeRetiredRows(self);
				return;
			}
			for (InfiniteEffects.TaggedEffect row : InfiniteEffects.infiniteTagsOf(self)) {
				if (InfiniteEffects.firstInfiniteInChain(self.getEffect(row.holder())) == null) {
					ActionLogs.infinite(self.level(), "sync | materializing " + row.shortForm() + " on "
							+ ActionLogs.subject(self));
				}
			}
			InfiniteEffects.sync(self);
		}
	}
}
