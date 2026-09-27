package io.github.stomarver.fundo.client.mixin;

import io.github.stomarver.fundo.fluid.FundoFluids;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 



@Mixin(FogRenderer.class)
public abstract class MilkFogMixin {
	private static final float POWDER_SNOW_SURVIVAL_FOG_END = 2.0F;
	private static final float POWDER_SNOW_SPECTATOR_FOG_START = -8.0F;
	private static final float POWDER_SNOW_SPECTATOR_END_FACTOR = 0.5F;

	@Inject(method = "setupFog", at = @At("RETURN"))
	private void fundo$applyDenseWhiteMilkFog(Camera camera, int renderDistanceChunks, DeltaTracker deltaTracker,
			float skyDarkness, ClientLevel level, CallbackInfoReturnable<FogData> cir) {
		if (FundoFluids.MILK == null) {
			return;
		}

		BlockPos cameraPos = camera.blockPosition();
		FluidState fluid = level.getFluidState(cameraPos);
		if (!fluid.getType().isSame(FundoFluids.MILK)
				|| camera.position().y >= cameraPos.getY() + fluid.getHeightForCamera(level, cameraPos)) {
			return;
		}

		 
		 
		 
		FogData fog = cir.getReturnValue();
		float fogEnd;
		if (camera.entity().isSpectator()) {
			fog.environmentalStart = POWDER_SNOW_SPECTATOR_FOG_START;
			fogEnd = renderDistanceChunks * 16.0F * POWDER_SNOW_SPECTATOR_END_FACTOR;
		} else {
			fog.environmentalStart = 0.0F;
			fogEnd = POWDER_SNOW_SURVIVAL_FOG_END;
		}
		fog.color.set(1.0F, 1.0F, 1.0F, 1.0F);
		fog.environmentalEnd = fogEnd;
		fog.skyEnd = fogEnd;
		fog.cloudEnd = fogEnd;
	}
}
