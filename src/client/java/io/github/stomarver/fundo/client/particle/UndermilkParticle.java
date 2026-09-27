package io.github.stomarver.fundo.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

 



public final class UndermilkParticle extends SingleQuadParticle {
	private UndermilkParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites,
			RandomSource random) {
		super(level, x, y - 0.125D, z, sprites.get(random));
		setSize(0.01F, 0.01F);
		quadSize *= random.nextFloat() * 0.6F + 0.2F;
		lifetime = (int) (16.0D / (random.nextFloat() * 0.8F + 0.2F));
		hasPhysics = false;
		friction = 1.0F;
		gravity = 0.0F;
		setColor(1.0F, 1.0F, 1.0F);
	}

	@Override
	public Layer getLayer() {
		return Layer.OPAQUE;
	}

	public static final class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
				double xd, double yd, double zd, RandomSource random) {
			return new UndermilkParticle(level, x, y, z, sprites, random);
		}
	}
}
