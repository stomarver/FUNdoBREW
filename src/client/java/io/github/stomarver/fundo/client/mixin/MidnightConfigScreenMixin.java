package io.github.stomarver.fundo.client.mixin;

import java.util.List;

import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.lib.config.MidnightConfigListWidget;
import eu.midnightdust.lib.config.MidnightConfigScreen;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxDebug;
import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MidnightConfigScreen.class)
public abstract class MidnightConfigScreenMixin {

	private static final String GENERAL_TAB_KEY = "fundo.midnightconfig.category.general";
	private static final String DEBUG_TAB_KEY = "fundo.midnightconfig.category.debug";
	private static final String FEATURES_HEADING_KEY = "fundo.midnightconfig.features";
	private static final String LOGS_HEADING_KEY = "fundo.midnightconfig.logs";
	private static final int FEATURES_GOLD = 0xFFD56A;
	private static final int YELLOW = 0xFFFF55;

	@Shadow public MidnightConfig instance;
	@Shadow public MidnightConfigListWidget list;
	@Shadow public Tab prevTab;

	@Inject(method = "init", at = @At("HEAD"))
	private void fundo$mirrorPotionHitboxDebugStatus(CallbackInfo ci) {
		if (instance instanceof FundoConfig) {
			PotionHitboxDebug.syncConfigFromDebugMenu(Minecraft.getInstance());
		}
	}

	@Inject(
			method = "updateList",
			at = @At(
					value = "INVOKE",
					target = "Leu/midnightdust/lib/config/MidnightConfig;onTabInit(Ljava/lang/String;Leu/midnightdust/lib/config/MidnightConfigListWidget;Leu/midnightdust/lib/config/MidnightConfigScreen;)V",
					shift = At.Shift.AFTER))
	private void fundo$prependPrimaryHeading(CallbackInfo ci) {
		if (!(instance instanceof FundoConfig)) {
			return;
		}
		if (fundo$isCurrentTab(GENERAL_TAB_KEY)) {
			list.addButton(List.of(), Component.translatable(FEATURES_HEADING_KEY)
					.setStyle(Style.EMPTY.withColor(FEATURES_GOLD).withBold(true)), null);
		} else if (fundo$isCurrentTab(DEBUG_TAB_KEY)) {
			list.addButton(List.of(), Component.translatable(LOGS_HEADING_KEY)
					.setStyle(Style.EMPTY.withColor(YELLOW).withBold(true)), null);
		}
	}

	private boolean fundo$isCurrentTab(String key) {
		if (prevTab == null || !(prevTab.getTabTitle().getContents() instanceof TranslatableContents contents)) {
			return false;
		}
		return key.equals(contents.getKey());
	}
}
