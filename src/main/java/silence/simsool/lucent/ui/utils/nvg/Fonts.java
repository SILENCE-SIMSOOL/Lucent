package silence.simsool.lucent.ui.utils.nvg;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.FontList;
import silence.simsool.lucent.general.utils.UFile;
import silence.simsool.lucent.ui.font.LucentFont;


public class Fonts {
	//public static LucentFont PRETENDARD_EXTRALIGHT;
	public static LucentFont PRETENDARD_LIGHT;
	public static LucentFont PRETENDARD;
	public static LucentFont PRETENDARD_MEDIUM;
	public static LucentFont PRETENDARD_SEMIBOLD;

	private static final File FONT_DIR = new File(Lucent.mc.gameDirectory, "config/lucent/resources/fonts");
	private static final String GITHUB_RAW_URL = "https://raw.githubusercontent.com/SILENCE-SIMSOOL/FontManager/main/pretendard/";

	private static boolean initialized = false;

	public static void initAsync() {
		if (initialized) return;
		initialized = true;

		new Thread(() -> {
			try {
				if (!FONT_DIR.exists()) FONT_DIR.mkdirs();
				prepareFontFiles();
				// 다운로드 완료 후 로드
				loadFont();
				Lucent.LOG.info("All fonts initialized asynchronously.");
			} catch (Exception e) {
				Lucent.LOG.error("Failed to initialize fonts asynchronously: " + e.getMessage());
			}
		}, "Lucent-Font-Downloader").start();
	}

	private static void prepareFontFiles() {
		FontList[] fontsToLoad = {
			FontList.PRETENDARD_LIGHT,
			FontList.PRETENDARD,
			FontList.PRETENDARD_MEDIUM,
			FontList.PRETENDARD_SEMIBOLD
		};

		for (FontList font : fontsToLoad) {
			String fileName = font.getName() + ".ttf";
			File fontFile = new File(FONT_DIR, fileName);

			if (!fontFile.exists()) {
				Lucent.LOG.info("Font missing: " + fileName + ". Starting download...");
				downloadFont(fileName, fontFile);
			}
		}
	}

	private static LucentFont getFontFromList(FontList font) throws Exception {
		String fileName = font.getName() + ".ttf";
		File fontFile = new File(FONT_DIR, fileName);

		if (!fontFile.exists()) {
			throw new RuntimeException("Critical: Font file still missing after preparation - " + fileName);
		}

		return new LucentFont(font.toString(), new FileInputStream(fontFile));
	}

	private static void downloadFont(String fileName, File dest) {
		try {
			String url = GITHUB_RAW_URL + fileName;
			byte[] data = UFile.fetchUrl(url);
			if (data != null && data.length > 0) {
				Files.write(dest.toPath(), data);
			}
		} catch (Exception e) {
			Lucent.LOG.error("Failed to download font: " + fileName + " " + e.getMessage());
		}
	}

	public static void loadFont() {
		try {
			PRETENDARD_LIGHT      = getFontFromList(FontList.PRETENDARD_LIGHT);
			PRETENDARD            = getFontFromList(FontList.PRETENDARD);
			PRETENDARD_MEDIUM     = getFontFromList(FontList.PRETENDARD_MEDIUM);
			PRETENDARD_SEMIBOLD   = getFontFromList(FontList.PRETENDARD_SEMIBOLD);
		} catch (Exception e) {
			Lucent.LOG.error("Critical error during font loading: " + e.getMessage());
		}
	}

}