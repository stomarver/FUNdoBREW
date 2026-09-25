package io.github.stomarver.fundo.client.mixin;

import java.util.Map;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntryList;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.stomarver.fundo.client.debug.EffectInfiniteTagListEntry;
import io.github.stomarver.fundo.client.debug.EffectInfiniteTagListOnEntry;
import io.github.stomarver.fundo.client.debug.PotionHitboxesEntry;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxDebug;

@Mixin(DebugScreenEntryList.class)
public abstract class DebugScreenEntryListMixin {

	@Shadow
	@Final
	private Map<Identifier, DebugScreenEntryStatus> allStatuses;

	@Inject(method = "resetStatuses", at = @At("TAIL"))
	private void fundo$seedEffectTagsList(Map<Identifier, DebugScreenEntryStatus> newEntries, CallbackInfo ci) {
		fundo$seedIfRegistered(EffectInfiniteTagListEntry.ID);
		fundo$seedIfRegistered(EffectInfiniteTagListOnEntry.ID);
	}

	@Inject(method = "setStatus", at = @At("TAIL"))
	private void fundo$mirrorVanillaPotionHitboxStatus(Identifier id,
			DebugScreenEntryStatus status, CallbackInfo ci) {
		if (PotionHitboxesEntry.ID.equals(id)) {
			PotionHitboxDebug.syncConfigFromDebugStatus(status);
		}
	}

	@Inject(method = "loadProfile", at = @At("TAIL"))
	private void fundo$mirrorVanillaDebugProfile(DebugScreenProfile profile, CallbackInfo ci) {
		PotionHitboxDebug.syncConfigFromDebugStatus(
				this.allStatuses.getOrDefault(PotionHitboxesEntry.ID, DebugScreenEntryStatus.NEVER));
	}

	private void fundo$seedIfRegistered(Identifier id) {
		if (DebugScreenEntries.getEntry(id) != null) {
			this.allStatuses.putIfAbsent(id, DebugScreenEntryStatus.IN_OVERLAY);
		}
	}
}
