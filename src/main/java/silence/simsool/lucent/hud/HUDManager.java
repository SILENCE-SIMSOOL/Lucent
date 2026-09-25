package silence.simsool.lucent.hud;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.general.enums.Align;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.general.utils.useful.UScreen;
import silence.simsool.lucent.skija.compositor.SkijaCompositor;
import silence.simsool.lucent.ui.screens.EditHUDScreen;
import silence.simsool.lucent.ui.utils.URender;
import silence.simsool.lucent.ui.utils.skija.SkijaRenderer;

public class HUDManager {

	private final List<LucentHUD> huds = new ArrayList<>();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private boolean cachedHasSkija = false;
	private List<Object> lastSkijaCacheKey;

	public HUDManager() {}

	public void register(LucentHUD hud) {
		register(Lucent.config, hud);
	}

	public void register(ModManager manager, LucentHUD hud) {
		hud.setParentManager(manager);
		huds.add(hud);
		load(hud);
		updateSkijaStatus();
	}

	private void load(LucentHUD hud) {
		ModManager manager = hud.getParentManager();
		File file = manager.getHudConfigFile();
		if (!file.exists()) return;

		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);
			if (root == null || !root.has("huds")) return;

			JsonObject hudsJson = root.getAsJsonObject("huds");
			if (!hudsJson.has(hud.id)) return;
			JsonObject entry = hudsJson.getAsJsonObject(hud.id);

			if (entry.has("x")) {
				float rawX = entry.get("x").getAsFloat();
				hud.x = (rawX > 1.1f) ? (rawX / 1920f) : rawX;
			}
			if (entry.has("y")) {
				float rawY = entry.get("y").getAsFloat();
				hud.y = (rawY > 1.1f) ? (rawY / 1080f) : rawY;
			}
			if (entry.has("scale")) {
				hud.scale = LucentHUD.clampScale(entry.get("scale").getAsFloat());
			}
			if (entry.has("alignment")) {
				try {
					hud.alignment = Align.valueOf(entry.get("alignment").getAsString());
				} catch (IllegalArgumentException ignored) {}
			}
		} catch (Exception e) {}
	}

	public List<LucentHUD> getHUDs() {
		return huds;
	}

	@SuppressWarnings("unchecked")
	public <T extends LucentHUD> T getHUD(Class<T> clazz) {
		for (LucentHUD hud : huds) {
			if (clazz.isAssignableFrom(hud.getClass())) {
				return (T) hud;
			}
		}
		return null;
	}

	public void render(GuiGraphicsExtractor graphics) {
		if (UScreen.getScreen() instanceof EditHUDScreen) return;

		if (Lucent.preview) {
			URender.drawImage(graphics, Lucent.PREVIEW_BACKGROUND, 0, 0, UDisplay.getGuiScaledWidth(), UDisplay.getGuiScaledHeight());
		}

		if (huds.isEmpty()) return;

		// mc draw
		for (LucentHUD hud : huds) {
			if (hud.isEnabled() && hud.getRenderType() == RenderType.MINECRAFT) {
				hud.draw(graphics);
			}
		}

		// skija draw
		if (cachedHasSkija) {
			Runnable drawSkijaHuds = () -> {
				for (LucentHUD hud : huds) {
					if (hud.isEnabled() && hud.getRenderType() == RenderType.SKIJA) {
						hud.draw(graphics);
					}
				}
			};
			List<Object> cacheKey = new ArrayList<>();
			for (LucentHUD hud : huds) {
				if (!hud.isEnabled() || hud.getRenderType() != RenderType.SKIJA) continue;
				Object key = hud.getRenderCacheKey();
				if (key == null) {
					cacheKey = null;
					break;
				}
				cacheKey.add(hud);
				cacheKey.add(key);
			}
			if (cacheKey != null && !cacheKey.isEmpty() && Objects.equals(cacheKey, lastSkijaCacheKey)
					&& SkijaCompositor.INSTANCE.canReuseHud()) {
				SkijaCompositor.INSTANCE.reuseHud(drawSkijaHuds);
				return;
			}
			lastSkijaCacheKey = cacheKey;
			SkijaCompositor.INSTANCE.invalidateHudCache();
			SkijaRenderer.draw(graphics, 0, 0, UDisplay.getWidth(), UDisplay.getHeight(), drawSkijaHuds);
		}
	}


	public void loadAll() {
		for (LucentHUD hud : huds) {
			load(hud);
		}
	}

	public void save() {
		Set<ModManager> managers = new HashSet<>();
		for (LucentHUD hud : huds) managers.add(hud.getParentManager());

		for (ModManager manager : managers) {
			save(manager);
		}
	}

	public void save(ModManager manager) {
		File file = manager.getHudConfigFile();
		if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

		JsonObject root = null;
		if (file.exists()) {
			try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
				root = GSON.fromJson(reader, JsonObject.class);
			} catch (Exception e) {
				Lucent.LOG.warn("Failed to read existing hud config for " + manager + ": " + e.getMessage());
			}
		}

		if (root == null) root = new JsonObject();
		JsonObject hudsJson = root.has("huds") && root.get("huds").isJsonObject() ? root.getAsJsonObject("huds") : new JsonObject();

		for (LucentHUD hud : huds) {
			if (hud.getParentManager() != manager) continue;
			JsonObject entry = new JsonObject();
			entry.addProperty("x", hud.x);
			entry.addProperty("y", hud.y);
			entry.addProperty("scale", hud.scale);
			entry.addProperty("alignment", hud.alignment.name());
			hudsJson.add(hud.id, entry);
		}

		root.add("huds", hudsJson);

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
			GSON.toJson(root, writer);
		} catch (Exception e) {
			Lucent.LOG.warn("Failed to save hud configs for " + manager + ": " + e.getMessage());
		}
	}

	private void updateSkijaStatus() {
		this.cachedHasSkija = huds.stream().anyMatch(h -> h.getRenderType() == RenderType.SKIJA);
	}

}