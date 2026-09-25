package io.github.stomarver.fundo.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;

@Mixin(EntityHitboxDebugRenderer.class)
public abstract class AreaEffectCloudHitboxDebugMixin {

	@Inject(method = "showHitboxes", at = @At("HEAD"), cancellable = true)
	private void fundo$hideCloudPhysicalAnchor(Entity entity, float partialTick, boolean serverBox, CallbackInfo ci) {
		if (entity instanceof AreaEffectCloud) {
			ci.cancel();
		}
	}
}
