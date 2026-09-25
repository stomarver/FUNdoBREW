package io.github.stomarver.fundo.effect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;

import io.github.stomarver.fundo.advancement.FundoAdvancements;
import io.github.stomarver.fundo.mixin.MobEffectInstanceAccessor;

public final class InfiniteEffects {

	public static final String TAG_PREFIX = "fundo:infinite.";

	private InfiniteEffects() {
	}

	public record TaggedEffect(Holder<MobEffect> holder, Identifier id, int amplifier) {

		public String shortForm() {
			return this.id + "." + this.amplifier;
		}
	}

	public enum State {

		ACTIVE("active"),

		COVERED("covered");

		private final String label;

		State(String label) {
			this.label = label;
		}

		public String label() {
			return this.label;
		}
	}

	public static String tagFor(Holder<MobEffect> effect, int amplifier) {
		return TAG_PREFIX + effectId(effect) + "." + amplifier;
	}

	public static String tagBase(Holder<MobEffect> effect) {
		return TAG_PREFIX + effectId(effect) + ".";
	}

	public static boolean hasAnyRow(LivingEntity entity, Holder<MobEffect> effect) {
		String base = tagBase(effect);
		for (String tag : entity.entityTags()) {
			if (tag.startsWith(base)) {
				return true;
			}
		}
		return false;
	}

	public static void stripTagsForEffect(LivingEntity entity, Holder<MobEffect> effect) {
		String base = tagBase(effect);
		for (String tag : List.copyOf(entity.entityTags())) {
			if (tag.startsWith(base)) {
				entity.removeTag(tag);
			}
		}
	}

	public static void mirrorTag(LivingEntity entity, Holder<MobEffect> effect, int amplifier) {
		stripTagsForEffect(entity, effect);
		entity.addTag(tagFor(effect, amplifier));
	}

	public static Identifier effectId(Holder<MobEffect> effect) {
		return effect.unwrapKey()
				.map(key -> key.identifier())
				.orElseGet(() -> BuiltInRegistries.MOB_EFFECT.getKey(effect.value()));
	}

	public static String describe(MobEffectInstance instance) {
		return effectId(instance.getEffect()) + "." + instance.getAmplifier();
	}

	public static Optional<TaggedEffect> parseTag(String tag) {
		if (!tag.startsWith(TAG_PREFIX)) {
			return Optional.empty();
		}
		String rest = tag.substring(TAG_PREFIX.length());
		int dot = rest.lastIndexOf('.');
		if (dot <= 0 || dot == rest.length() - 1) {
			return Optional.empty();
		}
		final int amplifier;
		try {
			amplifier = Integer.parseInt(rest.substring(dot + 1));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
		if (amplifier < 0 || amplifier > 255) {
			return Optional.empty();
		}
		String effectId = rest.substring(0, dot);
		int colon = effectId.indexOf(':');
		if (colon <= 0) {
			return Optional.empty();
		}
		final Identifier id;
		try {
			id = Identifier.fromNamespaceAndPath(effectId.substring(0, colon), effectId.substring(colon + 1));
		} catch (RuntimeException e) {
			return Optional.empty();
		}
		return BuiltInRegistries.MOB_EFFECT.get(id)
				.map(reference -> new TaggedEffect(reference, id, amplifier));
	}

	public static List<TaggedEffect> infiniteTagsOf(LivingEntity entity) {
		List<TaggedEffect> result = new ArrayList<>();
		for (String tag : entity.entityTags()) {
			parseTag(tag).ifPresent(result::add);
		}
		result.sort(Comparator.comparing(e -> e.id().toString()));
		return result;
	}

	public static @Nullable MobEffectInstance firstInfiniteInChain(@Nullable MobEffectInstance active) {
		for (MobEffectInstance layer = active; layer != null;
				layer = ((MobEffectInstanceAccessor) layer).fundo$getHiddenEffect()) {
			if (layer.isInfiniteDuration()) {
				return layer;
			}
		}
		return null;
	}

	public static List<String> stripUntaggedInfiniteShelters(LivingEntity entity) {
		List<String> razed = new ArrayList<>();
		for (MobEffectInstance active : entity.getActiveEffects()) {
			if (active.isInfiniteDuration()) {
				continue;
			}
			MobEffectInstance previous = active;
			MobEffectInstance node;
			while ((node = ((MobEffectInstanceAccessor) previous).fundo$getHiddenEffect()) != null) {
				if (node.isInfiniteDuration() && !hasAnyRow(entity, node.getEffect())) {
					((MobEffectInstanceAccessor) previous).fundo$setHiddenEffect(
							((MobEffectInstanceAccessor) node).fundo$getHiddenEffect());
					razed.add(describe(node));
					continue;
				}
				previous = node;
			}
		}
		return razed;
	}

	public static void purgeRetiredRows(LivingEntity entity) {
		List<Holder<MobEffect>> affected = new ArrayList<>();
		for (String tag : List.copyOf(entity.entityTags())) {
			if (!tag.startsWith(TAG_PREFIX)) {
				continue;
			}
			parseTag(tag).map(TaggedEffect::holder).ifPresent(affected::add);
			entity.removeTag(tag);
		}
		for (Holder<MobEffect> effect : affected.stream().distinct().toList()) {
			stripRetiredInfiniteLayers(entity, effect);
		}
	}

	private static void stripRetiredInfiniteLayers(LivingEntity entity, Holder<MobEffect> effect) {
		MobEffectInstance active = entity.getEffect(effect);
		if (active == null) {
			return;
		}
		if (active.isInfiniteDuration()) {
			MobEffectInstance replacement = firstFiniteInChain(((MobEffectInstanceAccessor) active).fundo$getHiddenEffect());
			if (replacement == null) {
				entity.removeEffect(effect);
				return;
			}
			stripInfiniteBelow(replacement);
			entity.removeEffect(effect);
			entity.addEffect(replacement, null);
			return;
		}
		stripInfiniteBelow(active);
	}

	private static @Nullable MobEffectInstance firstFiniteInChain(@Nullable MobEffectInstance layer) {
		for (MobEffectInstance current = layer; current != null;
				current = ((MobEffectInstanceAccessor) current).fundo$getHiddenEffect()) {
			if (!current.isInfiniteDuration()) {
				return current;
			}
		}
		return null;
	}

	private static void stripInfiniteBelow(MobEffectInstance root) {
		MobEffectInstance previous = root;
		MobEffectInstance node;
		while ((node = ((MobEffectInstanceAccessor) previous).fundo$getHiddenEffect()) != null) {
			if (node.isInfiniteDuration()) {
				((MobEffectInstanceAccessor) previous).fundo$setHiddenEffect(
						((MobEffectInstanceAccessor) node).fundo$getHiddenEffect());
				continue;
			}
			previous = node;
		}
	}

	public static List<MobEffectInstance> storedEndless(PotionContents contents) {
		List<MobEffectInstance> result = new ArrayList<>();
		for (MobEffectInstance stored : contents.getAllEffects()) {
			if (stored.isInfiniteDuration() && !stored.getEffect().value().isInstantaneous()) {
				result.add(stored);
			}
		}
		return result;
	}

	public static boolean mirrorArrival(LivingEntity entity, MobEffectInstance stored) {
		MobEffectInstance arrived = firstInfiniteInChain(entity.getEffect(stored.getEffect()));
		if (arrived != null && arrived.getAmplifier() == stored.getAmplifier()) {
			mirrorTag(entity, stored.getEffect(), stored.getAmplifier());
			FundoAdvancements.grantForeverYoung(entity);
			return true;
		}
		return false;
	}

	/**
	 * Grants a command-created Fundo row and immediately materializes its matching
	 * infinite instance. A row alone is not enough: an older infinite instance in
	 * the vanilla chain would otherwise prevent {@link #sync(LivingEntity)} from
	 * applying the new amplifier. The command's hide-particles choice is retained
	 * on the materialized instance.
	 */
	public static boolean grant(LivingEntity entity, Holder<MobEffect> effect, int amplifier,
			boolean hideParticles) {
		mirrorTag(entity, effect, amplifier);
		entity.addEffect(new MobEffectInstance(effect, -1, amplifier, true, !hideParticles, true), null);
		return hasActiveMatch(entity, effect, amplifier);
	}

	/**
	 * A Fundo row is active only when the visible infinite instance has its exact
	 * amplifier. A different visible infinite instance is a vanilla cover, even
	 * though it has the same effect holder and an infinite duration.
	 */
	private static boolean hasActiveMatch(LivingEntity entity, Holder<MobEffect> effect, int amplifier) {
		MobEffectInstance current = entity.getEffect(effect);
		return current != null && current.isInfiniteDuration() && current.getAmplifier() == amplifier;
	}

	public static State stateOf(LivingEntity entity, TaggedEffect tagged) {
		return hasActiveMatch(entity, tagged.holder(), tagged.amplifier())
				? State.ACTIVE
				: State.COVERED;
	}

	public static void sync(LivingEntity entity) {
		for (TaggedEffect tagged : infiniteTagsOf(entity)) {
			if (firstInfiniteInChain(entity.getEffect(tagged.holder())) == null) {
				MobEffectInstance instance = new MobEffectInstance(
						tagged.holder(), -1, tagged.amplifier(), true, true, true);
					if (entity.addEffect(instance, null)) {
						FundoAdvancements.grantForeverYoung(entity);
					}
				}
		}
	}
}
