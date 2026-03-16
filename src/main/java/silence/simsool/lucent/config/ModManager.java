package silence.simsool.lucent.config;

import silence.simsool.lucent.client.dev.examplemods.ChattingMod;
import silence.simsool.lucent.general.abstracts.Mod;
import silence.simsool.lucent.general.data.KeyBind;
import silence.simsool.lucent.general.interfaces.ModConfig;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ModManager {
	public final List<Mod> modules = new ArrayList<>();
	private final File configDirectory;

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
		if (!this.configDirectory.exists()) {
			this.configDirectory.mkdirs();
		}
	}

	public void register(Mod module) {
		modules.add(module);
	}

	public void registerAll() {
		register(new ChattingMod());
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
		if (!configDirectory.exists()) return;

		for (Mod module : modules) {
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

		for (Mod module : modules) {
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

	private String getFileName(Mod module) {
		return module.name.replaceAll("[^a-zA-Z0-9_\\-]", "") + ".json";
	}
}