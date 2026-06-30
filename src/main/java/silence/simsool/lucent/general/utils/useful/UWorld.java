package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

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

	public static BlockState getBlockState(BlockPos pos) {
		if (mc.level == null) return Blocks.AIR.defaultBlockState();
		return mc.level.getBlockState(pos);
	}

	public static BlockState getBlockState(int x, int y, int z) {
		return getBlockState(new BlockPos(x, y, z));
	}

	public static boolean isAir(BlockPos pos) {
		BlockState state = mc.level.getBlockState(pos); if (state == null) return true;
		return state == null || state.isAir();
	}

	public static boolean isAir(int x, int y, int z) {
		return isAir(new BlockPos(x, y, z));
	}

	public static AABB getBlockBounds(BlockPos pos) {
		if (mc.level == null) return null;

		VoxelShape shape = mc.level
			.getBlockState(pos)
			.getShape(mc.level, pos);

		VoxelShape encompassing = shape.singleEncompassing();

		if (encompassing.isEmpty()) return null;

		return encompassing.bounds();
	}

	public static Vec3 getCenter(BlockPos pos) {
		return Vec3.atCenterOf(pos);
	}

	public static Vec3 getBottomCenter(BlockPos pos) {
		return Vec3.atBottomCenterOf(pos);
	}

	public static Camera getCamera() {
		return mc.gameRenderer.mainCamera();
	}

	public static Vec3 getCameraPos() {
		return mc.gameRenderer.mainCamera().position();
	}

	public static float getPartialTick() {
		return mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
	}

}