package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class UWorld {

	public static boolean isChunkLoaded(BlockPos pos) {
		if (mc.level == null) return false;
		return mc.level.isLoaded(pos);
	}

	public static boolean isChunkLoaded(int x, int z) {
		if (mc.level == null) return false;
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		return mc.level.getChunkSource().hasChunk(chunkX, chunkZ);
	}

	public static int getBlockNumericID(BlockPos pos) {
		if (mc.level == null) return 0;
		BlockState state = mc.level.getBlockState(pos); if (state == null) return 0;
		return BuiltInRegistries.BLOCK.getId(state.getBlock());
	}

	public static int getBlockNumericID(int x, int y, int z) {
		return getBlockNumericID(new BlockPos(x, y, z));
	}

	public static BlockState getBlockStateAt(BlockPos pos) {
		if (mc.level == null) return Blocks.AIR.defaultBlockState();
		return mc.level.getBlockState(pos);
	}

	public static BlockState getBlockStateAt(int x, int y, int z) {
		return getBlockStateAt(x, y, z);
	}

}