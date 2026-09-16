package silence.simsool.lucent.ui.utils.skija;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.Typeface;
import io.github.humbleui.skija.paragraph.FontCollection;
import io.github.humbleui.skija.paragraph.TypefaceFontProvider;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.FontList;
import silence.simsool.lucent.general.utils.OSUtils;
import silence.simsool.lucent.general.utils.useful.UFile;
import silence.simsool.lucent.ui.font.LucentFont;

public class Fonts {
	//public static LucentFont PRETENDARD_EXTRALIGHT;
	public static LucentFont PRETENDARD_LIGHT;
	public static LucentFont PRETENDARD;
	public static LucentFont PRETENDARD_MEDIUM;
	public static LucentFont PRETENDARD_SEMIBOLD;
	public static LucentFont PRETENDARD_BOLD;
	public static LucentFont PRETENDARD_EXTRABOLD;
	public static LucentFont MATERIAL_ICONS;
	public static LucentFont MATERIAL_ICONS_ROUND;

	private static final File FONT_DIR = new File(OSUtils.getLucentDir(), "resources/fonts");
	private static final String GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com/SILENCE-SIMSOOL/FontManager/main/";

	private static final Map<LucentFont, Typeface> typefaceCache = new ConcurrentHashMap<>();
	private static TypefaceFontProvider fontProvider;
	private static FontCollection fontCollection;

	private static boolean initialized = false;

	public static synchronized TypefaceFontProvider getFontProvider() {
		if (fontProvider == null) {
			fontProvider = new TypefaceFontProvider();
		}
		return fontProvider;
	}

	public static synchronized FontCollection getFontCollection() {
		if (fontCollection == null) {
			fontCollection = new FontCollection();
			fontCollection.setDefaultFontManager(FontMgr.getDefault());
			fontCollection.setAssetFontManager(getFontProvider());
			fontCollection.setEnableFallback(true);
		}
		return fontCollection;
	}

	public static void initAsync() {
		if (initialized) return;
		initialized = true;

		new Thread(() -> {
			try {
				if (!FONT_DIR.exists()) FONT_DIR.mkdirs();
				prepareFontFiles();
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
			FontList.PRETENDARD_SEMIBOLD,
			FontList.PRETENDARD_BOLD,
			FontList.PRETENDARD_EXTRABOLD,
			FontList.MATERIAL_ICONS,
			FontList.MATERIAL_ICONS_ROUND
		};

		for (FontList font : fontsToLoad) {
			String fileName = font.getFileName();
			File fontFile = new File(FONT_DIR, fileName);

			if (!fontFile.exists()) {
				Lucent.LOG.info("Font missing: " + fileName + ". Starting download...");
				downloadFont(font.getRelativePath(), fontFile);
			}
		}
	}

	private static LucentFont getFontFromList(FontList font) throws Exception {
		String fileName = font.getFileName();
		File fontFile = new File(FONT_DIR, fileName);

		if (!fontFile.exists()) {
			throw new RuntimeException("Critical: Font file still missing after preparation - " + fileName);
		}

		return new LucentFont(font.toString(), new FileInputStream(fontFile));
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
		try {
			PRETENDARD_LIGHT      = getFontFromList(FontList.PRETENDARD_LIGHT);
			PRETENDARD            = getFontFromList(FontList.PRETENDARD);
			PRETENDARD_MEDIUM     = getFontFromList(FontList.PRETENDARD_MEDIUM);
			PRETENDARD_SEMIBOLD   = getFontFromList(FontList.PRETENDARD_SEMIBOLD);
			PRETENDARD_BOLD       = getFontFromList(FontList.PRETENDARD_BOLD);
			PRETENDARD_EXTRABOLD  = getFontFromList(FontList.PRETENDARD_EXTRABOLD);
			MATERIAL_ICONS        = getFontFromList(FontList.MATERIAL_ICONS);
			MATERIAL_ICONS_ROUND  = getFontFromList(FontList.MATERIAL_ICONS_ROUND);
		} catch (Exception e) {
			Lucent.LOG.error("Critical error during font loading: " + e.getMessage());
		}
	}

	public static void registerTypeface(Typeface typeface, String family) {
		if (typeface != null && family != null) {
			getFontProvider().registerTypeface(typeface, family);
		}
	}

	public static Typeface getTypeface(LucentFont font) {
		if (font == null) return null;
		return typefaceCache.computeIfAbsent(font, f -> {
			try {
				byte[] bytes = f.getBytes();
				if (bytes == null || bytes.length == 0) return null;
				Typeface tf = FontMgr.getDefault().makeFromData(Data.makeFromBytes(bytes));
				if (tf != null) {
					getFontProvider().registerTypeface(tf, f.getName());
				}
				return tf;
			} catch (Exception e) {
				return null;
			}
		});
	}

}