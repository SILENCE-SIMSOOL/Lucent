package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.input.MouseButtonEvent;
import silence.simsool.lucent.ui.utils.skija.SkijaRenderer;

public class UMouse {

	private static final Set<Integer> PRESSED_BUTTONS = ConcurrentHashMap.newKeySet();

	public static void setButtonPressed(int lucentButton, boolean pressed) {
		if (pressed) PRESSED_BUTTONS.add(lucentButton);
		else PRESSED_BUTTONS.remove(lucentButton);
	}

	public static boolean isButtonDown(int lucentButton) {
		return PRESSED_BUTTONS.contains(lucentButton);
	}

	public static int getButton(MouseButtonEvent event) {
		return toLucentButton(event.button());
	}

	public static int toLucentButton(int button) {
		if (button == InputConstants.MOUSE_BUTTON_LEFT) return 0;
		if (button == InputConstants.MOUSE_BUTTON_RIGHT) return 1;
		if (button == InputConstants.MOUSE_BUTTON_MIDDLE) return 2;
		if (button >= 4) return button - 1;
		return button;
	}

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

	public static float getSkijaScaledX(float scale) {
		return (getX() / (SkijaRenderer.getStandardGuiScale() * scale));
	}

	public static float getSkijaScaledY(float scale) {
		return (getY() / (SkijaRenderer.getStandardGuiScale() * scale));
	}

	public static float getNvgScaledX(float scale) {
		return getSkijaScaledX(scale);
	}

	public static float getNvgScaledY(float scale) {
		return getSkijaScaledY(scale);
	}

	public static boolean isAreaHovered(float x, float y, float w, float h, boolean scaled) {
		float mx = getX(); float my = getY();
		if (scaled) {
			float scale = (float) SkijaRenderer.getStandardGuiScale();
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
			float scale = (float) SkijaRenderer.getStandardGuiScale();
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