package io.github.stomarver.fundo.client.mixin;

import mezz.jei.api.ingredients.subtypes.UidContext;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.stomarver.fundo.effect.InfinitePotionMark;

/**
 * JEI identifies ordinary potion stacks by PotionContents#potion. Echoed
 * potions deliberately have no finite potion holder, so JEI would otherwise
 * collapse every infinite potion into its uncraftable-potion subtype.
 */
@Pseudo
@Mixin(targets = "mezz.jei.library.plugins.vanilla.ingredients.subtypes.PotionSubtypeInterpreter", remap = false)
public abstract class JeiPotionSubtypeMixin {

	@Inject(method = "getSubtypeData", at = @At("HEAD"), cancellable = true, remap = false)
	private void fundo$separateMarkedInfinitePotions(ItemStack stack, UidContext context,
			CallbackInfoReturnable<Object> cir) {
		Object subtype = InfinitePotionMark.jeiSubtype(stack);
		if (subtype != null) {
			cir.setReturnValue(subtype);
		}
	}
}
