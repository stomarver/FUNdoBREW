package io.github.stomarver.fundo.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.config.FundoFeaturePolicy;

 




public final class PotionStacking {

	private PotionStacking() {
	}

	public static boolean enabled() {
		return FundoFeaturePolicy.increasedPotionStacking();
	}

	public static boolean isUnmodifiedVanillaPotion(ItemStack stack) {
		if (stack.hasNonDefault(DataComponents.MAX_STACK_SIZE)) {
			return false;
		}
		Item item = stack.getItem();
		return item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION;
	}
}
