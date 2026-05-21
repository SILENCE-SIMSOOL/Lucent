package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;

public class UScreen {

	public static Screen getScreen() {
		return mc.screen;
	}

	public static boolean isScreenOpen() {
		return mc.screen != null;
	}

	public static String getTitle() {
		if (mc.screen != null) {
			return mc.screen.getTitle().getString();
		}
		return "";
	}

	public static boolean isChestScreen() {
		if (mc.screen instanceof AbstractContainerScreen<?> containerScreen) {
			return containerScreen.getMenu() instanceof ChestMenu;
		}
		return false;
	}

	public static boolean isInstanceOf(Class<? extends Screen> screenClass) {
		return mc.screen != null && screenClass.isInstance(mc.screen);
	}

	public static void closeScreen() {
		if (mc.player != null) {
			mc.player.closeContainer();
		}
	}

}