package silence.simsool.lucent.general.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;
import silence.simsool.lucent.config.LucentConfig;

public class L10n {
	private static final Map<String, String> translations = new HashMap<>();
	private static final Map<String, String> fallbackTranslations = new HashMap<>();
	private static String loadedLanguage = "";

	public static void load() {
		String lang = getLangCode(LucentConfig.setupLanguage);
		if (lang.equals(loadedLanguage)) return;

		translations.clear();
		loadLangFile("en_us", fallbackTranslations); // Always load English as fallback
		loadLangFile(lang, translations);
		loadedLanguage = lang;
	}

	private static void loadLangFile(String code, Map<String, String> targetMap) {
		try (InputStream is = L10n.class.getResourceAsStream("/assets/lucent/lang/" + code + ".json")) {
			if (is != null) loadFromStream(is, targetMap);
		} catch (Exception ignored) {}

		try {
			FabricLoader.getInstance().getAllMods().forEach(mod -> {
				String modId = mod.getMetadata().getId();
				if (modId.equals("lucent")) return;

				// assets/<modid>/lucent/lang/<code.json>
				mod.findPath("assets/" + modId + "/lucent/lang/" + code + ".json").ifPresent(path -> {
					try (InputStream is = Files.newInputStream(path)) {
						loadFromStream(is, targetMap);
					} catch (Exception ignored) {}
				});
			});
		} catch (Exception ignored) {}
	}

	private static void loadFromStream(InputStream is, Map<String, String> targetMap) {
		try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
			JsonObject json = new Gson().fromJson(reader, JsonObject.class);
			if (json != null) json.entrySet().forEach(entry -> targetMap.put(entry.getKey(), entry.getValue().getAsString()));
		} catch (Exception ignored) {}
	}

	public static String translate(String key) {
		if (translations.isEmpty()) load();
		if (translations.containsKey(key)) return translations.get(key);
		if (fallbackTranslations.containsKey(key)) return fallbackTranslations.get(key);
		return key;
	}

	private static String getLangCode(String languageName) {
		return switch (languageName) {
			case "Korean" -> "ko_kr";
			case "Chinese" -> "zh_cn";
			case "Japanese" -> "ja_jp";
			case "Russian" -> "ru_ru";
			default -> "en_us";
		};
	}
}