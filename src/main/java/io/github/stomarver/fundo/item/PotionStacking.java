package io.github.stomarver.fundo.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import io.github.stomarver.fundo.config.FundoConfig;

public final class PotionStacking {

	private static volatile Boolean serverEnabled;

	private PotionStacking() {
	}

	public static boolean enabled() {
		Boolean value = serverEnabled;
		return value != null ? value : FundoConfig.increased_potion_stacking;
	}

	public static void installServerValue(boolean enabled) {
		serverEnabled = enabled;
	}

	public static void clearServerValue() {
		serverEnabled = null;
	}

	public static boolean isUnmodifiedVanillaPotion(ItemStack stack) {
		if (stack.hasNonDefault(DataComponents.MAX_STACK_SIZE)) {
			return false;
		}
		Item item = stack.getItem();
		return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
	}

	public static int limitCount(ItemStack stack, int count) {
		return !enabled() && count > 1 && isUnmodifiedVanillaPotion(stack) ? 1 : count;
	}
}
