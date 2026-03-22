package silence.simsool.lucent.general.utils;

import static silence.simsool.lucent.Lucent.mc;

import com.mojang.blaze3d.platform.Window;

public class UDisplay {

	public static Window getWindow() {
		return mc.getWindow();
	}

	public static int getWidth() {
		return getWindow().getWidth();
	}

	public static int getHeight() {
		return getWindow().getHeight();
	}

	public static int getWidth2() {
		return getWindow().getWidth() / 2;
	}

	public static int getHeight2() {
		return getWindow().getHeight() / 2;
	}

	public static int getScreenWidth() {
		return getWindow().getScreenWidth();
	}

	public static int getScreenHeight() {
		return getWindow().getScreenHeight();
	}

	public static int getScreenWidth2() {
		return getWindow().getScreenWidth() / 2;
	}

	public static int getScreenHeight2() {
		return getWindow().getScreenHeight() / 2;
	}

	public static int getGuiScaledWidth() {
		return getWindow().getGuiScaledWidth();
	}

	public static int getGuiScaledHeight() {
		return getWindow().getGuiScaledHeight();
	}

	public static int getGuiScaledWidth2() {
		return getWindow().getGuiScaledWidth() / 2;
	}

	public static int getGuiScaledHeight2() {
		return getWindow().getGuiScaledHeight() / 2;
	}


	public static int getGuiScale() {
		return getWindow().getGuiScale();
	}

	public static int getX() {
		return getWindow().getX();
	}

	public static int getY() {
		return getWindow().getY();
	}

	public static int getRefreshRate() {
		return getWindow().getRefreshRate();
	}

	public static boolean isFullscreen() {
		return getWindow().isFullscreen();
	}

	public static boolean isMinimized() {
		return getWindow().isMinimized();
	}

	public static void close() {
		getWindow().close();
	}

	public static void toggleFullScreen() {
		getWindow().toggleFullScreen();
	}

	public static void setGuiScale(int scale) {
		getWindow().setGuiScale(scale);
	}

	public static boolean isDebugScreen() {
		return mc.gui.getDebugOverlay().showDebugScreen();
	}

}