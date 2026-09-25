package io.github.stomarver.fundo.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.hitbox.PotionHitboxes;

public record PotionHitboxImpactPayload(boolean cylinder, double centerX, double verticalOrigin,
		double centerZ, double radius, double height) implements CustomPacketPayload {

	public static final Type<PotionHitboxImpactPayload> TYPE = new Type<>(Fundo.id("potion_hitbox_impact"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PotionHitboxImpactPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, PotionHitboxImpactPayload::cylinder,
			ByteBufCodecs.DOUBLE, PotionHitboxImpactPayload::centerX,
			ByteBufCodecs.DOUBLE, PotionHitboxImpactPayload::verticalOrigin,
			ByteBufCodecs.DOUBLE, PotionHitboxImpactPayload::centerZ,
			ByteBufCodecs.DOUBLE, PotionHitboxImpactPayload::radius,
			ByteBufCodecs.DOUBLE, PotionHitboxImpactPayload::height,
			PotionHitboxImpactPayload::new);

	public static PotionHitboxImpactPayload sphere(PotionHitboxes.Sphere sphere) {
		return new PotionHitboxImpactPayload(false, sphere.centerX(), sphere.centerY(), sphere.centerZ(), sphere.radius(), 0.0D);
	}

	public static PotionHitboxImpactPayload cylinder(PotionHitboxes.Cylinder cylinder) {
		return new PotionHitboxImpactPayload(true, cylinder.centerX(), cylinder.baseY(), cylinder.centerZ(),
				cylinder.radius(), cylinder.height());
	}

	public PotionHitboxes.Sphere asSphere() {
		if (cylinder) {
			throw new IllegalStateException("cylinder payload requested as sphere");
		}
		return new PotionHitboxes.Sphere(centerX, verticalOrigin, centerZ, radius);
	}

	public PotionHitboxes.Cylinder asCylinder() {
		if (!cylinder) {
			throw new IllegalStateException("sphere payload requested as cylinder");
		}
		return new PotionHitboxes.Cylinder(centerX, verticalOrigin, centerZ, radius, height);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
