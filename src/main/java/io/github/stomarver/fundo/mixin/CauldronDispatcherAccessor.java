package io.github.stomarver.fundo.mixin;

import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CauldronInteraction.Dispatcher.class)
public interface CauldronDispatcherAccessor {
	@Accessor("items")
	Map<Item, CauldronInteraction> fundo$items();
}
