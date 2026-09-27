package io.github.stomarver.fundo.network;

import io.github.stomarver.fundo.Fundo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

 
public record FundoFeaturePolicyPayload(boolean milkAdditions, boolean brewingAdditions,
		boolean farmersDelightCompatibility, boolean increasedPotionStacking,
		boolean milkBucketPouring, boolean milkBucketDrinking, int brewingSpeedMultiplier) implements CustomPacketPayload {

	public static final Type<FundoFeaturePolicyPayload> TYPE = new Type<>(Fundo.id("feature_policy"));

	public static final StreamCodec<RegistryFriendlyByteBuf, FundoFeaturePolicyPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, FundoFeaturePolicyPayload::milkAdditions,
			ByteBufCodecs.BOOL, FundoFeaturePolicyPayload::brewingAdditions,
			ByteBufCodecs.BOOL, FundoFeaturePolicyPayload::farmersDelightCompatibility,
			ByteBufCodecs.BOOL, FundoFeaturePolicyPayload::increasedPotionStacking,
			ByteBufCodecs.BOOL, FundoFeaturePolicyPayload::milkBucketPouring,
			ByteBufCodecs.BOOL, FundoFeaturePolicyPayload::milkBucketDrinking,
			ByteBufCodecs.VAR_INT, FundoFeaturePolicyPayload::brewingSpeedMultiplier,
			FundoFeaturePolicyPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
