package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;

import io.github.stomarver.fundo.item.PotionStacking;

@Mixin(ItemInstance.class)
public interface ItemInstancePotionStackingMixin {

	@Inject(method = "getMaxStackSize", at = @At("RETURN"), cancellable = true)
	private void fundo$applyPotionStackingPolicy(CallbackInfoReturnable<Integer> cir) {
		if (!((Object) this instanceof ItemStack stack) || !PotionStacking.isUnmodifiedVanillaPotion(stack)) {
			return;
		}
		if (!PotionStacking.enabled()) {
			stack.setCount(PotionStacking.limitCount(stack, stack.getCount()));
			return;
		}
		if (cir.getReturnValue() == 1) {
			cir.setReturnValue(16);
		}
	}
}
