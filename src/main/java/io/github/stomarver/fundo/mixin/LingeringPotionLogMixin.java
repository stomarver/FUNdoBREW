package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.HitResult;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.effect.InfiniteEffects;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(ThrownLingeringPotion.class)
public abstract class LingeringPotionLogMixin {

	@Inject(method = "onHitAsPotion(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/HitResult;)V",
			at = @At("TAIL"))
	private void fundo$logLingeringHit(ServerLevel level, ItemStack potionItem, HitResult hitResult, CallbackInfo ci) {
		if (!FundoConfig.infinite_potions) {
			return;
		}
		PotionContents contents = potionItem.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return;
		}
		ActionLogs.infinite(level, "cloud | lingering hit | endless="
				+ InfiniteEffects.storedEndless(contents).size()
				+ " | mark=" + (InfinitePotionMark.isMarked(potionItem) ? "fundo:infinite" : "none"));
	}
}
