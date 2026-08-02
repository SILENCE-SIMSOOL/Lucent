package silence.simsool.lucent.ui.manager;

import java.util.HashMap;
import java.util.Map;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.utils.LucentUtils;
import silence.simsool.lucent.general.utils.useful.UThread;
import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class LucentResourceManager {

	public static Image iconDiscord;
	public static final Map<String, Image> modIconsMap = new HashMap<>();

	private static boolean onemoretry = true;

	public static void loadLucentIcons() {
		try {
			if (iconDiscord == null) iconDiscord = LucentUtils.createIcon("discord");
		} catch (Exception e) {
			if (onemoretry) {
				onemoretry = false;
				Lucent.LOG.info("Retrying to load icons..");
				UThread.EXECUTOR.execute(() -> {
					try {
						Thread.sleep(1000);
						loadLucentIcons();
					} catch (InterruptedException ex) {}
				});
			}
		}
	}

	public static void loadModIcons(ModManager config) {
		try {
			if (config != null) {
				for (Mod mod : config.modules) {
					if (mod.icon != null && !mod.icon.isEmpty() && !modIconsMap.containsKey(mod.name)) {
						try {
							modIconsMap.put(mod.name, NVGRenderer.createImage(mod.icon));
						} catch (Exception ex) {
							//ex.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}

}