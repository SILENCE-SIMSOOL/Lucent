package silence.simsool.lucent.general.utils.useful;

import com.mojang.blaze3d.platform.InputConstants;

public class UKeyboard {

	public static boolean withShift() {
		return InputConstants.isKeyDown(InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(InputConstants.KEY_RSHIFT);
	}
}