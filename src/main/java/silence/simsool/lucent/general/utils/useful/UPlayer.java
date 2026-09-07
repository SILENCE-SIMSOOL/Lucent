package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class UPlayer {

	public static boolean isNull() {
		return mc.player == null;
	}

	public static String getName() {
		if (isNull()) return "";
		return mc.player.getPlainTextName();
	}

	public static String getName(AbstractClientPlayer player) {
		if (player == null) return "";
		return player.getPlainTextName();
	}

	public static ItemStack getItem() {
		if (isNull()) return ItemStack.EMPTY;
		return mc.player.getMainHandItem();
	}

	public static ItemStack getSubItem() {
		if (isNull()) return ItemStack.EMPTY;
		return mc.player.getOffhandItem();
	}

	public static Inventory getInventory() {
		if (isNull()) return null;
		return mc.player.getInventory();
	}

	public static float getPitch() {
		if (isNull()) return 0;
		return mc.player.getXRot();
	}

	public static float getYaw() {
		if (isNull()) return 0;
		return mc.player.getYRot();
	}

	public static void setPitch(float pitch) {
		if (isNull()) return;
		mc.player.setXRot(pitch);
	}

	public static void setYaw(float yaw) {
		if (isNull()) return;
		mc.player.setYRot(yaw);
	}

	public static BlockPos getPosotion() {
		if (isNull()) return null;
		return mc.player.blockPosition();
	}

	public static Vec3 getPosotionVec() {
		if (isNull()) return null;
		return mc.player.position();
	}

}