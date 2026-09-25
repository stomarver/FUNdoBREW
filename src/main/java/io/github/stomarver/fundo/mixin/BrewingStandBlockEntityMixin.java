package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;

import io.github.stomarver.fundo.brewing.ExtendedPotionMark;
import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

	@ModifyArg(
			method = "serverTick",
			at = @At(value = "INVOKE", target = "Ljava/lang/Math;ceil(D)D"),
			index = 0)
	private static double fundo$applyBrewingSpeedMultiplier(double vanillaBrewDuration) {
		return vanillaBrewDuration / FundoConfig.brewingSpeedMultiplier();
	}

	@Inject(method = "doBrew", at = @At("TAIL"))
	private static void fundo$logCompletedBrew(ServerLevel level, BlockPos pos,
			BrewingStandBlockEntity stand, CallbackInfo ci) {
		for (int slot = 0; slot < 3; slot++) {
			ItemStack output = stand.getItem(slot);
			if (PotionActionLog.tracks(output) && ActionLogs.potionsEnabled()) {
				ActionLogs.potions(level, "brew | stand=" + pos + " slot=" + slot + " | "
						+ PotionActionLog.describe(output));
			}
			if (InfinitePotionMark.isMarked(output) || ExtendedPotionMark.isMarked(output)) {
				ActionLogs.infinite(level, "brew | stand=" + pos + " slot=" + slot + " | "
						+ PotionActionLog.describe(output)
						+ (ExtendedPotionMark.isMarked(output) ? " mark=fundo:extended" : ""));
			}
		}
	}
}
