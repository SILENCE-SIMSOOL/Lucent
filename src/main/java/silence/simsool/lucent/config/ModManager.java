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

import static silence.simsool.lucent.Lucent.mc;
import silence.simsool.lucent.client.dev.examplemods.ChattingMod;
import silence.simsool.lucent.client.dev.examplemods.ExampleMod;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.data.KeyBind;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.ui.theme.ThemeManager;

public class ModManager {

	public final List<Mod> modules = new ArrayList<>();
	private final File configDirectory;
	private static String currentProfile = "default";

	private File getGlobalLucentDir() {
		File f = new File(mc.gameDirectory, "config/lucent");
		if (!f.exists()) f.mkdirs();
		return f;
	}

	private File getGlobalProfilesDir() {
		File f = new File(getGlobalLucentDir(), "profiles");
		if (!f.exists()) f.mkdirs();
		return f;
	}

	public String getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(String profile) {
		currentProfile = profile;
		saveGlobalConfig();
		loadConfigs();
		LucentAPI.getHUDManager().loadAll();
	}

	public List<String> getProfiles() {
		File profilesDir = getGlobalProfilesDir();

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
		File profilesDir = getGlobalProfilesDir();
		File profileDir = new File(profilesDir, name);
		if (!profileDir.exists()) profileDir.mkdirs();
	}

	public void deleteProfile(String name) {
		if (name.equals("default")) return;
		File profilesDir = getGlobalProfilesDir();
		File profileDir = new File(profilesDir, name);
		if (profileDir.exists()) deleteDirectory(profileDir);
		if (currentProfile.equals(name)) setCurrentProfile("default");
	}

	public void renameProfile(String oldName, String newName) {
		if (oldName.equals("default") || newName.equals("default") || newName.isEmpty()) return;
		File profilesDir = getGlobalProfilesDir();
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
				if (in.peek() == JsonToken.NULL) {
					in.nextNull();
					return KeyBind.none();
				}
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

		// Ensure local profiles directory exists
		File localProfilesDir = new File(configDirectory, "profiles");
		if (!localProfilesDir.exists()) localProfilesDir.mkdirs();

		// Ensure global profiles directory and default profile exist
		File defaultProfile = new File(getGlobalProfilesDir(), "default");
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

	public boolean isModuleEnabled(Class<? extends Mod> moduleClass) {
		Mod m = getModule(moduleClass);
		return m != null && m.isEnabled;
	}

	public void setModuleEnabled(Class<? extends Mod> moduleClass, boolean enabled) {
		Mod m = getModule(moduleClass);
		if (m != null) {
			m.isEnabled = enabled;
			saveConfigs();
		}
	}

	public void loadConfigs() {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, currentProfile);
		
		// Ensure directory exists
		if (!profileDir.exists()) profileDir.mkdirs();

		File configFile = new File(profileDir, "config.json");
		boolean needsResave = false;

		if (!configFile.exists()) {
			for (Mod module : modules) {
				module.isEnabled = false;
			}
			if (!modules.isEmpty()) saveConfigs();
			return;
		}

		try (FileReader reader = new FileReader(configFile)) {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);
			if (root == null) return;
			JsonObject modulesJson = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();

			for (Mod module : modules) {
				String key = module.name;
				if (!modulesJson.has(key)) {
					module.isEnabled = false; // 기본값 false로 설정
					needsResave = true;
					continue;
				}
				
				JsonObject json = modulesJson.getAsJsonObject(key);
				if (json.has("isEnabled")) {
					module.isEnabled = json.get("isEnabled").getAsBoolean();
				} else {
					module.isEnabled = false;
					needsResave = true;
				}

				for (Field field : module.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(ModConfig.class)) {
						field.setAccessible(true);
						String fkey = field.getName();
						if (json.has(fkey)) {
							try {
								field.set(module, GSON.fromJson(json.get(fkey), field.getType()));
							} catch (Exception e) {}
						} else {
							// data not in JSON, keep variable default but mark for resave
							needsResave = true;
						}
					}
				}
			}
		} catch (Exception e) {}
		
		if (needsResave && !modules.isEmpty()) {
			saveConfigs();
		}
		
		LucentAPI.getHUDManager().loadAll();
	}

	public void saveConfigs() {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, currentProfile);
		if (!profileDir.exists()) profileDir.mkdirs();

		JsonObject root = new JsonObject();
		JsonObject modulesJson = new JsonObject();

		for (Mod module : modules) {
			JsonObject json = new JsonObject();
			json.addProperty("isEnabled", module.isEnabled);
			for (Field field : module.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(ModConfig.class)) {
					field.setAccessible(true);
					try {
						Object val = field.get(module);
						if (val != null) {
							json.add(field.getName(), GSON.toJsonTree(val));
						}
					} catch (Exception e) {}
				}
			}
			modulesJson.add(module.name, json);
		}

		root.add("modules", modulesJson);

		try (FileWriter writer = new FileWriter(new File(profileDir, "config.json"))) {
			GSON.toJson(root, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadGlobalConfig() {
		File file = new File(getGlobalLucentDir(), "lucent_global.json"); 
		if (!file.exists()) {
			saveGlobalConfig();
			return;
		}

		try (FileReader reader = new FileReader(file)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class); if (json == null) return;

			if (json.has("currentProfile")) currentProfile = json.get("currentProfile").getAsString();

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
		File f = getGlobalLucentDir();
		if (!f.exists()) f.mkdirs();
		File file = new File(f, "lucent_global.json");
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

//	private String getFileName(Mod module) {
//		return module.name.replaceAll("[^a-zA-Z0-9_\\-]", "") + ".json";
//	}

}