package silence.simsool.lucent.ui.manager;

import java.util.HashMap;
import java.util.Map;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.utils.LucentUtils;
import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class LucentResourceManager {
	public static Image iconMods, iconProfiles, iconThemes, iconPreferences, iconEditHud, iconClose, iconSearch, iconSettings, iconDelete, iconEdit;
	public static final Map<String, Image> modIconsMap = new HashMap<>();
	private static boolean iconsLoaded = false;

	public static void loadIcons(ModManager moduleManager) {
		if (iconsLoaded) return;
		iconsLoaded = true;

		try {
			iconMods        = LucentUtils.createIcon("mods");
			iconProfiles    = LucentUtils.createIcon("profiles");
			iconThemes      = LucentUtils.createIcon("themes");
			iconPreferences = LucentUtils.createIcon("preferences");
			iconEditHud     = LucentUtils.createIcon("edithud");
			iconClose       = LucentUtils.createIcon("close");
			iconSearch      = LucentUtils.createIcon("search");
			iconSettings    = LucentUtils.createIcon("settings");
			iconDelete      = LucentUtils.createIcon("delete");
			iconEdit        = LucentUtils.createIcon("edit");

			if (moduleManager != null) {
				for (Mod m : moduleManager.modules) {
					if (m.icon != null && !m.icon.isEmpty() && !modIconsMap.containsKey(m.name)) {
						try {
							modIconsMap.put(m.name, NVGRenderer.createImage(m.icon));
						} catch (Exception ex) {}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
