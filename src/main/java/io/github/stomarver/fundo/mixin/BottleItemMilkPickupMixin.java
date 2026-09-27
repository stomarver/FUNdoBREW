package io.github.stomarver.fundo.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.config.FundoFeaturePolicy;
import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.item.FundoItems;
import io.github.stomarver.fundo.sound.FundoSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

 





@Mixin(BottleItem.class)
public abstract class BottleItemMilkPickupMixin {


	@Shadow
	protected abstract ItemStack turnBottleIntoItem(ItemStack bottle, Player player, ItemStack result);

	@Inject(
			method = "use",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BottleItem;getPlayerPOVHitResult(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/ClipContext$Fluid;)Lnet/minecraft/world/phys/BlockHitResult;"),
			cancellable = true)
	private void fundo$fillBottleFromMilk(Level level, Player player, InteractionHand hand,
			CallbackInfoReturnable<InteractionResult> cir) {
		if ((Object) this != Items.GLASS_BOTTLE || FundoFluids.MILK == null) {
			return;
		}

		BlockHitResult hit = ItemRaycastInvoker.fundo$getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (hit.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockPos pos = hit.getBlockPos();
		FluidState fluid = level.getFluidState(pos);
		if (!fluid.isSource() || !fluid.getType().isSame(FundoFluids.MILK)) {
			return;
		}

		 
		if (!FundoFeaturePolicy.milkAdditions() || !level.mayInteract(player, pos)) {
			cir.setReturnValue(InteractionResult.PASS);
			return;
		}
		Item milkBottle = FundoItems.normalMilkBottle();
		if (milkBottle == null) {
			 
			 
			cir.setReturnValue(InteractionResult.PASS);
			return;
		}

		ItemStack bottle = player.getItemInHand(hand);
		level.playSound(player, player.getX(), player.getY(), player.getZ(),
				FundoSounds.MILK_BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
		level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
		ItemStack filled = turnBottleIntoItem(bottle, player, new ItemStack(milkBottle));
		cir.setReturnValue(InteractionResult.SUCCESS.heldItemTransformedTo(filled));
	}
}
