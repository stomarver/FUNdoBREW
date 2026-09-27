package io.github.stomarver.fundo.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import io.github.stomarver.fundo.hitbox.PotionHitboxes;

@Mixin(AreaEffectCloud.class)
public abstract class AreaEffectCloudPotionHitboxMixin {

	@Redirect(method = "serverTick(Lnet/minecraft/server/level/ServerLevel;)V", require = 1,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;getRadius()F"))
	private float fundo$useLiveCloudHitboxRadius(AreaEffectCloud cloud) {
		return (float) PotionHitboxes.cloudCylinder(cloud).radius();
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
