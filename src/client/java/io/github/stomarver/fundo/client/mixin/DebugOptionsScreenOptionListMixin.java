package io.github.stomarver.fundo.client.mixin;

import java.util.Comparator;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.resources.Identifier;

import io.github.stomarver.fundo.client.debug.PotionHitboxesEntry;

@Mixin(DebugOptionsScreen.OptionList.class)
public class DebugOptionsScreenOptionListMixin {

	@ModifyArg(
			method = "updateSearch",
			at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"),
			index = 0)
	private Comparator<Map.Entry<Identifier, DebugScreenEntry>> fundo$placePotionHitboxesAfterEntityHitboxes(
			Comparator<Map.Entry<Identifier, DebugScreenEntry>> vanillaComparator) {
		Map.Entry<Identifier, DebugScreenEntry> entityHitboxes = Map.entry(
				DebugScreenEntries.ENTITY_HITBOXES,
				DebugScreenEntries.getEntry(DebugScreenEntries.ENTITY_HITBOXES));
		return (left, right) -> {
			boolean leftIsPotionHitboxes = left.getKey().equals(PotionHitboxesEntry.ID);
			boolean rightIsPotionHitboxes = right.getKey().equals(PotionHitboxesEntry.ID);
			if (!leftIsPotionHitboxes && !rightIsPotionHitboxes) {
				return vanillaComparator.compare(left, right);
			}
			if (leftIsPotionHitboxes && rightIsPotionHitboxes) {
				return 0;
			}
			if (leftIsPotionHitboxes) {
				return right.getKey().equals(DebugScreenEntries.ENTITY_HITBOXES)
						? 1
						: vanillaComparator.compare(entityHitboxes, right);
			}
			return left.getKey().equals(DebugScreenEntries.ENTITY_HITBOXES)
					? -1
					: vanillaComparator.compare(left, entityHitboxes);
		};
	}
}
