package io.github.stomarver.fundo.network;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.brewing.ExtendedPotionIndex.CreativeEchoSource;

public record EchoCreativeVariantsPayload(List<CreativeEchoSource> sources) implements CustomPacketPayload {

	public static final Type<EchoCreativeVariantsPayload> TYPE = new Type<>(Fundo.id("echo_creative_variants"));

	private static final StreamCodec<RegistryFriendlyByteBuf, CreativeEchoSource> SOURCE_CODEC = StreamCodec.composite(
			Identifier.STREAM_CODEC, CreativeEchoSource::container,
			Identifier.STREAM_CODEC, CreativeEchoSource::potion,
			CreativeEchoSource::new);

	private static final StreamCodec<RegistryFriendlyByteBuf, List<CreativeEchoSource>> SOURCES_CODEC =
			ByteBufCodecs.<RegistryFriendlyByteBuf, CreativeEchoSource>list(256).apply(SOURCE_CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, EchoCreativeVariantsPayload> CODEC = StreamCodec.composite(
			SOURCES_CODEC, EchoCreativeVariantsPayload::sources, EchoCreativeVariantsPayload::new);

	public EchoCreativeVariantsPayload {
		sources = List.copyOf(sources);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
