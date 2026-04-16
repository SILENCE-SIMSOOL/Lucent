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

	public static void loadLucentIcons() {
		try {
			if (iconMods == null) iconMods = LucentUtils.createIcon("mods");
			if (iconProfiles == null) iconProfiles = LucentUtils.createIcon("profiles");
			if (iconThemes == null) iconThemes = LucentUtils.createIcon("themes");
			if (iconPreferences == null) iconPreferences = LucentUtils.createIcon("preferences");
			if (iconEditHud == null) iconEditHud = LucentUtils.createIcon("edithud");
			if (iconClose == null) iconClose = LucentUtils.createIcon("close");
			if (iconSearch == null) iconSearch = LucentUtils.createIcon("search");
			if (iconSettings == null) iconSettings = LucentUtils.createIcon("settings");
			if (iconDelete == null) iconDelete = LucentUtils.createIcon("delete");
			if (iconEdit == null) iconEdit = LucentUtils.createIcon("edit");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadModIcons(ModManager config) {
		try {
			if (config != null) {
				for (Mod m : config.modules) {
					if (m.icon != null && !m.icon.isEmpty() && !modIconsMap.containsKey(m.name)) {
						try {
							modIconsMap.put(m.name, NVGRenderer.createImage(m.icon));
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