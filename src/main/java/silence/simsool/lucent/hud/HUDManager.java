package silence.simsool.lucent.hud;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.LucentHUD;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;

public class HUDManager {
	public static final HUDManager INSTANCE = new HUDManager();

	private final List<LucentHUD> huds = new ArrayList<>();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private boolean cachedHasNanoVG = false;

	private HUDManager() {}

	public void register(LucentHUD hud) {
		huds.add(hud);
		updateNanoVGStatus();
	}

	public List<LucentHUD> getHuds() {
		return huds;
	}

	@SuppressWarnings("unchecked")
	public <T extends LucentHUD> T getHud(Class<T> clazz) {
		for (LucentHUD hud : huds) {
			if (clazz.isAssignableFrom(hud.getClass())) return (T) hud;
		}
		return null;
	}

	public void render(GuiGraphics guiGraphics, int screenW, int screenH) {
		if (huds.isEmpty()) return;

		// mc draw
		for (LucentHUD hud : huds) {
			if (hud.isEnabled() && hud.getRenderType() == RenderType.MINECRAFT) hud.draw();
		}

		// nano draw
		if (cachedHasNanoVG) {
			NVGPIPRenderer.draw(guiGraphics, 0, 0, screenW, screenH, () -> {
				for (LucentHUD hud : huds) {
					if (hud.isEnabled() && hud.getRenderType() == RenderType.NANOVG) hud.draw();
				}
			});
		}
	}

	public void preview(GuiGraphics guiGraphics, int screenW, int screenH) {
		if (huds.isEmpty()) return;

		// mc preview
		for (LucentHUD hud : huds) {
			if (hud.isEnabled() && hud.getRenderType() == RenderType.MINECRAFT) hud.preview();
		}

		// nano preview
		if (cachedHasNanoVG) {
			NVGPIPRenderer.draw(guiGraphics, 0, 0, screenW, screenH, () -> {
				for (LucentHUD hud : huds) {
					if (hud.isEnabled() && hud.getRenderType() == RenderType.NANOVG) hud.preview();
				}
			});
		}
	}

	public void loadAll() {
		for (LucentHUD hud : huds) {
			File file = getHudConfigFile(hud); if (!file.exists()) continue;

			try (FileReader reader = new FileReader(file)) {
				JsonObject entry = GSON.fromJson(reader, JsonObject.class); if (entry == null) continue;

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
						hud.alignment = HUDAlignment.valueOf(entry.get("alignment").getAsString());
					} catch (IllegalArgumentException ignored) {}
				}

			} catch (Exception e) {
				Lucent.LOG.warn("Failed to load hud config (" + hud.id + "): " + e.getMessage());
			}
		}
	}

	public void save() {
		for (LucentHUD hud : huds) {
			File file = getHudConfigFile(hud);
			file.getParentFile().mkdirs();

			JsonObject entry = new JsonObject();
			entry.addProperty("x", hud.x);
			entry.addProperty("y", hud.y);
			entry.addProperty("scale", hud.scale);
			entry.addProperty("alignment", hud.alignment.name());

			try (FileWriter writer = new FileWriter(file)) {
				GSON.toJson(entry, writer);
			} catch (Exception e) {
				Lucent.LOG.warn("Failed to save hud config (" + hud.id + "): " + e.getMessage());
			}
		}
	}

	private void updateNanoVGStatus() {
		this.cachedHasNanoVG = huds.stream().anyMatch(h -> h.getRenderType() == RenderType.NANOVG);
	}

	private File getHudConfigFile(LucentHUD hud) {
		String profile = Lucent.config.getCurrentProfile();
		File profilesDir = new File("config/lucent", "profiles");
		File profileDir = new File(profilesDir, profile);
		return new File(profileDir, "hud_" + hud.id + ".json");
	}
}