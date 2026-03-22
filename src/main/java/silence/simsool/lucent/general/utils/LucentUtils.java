package silence.simsool.lucent.general.utils;

import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class LucentUtils {

	public static Image createIcon(String name) throws Exception {
		return NVGRenderer.createImage("/assets/lucent/textures/icons/" + name + ".png");
	}

}