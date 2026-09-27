package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;

import io.github.stomarver.fundo.item.PotionStacking;

 




@Mixin(value = ItemInstance.class, priority = 500)
public interface ItemInstancePotionStackingMixin {

	@Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
	private void fundo$applyPotionStackingFallback(CallbackInfoReturnable<Integer> cir) {
		if (!PotionStacking.enabled()
				|| !((Object) this instanceof ItemStack stack)
				|| !PotionStacking.isUnmodifiedVanillaPotion(stack)
				|| cir.getReturnValue() != 1) {
			return;
		}
		cir.setReturnValue(16);
	}
}
