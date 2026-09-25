package io.github.stomarver.fundo.particle;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.config.FundoConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class FundoParticles {

	public static SimpleParticleType MILK_MIST;
	public static SimpleParticleType MILK_SPLASH;

	private FundoParticles() {
	}

	private static SimpleParticleType register(String name) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Fundo.id(name), new FundoSimpleParticleType());
	}

	public static void register() {
		if (!FundoConfig.milk_changes) {
			return;
		}
		MILK_MIST = register("milk_mist");
		MILK_SPLASH = register("milk_splash");
	}

	private static final class FundoSimpleParticleType extends SimpleParticleType {
		private FundoSimpleParticleType() {
			super(false);
		}
	}
}
