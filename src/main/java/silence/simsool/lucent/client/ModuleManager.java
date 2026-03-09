package silence.simsool.lucent.client;

import silence.simsool.lucent.general.abstracts.Module;
import silence.simsool.lucent.general.interfaces.ModConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ModuleManager {
	public final List<Module> modules = new ArrayList<>();
	private final File configDirectory;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public ModuleManager(File configDirectory) {
		this.configDirectory = configDirectory;
		if (!this.configDirectory.exists()) {
			this.configDirectory.mkdirs();
		}
	}

	public void register(Module module) {
		modules.add(module);
	}

	public void registerAll() {
		register(new ChattingMod());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Module> T getModule(Class<T> moduleClass) {
		for (Module m : modules) {
			if (moduleClass.isAssignableFrom(m.getClass())) {
				return (T) m;
			}
		}
		return null;
	}

	public void loadConfigs() {
		if (!configDirectory.exists()) return;

		for (Module module : modules) {
			File file = new File(configDirectory, getFileName(module));
			if (!file.exists()) continue;

			try (FileReader reader = new FileReader(file)) {
				JsonObject json = GSON.fromJson(reader, JsonObject.class);
				if (json == null) continue;
				
				if (json.has("isEnabled")) {
					module.isEnabled = json.get("isEnabled").getAsBoolean();
				}

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
				e.printStackTrace();
			}
		}
	}

	public void saveConfigs() {
		if (!configDirectory.exists()) configDirectory.mkdirs();

		for (Module module : modules) {
			File file = new File(configDirectory, getFileName(module));
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
				e.printStackTrace();
			}
		}
	}

	private String getFileName(Module module) {
		return module.name.replaceAll("[^a-zA-Z0-9_\\-]", "") + ".json";
	}
}