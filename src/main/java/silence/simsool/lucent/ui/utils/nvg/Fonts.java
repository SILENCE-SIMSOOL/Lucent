package silence.simsool.lucent.ui.utils.nvg;

import silence.simsool.lucent.general.enums.FontList;
import silence.simsool.lucent.general.utils.UFile;
import silence.simsool.lucent.ui.font.LucentFont;

public class Fonts {
	public static LucentFont PRETENDARD_EXTRALIGHT;
	public static LucentFont PRETENDARD_LIGHT;
	public static LucentFont PRETENDARD;
	public static LucentFont PRETENDARD_MEDIUM;
	public static LucentFont PRETENDARD_SEMIBOLD;

	static {
		loadFont();
	}

	private static LucentFont getFontFromList(FontList font) throws Exception {
		return new LucentFont(
				font.toString(),
				UFile.getResourceInputStream("lucent:fonts/" + font.getName() + ".ttf")
		);
	}

	public static void loadFont() {
		try {

			PRETENDARD_EXTRALIGHT = getFontFromList(FontList.PRETENDARD_EXTRALIGHT);
			PRETENDARD_LIGHT      = getFontFromList(FontList.PRETENDARD_LIGHT);
			PRETENDARD            = getFontFromList(FontList.PRETENDARD);
			PRETENDARD_MEDIUM     = getFontFromList(FontList.PRETENDARD_MEDIUM);
			PRETENDARD_SEMIBOLD   = getFontFromList(FontList.PRETENDARD_SEMIBOLD);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}