package io.github.stomarver.fundo.migration;

import io.github.stomarver.fundo.Fundo;
import io.github.stomarver.fundo.block.FundoBlocks;
import io.github.stomarver.fundo.config.FundoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public final class FundoContentMigration {

	private FundoContentMigration() {
	}

	public static void register() {
		ServerChunkEvents.CHUNK_LOAD.register((level, chunk, newlyGenerated) -> {
			if (!FundoConfig.milk_changes) {
				retireMilkCauldrons(level, chunk);
			}
		});
	}

	private static void retireMilkCauldrons(ServerLevel level, LevelChunk chunk) {
		LevelChunkSection[] sections = chunk.getSections();
		int changed = 0;
		int minX = chunk.getPos().getMinBlockX();
		int minZ = chunk.getPos().getMinBlockZ();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
			LevelChunkSection section = sections[sectionIndex];
			if (section == null || !section.maybeHas(state -> state.is(FundoBlocks.MILK_CAULDRON))) {
				continue;
			}

			int minY = level.getSectionYFromSectionIndex(sectionIndex) << 4;
			for (int localY = 0; localY < 16; localY++) {
				for (int localZ = 0; localZ < 16; localZ++) {
					for (int localX = 0; localX < 16; localX++) {
						if (!section.getBlockState(localX, localY, localZ).is(FundoBlocks.MILK_CAULDRON)) {
							continue;
						}
						pos.set(minX + localX, minY + localY, minZ + localZ);
						level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), Block.UPDATE_ALL);
						changed++;
					}
				}
			}
		}

		if (changed > 0) {
			Fundo.LOGGER.info("Retired {} legacy milk cauldron(s) in loaded chunk {}", changed, chunk.getPos());
		}
	}
}
