package io.github.stomarver.fundo.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.github.stomarver.fundo.Fundo;

public record PotionStackingPayload(boolean enabled) implements CustomPacketPayload {

	public static final Type<PotionStackingPayload> TYPE = new Type<>(Fundo.id("potion_stacking"));

	public static final StreamCodec<RegistryFriendlyByteBuf, PotionStackingPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, PotionStackingPayload::enabled,
			PotionStackingPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
