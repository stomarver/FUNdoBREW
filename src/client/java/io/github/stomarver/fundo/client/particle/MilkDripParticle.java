package io.github.stomarver.fundo.client.particle;

import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.particle.FundoParticles;
import io.github.stomarver.fundo.sound.FundoSounds;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.FluidState;

 




public final class MilkDripParticle {
	private static final float MILK_RED = 1.0F;
	private static final float MILK_GREEN = 0.985F;
	private static final float MILK_BLUE = 0.965F;

	private MilkDripParticle() {
	}

	private abstract static class Base extends SingleQuadParticle {
		Base(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
			super(level, x, y, z, sprite);
			setSize(0.01F, 0.01F);
			gravity = 0.06F;
			setColor(MILK_RED, MILK_GREEN, MILK_BLUE);
		}

		@Override
		public Layer getLayer() {
			return Layer.OPAQUE;
		}

		final boolean isInsideMilk() {
			BlockPos pos = BlockPos.containing(x, y, z);
			FluidState fluid = level.getFluidState(pos);
			return FundoFluids.MILK != null
					&& fluid.getType().isSame(FundoFluids.MILK)
					&& y < pos.getY() + fluid.getHeight(level, pos);
		}

		final void updateMotion() {
			xo = x;
			yo = y;
			zo = z;
			yd -= gravity;
			move(xd, yd, zd);
		}
	}

	private static final class Hanging extends Base {
		Hanging(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
			super(level, x, y, z, sprite);
			gravity *= 0.02F;
			lifetime = 40;
		}

		@Override
		public void tick() {
			xo = x;
			yo = y;
			zo = z;
			if (lifetime-- <= 0) {
				remove();
				if (FundoParticles.MILK_FALLING != null) {
					level.addParticle(FundoParticles.MILK_FALLING, x, y, z, xd, yd, zd);
				}
				return;
			}

			yd -= gravity;
			move(xd, yd, zd);
			if (isInsideMilk()) {
				remove();
				return;
			}
			xd *= 0.02D;
			yd *= 0.02D;
			zd *= 0.02D;
		}
	}

	private static final class Falling extends Base {
		Falling(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
			super(level, x, y, z, sprite);
			lifetime = (int) (64.0D / (random.nextFloat() * 0.8F + 0.2F));
		}

		@Override
		public void tick() {
			if (lifetime-- <= 0) {
				remove();
				return;
			}

			updateMotion();
			if (isInsideMilk()) {
				remove();
				return;
			}
			if (onGround) {
				remove();
				level.playLocalSound(x, y, z, FundoSounds.MILK_DRIP, SoundSource.BLOCKS,
						0.3F, 0.9F + random.nextFloat() * 0.2F, false);
				if (FundoParticles.MILK_DRIP_SPLASH != null) {
					level.addParticle(FundoParticles.MILK_DRIP_SPLASH, x, y, z, 0.0D, 0.0D, 0.0D);
				}
				return;
			}
			xd *= 0.98D;
			yd *= 0.98D;
			zd *= 0.98D;
		}
	}

	private static final class Splash extends WaterDropParticle {
		Splash(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,
				TextureAtlasSprite sprite) {
			super(level, x, y, z, sprite);
			gravity = 0.04F;
			setColor(MILK_RED, MILK_GREEN, MILK_BLUE);
			if (yd == 0.0D && (xd != 0.0D || zd != 0.0D)) {
				this.xd = xd;
				this.yd = 0.1D;
				this.zd = zd;
			}
		}
	}

	public static final class HangingProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public HangingProvider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
				double xd, double yd, double zd, RandomSource random) {
			return new Hanging(level, x, y, z, sprites.get(random));
		}
	}

	public static final class FallingProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public FallingProvider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
				double xd, double yd, double zd, RandomSource random) {
			return new Falling(level, x, y, z, sprites.get(random));
		}
	}

	public static final class SplashProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public SplashProvider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
				double xd, double yd, double zd, RandomSource random) {
			return new Splash(level, x, y, z, xd, yd, zd, sprites.get(random));
		}
	}
}
