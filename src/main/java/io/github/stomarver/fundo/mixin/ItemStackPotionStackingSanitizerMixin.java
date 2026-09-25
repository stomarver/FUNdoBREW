package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.Holder;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import io.github.stomarver.fundo.item.PotionStacking;

@Mixin(ItemStack.class)
public abstract class ItemStackPotionStackingSanitizerMixin {

	@Inject(method = "<init>(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("TAIL"))
	private void fundo$normalizeLoadedPotionStack(Holder<Item> item, int count, PatchedDataComponentMap components,
			CallbackInfo ci) {
		ItemStack self = (ItemStack) (Object) this;
		self.setCount(PotionStacking.limitCount(self, self.getCount()));
	}

	@ModifyVariable(method = "setCount", at = @At("HEAD"), argsOnly = true)
	private int fundo$normalizePotionStackCount(int count) {
		return PotionStacking.limitCount((ItemStack) (Object) this, count);
	}
}
