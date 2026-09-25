package io.github.stomarver.fundo.client.debug;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import org.jspecify.annotations.Nullable;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.effect.InfiniteEffects;

public class EffectInfiniteTagListOnEntry implements DebugScreenEntry {

	public static final Identifier ID = Fundo.id("effect_infinite_tag_list_on");

	public static final Identifier GROUP = ID;

	private static final String LABEL_PREFIX = "fundo:infinite tags list on ";

	@Override
	public void display(DebugScreenDisplayer displayer, @Nullable Level serverOrClientLevel,
			@Nullable LevelChunk clientChunk, @Nullable LevelChunk serverChunk) {
		LivingEntity subject = fundo$target();
		if (subject == null) {
			return;
		}
		List<String> lines = new ArrayList<>();
		lines.add(LABEL_PREFIX + fundo$localizedName(subject) + ":");
		for (InfiniteEffects.TaggedEffect tagged : InfiniteEffects.infiniteTagsOf(subject)) {
			lines.add(tagged.shortForm() + " " + InfiniteEffects.stateOf(subject, tagged).label());
		}
		displayer.addToGroup(GROUP, lines);
	}

	@Override
	public boolean isAllowed(boolean reducedDebugInfo) {
		return true;
	}

	@Nullable
	private static LivingEntity fundo$target() {
		Minecraft minecraft = Minecraft.getInstance();
		Entity picked = minecraft.crosshairPickEntity;
		if (picked == null) {
			return null;
		}
		IntegratedServer server = minecraft.getSingleplayerServer();
		if (server != null && minecraft.level != null) {
			ServerLevel serverLevel = server.getLevel(minecraft.level.dimension());
			if (serverLevel != null && serverLevel.getEntity(picked.getUUID()) instanceof LivingEntity living) {
				return living;
			}
		}
		return picked instanceof LivingEntity living ? living : null;
	}

	private static String fundo$localizedName(LivingEntity entity) {
		Component custom = entity.getCustomName();
		if (custom != null) {
			return custom.getString();
		}
		Identifier typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
		return Language.getInstance().getOrDefault("entity." + typeId.getNamespace() + "." + typeId.getPath());
	}
}
