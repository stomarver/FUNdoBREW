package io.github.stomarver.fundo.brewing;

import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ExtendedPotionMark {

	public static final String KEY = "fundo:extended";

	private ExtendedPotionMark() {
	}

	public static void mark(ItemStack stack) {
		if (FundoConfig.infinite_potions) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(KEY, true));
		}
	}

	public static boolean isMarked(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBooleanOr(KEY, false);
	}
}
