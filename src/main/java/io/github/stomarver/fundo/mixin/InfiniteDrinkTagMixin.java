package io.github.stomarver.fundo.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.effect.InfiniteEffects;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(ItemStack.class)
public abstract class InfiniteDrinkTagMixin {

	@Inject(method = "finishUsingItem", at = @At("TAIL"))
	private void fundo$tagInfiniteDrinker(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
		if (level.isClientSide() || !FundoConfig.infinite_potions) {
			return;
		}
		ItemStack self = (ItemStack) (Object) this;
		PotionContents contents = self.get(DataComponents.POTION_CONTENTS);
		if (contents == null) {
			return;
		}
		List<MobEffectInstance> endless = InfiniteEffects.storedEndless(contents);
		boolean marked = InfinitePotionMark.isMarked(self);
		logDrink(level, entity, self, contents, endless, marked);
		if (!marked || endless.isEmpty()) {
			return;
		}
		for (MobEffectInstance stored : endless) {
			boolean hadRow = InfiniteEffects.hasAnyRow(entity, stored.getEffect());
			boolean tagged = InfiniteEffects.mirrorArrival(entity, stored);
			if (tagged && !hadRow) {
				ActionLogs.infinite(level, "drink | " + ActionLogs.subject(entity)
						+ " fundo:infinite landed: " + InfiniteEffects.describe(stored) + " -> tag");
			} else if (!tagged) {
				ActionLogs.infinite(level, "drink | " + ActionLogs.subject(entity)
						+ " fundo:infinite carried " + InfiniteEffects.describe(stored)
						+ " but vanilla merged it away (stronger active infinite) — no tag");
			}
		}
	}

	private static void logDrink(Level level, LivingEntity entity, ItemStack stack, PotionContents contents,
			List<MobEffectInstance> endless, boolean marked) {
		StringBuilder effects = new StringBuilder();
		for (MobEffectInstance stored : contents.getAllEffects()) {
			if (effects.length() > 0) {
				effects.append(", ");
			}
			effects.append(InfiniteEffects.describe(stored))
					.append(stored.isInfiniteDuration() ? "∞" : ":" + stored.getDuration() + "t");
		}
		ActionLogs.infinite(level, "drink | " + ActionLogs.subject(entity) + " consumed "
				+ BuiltInRegistries.ITEM.getKey(stack.getItem())
				+ " | effects [" + effects + "] | mark=" + (marked ? "fundo:infinite" : "none")
				+ (endless.isEmpty() ? " (no endless payload)" : ""));
	}
}
