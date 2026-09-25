package io.github.stomarver.fundo.client.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxDebug;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
	private void fundo$togglePotionHitboxes(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {

		if (PotionHitboxDebug.TOGGLE.matches(event) && PotionHitboxDebug.toggle(minecraft)) {
			cir.setReturnValue(true);
		}
	}
}
