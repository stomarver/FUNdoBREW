package io.github.stomarver.fundo.client.particle;

import net.minecraft.client.particle.SingleQuadParticle;

final class MilkParticleTints {

	private MilkParticleTints() {
	}

	 
	static void applyVanillaWhite(SingleQuadParticle particle) {
		particle.setColor(1.0F, 1.0F, 1.0F);
	}
}
