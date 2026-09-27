package io.github.stomarver.fundo.particle;

import io.github.stomarver.fundo.Fundo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class FundoParticles {

	public static SimpleParticleType MILK_MIST;
	public static SimpleParticleType MILK_SPLASH;
	public static SimpleParticleType UNDERMILK;
	public static SimpleParticleType MILK_DRIPPING;
	public static SimpleParticleType MILK_FALLING;
	public static SimpleParticleType MILK_DRIP_SPLASH;

	private FundoParticles() {
	}

	private static SimpleParticleType register(String name) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Fundo.id(name), new FundoSimpleParticleType());
	}

	public static void register() {
		MILK_MIST = register("milk_mist");
		MILK_SPLASH = register("milk_splash");
		UNDERMILK = register("undermilk");
		MILK_DRIPPING = register("milk_dripping");
		MILK_FALLING = register("milk_falling");
		MILK_DRIP_SPLASH = register("milk_drip_splash");
	}

	private static final class FundoSimpleParticleType extends SimpleParticleType {
		private FundoSimpleParticleType() {
			super(false);
		}
	}
}
