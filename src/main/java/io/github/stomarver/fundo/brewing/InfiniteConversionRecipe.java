package io.github.stomarver.fundo.brewing;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.effect.InfinitePotionMark;

import java.util.Optional;

public final class InfiniteConversionRecipe extends BrewingRecipe {

	private final Item from;
	private final Item reagentItem;
	private final Item to;

	public static final MapCodec<InfiniteConversionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(
					BuiltInRegistries.ITEM.byNameCodec().fieldOf("input").forGetter(recipe -> recipe.from),
					BuiltInRegistries.ITEM.byNameCodec().fieldOf("reagent").forGetter(recipe -> recipe.reagentItem),
					BuiltInRegistries.ITEM.byNameCodec().fieldOf("output").forGetter(recipe -> recipe.to))
			.apply(instance, InfiniteConversionRecipe::new));

	private static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_STREAM =
			ByteBufCodecs.registry(Registries.ITEM);

	public static final StreamCodec<RegistryFriendlyByteBuf, InfiniteConversionRecipe> STREAM_CODEC =
			StreamCodec.composite(
					ITEM_STREAM, recipe -> recipe.from,
					ITEM_STREAM, recipe -> recipe.reagentItem,
					ITEM_STREAM, recipe -> recipe.to,
					InfiniteConversionRecipe::new);

	public static final RecipeSerializer<InfiniteConversionRecipe> SERIALIZER =
			new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	public InfiniteConversionRecipe(Item from, Item reagent, Item to) {
		super(
				new PotionIngredient(Ingredient.of(from), Optional.empty()),
				new PotionIngredient(Ingredient.of(reagent), Optional.empty()),
				new ItemStackTemplate(to));
		this.from = from;
		this.reagentItem = reagent;
		this.to = to;
	}

	@Override
	public boolean matches(BrewingInput input, Level level) {
		if (!FundoConfig.infinite_potions || !input.reagent().is(this.reagentItem) || !input.input().is(this.from)) {
			return false;
		}

		return InfinitePotionMark.isMarked(input.input());
	}

	@Override
	public ItemStack assemble(BrewingInput input) {

		// transmuteCopy carries the complete component patch into the new potion container.
		return input.input().transmuteCopy(this.to, 1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeSerializer<BrewingRecipe> getSerializer() {
		return (RecipeSerializer<BrewingRecipe>) (RecipeSerializer<?>) SERIALIZER;
	}

	@Override
	public RecipeType<BrewingRecipe> getType() {
		return RecipeType.BREWING;
	}
}
