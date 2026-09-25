package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

@Mixin(ItemStack.class)
public abstract class ItemStackPotionActionLogMixin {

	@Inject(method = "use", at = @At("RETURN"))
	private void fundo$logTrackedHandUse(Level level, Player player, InteractionHand hand,
			CallbackInfoReturnable<InteractionResult> cir) {
		if (!(level instanceof ServerLevel) || !cir.getReturnValue().consumesAction() || !ActionLogs.potionsEnabled()) {
			return;
		}
		ItemStack self = (ItemStack) (Object) this;
		if (PotionActionLog.tracks(self)) {
			String event = "inventory -> hand | actor=" + ActionLogs.subject(player)
					+ " hand=" + hand + " | input " + PotionActionLog.describe(self)
					+ " | hand-after " + PotionActionLog.describe(player.getItemInHand(hand));
			ActionLogs.potions(level, event);
			if (InfinitePotionMark.isMarked(self)) {
				ActionLogs.infinite(level, "lifecycle | " + event);
			}
		}
	}

	@Inject(method = "finishUsingItem", at = @At("TAIL"))
	private void fundo$logTrackedConsumption(Level level, LivingEntity consumer,
			CallbackInfoReturnable<ItemStack> cir) {
		if (!(level instanceof ServerLevel) || !ActionLogs.potionsEnabled()) {
			return;
		}
		ItemStack self = (ItemStack) (Object) this;
		if (PotionActionLog.tracks(self)) {
			String event = "hand -> consumer | actor=" + ActionLogs.subject(consumer)
					+ " | consumed " + PotionActionLog.describe(self)
					+ " | remainder " + PotionActionLog.describe(cir.getReturnValue());
			ActionLogs.potions(level, event);
			if (InfinitePotionMark.isMarked(self)) {
				ActionLogs.infinite(level, "lifecycle | " + event);
			}
		}
	}
}
