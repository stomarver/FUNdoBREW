package io.github.stomarver.fundo.client.mixin;

import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfig;
import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxDebug;
import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MidnightConfig.class)
public abstract class MidnightConfigWriteMixin {

	@Inject(method = "writeChanges()V", at = @At("HEAD"))
	private void fundo$applyPotionHitboxVisibilityToDebugMenu(CallbackInfo ci) {
		if ((Object) this instanceof FundoConfig) {
			PotionHitboxDebug.applyVisibility(Minecraft.getInstance(), FundoConfig.show_potion_hitboxes);
		}
	}

	@Inject(method = "getEnumTranslatableText", at = @At("HEAD"), cancellable = true)
	private void fundo$useVanillaDebugStatusText(Object value, EntryInfo info,
			CallbackInfoReturnable<Component> cir) {
		if (!((Object) this instanceof FundoConfig)
				|| !"show_potion_hitboxes".equals(info.fieldName)
				|| !(value instanceof FundoConfig.PotionHitboxVisibility visibility)) {
			return;
		}
		String translationKey = switch (visibility) {
			case OFF -> "options.off";
			case IN_OVERLAY -> "debug.entry.overlay";
			case ALWAYS -> "debug.entry.always";
		};
		cir.setReturnValue(Component.translatable(translationKey));
	}
}
