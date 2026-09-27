package io.github.stomarver.fundo.compat;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.FundoFeaturePolicy;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

 





public final class MilkBottleCompatibility {

	public static final String FARMERS_DELIGHT_MOD_ID = "farmersdelight";
	public static final Identifier FARMERS_DELIGHT_MILK_BOTTLE =
			Identifier.fromNamespaceAndPath(FARMERS_DELIGHT_MOD_ID, "milk_bottle");

	private static boolean loggedFarmersDelightSelection;

	private MilkBottleCompatibility() {
	}

	 




	public static boolean usesFarmersDelightBottle() {
		boolean usesFarmersDelight = FundoFeaturePolicy.farmersDelightCompatibility()
				&& FabricLoader.getInstance().isModLoaded(FARMERS_DELIGHT_MOD_ID);
		if (usesFarmersDelight && !loggedFarmersDelightSelection) {
			loggedFarmersDelightSelection = true;
			Fundo.LOGGER.info("Using Farmer's Delight's milk bottle for FUNdoBREW milk integrations");
		}
		return usesFarmersDelight;
	}

	 
	public static void selectSourceAtStartup() {
		usesFarmersDelightBottle();
	}

	 
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
