package io.github.stomarver.fundo.client.mixin;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DebugScreenEntries.class)
public interface DebugScreenEntriesInvoker {

	@Invoker("register")
	static Identifier fundo$register(Identifier id, DebugScreenEntry entry) {
		throw new AssertionError();
	}
}
