package io.github.stomarver.fundo.client.mixin;

import java.util.LinkedHashMap;

import eu.midnightdust.lib.config.EntryInfo;
import eu.midnightdust.lib.config.MidnightConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MidnightConfig.class)
public interface MidnightConfigEntriesAccessor {

	@Accessor("entries")
	static LinkedHashMap<String, EntryInfo> fundo$entries() {
		throw new AssertionError();
	}
}
