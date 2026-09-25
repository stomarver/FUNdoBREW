package io.github.stomarver.fundo.compat.jei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.brewing.EchoBrewingRecipe;
import io.github.stomarver.fundo.brewing.ExtendedPotionIndex;
import io.github.stomarver.fundo.brewing.ExtendedPotionIndex.CreativeEchoSource;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.effect.InfinitePotionMark;
import io.github.stomarver.fundo.item.FundoItems;

/** JEI rows for the server-synchronised set of valid Echo Dust brewing paths. */
public final class FundoJeiPlugin implements IModPlugin {

	private static final Identifier UID = Fundo.id("jei_plugin");
	/**
	 * The only recipe-manager BrewingRecipe templates that Fundo owns. Their
	 * ingredients are intentionally broad because their real validity lives in
	 * matches(...), so JEI's generic importer cannot safely render them.
	 */
	private static final Set<Identifier> OPAQUE_TEMPLATE_UIDS = Set.of(
			Fundo.id("echo_brewing"),
			Fundo.id("infinite_to_splash"),
			Fundo.id("infinite_to_lingering"));
	private static volatile IJeiRuntime runtime;
	private static List<IJeiBrewingRecipe> dynamicRecipes = List.of();
	private static List<RecipeHolder<CraftingRecipe>> dynamicInfiniteArrowRecipes = List.of();
	private static List<CreativeEchoSource> dynamicSources = List.of();
	private static int recipeRevision;

	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public synchronized void registerRecipes(IRecipeRegistration registration) {
		dynamicSources = currentSources();
		recipeRevision = 0;
		IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
		dynamicRecipes = buildDynamicEntries(factory, recipeRevision);
		dynamicInfiniteArrowRecipes = buildInfiniteArrowEntries(recipeRevision);
		registration.addRecipes(RecipeTypes.BREWING, dynamicRecipes);
		registration.addRecipes(RecipeTypes.CRAFTING, dynamicInfiniteArrowRecipes);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime availableRuntime) {
		runtime = availableRuntime;
		hideOpaqueTemplateRows(availableRuntime);
		refreshAfterCreativeContract();
	}

	@Override
	public void onRuntimeUnavailable() {
		runtime = null;
	}

	/**
	 * The server sends the exact list of Echo-eligible potion containers after a
	 * join or data-pack reload. JEI has no runtime removal API, so superseded
	 * rows are hidden and replacement rows receive a new internal recipe id.
	 */
	public static synchronized void refreshAfterCreativeContract() {
		IJeiRuntime current = runtime;
		if (current == null) {
			return;
		}

		// A client recipe reload can reintroduce JEI's raw RecipeManager import.
		// Repeat this idempotent hide before deciding whether dynamic rows changed.
		hideOpaqueTemplateRows(current);
		List<CreativeEchoSource> sources = currentSources();
		if (sources.equals(dynamicSources)) {
			return;
		}

		if (!dynamicRecipes.isEmpty()) {
			current.getRecipeManager().hideRecipes(RecipeTypes.BREWING, dynamicRecipes);
		}
		if (!dynamicInfiniteArrowRecipes.isEmpty()) {
			current.getRecipeManager().hideRecipes(RecipeTypes.CRAFTING, dynamicInfiniteArrowRecipes);
		}
		dynamicSources = sources;
		recipeRevision++;
		IVanillaRecipeFactory factory = current.getJeiHelpers().getVanillaRecipeFactory();
		dynamicRecipes = buildDynamicEntries(factory, recipeRevision);
		dynamicInfiniteArrowRecipes = buildInfiniteArrowEntries(recipeRevision);
		if (!dynamicRecipes.isEmpty()) {
			current.getRecipeManager().addRecipes(RecipeTypes.BREWING, dynamicRecipes);
		}
		if (!dynamicInfiniteArrowRecipes.isEmpty()) {
			current.getRecipeManager().addRecipes(RecipeTypes.CRAFTING, dynamicInfiniteArrowRecipes);
		}
	}

	private static void hideOpaqueTemplateRows(IJeiRuntime current) {
		/*
		 * JEI's hidden-recipe set is identity-based, not equality-based. Looking
		 * up the rows from the live manager is therefore essential: a freshly
		 * constructed recipe with the same UID cannot hide the imported object.
		 * includeHidden also makes this safe after a client recipe reload.
		 */
		List<IJeiBrewingRecipe> importedTemplateRows = current.getRecipeManager()
				.createRecipeLookup(RecipeTypes.BREWING)
				.includeHidden()
				.get()
				.filter(recipe -> OPAQUE_TEMPLATE_UIDS.contains(recipe.getUid()))
				.toList();
		if (!importedTemplateRows.isEmpty()) {
			current.getRecipeManager().hideRecipes(RecipeTypes.BREWING, importedTemplateRows);
		}
	}

	private static List<CreativeEchoSource> currentSources() {
		return ExtendedPotionIndex.creativeSources().stream()
				// Match vanilla's normal -> splash -> lingering brewing progression,
				// rather than lexicographic item-id order (lingering came first).
				.sorted(Comparator.comparingInt((CreativeEchoSource source) -> containerOrder(source.container()))
						.thenComparing(source -> source.container().toString())
						.thenComparing(source -> source.potion().toString()))
				.toList();
	}

	private static int containerOrder(Identifier container) {
		if (container.equals(BuiltInRegistries.ITEM.getKey(Items.POTION))) {
			return 0;
		}
		if (container.equals(BuiltInRegistries.ITEM.getKey(Items.SPLASH_POTION))) {
			return 1;
		}
		if (container.equals(BuiltInRegistries.ITEM.getKey(Items.LINGERING_POTION))) {
			return 2;
		}
		return 3;
	}

	private static List<IJeiBrewingRecipe> buildDynamicEntries(IVanillaRecipeFactory factory, int revision) {
		if (!FundoConfig.infinite_potions || FundoItems.ECHO_DUST == null || dynamicSources.isEmpty()) {
			return List.of();
		}

		List<IJeiBrewingRecipe> entries = new ArrayList<>();
		entries.addAll(buildEchoBrewingEntries(factory, revision));
		entries.addAll(buildConversionEntries(factory, revision));
		return List.copyOf(entries);
	}

	private static List<IJeiBrewingRecipe> buildConversionEntries(IVanillaRecipeFactory factory, int revision) {
		List<IJeiBrewingRecipe> entries = new ArrayList<>();
		Identifier potionItemId = BuiltInRegistries.ITEM.getKey(Items.POTION);

		for (CreativeEchoSource source : dynamicSources) {
			if (!source.container().equals(potionItemId)) {
				continue;
			}
			var potionReference = BuiltInRegistries.POTION.get(source.potion());
			if (potionReference.isEmpty()) {
				continue;
			}

			ItemStack normalPotion = PotionContents.createItemStack(Items.POTION, potionReference.get());
			ItemStack infinitePotion = EchoBrewingRecipe.applyCreativeEchoBrew(normalPotion);
			if (infinitePotion.isEmpty()) {
				continue;
			}
			ItemStack infiniteSplash = infinitePotion.transmuteCopy(Items.SPLASH_POTION, 1);
			ItemStack infiniteLingering = infiniteSplash.transmuteCopy(Items.LINGERING_POTION, 1);
			String suffix = potionPath(source.potion());

			entries.add(factory.createBrewingRecipe(
					List.of(new ItemStack(Items.GUNPOWDER)),
					List.of(infinitePotion),
					infiniteSplash,
					recipeId(revision, "infinite_to_splash/" + suffix)));
			entries.add(factory.createBrewingRecipe(
					List.of(new ItemStack(Items.DRAGON_BREATH)),
					List.of(infiniteSplash),
					infiniteLingering,
					recipeId(revision, "infinite_to_lingering/" + suffix)));
		}
		return List.copyOf(entries);
	}

	/**
	 * Mirrors vanilla's one-card crafting_imbue presentation. Minecraft's
	 * ImbueRecipe uses WithAnyPotion, which derives every registered finite
	 * potion and cannot retain Fundo's custom infinite components. The JEI-only
	 * companion instead exposes one bounded composite of all exact marked
	 * lingering sources and marked tipped-arrow results.
	 */
	private static List<RecipeHolder<CraftingRecipe>> buildInfiniteArrowEntries(int revision) {
		if (!FundoConfig.infinite_potions || dynamicSources.isEmpty()) {
			return List.of();
		}

		List<ItemStack> infiniteLingering = new ArrayList<>();
		List<ItemStack> infiniteArrows = new ArrayList<>();
		Identifier potionItemId = BuiltInRegistries.ITEM.getKey(Items.POTION);
		for (CreativeEchoSource source : dynamicSources) {
			if (!source.container().equals(potionItemId)) {
				continue;
			}
			var potionReference = BuiltInRegistries.POTION.get(source.potion());
			if (potionReference.isEmpty()) {
				continue;
			}

			ItemStack normalPotion = PotionContents.createItemStack(Items.POTION, potionReference.get());
			ItemStack infinitePotion = EchoBrewingRecipe.applyCreativeEchoBrew(normalPotion);
			if (infinitePotion.isEmpty()) {
				continue;
			}
			ItemStack lingering = infinitePotion.transmuteCopy(Items.LINGERING_POTION, 1);
			PotionContents contents = lingering.get(DataComponents.POTION_CONTENTS);
			if (contents == null) {
				continue;
			}
			ItemStack arrows = new ItemStack(Items.TIPPED_ARROW, 8);
			arrows.set(DataComponents.POTION_CONTENTS, contents);
			InfinitePotionMark.mark(arrows);
			infiniteLingering.add(lingering);
			infiniteArrows.add(arrows);
		}

		if (infiniteLingering.isEmpty()) {
			return List.of();
		}
		Identifier id = recipeId(revision, "infinite_tipped_arrows");
		ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);
		return List.of(new RecipeHolder<>(key,
				new InfiniteTippedArrowJeiRecipe(infiniteLingering, infiniteArrows)));
	}

	private static List<IJeiBrewingRecipe> buildEchoBrewingEntries(IVanillaRecipeFactory factory, int revision) {
		List<IJeiBrewingRecipe> entries = new ArrayList<>();
		ItemStack echoDust = new ItemStack(FundoItems.ECHO_DUST);

		for (CreativeEchoSource source : dynamicSources) {
			var itemReference = BuiltInRegistries.ITEM.get(source.container());
			var potionReference = BuiltInRegistries.POTION.get(source.potion());
			if (itemReference.isEmpty() || potionReference.isEmpty()) {
				continue;
			}

			Item container = itemReference.get().value();
			Holder<Potion> potion = potionReference.get();
			ItemStack input = PotionContents.createItemStack(container, potion);
			ItemStack output = EchoBrewingRecipe.applyCreativeEchoBrew(input);
			if (output.isEmpty()) {
				continue;
			}

			entries.add(factory.createBrewingRecipe(
					List.of(echoDust.copy()),
					List.of(input),
					output,
					recipeId(revision, "echo_brewing/" + potionPath(source.container())
							+ "/" + potionPath(source.potion()))));
		}
		return List.copyOf(entries);
	}

	private static Identifier recipeId(int revision, String path) {
		return Fundo.id("jei/" + revision + "/" + path);
	}

	private static String potionPath(Identifier id) {
		return id.getNamespace() + "/" + id.getPath();
	}
}
