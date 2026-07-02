package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;

public class UPlayer {

	public static String getName() {
		if (mc.player == null) return "";
		return mc.player.getPlainTextName();
	}

	public static String getName(AbstractClientPlayer player) {
		if (player == null) return "";
		return player.getPlainTextName();
	}

	public static ItemStack getItem() {
		if (mc.player == null) return null;
		return mc.player.getMainHandItem();
	}

	public static ItemStack getSubItem() {
		if (mc.player == null) return null;
		return mc.player.getOffhandItem();
	}

}