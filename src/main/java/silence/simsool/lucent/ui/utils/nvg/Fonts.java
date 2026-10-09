package silence.simsool.lucent.ui.utils.nvg;

import java.io.File;
import java.nio.file.Files;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.FontList;
import silence.simsool.lucent.general.utils.OSUtils;
import silence.simsool.lucent.general.utils.useful.UFile;
import silence.simsool.lucent.ui.font.LucentFont;

public class Fonts {
	//public static LucentFont PRETENDARD_EXTRALIGHT;
	public static volatile LucentFont PRETENDARD_LIGHT;
	public static volatile LucentFont PRETENDARD;
	public static volatile LucentFont PRETENDARD_MEDIUM;
	public static volatile LucentFont PRETENDARD_SEMIBOLD;
	public static volatile LucentFont PRETENDARD_BOLD;
	public static volatile LucentFont PRETENDARD_EXTRABOLD;
	public static volatile LucentFont MATERIAL_ICONS;
	public static volatile LucentFont MATERIAL_ICONS_ROUND;

	private static final File FONT_DIR = new File(OSUtils.getLucentDir(), "resources/fonts");
	private static final String GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com/SILENCE-SIMSOOL/FontManager/main/";

	private static boolean initialized = false;
	private static volatile int revision;

	public static int getRevision() {
		return revision;
	}

	public static synchronized void initAsync() {
		if (initialized) return;
		initialized = true;

		new Thread(() -> {
			try {
				if (!FONT_DIR.exists()) FONT_DIR.mkdirs();
				loadFont();
				prepareFontFiles();
				Lucent.LOG.info("All fonts initialized asynchronously.");
			} catch (Exception e) {
				Lucent.LOG.error("Failed to initialize fonts asynchronously: " + e.getMessage());
			}
		}, "Lucent-Font-Downloader").start();
	}

	private static void prepareFontFiles() {
		FontList[] fontsToLoad = {
			FontList.PRETENDARD,
			FontList.MATERIAL_ICONS,
			FontList.MATERIAL_ICONS_ROUND,
			FontList.PRETENDARD_MEDIUM,
			FontList.PRETENDARD_SEMIBOLD,
			FontList.PRETENDARD_LIGHT,
			FontList.PRETENDARD_BOLD,
			FontList.PRETENDARD_EXTRABOLD
		};

		for (FontList font : fontsToLoad) {
			String fileName = font.getFileName();
			File fontFile = new File(FONT_DIR, fileName);

			if (!fontFile.exists()) {
				Lucent.LOG.info("Font missing: " + fileName + ". Starting download...");
				downloadFont(font.getRelativePath(), fontFile);
				loadFont(font);
			}
		}
	}

	private static LucentFont getFontFromList(FontList font) throws Exception {
		String fileName = font.getFileName();
		File fontFile = new File(FONT_DIR, fileName);

		if (!fontFile.exists()) {
			throw new RuntimeException("Critical: Font file still missing after preparation - " + fileName);
		}

		return new LucentFont(font.toString(), fontFile.toPath());
	}

	private static void downloadFont(String relativePath, File dest) {
		try {
			String url = GITHUB_RAW_BASE_URL + relativePath;
			byte[] data = UFile.fetchUrl(url);
			if (data != null && data.length > 0) {
				Files.write(dest.toPath(), data);
			}
		} catch (Exception e) {
			Lucent.LOG.error("Failed to download font: " + relativePath + " " + e.getMessage());
		}
	}

	public static void loadFont() {
		loadFont(FontList.PRETENDARD);
		loadFont(FontList.MATERIAL_ICONS);
		loadFont(FontList.MATERIAL_ICONS_ROUND);
		loadFont(FontList.PRETENDARD_MEDIUM);
		loadFont(FontList.PRETENDARD_SEMIBOLD);
		loadFont(FontList.PRETENDARD_LIGHT);
		loadFont(FontList.PRETENDARD_BOLD);
		loadFont(FontList.PRETENDARD_EXTRABOLD);
	}

	private static void loadFont(FontList font) {
		if (!new File(FONT_DIR, font.getFileName()).isFile()) return;
		try {
			LucentFont loaded = getFontFromList(font);
			switch (font) {
				case PRETENDARD -> PRETENDARD = loaded;
				case MATERIAL_ICONS -> MATERIAL_ICONS = loaded;
				case MATERIAL_ICONS_ROUND -> MATERIAL_ICONS_ROUND = loaded;
				case PRETENDARD_MEDIUM -> PRETENDARD_MEDIUM = loaded;
				case PRETENDARD_SEMIBOLD -> PRETENDARD_SEMIBOLD = loaded;
				case PRETENDARD_LIGHT -> PRETENDARD_LIGHT = loaded;
				case PRETENDARD_BOLD -> PRETENDARD_BOLD = loaded;
				case PRETENDARD_EXTRABOLD -> PRETENDARD_EXTRABOLD = loaded;
				default -> { return; }
			}
			revision++;
		} catch (Exception e) {
			Lucent.LOG.error("Failed to load font " + font + ": " + e.getMessage());
		}
	}

}