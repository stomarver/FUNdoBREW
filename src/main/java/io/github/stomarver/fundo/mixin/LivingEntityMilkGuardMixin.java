package io.github.stomarver.fundo.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.entity.MilkCleansingCloud;
import io.github.stomarver.fundo.hitbox.PotionHitboxes;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMilkGuardMixin {

	@Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
			at = @At("HEAD"), cancellable = true)
	private void fundo$denyEffectsInsideMilkCloud(MobEffectInstance instance, Entity source,
			CallbackInfoReturnable<Boolean> cir) {
		if (!FundoConfig.milk_changes) {
			return;
		}
		LivingEntity self = (LivingEntity) (Object) this;
		Level level = self.level();
		if (level.isClientSide()) {
			return;
		}
		AABB selfBox = self.getBoundingBox();
		for (MilkCleansingCloud cloud : level.getEntitiesOfClass(MilkCleansingCloud.class,
				PotionHitboxes.cloudCandidateBox(selfBox))) {
			if (PotionHitboxes.cloudCylinder(cloud).intersects(selfBox)) {
				cir.setReturnValue(false);
				return;
			}
		}
	}
}
