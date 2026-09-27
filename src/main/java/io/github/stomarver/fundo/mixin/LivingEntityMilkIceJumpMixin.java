package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.block.FundoBlocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 
@Mixin(LivingEntity.class)
public abstract class LivingEntityMilkIceJumpMixin {
	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	private void fundo$halveJumpFromMilkIce(CallbackInfo ci) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (FundoBlocks.MILK_ICE == null || !entity.getBlockStateOn().is(FundoBlocks.MILK_ICE)) {
			return;
		}

		Vec3 movement = entity.getDeltaMovement();
		if (movement.y > 0.0D) {
			entity.setDeltaMovement(movement.x, movement.y * 0.5D, movement.z);
		}
	}
}
