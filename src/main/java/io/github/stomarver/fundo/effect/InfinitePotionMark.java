package io.github.stomarver.fundo.effect;

import java.util.List;

import io.github.stomarver.fundo.brewing.ExtendedPotionMark;
import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;

public final class InfinitePotionMark {

	public static final String KEY = "fundo:infinite";

	private InfinitePotionMark() {
	}

	public static void mark(ItemStack stack) {
		if (FundoConfig.infinite_potions) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(KEY, true));
		}
	}

	public static boolean isMarked(ItemStack stack) {
		if (!FundoConfig.infinite_potions) {
			sanitizeIfDisabled(stack);
			return false;
		}
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBooleanOr(KEY, false);
	}

	public static boolean isMarked(Entity entity) {
		if (!FundoConfig.infinite_potions) {
			sanitizeIfDisabled(entity);
			return false;
		}
		CustomData data = entity.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBooleanOr(KEY, false);
	}

	/**
	 * JEI's vanilla potion interpreter only keys on a registered potion holder.
	 * Echoed potions intentionally use custom infinite effects instead, so give
	 * marked stacks a stable subtype based on their complete component patch.
	 * This also keeps resource-pack component variants distinct in JEI.
	 */
	public static Object jeiSubtype(ItemStack stack) {
		if (!isMarked(stack)) {
			return null;
		}
		return new JeiSubtype(stack.getComponentsPatch());
	}

	private record JeiSubtype(DataComponentPatch components) {
	}

	public static void sanitizeIfDisabled(ItemStack stack) {
		if (FundoConfig.infinite_potions || stack.isEmpty()) {
			return;
		}
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null) {
			return;
		}
		CompoundTag tag = data.copyTag();
		boolean hadFundoData = tag.contains(KEY) || tag.contains(ExtendedPotionMark.KEY);
		if (!hadFundoData) {
			return;
		}
		tag.remove(KEY);
		tag.remove(ExtendedPotionMark.KEY);
		CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);

		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		if (contents != null) {
			PotionContents clean = withoutInfiniteEffects(contents);
			if (clean != contents) {
				stack.set(DataComponents.POTION_CONTENTS, clean);
			}
		}
	}

	public static void sanitizeIfDisabled(Entity entity) {
		if (FundoConfig.infinite_potions) {
			return;
		}
		CustomData data = entity.get(DataComponents.CUSTOM_DATA);
		if (data == null) {
			return;
		}
		CompoundTag tag = data.copyTag();
		if (!tag.contains(KEY) && !tag.contains(ExtendedPotionMark.KEY)) {
			return;
		}
		tag.remove(KEY);
		tag.remove(ExtendedPotionMark.KEY);
		entity.setComponent(DataComponents.CUSTOM_DATA, CustomData.of(tag));

		PotionContents contents = entity.get(DataComponents.POTION_CONTENTS);
		if (contents != null) {
			PotionContents clean = withoutInfiniteEffects(contents);
			if (clean != contents) {
				entity.setComponent(DataComponents.POTION_CONTENTS, clean);
			}
		}
	}

	public static PotionContents withoutInfiniteEffects(PotionContents contents) {
		List<MobEffectInstance> finite = contents.customEffects().stream()
				.filter(effect -> !effect.isInfiniteDuration())
				.toList();
		if (finite.size() == contents.customEffects().size()) {
			return contents;
		}
		return new PotionContents(contents.potion(), contents.customColor(), finite, contents.customName());
	}
}
