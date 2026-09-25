package io.github.stomarver.fundo.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class MilkMistParticle extends SingleQuadParticle {

private final SpriteSet sprites;
	private final double startY;

	private MilkMistParticle(ClientLevel level, double x, double y, double z,
			double xd, double yd, double zd, SpriteSet sprites) {
		super(level, x, y, z, 0.0D, 0.0D, 0.0D, sprites.first());
		this.friction = 0.96F;
		this.sprites = sprites;
		this.xd = this.xd * 0.1D + xd;
		this.yd = this.yd * 0.1D + yd;
		this.zd = this.zd * 0.1D + zd;

		MilkParticleTints.applyVanillaWhite(this);
		this.quadSize *= 1.875F;

		this.lifetime = 22 + this.random.nextInt(5);
		this.hasPhysics = false;
		this.startY = y;
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

		if (this.y > this.startY + 0.125D) {
			this.y = this.startY + 0.125D;
			if (this.yd > 0.0D) {
				this.yd = 0.0D;
			}
		} else if (this.y < this.startY - 0.125D) {
			this.y = this.startY - 0.125D;
			if (this.yd < 0.0D) {
				this.yd = 0.0D;
			}
		}
	}

	public static final class Provider implements ParticleProvider<SimpleParticleType> {

		private static final float KEEP_PROBABILITY = 2.0F / 3.0F;

		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level,
				double x, double y, double z, double xd, double yd, double zd, RandomSource random) {
			if (random.nextFloat() >= KEEP_PROBABILITY) {
				return null;
			}

			return new MilkMistParticle(level, x, y + 0.25D, z, xd, yd, zd, this.sprites);
		}
	}
}
