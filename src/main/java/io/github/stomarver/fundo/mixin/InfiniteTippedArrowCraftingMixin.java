package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ImbueRecipe;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

/**
 * Carries the Fundo mark through Minecraft's vanilla lingering-potion-to-eight-
 * tipped-arrows craft. PotionContents are already copied by ImbueRecipe; the
 * mark is the additional provenance needed when an arrow later hits a target.
 */
@Mixin(ImbueRecipe.class)
public abstract class InfiniteTippedArrowCraftingMixin {

	@Inject(method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;",
			at = @At("RETURN"))
	private void fundo$markInfiniteTippedArrows(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
		if (!FundoConfig.infinite_potions) {
			return;
		}
		ItemStack lingering = input.getItem(1, 1);
		ItemStack result = cir.getReturnValue();
		if (lingering.is(Items.LINGERING_POTION) && result.is(Items.TIPPED_ARROW)
				&& InfinitePotionMark.isMarked(lingering)) {
			InfinitePotionMark.mark(result);
		}
	}
}
