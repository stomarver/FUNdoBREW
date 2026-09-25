package io.github.stomarver.fundo.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.commands.EffectCommands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.effect.FundoInfiniteCommands;

@Mixin(EffectCommands.class)
public abstract class EffectCommandsMixin {

	@Inject(method = "register(Lcom/mojang/brigadier/CommandDispatcher;Lnet/minecraft/commands/CommandBuildContext;)V",
			at = @At("TAIL"))
	private static void fundo$registerFundoInfinite(CommandDispatcher<CommandSourceStack> dispatcher,
			CommandBuildContext context, CallbackInfo ci) {
		if (!FundoConfig.infinite_potions) {
			return;
		}
		CommandNode<CommandSourceStack> effectRoot = dispatcher.getRoot().getChild("effect");
		if (effectRoot == null) {
			return;
		}
		CommandNode<CommandSourceStack> give = effectRoot.getChild("give");
		CommandNode<CommandSourceStack> targets = give == null ? null : give.getChild("targets");
		CommandNode<CommandSourceStack> effectArgument = targets == null ? null : targets.getChild("effect");
		if (effectArgument != null) {
			effectArgument.addChild(FundoInfiniteCommands.node().build());
		}
	}
}
