package io.github.stomarver.fundo.block;

import io.github.stomarver.fundo.sound.FundoSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

 






public final class MilkIceBlock extends IceBlock {
	private static final VoxelShape HONEY_HEIGHT_COLLISION = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

	public MilkIceBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
			CollisionContext context) {
		return HONEY_HEIGHT_COLLISION;
	}

	@Override
	protected void melt(BlockState state, Level level, BlockPos pos) {
		if (level.environmentAttributes().getValue(
				net.minecraft.world.attribute.EnvironmentAttributes.WATER_EVAPORATES, pos)) {
			level.removeBlock(pos, false);
			return;
		}

		level.setBlockAndUpdate(pos, IceBlock.meltsInto());
		level.neighborChanged(pos, IceBlock.meltsInto().getBlock(), null);
		level.playSound(null, pos, FundoSounds.MILK_ICE_MELT, SoundSource.BLOCKS, 0.8F, 1.0F);
		level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
	}
}
