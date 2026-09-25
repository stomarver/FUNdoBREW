package io.github.stomarver.fundo.item;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.brewing.EchoBrewingRecipe;
import io.github.stomarver.fundo.brewing.ExtendedPotionIndex;
import io.github.stomarver.fundo.brewing.ExtendedPotionIndex.CreativeEchoSource;
import io.github.stomarver.fundo.compat.MilkBottleCompatibility;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.effect.InfinitePotionMark;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class FundoItems {

	public static Item MILK_BOTTLE = null;

	public static Item ECHO_DUST = null;

	private static final ResourceKey<CreativeModeTab> TAB_FOOD_AND_DRINKS =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("food_and_drinks"));
	private static final ResourceKey<CreativeModeTab> TAB_INGREDIENTS =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("ingredients"));
	private static final ResourceKey<CreativeModeTab> TAB_COMBAT =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("combat"));

	public static Item SPLASH_MILK_BOTTLE = null;

	public static Item LINGERING_MILK_BOTTLE = null;

	private FundoItems() {
	}

	/**
	 * Returns the one normal milk bottle that FUNdoBREW should consume, produce, and log.
	 * The value can be null before an optional Farmer's Delight registry entry exists or when milk additions are disabled.
	 */
	public static Item normalMilkBottle() {
		if (MilkBottleCompatibility.usesFarmersDelightBottle()) {
			return MilkBottleCompatibility.farmersDelightMilkBottle();
		}
		return MILK_BOTTLE;
	}

	public static boolean isNormalMilkBottle(Item item) {
		return item != null && item == normalMilkBottle();
	}

	private static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Fundo.id(name));

		return Registry.register(BuiltInRegistries.ITEM, itemKey, itemFactory.apply(settings.setId(itemKey)));
	}

	public static void register() {

		MilkBottleCompatibility.selectSourceAtStartup();
		if (FundoConfig.milk_changes && !MilkBottleCompatibility.usesFarmersDelightBottle()) {
			MILK_BOTTLE = register(
					"milk_bottle",
					Item::new,
					new Item.Properties()
							.stacksTo(16)
							.component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET)

							.component(DataComponents.USE_REMAINDER,
									new UseRemainder(new ItemStackTemplate(Items.GLASS_BOTTLE))));
		}
		if (FundoConfig.infinite_potions) {
			ECHO_DUST = register("echo_dust", Item::new, new Item.Properties());
		}
		if (FundoConfig.milk_changes) {
			SPLASH_MILK_BOTTLE = register(
					"splash_milk_bottle", SplashMilkBottleItem::new, new Item.Properties().stacksTo(16));

			DispenserBlock.registerBehavior(SPLASH_MILK_BOTTLE, new ProjectileDispenseBehavior(SPLASH_MILK_BOTTLE));
		}
		if (FundoConfig.milk_changes) {
			LINGERING_MILK_BOTTLE = register(
					"lingering_milk_bottle", LingeringMilkBottleItem::new, new Item.Properties().stacksTo(16));
			DispenserBlock.registerBehavior(LINGERING_MILK_BOTTLE, new ProjectileDispenseBehavior(LINGERING_MILK_BOTTLE));
		}

		CreativeModeTabEvents.modifyOutputEvent(TAB_FOOD_AND_DRINKS)
				.register(entries -> {
					Item milkBottle = normalMilkBottle();
					// Farmer's Delight owns its active bottle and its native creative-tab placement.
					if (milkBottle != null && !MilkBottleCompatibility.usesFarmersDelightBottle()) {
						entries.insertBefore(Items.HONEY_BOTTLE, milkBottle);
					}
					if (SPLASH_MILK_BOTTLE != null) {
						entries.insertBefore(Items.SPLASH_POTION, SPLASH_MILK_BOTTLE);
					}
					if (LINGERING_MILK_BOTTLE != null) {
						entries.insertBefore(Items.LINGERING_POTION, LINGERING_MILK_BOTTLE);
					}
				});

		CreativeModeTabEvents.modifyOutputEvent(TAB_INGREDIENTS)
				.register(entries -> {
					if (ECHO_DUST != null) {
						entries.insertAfter(Items.REDSTONE, ECHO_DUST);
					}
				});

		CreativeModeTabEvents.modifyOutputEvent(TAB_FOOD_AND_DRINKS)
				.register(FundoItems::insertEchoPotionVariants);
		CreativeModeTabEvents.modifyOutputEvent(TAB_COMBAT)
				.register(FundoItems::insertEchoTippedArrowVariants);
	}

	private static void insertEchoPotionVariants(FabricCreativeModeTabOutput out) {

		if (!FundoConfig.infinite_potions || ECHO_DUST == null) {
			return;
		}
		Item[] containers = {Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};
		for (Item container : containers) {
			for (Map.Entry<ResourceKey<Potion>, Potion> entry : BuiltInRegistries.POTION.entrySet()) {
				Identifier id = entry.getKey().identifier();
				String path = id.getPath();

				if (path.startsWith("long_")) {

					insertEchoAfterAnchor(out, container, id, id.withPath(path.substring("long_".length())));
				} else if (path.startsWith("strong_")) {

					insertEchoAfterAnchor(out, container, id, id);
				} else if (noUpgrades(id)) {

					insertEchoAfterAnchor(out, container, id, id);
				}
			}
		}
	}

	/**
	 * The vanilla combat tab already carries one tipped-arrow variant per potion.
	 * Insert the matching infinite arrow after that exact vanilla variant, using
	 * the server-synchronised creative source contract. Creative tab output
	 * entries must have count one; vanilla's crafting recipe still outputs eight.
	 */
	private static void insertEchoTippedArrowVariants(FabricCreativeModeTabOutput out) {
		if (!FundoConfig.infinite_potions || ECHO_DUST == null) {
			return;
		}
		Identifier potionContainer = BuiltInRegistries.ITEM.getKey(Items.POTION);
		for (CreativeEchoSource source : ExtendedPotionIndex.creativeSources()) {
			if (!source.container().equals(potionContainer)) {
				continue;
			}
			var potionReference = BuiltInRegistries.POTION.get(source.potion());
			if (potionReference.isEmpty()) {
				continue;
			}

			ItemStack infinitePotion = EchoBrewingRecipe.applyCreativeEchoBrew(
					PotionContents.createItemStack(Items.POTION, potionReference.get()));
			if (infinitePotion.isEmpty()) {
				continue;
			}
			ItemStack arrows = infiniteTippedArrows(
					infinitePotion.transmuteCopy(Items.LINGERING_POTION, 1));
			if (arrows.isEmpty()) {
				continue;
			}
			out.insertAfter(stack -> isPotionStack(stack, Items.TIPPED_ARROW, source.potion()),
					List.of(arrows), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}

	private static ItemStack infiniteTippedArrows(ItemStack infiniteLingering) {
		PotionContents contents = infiniteLingering.get(DataComponents.POTION_CONTENTS);
		if (contents == null || !InfinitePotionMark.isMarked(infiniteLingering)) {
			return ItemStack.EMPTY;
		}
		ItemStack arrows = new ItemStack(Items.TIPPED_ARROW);
		arrows.set(DataComponents.POTION_CONTENTS, contents);
		InfinitePotionMark.mark(arrows);
		return arrows;
	}

	private static boolean noUpgrades(Identifier baseId) {
		String ns = baseId.getNamespace();
		String path = baseId.getPath();
		return BuiltInRegistries.POTION.get(Identifier.fromNamespaceAndPath(ns, "long_" + path)).isEmpty()
				&& BuiltInRegistries.POTION.get(Identifier.fromNamespaceAndPath(ns, "strong_" + path)).isEmpty();
	}

	private static void insertEchoAfterAnchor(FabricCreativeModeTabOutput out,
			Item container, Identifier anchorId, Identifier sourceId) {
		var baseRef = BuiltInRegistries.POTION.get(anchorId);
		var sourceRef = BuiltInRegistries.POTION.get(sourceId);
		if (baseRef.isEmpty() || sourceRef.isEmpty()) {
			return;
		}

		ItemStack echoed = EchoBrewingRecipe.applyCreativeEchoBrew(
				PotionContents.createItemStack(container, sourceRef.get()));
		if (echoed.isEmpty()) {
			return;
		}
		out.insertAfter(
				stack -> isPotionStack(stack, container, anchorId),
				List.of(echoed),
				CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
	}

	private static boolean isPotionStack(ItemStack stack, Item container, Identifier potionId) {
		if (stack.getItem() != container) {
			return false;
		}
		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return false;
		}
		return contents.potion()
				.flatMap(Holder::unwrapKey)
				.map(key -> key.identifier().equals(potionId))
				.orElse(false);
	}
}
