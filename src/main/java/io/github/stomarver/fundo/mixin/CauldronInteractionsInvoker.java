package io.github.stomarver.fundo.mixin;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CauldronInteractions.class)
public interface CauldronInteractionsInvoker {
	@Invoker("newDispatcher")
	static CauldronInteraction.Dispatcher fundo$newDispatcher(String name) {
		throw new AssertionError();
	}
}
