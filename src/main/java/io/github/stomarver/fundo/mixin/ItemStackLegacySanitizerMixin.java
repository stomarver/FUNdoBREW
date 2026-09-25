package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.effect.InfinitePotionMark;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackLegacySanitizerMixin {

	@Inject(
			method = "<init>(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/DataComponentPatch;)V",
			at = @At("TAIL"))
	private void fundo$sanitizeRetiredInfinitePotionData(Holder<Item> item, int count,
			DataComponentPatch patch, CallbackInfo ci) {
		InfinitePotionMark.sanitizeIfDisabled((ItemStack) (Object) this);
	}
}
