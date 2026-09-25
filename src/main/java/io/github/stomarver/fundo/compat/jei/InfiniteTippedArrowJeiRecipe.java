package io.github.stomarver.fundo.compat.jei;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.ImbueRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * JEI-only presentation companion for Minecraft's crafting_imbue tipped-arrow
 * recipe. It retains the native 3x3 ImbueRecipe shape, but substitutes bounded
 * lists of Fundo's marked lingering inputs and marked arrow outputs for
 * vanilla's unbounded WithAnyPotion display.
 *
 * <p>This object is registered only with JEI, never with Minecraft's recipe
 * manager. Gameplay crafting remains the vanilla ImbueRecipe plus Fundo's
 * marker-preservation mixin.</p>
 */
final class InfiniteTippedArrowJeiRecipe extends ImbueRecipe {

	private final List<RecipeDisplay> displays;

	InfiniteTippedArrowJeiRecipe(List<ItemStack> infiniteLingering, List<ItemStack> infiniteArrows) {
		super(
				new Recipe.CommonInfo(false),
				new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, "jei.tipped.arrow"),
				Ingredient.of(Items.LINGERING_POTION),
				Ingredient.of(Items.ARROW),
				ItemStackTemplate.fromNonEmptyStack(new ItemStack(Items.TIPPED_ARROW, 8)));
		if (infiniteLingering.isEmpty() || infiniteArrows.isEmpty()) {
			throw new IllegalArgumentException("Infinite tipped-arrow JEI display requires at least one valid potion path");
		}

		SlotDisplay arrows = new SlotDisplay.ItemSlotDisplay(Items.ARROW);
		SlotDisplay sources = alternatives(infiniteLingering);
		SlotDisplay results = alternatives(infiniteArrows);
		this.displays = List.of(new ShapedCraftingRecipeDisplay(
				3,
				3,
				List.of(arrows, arrows, arrows, arrows, sources, arrows, arrows, arrows, arrows),
				results,
				new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
	}

	@Override
	public List<RecipeDisplay> display() {
		return this.displays;
	}

	private static SlotDisplay alternatives(List<ItemStack> stacks) {
		return new SlotDisplay.Composite(stacks.stream()
				.map(stack -> new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack)))
				.map(SlotDisplay.class::cast)
				.toList());
	}
}
