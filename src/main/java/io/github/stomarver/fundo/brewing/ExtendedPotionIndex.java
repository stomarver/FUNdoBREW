package io.github.stomarver.fundo.brewing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.FundoConfig;

public final class ExtendedPotionIndex {

	private ExtendedPotionIndex() {
	}

	public record CreativeEchoSource(Identifier container, Identifier potion) {
	}

	private static volatile List<PotionIngredient> redstoneInputs = List.of();
	private static volatile List<ItemStack> glowstoneOutputs = List.of();
	private static volatile Set<CreativeEchoSource> creativeSources = Set.of();
	private static volatile boolean computed;

	public static boolean isExactRedstoneInput(ItemStack stack) {
		return redstoneInputs.stream().anyMatch(input -> input.test(stack));
	}

	public static boolean isGlowstoneResult(ItemStack stack) {
		return glowstoneOutputs.stream().anyMatch(output -> ItemStack.isSameItemSameComponents(output, stack));
	}

	public static boolean isCreativeSourceAllowed(ItemStack stack) {
		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return false;
		}
		return contents.potion().flatMap(holder -> holder.unwrapKey())
				.map(key -> creativeSources.contains(new CreativeEchoSource(
						BuiltInRegistries.ITEM.getKey(stack.getItem()), key.identifier())))
				.orElse(false);
	}

	public static List<CreativeEchoSource> creativeSources() {
		return creativeSources.stream()
				.sorted(Comparator.comparing(CreativeEchoSource::container)
						.thenComparing(CreativeEchoSource::potion))
				.toList();
	}

	public static void installCreativeSources(List<CreativeEchoSource> sources) {
		creativeSources = Set.copyOf(sources);
	}

	public static void ensureComputed(RecipeAccess recipeAccess) {
		if (!computed && recipeAccess instanceof RecipeManager manager) {
			rebuild(manager);
		}
	}

	public static void refresh(RecipeManager recipes) {
		rebuild(recipes);
	}

	private static void rebuild(RecipeManager recipes) {
		List<PotionIngredient> redstone = new ArrayList<>();
		List<ItemStack> glowstone = new ArrayList<>();
		int redstoneRecipeCount = 0;
		int glowstoneRecipeCount = 0;

		for (RecipeHolder<?> holder : recipes.getRecipes()) {
			if (!(holder.value() instanceof BrewingRecipe brewing)) {
				continue;
			}
			if (brewing.getReagent().test(new ItemStack(Items.REDSTONE))) {
				redstone.add(brewing.getInput());
				redstoneRecipeCount++;
			}
			if (brewing.getReagent().test(new ItemStack(Items.GLOWSTONE_DUST))) {
				ItemStack output = brewing.getOutput().create();
				if (!output.isEmpty()) {
					glowstone.add(output);
				}
				glowstoneRecipeCount++;
			}
		}

		redstoneInputs = List.copyOf(redstone);
		glowstoneOutputs = List.copyOf(glowstone);
		computed = true;
		creativeSources = collectCreativeSources();
		Fundo.LOGGER.info("[fundo] echo recipe contract: {} exact redstone input predicate(s), "
				+ "{} glowstone output exception(s), {} creative source stack(s) from {} / {} brewing recipe(s)",
				redstoneInputs.size(), glowstoneOutputs.size(), creativeSources.size(),
				redstoneRecipeCount, glowstoneRecipeCount);
	}

	private static Set<CreativeEchoSource> collectCreativeSources() {
		if (!FundoConfig.infinite_potions) {
			return Set.of();
		}
		Set<CreativeEchoSource> result = new LinkedHashSet<>();
		Item[] containers = {Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};
		for (Item container : containers) {
			for (var entry : BuiltInRegistries.POTION.entrySet()) {
				ResourceKey<Potion> key = entry.getKey();
				ItemStack candidate = PotionContents.createItemStack(container,
						BuiltInRegistries.POTION.wrapAsHolder(entry.getValue()));
				if (EchoBrewingRecipe.canEchoBrew(candidate)) {
					result.add(new CreativeEchoSource(BuiltInRegistries.ITEM.getKey(container), key.identifier()));
				}
			}
		}
		return Set.copyOf(result);
	}
}
