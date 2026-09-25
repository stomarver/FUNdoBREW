package io.github.stomarver.fundo.client.debug;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import org.jspecify.annotations.Nullable;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.effect.InfiniteEffects;

public class EffectInfiniteTagListEntry implements DebugScreenEntry {

	public static final Identifier ID = Fundo.id("effect_infinite_tag_list");

	public static final Identifier GROUP = ID;

	private static final String LABEL = "fundo:infinite tags list:";

	@Override
	public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel,
			@Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
		List<String> lines = new ArrayList<>();
		lines.add(LABEL);
		LivingEntity subject = fundo$self();
		if (subject != null) {
			for (InfiniteEffects.TaggedEffect tagged : InfiniteEffects.infiniteTagsOf(subject)) {
				lines.add(tagged.shortForm() + " " + InfiniteEffects.stateOf(subject, tagged).label());
			}
		}
		displayer.addToGroup(GROUP, lines);
	}

	@Override
	public boolean isAllowed(boolean reducedDebugInfo) {
		return true;
	}

	@Nullable
	private static LivingEntity fundo$self() {
		Minecraft minecraft = Minecraft.getInstance();
		Player local = minecraft.player;
		if (local == null) {
			return null;
		}
		IntegratedServer server = minecraft.getSingleplayerServer();
		if (server != null) {
			ServerPlayer serverPlayer = server.getPlayerList().getPlayer(local.getUUID());
			if (serverPlayer != null) {
				return serverPlayer;
			}
		}
		return local;
	}
}
