package io.github.stomarver.fundo.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.phys.HitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.stomarver.fundo.entity.PotionImpactEffects;

/** Replaces block-centred potion level events with exact-impact particles and sound. */
@Mixin(AbstractThrownPotion.class)
public abstract class AbstractThrownPotionImpactEffectsMixin {

	@Redirect(
			method = "onHit(Lnet/minecraft/world/phys/HitResult;)V",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;levelEvent(ILnet/minecraft/core/BlockPos;I)V"))
	private void fundo$playPotionImpactAtExactHit(ServerLevel level, int eventId, BlockPos ignoredPosition,
			int data, HitResult hitResult) {
		if (eventId == 2002 || eventId == 2007) {
			AbstractThrownPotion self = (AbstractThrownPotion) (Object) this;
			PotionImpactEffects.vanillaPotion(level, hitResult.getLocation(), data, eventId == 2007, self.isSilent());
			return;
		}
		if (eventId == 1053 || eventId == 1054) {
			return;
		}
		level.levelEvent(eventId, ignoredPosition, data);
	}
}
