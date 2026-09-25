package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;

@Mixin(ThrownLingeringPotion.class)
public abstract class ThrownLingeringPotionImmediateCloudMixin {

	@ModifyArg(
			method = "onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setWaitTime(I)V"),
			index = 0)
	private int fundo$activateLingeringCloudImmediately(int vanillaWaitTicks) {
		return 0;
	}
}
