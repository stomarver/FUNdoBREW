package io.github.stomarver.fundo.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class MilkSplashParticle extends SingleQuadParticle {

	/** Rounded to one decimal. */
	private static final double LAUNCH_SPEED = 0.6D;

	/** 2.52 × 1.12 = 2.8224 particle-gravity units, rounded to one decimal. */
	private static final float PARTICLE_GRAVITY = 2.8F;

	private static final double GRAVITY = PARTICLE_GRAVITY * 0.04D;

	/** 21.2° minus 6°, rounded to a whole degree. */
	private static final double CONE_DEG = 15.0D;

	private static final double SPAWN_HEIGHT = 0.2D;

	private final SpriteSet sprites;

	private MilkSplashParticle(ClientLevel level, double x, double y, double z,
			double xd, double yd, double zd, SpriteSet sprites) {
		super(level, x, y, z, 0.0D, 0.0D, 0.0D, sprites.first());
		this.sprites = sprites;

		double theta = this.random.nextDouble() * Math.toRadians(CONE_DEG);
		double phi = this.random.nextDouble() * (Math.PI * 2.0D);
		double vY = Math.cos(theta) * LAUNCH_SPEED;
		double vH = Math.sin(theta) * LAUNCH_SPEED;
		this.xd = vH * Math.cos(phi);
		this.yd = vY;
		this.zd = vH * Math.sin(phi);

		this.friction = 1.0F;
		this.gravity = PARTICLE_GRAVITY;
		this.hasPhysics = true;
		this.speedUpWhenYMotionIsBlocked = false;

		this.quadSize *= 1.875F;

		this.lifetime = (int) Math.ceil(
				(vY + Math.sqrt(vY * vY + 2.0D * GRAVITY * SPAWN_HEIGHT)) / GRAVITY);

		MilkParticleTints.applyVanillaWhite(this);
		this.setSpriteFromAge(sprites);
	}

	@Override
	public SingleQuadParticle.Layer getLayer() {
		return SingleQuadParticle.Layer.TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.removed) {
			return;
		}
		this.setSpriteFromAge(this.sprites);

		if (this.onGround) {
			this.remove();
		}
	}

	public static final class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
				double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
			return new MilkSplashParticle(level, x, y, z, xd, yd, zd, this.sprites);
		}
	}
}
