package silence.simsool.lucent.general.utils;

import net.minecraft.resources.Identifier;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class LucentUtils {

	public static Image createIcon(String name) throws Exception {
		return NVGRenderer.createImage("/assets/lucent/textures/icons/" + name + ".png");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(Lucent.ID, path);
	}

	public static Identifier id(String id, String path) {
		return Identifier.fromNamespaceAndPath(id, path);
	}

	public static String stripColorCodes(String text) {
		if (text == null) return "";
		return text.replace("&&", "\u0000").replaceAll("&[0-9a-fA-FrR]", "").replace("\u0000", "&");
	}

}