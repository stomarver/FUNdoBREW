package io.github.stomarver.fundo.mixin;

import io.github.stomarver.fundo.config.FundoConfig;
import io.github.stomarver.fundo.config.FundoFeaturePolicy;
import io.github.stomarver.fundo.fluid.FundoFluids;
import io.github.stomarver.fundo.milklogged.MilkloggedState;
import io.github.stomarver.fundo.sound.FundoSounds;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

 
@Mixin(Item.class)
public abstract class MilkBucketUseMixin {

	@Shadow
	protected static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
		throw new AssertionError();
	}

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void fundo$useConfiguredMilkBucket(Level level, Player player, InteractionHand hand,
			CallbackInfoReturnable<InteractionResult> cir) {
		if ((Object) this != Items.MILK_BUCKET) {
			return;
		}

		ItemStack stack = player.getItemInHand(hand);
		if (FundoFeaturePolicy.milkBucketPouring() && FundoFluids.MILK != null) {
			 
			 
			BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
			if (hit.getType() == HitResult.Type.BLOCK) {
				cir.setReturnValue(fundo$pourMilk(level, player, stack, hit));
				return;
			}
		}

		 
		if (!FundoFeaturePolicy.milkBucketDrinking()) {
			cir.setReturnValue(InteractionResult.PASS);
		}
	}

	private static InteractionResult fundo$pourMilk(Level level, Player player, ItemStack milkBucket,
			BlockHitResult hit) {
		BlockPos clicked = hit.getBlockPos();
		BlockState clickedState = level.getBlockState(clicked);

		 
		 
		 
		 
		BlockPos target = MilkloggedState.canMilklog(clickedState)
				? clicked
				: clicked.relative(hit.getDirection());
		if (!level.mayInteract(player, clicked)
				|| !player.mayUseItemAt(target, hit.getDirection(), milkBucket)) {
			return InteractionResult.FAIL;
		}

		BlockState replaced = level.getBlockState(target);
		if (MilkloggedState.tryMilklog(level, target, replaced)) {
			return fundo$finishPour(level, player, milkBucket, target);
		}

		 
		 
		 
		if (!clickedState.getFluidState().isEmpty()
				|| !Block.isShapeFullBlock(clickedState.getCollisionShape(level, clicked))) {
			return InteractionResult.FAIL;
		}

		boolean replacingDirectFluid = replaced.liquid();
		if (!replacingDirectFluid && !replaced.getFluidState().isEmpty()) {
			return InteractionResult.FAIL;
		}
		if (!replacingDirectFluid && !replaced.canBeReplaced(FundoFluids.MILK)) {
			return InteractionResult.FAIL;
		}

		if (!level.isClientSide() && !replaced.isAir() && !replaced.liquid()) {
			level.destroyBlock(target, true);
		}
		if (!level.setBlock(target, FundoFluids.MILK.defaultFluidState().createLegacyBlock(), 11)) {
			return InteractionResult.FAIL;
		}
		return fundo$finishPour(level, player, milkBucket, target);
	}

	private static InteractionResult fundo$finishPour(Level level, Player player, ItemStack milkBucket, BlockPos target) {
		level.playSound(player, target, FundoSounds.MILK_BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
		level.gameEvent(player, GameEvent.FLUID_PLACE, target);
		if (player instanceof ServerPlayer serverPlayer) {
			CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, target, milkBucket);
		}
		player.awardStat(Stats.ITEM_USED.get(Items.MILK_BUCKET));
		ItemStack emptied = ItemUtils.createFilledResult(milkBucket, player,
				BucketItem.getEmptySuccessItem(milkBucket, player));
		return InteractionResult.SUCCESS.heldItemTransformedTo(emptied);
	}

}
