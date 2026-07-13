package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UWorld {

	public static boolean isNull() {
		return mc.level == null;
	}

	public static boolean isChunkLoaded(BlockPos pos) {
		if (isNull()) return false;
		return mc.level.isLoaded(pos);
	}

	public static boolean isChunkLoaded(int x, int z) {
		if (isNull()) return false;
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		return mc.level.getChunkSource().hasChunk(chunkX, chunkZ);
	}

	public static int getBlockNumericID(BlockPos pos) {
		if (isNull()) return 0;
		BlockState state = mc.level.getBlockState(pos); if (state == null) return 0;
		return BuiltInRegistries.BLOCK.getId(state.getBlock());
	}

	public static int getBlockNumericID(int x, int y, int z) {
		return getBlockNumericID(new BlockPos(x, y, z));
	}

	public static BlockState getBlockState(BlockPos pos) {
		if (isNull()) return Blocks.AIR.defaultBlockState();
		return mc.level.getBlockState(pos);
	}

	public static BlockState getBlockState(int x, int y, int z) {
		return getBlockState(new BlockPos(x, y, z));
	}

	public static boolean isAir(BlockPos pos) {
		BlockState state = getBlockState(pos);
		return state == null || state.isAir();
	}

	public static boolean isAir(int x, int y, int z) {
		return isAir(new BlockPos(x, y, z));
	}

	public static AABB getBlockBounds(BlockPos pos) {
		if (isNull()) return null;

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

	public static void destroyBlock(BlockPos pos) {
		if (getBlockState(pos).isAir()) return;
		mc.level.destroyBlock(pos, false);
	}

	public static BlockEntity getBlockEntity(BlockPos pos) {
		if (isNull()) return null;
		return mc.level.getBlockEntity(pos);
	}

	public static VoxelShape getCollisionShape(BlockPos pos) {
		if (isNull()) return null;
		return getBlockState(pos).getCollisionShape(mc.level, pos);
	}

	public static ChunkAccess getChunk(BlockPos pos) {
		if (isNull()) return null;
		return mc.level.getChunk(pos);
	}

	public static LevelChunk getChunkAt(BlockPos pos) {
		if (isNull()) return null;
		return mc.level.getChunkAt(pos);
	}

	public static LevelChunk getChunk(int x, int z) {
		if (isNull()) return null;
		return mc.level.getChunk(x, z);
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