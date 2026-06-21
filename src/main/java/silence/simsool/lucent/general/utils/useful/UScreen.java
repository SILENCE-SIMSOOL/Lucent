package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;

public class UScreen {

	public static Screen getScreen() {
		return mc.gui.screen();
	}

	public static boolean isScreenOpen() {
		return getScreen() != null;
	}

	public static boolean isScreenClose() {
		return getScreen() == null;
	}

	public static String getTitle() {
		if (getScreen() != null) {
			return getScreen().getTitle().getString();
		}
		return "";
	}

	public static boolean isChestScreen() {
		if (getScreen() instanceof AbstractContainerScreen<?> containerScreen) {
			return containerScreen.getMenu() instanceof ChestMenu;
		}
		return false;
	}

	public static boolean isInstanceOf(Class<? extends Screen> screenClass) {
		return isScreenOpen() && screenClass.isInstance(getScreen());
	}

	public static void close() {
		if (mc.player != null) {
			mc.player.closeContainer();
		}
	}

}