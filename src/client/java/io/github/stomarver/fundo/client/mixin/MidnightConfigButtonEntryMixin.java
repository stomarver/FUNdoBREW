package io.github.stomarver.fundo.client.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.midnightdust.lib.config.ButtonEntry;
import eu.midnightdust.lib.config.EntryInfo;
import io.github.stomarver.fundo.compat.MilkBottleCompatibility;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(ButtonEntry.class)
public abstract class MidnightConfigButtonEntryMixin {

	private static final int ICON_X = 12;
	private static final int TEXT_X = 34;

	@Shadow @Final public List<AbstractWidget> buttons;
	@Shadow @Final public EntryInfo info;
	@Shadow public MultiLineTextWidget title;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void fundo$styleRegistryRows(List<AbstractWidget> buttons, Component text, EntryInfo info,
			CallbackInfo ci) {
		if (info == null || title == null) {
			return;
		}

		Identifier model = fundo$itemModel(info.fieldName);
		if (model != null) {

			title.setX(TEXT_X);
			int rightEdge = buttons.isEmpty() ? TEXT_X + 180 : buttons.getFirst().getX() - 16;
			title.setMaxWidth(Math.max(1, rightEdge - TEXT_X));
		}

		if ("quality_of_life".equals(info.fieldName)) {

			title.setMessage(Component.translatable(info.translationKey)
					.setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withBold(true)));
		} else if ("compatibility".equals(info.fieldName)) {

			title.setMessage(Component.translatable(info.translationKey)
					.setStyle(Style.EMPTY.withColor(0xFFFFAA00).withBold(true)));
		} else if ("visual".equals(info.fieldName)) {

			title.setMessage(Component.translatable(info.translationKey)
					.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE).withBold(true)));
		} else if ("features_restart_notice".equals(info.fieldName)
				|| "compatibility_restart_notice".equals(info.fieldName)) {

			title.setMessage(Component.translatable(info.translationKey)
					.setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
		}
	}

	@Inject(method = "extractContent", at = @At("TAIL"))
	private void fundo$renderStableRegistryIcon(GuiGraphicsExtractor graphics, int mouseX, int mouseY,
			boolean hovered, float tickProgress, CallbackInfo ci) {
		if (info == null || title == null) {
			return;
		}
		Identifier model = fundo$itemModel(info.fieldName);
		if (model == null) {
			return;
		}

		ItemStack preview = new ItemStack(Holder.direct(
				Items.PAPER,
				DataComponentMap.builder().set(DataComponents.ITEM_MODEL, model).build()));

		graphics.fakeItem(preview, ICON_X, title.getY() - 3);
	}

	private static Identifier fundo$itemModel(String fieldName) {
		return switch (fieldName) {
			case "milk_changes" -> MilkBottleCompatibility.usesFarmersDelightBottle()
					? MilkBottleCompatibility.FARMERS_DELIGHT_MILK_BOTTLE
					: Identifier.fromNamespaceAndPath("fundo", "milk_bottle");
			case "infinite_potions" -> Identifier.fromNamespaceAndPath("fundo", "echo_dust");
			default -> null;
		};
	}
}
