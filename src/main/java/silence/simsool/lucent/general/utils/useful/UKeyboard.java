package silence.simsool.lucent.general.utils.useful;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

public class UKeyboard {

	public static boolean withShift() {
		//Window window = UDisplay.getWindow();
		//return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
		return InputConstants.isKeyDown( UDisplay.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT);
	}

}