package io.github.stomarver.fundo.brewing;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.effect.InfinitePotionMark;
import io.github.stomarver.fundo.item.FundoItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class EchoBrewingRecipe extends BrewingRecipe {

	public static EchoBrewingRecipe createRecipe() {
		return new EchoBrewingRecipe();
	}

	public static RecipeSerializer<EchoBrewingRecipe> createSerializer(EchoBrewingRecipe recipe) {
		return new RecipeSerializer<>(
				MapCodec.unit(() -> recipe),
				StreamCodec.unit(recipe));
	}

	private EchoBrewingRecipe() {

		super(
				new PotionIngredient(
						Ingredient.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION),
						Optional.empty()),
				new PotionIngredient(
						Ingredient.of(FundoItems.ECHO_DUST == null ? Items.BARRIER : FundoItems.ECHO_DUST),
						Optional.empty()),
				new ItemStackTemplate(Items.POTION));
	}

	@Override
	public boolean matches(BrewingInput input, Level level) {

		if (!FundoConfig.infinite_potions || FundoItems.ECHO_DUST == null || !input.reagent().is(FundoItems.ECHO_DUST)) {
			return false;
		}

		ExtendedPotionIndex.ensureComputed(level.recipeAccess());
		return canEchoBrew(input.input());
	}

	public static boolean canEchoBrew(ItemStack stack) {
		if (!FundoConfig.infinite_potions) {
			return false;
		}
		if (ExtendedPotionMark.isMarked(stack)) {
			return false;
		}
		if (!ExtendedPotionIndex.isExactRedstoneInput(stack)
				&& !ExtendedPotionIndex.isGlowstoneResult(stack)) {
			return false;
		}
		PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
		if (contents == null || !contents.hasEffects()) {
			return false;
		}

		for (MobEffectInstance effect : contents.getAllEffects()) {
			if (effect.isInfiniteDuration() || effect.getEffect().value().isInstantaneous()) {
				return false;
			}
		}
		return true;
	}

	public static ItemStack applyEchoBrew(ItemStack potionStack) {

		if (!canEchoBrew(potionStack)) {
			return ItemStack.EMPTY;
		}
		return assembleApprovedEcho(potionStack);
	}

	public static ItemStack applyCreativeEchoBrew(ItemStack potionStack) {
		if (!FundoConfig.infinite_potions || !ExtendedPotionIndex.isCreativeSourceAllowed(potionStack)) {
			return ItemStack.EMPTY;
		}
		return assembleApprovedEcho(potionStack);
	}

	private static ItemStack assembleApprovedEcho(ItemStack potionStack) {
		PotionContents contents = potionStack.get(DataComponents.POTION_CONTENTS);

		List<MobEffectInstance> infinite = new ArrayList<>();
		for (MobEffectInstance effect : contents.getAllEffects()) {

			MobEffectInstance instance = new MobEffectInstance(
					effect.getEffect(), -1, effect.getAmplifier(),

					true, effect.isVisible(), effect.showIcon());
			infinite.add(instance);
		}

		Optional<String> name = contents.customName()
				.or(() -> contents.potion().map(holder -> holder.value().name()));

		PotionContents echoed = new PotionContents(
				Optional.empty(),
				contents.customColor(),
				List.copyOf(infinite),
				name);

		// Keep every unrelated component intact: resource packs can select custom_data,
		// custom_model_data, item_model, and other stack components on the echoed potion.
		ItemStack result = potionStack.copyWithCount(1);
		result.set(DataComponents.POTION_CONTENTS, echoed);

		InfinitePotionMark.mark(result);
		return result;
	}

	@Override
	public ItemStack assemble(BrewingInput input) {
		return applyEchoBrew(input.input());
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
