package io.github.stomarver.fundo.client.potionhitbox;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import com.mojang.blaze3d.platform.InputConstants;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.client.debug.PotionHitboxesEntry;
import io.github.stomarver.fundo.client.mixin.MidnightConfigEntriesAccessor;
import io.github.stomarver.fundo.config.FundoConfig;

public final class PotionHitboxDebug {

	public static final KeyMapping TOGGLE = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.fundo.potion_hitboxes", InputConstants.Type.KEYBOARD, InputConstants.KEY_R,
			KeyMapping.Category.DEBUG));

	private PotionHitboxDebug() {
	}

	public static void register() {

	}

	public static boolean toggle(Minecraft minecraft) {
		if (minecraft.player == null || minecraft.showOnlyReducedInfo()) {
			return false;
		}

		boolean enabled = minecraft.debugEntries.toggleStatus(PotionHitboxesEntry.ID);
		MutableComponent prefix = Component.translatable("debug.prefix")
				.withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);

		Component status = Component.translatable(enabled
				? "fundo.debug.potion_hitboxes.on"
				: "fundo.debug.potion_hitboxes.off")
				.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(false));
		minecraft.player.sendSystemMessage(prefix.append(" ").append(status));
		return true;
	}

	public static FundoConfig.PotionHitboxVisibility visibility(Minecraft minecraft) {
		DebugScreenEntryStatus status = minecraft.debugEntries.getStatus(PotionHitboxesEntry.ID);
		if (status == DebugScreenEntryStatus.ALWAYS_ON) {
			return FundoConfig.PotionHitboxVisibility.ALWAYS;
		}
		if (status == DebugScreenEntryStatus.IN_OVERLAY) {
			return FundoConfig.PotionHitboxVisibility.IN_OVERLAY;
		}
		return FundoConfig.PotionHitboxVisibility.OFF;
	}

	public static void applyVisibility(Minecraft minecraft, FundoConfig.PotionHitboxVisibility visibility) {

		if (minecraft == null || minecraft.debugEntries == null) {
			return;
		}
		DebugScreenEntryStatus status = switch (visibility) {
			case OFF -> DebugScreenEntryStatus.NEVER;
			case IN_OVERLAY -> DebugScreenEntryStatus.IN_OVERLAY;
			case ALWAYS -> DebugScreenEntryStatus.ALWAYS_ON;
		};
		if (minecraft.debugEntries.getStatus(PotionHitboxesEntry.ID) != status) {
			minecraft.debugEntries.setStatus(PotionHitboxesEntry.ID, status);
		}
	}

	public static void syncConfigFromDebugStatus(DebugScreenEntryStatus status) {
		FundoConfig.PotionHitboxVisibility visibility = switch (status) {
			case NEVER -> FundoConfig.PotionHitboxVisibility.OFF;
			case IN_OVERLAY -> FundoConfig.PotionHitboxVisibility.IN_OVERLAY;
			case ALWAYS_ON -> FundoConfig.PotionHitboxVisibility.ALWAYS;
		};

		var configEntry = MidnightConfigEntriesAccessor.fundo$entries()
				.get(Fundo.MOD_ID + ":show_potion_hitboxes");
		if (configEntry != null) {
			configEntry.setValue(visibility);
		}
		if (FundoConfig.show_potion_hitboxes != visibility) {
			FundoConfig.show_potion_hitboxes = visibility;
			MidnightConfig.write(Fundo.MOD_ID);
		}
	}

	public static void syncConfigFromDebugMenu(Minecraft minecraft) {
		syncConfigFromDebugStatus(minecraft.debugEntries.getStatus(PotionHitboxesEntry.ID));
	}

	public static boolean isEnabled(Minecraft minecraft) {
		return minecraft.debugEntries.isCurrentlyEnabled(PotionHitboxesEntry.ID);
	}
}
