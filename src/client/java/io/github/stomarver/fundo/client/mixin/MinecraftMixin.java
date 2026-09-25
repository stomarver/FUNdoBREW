package io.github.stomarver.fundo.client.mixin;

import io.github.stomarver.fundo.client.potionhitbox.PotionHitboxDebug;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(at = @At("HEAD"), method = "run")
	private void fundo$syncPotionHitboxConfigAtClientStart(CallbackInfo ci) {
		PotionHitboxDebug.syncConfigFromDebugMenu((Minecraft) (Object) this);
	}
}
