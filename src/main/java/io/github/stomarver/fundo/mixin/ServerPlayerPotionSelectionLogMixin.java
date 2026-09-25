package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

import io.github.stomarver.fundo.debug.ActionLogs;
import io.github.stomarver.fundo.debug.PotionActionLog;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayerPotionSelectionLogMixin {

	@Inject(method = "handleSetCarriedItem", at = @At("TAIL"))
	private void fundo$logTrackedHotbarSelection(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
		if (!ActionLogs.potionsEnabled()) {
			return;
		}
		ServerGamePacketListenerImpl connection = (ServerGamePacketListenerImpl) (Object) this;
		ItemStack selected = connection.player.getMainHandItem();
		if (PotionActionLog.tracks(selected)) {
			ActionLogs.potions(connection.player.level(), "inventory -> hand | actor="
					+ ActionLogs.subject(connection.player) + " hotbar-slot=" + packet.getSlot()
					+ " | " + PotionActionLog.describe(selected));
		}
	}
}
