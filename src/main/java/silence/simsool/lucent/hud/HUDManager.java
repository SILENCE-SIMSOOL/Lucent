package silence.simsool.lucent.hud;

import static silence.simsool.lucent.Lucent.mc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.ui.screens.EditHUDScreen;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;

public class HUDManager {

	private final List<LucentHUD> huds = new ArrayList<>();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private boolean cachedHasNanoVG = false;

	public HUDManager() {}

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
			if (clazz.isAssignableFrom(hud.getClass())) {
				return (T) hud;
			}
		}
		return null;
	}

	public void render(GuiGraphics graphics, DeltaTracker tickDelta) {
		if (huds.isEmpty() || mc.player == null || mc.level == null || mc.screen instanceof EditHUDScreen || mc.options.hideGui) return;

		// mc draw
		for (LucentHUD hud : huds) {
			if (hud.isEnabled() && hud.getRenderType() == RenderType.MINECRAFT) {
				hud.draw(graphics);
			}
		}

		// nano draw
		if (cachedHasNanoVG) {
			NVGPIPRenderer.draw(graphics, 0, 0, UDisplay.getWidth(), UDisplay.getHeight(), () -> {
				for (LucentHUD hud : huds) {
					if (hud.isEnabled() && hud.getRenderType() == RenderType.NANOVG) {
						hud.draw(graphics);
					}
				}
			});
		}
	}

//	public void preview(GuiGraphics graphics, int screenW, int screenH) {
//		if (huds.isEmpty() || mc.player == null || mc.level == null) return;
//
//		// mc preview
//		for (LucentHUD hud : huds) {
//			if (hud.isEnabled() && hud.getRenderType() == RenderType.MINECRAFT) {
//				hud.preview(graphics);
//			}
//		}
//
//		// nano preview
//		if (cachedHasNanoVG) {
//			NVGPIPRenderer.draw(graphics, 0, 0, screenW, screenH, () -> {
//				for (LucentHUD hud : huds) {
//					if (hud.isEnabled() && hud.getRenderType() == RenderType.NANOVG) {
//						hud.preview(graphics);
//					}
//				}
//			});
//		}
//	}

	public void loadAll() {
		File file = getHudConfigFile();
		if (!file.exists()) return;

		try (FileReader reader = new FileReader(file)) {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);
			if (root == null || !root.has("huds")) return;

			JsonObject hudsJson = root.getAsJsonObject("huds");
			for (LucentHUD hud : huds) {
				if (!hudsJson.has(hud.id)) continue;
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
						hud.alignment = HUDAlignment.valueOf(entry.get("alignment").getAsString());
					} catch (IllegalArgumentException ignored) {}
				}
			}
		} catch (Exception e) {
			Lucent.LOG.warn("Failed to load hud configs: " + e.getMessage());
		}
	}

	public void save() {
		File file = getHudConfigFile();
		file.getParentFile().mkdirs();

		JsonObject root = new JsonObject();
		JsonObject hudsJson = new JsonObject();

		for (LucentHUD hud : huds) {
			JsonObject entry = new JsonObject();
			entry.addProperty("x", hud.x);
			entry.addProperty("y", hud.y);
			entry.addProperty("scale", hud.scale);
			entry.addProperty("alignment", hud.alignment.name());
			hudsJson.add(hud.id, entry);
		}

		root.add("huds", hudsJson);

		try (FileWriter writer = new FileWriter(file)) {
			GSON.toJson(root, writer);
		} catch (Exception e) {
			Lucent.LOG.warn("Failed to save hud configs: " + e.getMessage());
		}
	}

	private void updateNanoVGStatus() {
		this.cachedHasNanoVG = huds.stream().anyMatch(h -> h.getRenderType() == RenderType.NANOVG);
	}

	private File getHudConfigFile() {
		String profile = Lucent.config.getCurrentProfile();
		File profilesDir = new File("config/lucent", "profiles");
		File profileDir = new File(profilesDir, profile);
		return new File(profileDir, "hud.json");
	}

}