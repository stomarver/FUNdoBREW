package io.github.stomarver.fundo.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;

@Mixin(AreaEffectCloud.class)
public interface AreaEffectCloudAccessor {

	@Accessor("victims")
	Map<Entity, Integer> fundo$getVictims();
}
