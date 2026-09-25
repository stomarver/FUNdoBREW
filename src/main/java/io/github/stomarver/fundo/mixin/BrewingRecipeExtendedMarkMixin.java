package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;

import io.github.stomarver.fundo.brewing.ExtendedPotionMark;
import io.github.stomarver.fundo.config.FundoConfig;

@Mixin(BrewingRecipe.class)
public abstract class BrewingRecipeExtendedMarkMixin {

	@Inject(method = "assemble", at = @At("RETURN"))
	private void fundo$markRedstoneBrewedOutput(BrewingInput input, CallbackInfoReturnable<ItemStack> cir) {
		if (!FundoConfig.infinite_potions) {
			return;
		}
		ItemStack result = cir.getReturnValue();
		if (result == null || result.isEmpty()) {
			return;
		}
		boolean redstoneBrewed = input.reagent().is(Items.REDSTONE);
		boolean carried = ExtendedPotionMark.isMarked(input.input());
		if (!redstoneBrewed && !carried) {
			return;
		}
		boolean wasMarked = ExtendedPotionMark.isMarked(result);
		if (!wasMarked) {
			ExtendedPotionMark.mark(result);
		}
	}
}
