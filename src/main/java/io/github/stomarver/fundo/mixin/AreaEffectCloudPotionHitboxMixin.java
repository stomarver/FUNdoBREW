package io.github.stomarver.fundo.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import io.github.stomarver.fundo.hitbox.PotionHitboxes;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudPotionHitboxMixin {

	@Unique
	private static final float FUNDO_PHYSICAL_ANCHOR_SIZE = 1.0F / 64.0F;

	@Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
	private void fundo$keepPhysicalAnchorStatic(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		cir.setReturnValue(EntityDimensions.scalable(FUNDO_PHYSICAL_ANCHOR_SIZE, FUNDO_PHYSICAL_ANCHOR_SIZE));
	}

	@Redirect(method = "serverTick(Lnet/minecraft/server/level/ServerLevel;)V", require = 1,
			at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	private List<LivingEntity> fundo$cylindricCloudSelection(Level level, Class<LivingEntity> entityClass, AABB vanillaBox) {
		AreaEffectCloud self = (AreaEffectCloud) (Object) this;
		return PotionHitboxes.collect(level, PotionHitboxes.cloudCylinder(self));
	}

	@Redirect(method = "serverTick(Lnet/minecraft/server/level/ServerLevel;)V", require = 1,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getX()D"))
	private double fundo$cloudNarrowPhaseX(LivingEntity target) {
		AreaEffectCloud self = (AreaEffectCloud) (Object) this;
		AABB box = target.getBoundingBox();
		return PotionHitboxes.nearestCoordinate(self.getX(), box.minX, box.maxX);
	}

	@Redirect(method = "serverTick(Lnet/minecraft/server/level/ServerLevel;)V", require = 1,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getZ()D"))
	private double fundo$cloudNarrowPhaseZ(LivingEntity target) {
		AreaEffectCloud self = (AreaEffectCloud) (Object) this;
		AABB box = target.getBoundingBox();
		return PotionHitboxes.nearestCoordinate(self.getZ(), box.minZ, box.maxZ);
	}
}
