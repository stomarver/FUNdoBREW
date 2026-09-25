package io.github.stomarver.fundo.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.effect.MobEffectInstance;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

	@Accessor("hiddenEffect")
	@Nullable
	MobEffectInstance fundo$getHiddenEffect();

	@Accessor("hiddenEffect")
	void fundo$setHiddenEffect(@Nullable MobEffectInstance hiddenEffect);
}
