package io.github.stomarver.fundo.block;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.compat.MilkBottleCompatibility;
import io.github.stomarver.fundo.config.FundoFeaturePolicy;
import io.github.stomarver.fundo.item.FundoItems;
import io.github.stomarver.fundo.mixin.CauldronDispatcherAccessor;
import io.github.stomarver.fundo.sound.FundoSounds;
import io.github.stomarver.fundo.mixin.CauldronInteractionsInvoker;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Map;

public final class FundoCauldronInteractions {

	public static final CauldronInteraction.Dispatcher MILK =
			CauldronInteractionsInvoker.fundo$newDispatcher("milk");

	private static boolean installed;
	@org.jetbrains.annotations.Nullable
	private static Item installedMilkBottle;

	private static void install(Item milkBottle) {

		Map<Item, CauldronInteraction> milk = items(MILK);

		milk.put(Items.MILK_BUCKET, (state, level, pos, player, hand, stack) -> {
			if (state.getValue(LayeredCauldronBlock.LEVEL) == 3) {

				return InteractionResult.TRY_WITH_EMPTY_HAND;
			}
			if (!level.isClientSide()) {
				Item item = stack.getItem();
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, 3), Block.UPDATE_ALL);
				level.playSound(null, pos, FundoSounds.MILK_BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			}
			return InteractionResult.SUCCESS;
		});

		milk.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {
			if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) {
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			}
			if (!level.isClientSide()) {
				Item item = stack.getItem();
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.MILK_BUCKET)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
				level.playSound(null, pos, FundoSounds.MILK_BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
			}
			return InteractionResult.SUCCESS;
		});

		milk.put(Items.GLASS_BOTTLE, (state, level, pos, player, hand, stack) -> {
			if (!FundoFeaturePolicy.milkAdditions()) {
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			}
			if (!level.isClientSide()) {
				Item item = stack.getItem();
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(milkBottle)));
				player.awardStat(Stats.USE_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				LayeredCauldronBlock.lowerFillLevel(state, level, pos);
				level.playSound(null, pos, FundoSounds.MILK_BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
			}
			return InteractionResult.SUCCESS;
		});

		 
		milk.put(milkBottle, (state, level, pos, player, hand, stack) -> {
			int fillLevel = state.getValue(LayeredCauldronBlock.LEVEL);
			if (fillLevel == 3) {
				return InteractionResult.TRY_WITH_EMPTY_HAND;
			}
			if (!level.isClientSide()) {
				Item item = stack.getItem();
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, fillLevel + 1), Block.UPDATE_ALL);
				level.playSound(null, pos, FundoSounds.MILK_BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			}
			return InteractionResult.SUCCESS;
		});

		items(CauldronInteractions.EMPTY).put(Items.MILK_BUCKET, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) {
				Item item = stack.getItem();
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlock(pos, FundoBlocks.MILK_CAULDRON.defaultBlockState()
						.setValue(LayeredCauldronBlock.LEVEL, 3), Block.UPDATE_ALL);
				level.playSound(null, pos, FundoSounds.MILK_BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			}
			return InteractionResult.SUCCESS;
		});

		 
		items(CauldronInteractions.EMPTY).put(milkBottle, (state, level, pos, player, hand, stack) -> {
			if (!level.isClientSide()) {
				Item item = stack.getItem();
				player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
				player.awardStat(Stats.FILL_CAULDRON);
				player.awardStat(Stats.ITEM_USED.get(item));
				level.setBlock(pos, FundoBlocks.MILK_CAULDRON.defaultBlockState()
						.setValue(LayeredCauldronBlock.LEVEL, 1), Block.UPDATE_ALL);
				level.playSound(null, pos, FundoSounds.MILK_BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
			}
			return InteractionResult.SUCCESS;
		});
	}

	private FundoCauldronInteractions() {
	}

	private static Map<Item, CauldronInteraction> items(CauldronInteraction.Dispatcher dispatcher) {
		return ((CauldronDispatcherAccessor) dispatcher).fundo$items();
	}

	 




	public static synchronized void register() {
		refresh();
	}

	public static synchronized void refresh() {
		Item milkBottle = FundoItems.normalMilkBottle();
		if (milkBottle == null) {
			if (MilkBottleCompatibility.usesFarmersDelightBottle()) {
				Fundo.LOGGER.warn("Farmer's Delight is loaded, but {} is not registered yet; Milk Cauldron interactions will retry when the policy refreshes",
						MilkBottleCompatibility.FARMERS_DELIGHT_MILK_BOTTLE);
			}
			return;
		}
		if (installed && installedMilkBottle == milkBottle) {
			return;
		}
		if (installedMilkBottle != null) {
			items(MILK).remove(installedMilkBottle);
			items(CauldronInteractions.EMPTY).remove(installedMilkBottle);
		}
		install(milkBottle);
		installedMilkBottle = milkBottle;
		installed = true;
	}
}
