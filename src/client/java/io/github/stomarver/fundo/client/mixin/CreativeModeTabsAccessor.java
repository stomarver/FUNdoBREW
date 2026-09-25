package io.github.stomarver.fundo.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

@Mixin(CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {

	@Accessor("CACHED_PARAMETERS")
	static void fundo$setCachedParameters(CreativeModeTab.ItemDisplayParameters parameters) {
		throw new AssertionError();
	}
}
