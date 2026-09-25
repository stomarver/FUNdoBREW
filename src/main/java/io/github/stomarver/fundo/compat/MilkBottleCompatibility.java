package io.github.stomarver.fundo.compat;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.FundoConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

/**
 * Resolves the normal milk bottle without linking FUNdoBREW against Farmer's Delight.
 *
 * <p>The source is selected once during common item registration. Registry lookup is
 * deliberately deferred until an interaction or creative-tab callback needs the item:
 * Farmer's Delight is therefore never touched through its Java classes and a mod
 * initializer ordering difference cannot turn into a class-loading failure.</p>
 */
public final class MilkBottleCompatibility {

	public static final String FARMERS_DELIGHT_MOD_ID = "farmersdelight";
	public static final Identifier FARMERS_DELIGHT_MILK_BOTTLE =
			Identifier.fromNamespaceAndPath(FARMERS_DELIGHT_MOD_ID, "milk_bottle");

	private static boolean sourceSelected;
	private static boolean useFarmersDelightBottle;

	private MilkBottleCompatibility() {
	}

	/** Selects the source without resolving another mod's registry object. */
	public static void selectSourceAtStartup() {
		if (sourceSelected) {
			return;
		}
		sourceSelected = true;
		useFarmersDelightBottle = FundoConfig.milk_changes
				&& FundoConfig.farmers_delight
				&& FabricLoader.getInstance().isModLoaded(FARMERS_DELIGHT_MOD_ID);
		if (useFarmersDelightBottle) {
			Fundo.LOGGER.info("Using Farmer's Delight's milk bottle for FUNdoBREW milk integrations");
		}
	}

	/**
	 * Returns whether the startup selection delegates normal milk bottles to Farmer's Delight.
	 * This value is intentionally stable until restart, matching the feature-setting contract.
	 */
	public static boolean usesFarmersDelightBottle() {
		selectSourceAtStartup();
		return useFarmersDelightBottle;
	}

	/** Returns the active external item when its registry entry is available, never a foreign class. */
	@Nullable
	public static Item farmersDelightMilkBottle() {
		if (!usesFarmersDelightBottle()) {
			return null;
		}
		return BuiltInRegistries.ITEM.get(FARMERS_DELIGHT_MILK_BOTTLE)
				.map(Holder::value)
				.orElse(null);
	}
}
