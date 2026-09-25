package io.github.stomarver.fundo.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SpellParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import io.github.stomarver.fundo.particle.FundoParticles;

/** Server-side version of vanilla's potion-break level events at an exact hit point. */
public final class PotionImpactEffects {

	private PotionImpactEffects() {
	}

	public static void vanillaPotion(ServerLevel level, Vec3 impact, int color, boolean instant, boolean silent) {
		RandomSource random = level.getRandom();
		ItemParticleOption bottleShard = new ItemParticleOption(ParticleTypes.ITEM, Items.SPLASH_POTION);
		for (int index = 0; index < 8; index++) {
			level.sendParticles(bottleShard, impact.x, impact.y, impact.z, 0,
					random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D,
					random.nextGaussian() * 0.15D, 1.0D);
		}

		ParticleType<SpellParticleOption> particle = instant ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;
		float red = ((color >> 16) & 0xFF) / 255.0F;
		float green = ((color >> 8) & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		for (int index = 0; index < 100; index++) {
			double magnitude = random.nextDouble() * 4.0D;
			double angle = random.nextDouble() * Math.PI * 2.0D;
			double xVelocity = Math.cos(angle) * magnitude;
			double yVelocity = 0.01D + random.nextDouble() * 0.5D;
			double zVelocity = Math.sin(angle) * magnitude;
			float brightness = 0.75F + random.nextFloat() * 0.25F;
			SpellParticleOption effect = SpellParticleOption.create(particle,
					red * brightness, green * brightness, blue * brightness, (float) magnitude);
			level.sendParticles(effect,
					impact.x + xVelocity * 0.1D, impact.y + 0.3D, impact.z + zVelocity * 0.1D,
					0, xVelocity, yVelocity, zVelocity, 1.0D);
		}

		if (!silent) {
			level.playSound(null, impact.x, impact.y, impact.z, SoundEvents.SPLASH_POTION_BREAK,
					SoundSource.NEUTRAL, 1.0F, 0.9F + random.nextFloat() * 0.1F);
		}
	}

	public static void milkBottle(ServerLevel level, Vec3 impact) {
		level.sendParticles(FundoParticles.MILK_SPLASH, impact.x, impact.y + 0.2D, impact.z,
				35, 0.025D, 0.025D, 0.025D, 0.0D);
		level.playSound(null, impact.x, impact.y, impact.z, SoundEvents.SPLASH_POTION_BREAK,
				SoundSource.NEUTRAL, 1.0F, 0.9F + level.getRandom().nextFloat() * 0.1F);
	}
}
