package silence.simsool.lucent.config;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import silence.simsool.lucent.client.dev.examplemods.ChattingMod;
import silence.simsool.lucent.client.dev.examplemods.ExampleMod;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.data.KeyBind;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.ui.theme.ThemeManager;

public class ModManager {
	public final List<Mod> modules = new ArrayList<>();
	private final File configDirectory;
	private String currentProfile = "default";

	public String getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(String profile) {
		this.currentProfile = profile;
		saveGlobalConfig();
		loadConfigs();
		HUDManager.INSTANCE.loadAll();
	}

	public List<String> getProfiles() {
		File profilesDir = new File(configDirectory, "profiles");
		if (!profilesDir.exists()) profilesDir.mkdirs();

		List<String> list = new ArrayList<>();
		File[] files = profilesDir.listFiles(File::isDirectory);
		if (files != null) {
			for (File f : files) {
				String name = f.getName();
				if (!name.equals("default")) list.add(name);
			}
		}
		Collections.sort(list);
		list.add(0, "default");

		// Ensure default folder exists physically
		new File(profilesDir, "default").mkdirs();

		return list;
	}

	public void createProfile(String name) {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, name);
		if (!profileDir.exists()) profileDir.mkdirs();
	}

	public void deleteProfile(String name) {
		if (name.equals("default")) return;
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, name);
		if (profileDir.exists()) deleteDirectory(profileDir);
		if (currentProfile.equals(name)) setCurrentProfile("default");
	}

	public void renameProfile(String oldName, String newName) {
		if (oldName.equals("default") || newName.equals("default") || newName.isEmpty()) return;
		File profilesDir = new File(configDirectory, "profiles");
		File oldDir = new File(profilesDir, oldName);
		File newDir = new File(profilesDir, newName);

		if (oldDir.exists() && !newDir.exists()) oldDir.renameTo(newDir);
		if (currentProfile.equals(oldName)) {
			currentProfile = newName;
			saveGlobalConfig();
		}
	}

	private void deleteDirectory(File dir) {
		File[] children = dir.listFiles();
		if (children != null) {
			for (File child : children) {
				if (child.isDirectory()) deleteDirectory(child);
				else child.delete();
			}
		}
		dir.delete();
	}

	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(Color.class, new TypeAdapter<Color>() {
			@Override
			public void write(JsonWriter out, Color value) throws IOException {
				if (value == null) out.nullValue();
				else out.value(value.getRGB());
			}
			@Override
			public Color read(JsonReader in) throws IOException {
				if (in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				return new Color(in.nextInt(), true);
			}
		})
		.registerTypeAdapter(KeyBind.class, new TypeAdapter<KeyBind>() {
			@Override
			public void write(JsonWriter out, KeyBind value) throws IOException {
				if (value == null) { out.nullValue(); return; }
				out.beginObject();
				out.name("key").value(value.keyCode);
				out.name("mouse").value(value.mouseButton);
				out.name("mods").value(value.mods);
				out.endObject();
			}
			@Override
			public KeyBind read(JsonReader in) throws IOException {
				if (in.peek() == JsonToken.NULL) { in.nextNull(); return KeyBind.none(); }
				KeyBind kb = KeyBind.none();
				in.beginObject();
				while (in.hasNext()) {
					switch (in.nextName()) {
						case "key"   -> kb.keyCode     = in.nextInt();
						case "mouse" -> kb.mouseButton = in.nextInt();
						case "mods"  -> kb.mods        = in.nextInt();
						default      -> in.skipValue();
					}
				}
				in.endObject();
				return kb;
			}
		})
		.setPrettyPrinting().create();

	public ModManager(File configDirectory) {
		this.configDirectory = configDirectory;
		if (!this.configDirectory.exists()) this.configDirectory.mkdirs();

		// Ensure profiles directory and default profile exist
		File profilesDir = new File(configDirectory, "profiles");
		if (!profilesDir.exists()) profilesDir.mkdirs();

		File defaultProfile = new File(profilesDir, "default");
		if (!defaultProfile.exists()) defaultProfile.mkdirs();
	}

	public void register(Mod module) {
		modules.add(module);
	}

	public void registerExampleMods() {
		register(new ChattingMod());
		register(new ExampleMod());
	}

	@SuppressWarnings("unchecked")
	public <T extends Mod> T getModule(Class<T> moduleClass) {
		for (Mod m : modules) {
			if (moduleClass.isAssignableFrom(m.getClass())) {
				return (T) m;
			}
		}
		return null;
	}

	public void loadConfigs() {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, currentProfile);
		if (!profileDir.exists()) profileDir.mkdirs();

		for (Mod module : modules) {
			File file = new File(profileDir, getFileName(module));
			if (!file.exists()) {
				saveModConfig(module, file);
				continue;
			}

			try (FileReader reader = new FileReader(file)) {
				JsonObject json = GSON.fromJson(reader, JsonObject.class); if (json == null) continue;

				if (json.has("isEnabled")) module.isEnabled = json.get("isEnabled").getAsBoolean();

				for (Field field : module.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(ModConfig.class)) {
						field.setAccessible(true);
						String key = field.getName();
						if (json.has(key)) {
							try {
								Object parsedValue = GSON.fromJson(json.get(key), field.getType());
								field.set(module, parsedValue);
							} catch (Exception e) {}
						}
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	private void saveModConfig(Mod module, File file) {
		JsonObject json = new JsonObject();

		json.addProperty("isEnabled", module.isEnabled);

		for (Field field : module.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(ModConfig.class)) {
				field.setAccessible(true);
				try {
					json.add(field.getName(), GSON.toJsonTree(field.get(module)));
				} catch (Exception e) {}
			}
		}

		try (FileWriter writer = new FileWriter(file)) {
			GSON.toJson(json, writer);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public void saveConfigs() {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, currentProfile);
		if (!profileDir.exists()) profileDir.mkdirs();

		for (Mod module : modules) {
			File file = new File(profileDir, getFileName(module));
			saveModConfig(module, file);
		}
	}

	public void loadGlobalConfig() {
		File file = new File(configDirectory, "lucent_global.json"); 
		if (!file.exists()) {
			saveGlobalConfig();
			return;
		}

		try (FileReader reader = new FileReader(file)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class); if (json == null) return;

			if (json.has("currentProfile")) this.currentProfile = json.get("currentProfile").getAsString();

			if (json.has("theme")) {
				String themeName = json.get("theme").getAsString();
				ThemeManager.applyTheme(ThemeManager.findTheme(themeName));
			}

			if (json.has("openAnimation")) LucentConfig.openAnimation = json.get("openAnimation").getAsBoolean();
			if (json.has("uiBlur")) LucentConfig.uiBlur = json.get("uiBlur").getAsBoolean();
			if (json.has("uiBlurStrength")) LucentConfig.uiBlurStrength = json.get("uiBlurStrength").getAsFloat();
			if (json.has("setupLanguage")) LucentConfig.setupLanguage = json.get("setupLanguage").getAsString();

		} catch (Exception e) {}
	}

	public void saveGlobalConfig() {
		if (!configDirectory.exists()) configDirectory.mkdirs();
		File file = new File(configDirectory, "lucent_global.json");
		JsonObject json = new JsonObject();

		json.addProperty("currentProfile", currentProfile);

		if (ThemeManager.currentTheme != null) json.addProperty("theme", ThemeManager.currentTheme.name);
		json.addProperty("openAnimation", LucentConfig.openAnimation);
		json.addProperty("uiBlur", LucentConfig.uiBlur);
		json.addProperty("uiBlurStrength", LucentConfig.uiBlurStrength);
		json.addProperty("setupLanguage", LucentConfig.setupLanguage);

		try (FileWriter writer = new FileWriter(file)) {
			GSON.toJson(json, writer);
		} catch (Exception e) {}
	}

	private String getFileName(Mod module) {
		return module.name.replaceAll("[^a-zA-Z0-9_\\-]", "") + ".json";
	}
}