package io.github.stomarver.fundo.effect;

import java.util.Collection;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import io.github.stomarver.fundo.debug.ActionLogs;

public final class FundoInfiniteCommands {

	private static final SimpleCommandExceptionType ERROR_GIVE_FAILED =
			new SimpleCommandExceptionType(Component.translatable("commands.effect.give.failed"));

	private FundoInfiniteCommands() {
	}

	public static LiteralArgumentBuilder<CommandSourceStack> node() {
		return Commands.literal("fundo:infinite")
				.executes(context -> giveFundoInfinite(context, 0, false))
				.then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255))
						.executes(context -> giveFundoInfinite(context,
								IntegerArgumentType.getInteger(context, "amplifier"), false))
						.then(Commands.argument("hideParticles", BoolArgumentType.bool())
								.executes(context -> giveFundoInfinite(context,
										IntegerArgumentType.getInteger(context, "amplifier"),
										BoolArgumentType.getBool(context, "hideParticles")))));
	}

	private static int giveFundoInfinite(CommandContext<CommandSourceStack> context, int amplifier,
			boolean hideParticles) throws CommandSyntaxException {
		CommandSourceStack source = context.getSource();
		Holder<MobEffect> effect = ResourceArgument.getMobEffect(context, "effect");
		Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

		int applied = 0;
		LivingEntity last = null;
		for (Entity entity : targets) {
			if (entity instanceof LivingEntity living) {

				boolean active = InfiniteEffects.grant(living, effect, amplifier, hideParticles);
				ActionLogs.infinite(living.level(), "command | row " + InfiniteEffects.tagFor(effect, amplifier)
						+ " granted to " + ActionLogs.subject(living)
						+ (active ? " | active" : " | covered by the current effect"));
				applied++;
				last = living;
			}
		}

		if (applied == 0) {
			throw ERROR_GIVE_FAILED.create();
		}

		final Component effectName = effect.value().getDisplayName();
		if (applied == 1) {
			final LivingEntity single = last;
			source.sendSuccess(
					() -> Component.translatable("command.fundo.effect.give.fundo_infinite.single", effectName, single.getDisplayName()),
					true);
		} else {
			final int count = applied;
			source.sendSuccess(
					() -> Component.translatable("command.fundo.effect.give.fundo_infinite.multiple", effectName, count),
					true);
		}
		return applied;
	}
}
