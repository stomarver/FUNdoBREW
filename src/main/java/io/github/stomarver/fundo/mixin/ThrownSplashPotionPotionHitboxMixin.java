package io.github.stomarver.fundo.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import io.github.stomarver.fundo.hitbox.PotionHitboxes;
import io.github.stomarver.fundo.network.PotionHitboxImpactNetwork;
import io.github.stomarver.fundo.network.PotionHitboxImpactPayload;

@Mixin(ThrownSplashPotion.class)
public abstract class ThrownSplashPotionPotionHitboxMixin {

	@Inject(method = "onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V",
			at = @At("HEAD"))
	private void fundo$broadcastExactSplashImpact(ServerLevel level, ItemStack stack, HitResult hitResult, CallbackInfo ci) {
		ThrownSplashPotion self = (ThrownSplashPotion) (Object) this;

		AABB impactBox = self.getBoundingBox().move(hitResult.getLocation().subtract(self.position()));
		PotionHitboxImpactNetwork.sendToLevelPlayers(level,
				PotionHitboxImpactPayload.sphere(PotionHitboxes.Sphere.ofCenter(impactBox.getCenter(),
						PotionHitboxes.SPLASH_WIDTH)));
	}

	@Redirect(method = "onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V",
			require = 1, at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	private List<LivingEntity> fundo$sphericalSplashSelection(Level level, Class<LivingEntity> entityClass, AABB effectBox) {
		return PotionHitboxes.collect(level,
				PotionHitboxes.Sphere.ofCenter(effectBox.getCenter(), PotionHitboxes.SPLASH_WIDTH));
	}
}
