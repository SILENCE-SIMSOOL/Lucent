package silence.simsool.lucent.general.utils;

import static silence.simsool.lucent.Lucent.mc;

import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class UMouse {

	public static float getX() {
		return (float) mc.mouseHandler.xpos();
	}

	public static float getY() {
		return (float) mc.mouseHandler.ypos();
	}

	public static float getScaledX() {
		return (float) mc.mouseHandler.getScaledXPos(UDisplay.getWindow());
	}

	public static float getScaledY() {
		return (float) mc.mouseHandler.getScaledYPos(UDisplay.getWindow());
	}

	public static float getNvgScaledX(float scale) {
		return (getX() / (NVGRenderer.getStandardGuiScale() * scale));
	}

	public static float getNvgScaledY(float scale) {
		return (getY() / (NVGRenderer.getStandardGuiScale() * scale));
	}

	public static boolean isAreaHovered(float x, float y, float w, float h, boolean scaled) {
		float mx = getX(); float my = getY();
		if (scaled) {
			float scale = (float) NVGRenderer.getStandardGuiScale();
			return mx / scale >= x && mx / scale <= x + w && my / scale >= y && my / scale <= y + h;
		}
		return mx >= x && mx <= x + w && my >= y && my <= y + h;
	}

	public static boolean isAreaHovered(float x, float y, float w, float h) {
		return isAreaHovered(x, y, w, h, false);
	}

	public static boolean isAreaHovered(float x, float y, float w, boolean scaled) {
		float mx = getX(); float my = getY();
		if (scaled) {
			float scale = (float) NVGRenderer.getStandardGuiScale();
			return mx / scale >= x && mx / scale <= x + w && my / scale >= y;
		}
		return mx >= x && mx <= x + w && my >= y;
	}

	public static boolean isAreaHovered(float x, float y, float w) {
		return isAreaHovered(x, y, w, false);
	}

	public static int getQuadrant() {
		float mx = getX();
		float my = getY();
		int width2 = UDisplay.getScreenWidth2();
		int height2 = UDisplay.getScreenHeight2();

		if (mx >= width2) return (my >= height2) ? 4 : 2;
		else return (my >= height2) ? 3 : 1;
	}

}