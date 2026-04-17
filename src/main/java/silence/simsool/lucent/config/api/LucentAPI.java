package silence.simsool.lucent.config.api;

import java.io.File;

import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.general.utils.OSUtils;
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.ui.screens.ConfigScreen;
import silence.simsool.lucent.ui.screens.EditHUDScreen;

/**
 * The main API entrypoint for integrating Lucent into your Fabric Mod.
 */
public class LucentAPI {

	/**
	 * Returns the global HUDManager instance managed by Lucent.
	 * Your mod should use this instance to register custom HUD elements.
	 *
	 * @return The HUDManager instance
	 */
	public static HUDManager getHUDManager() {
		return Lucent.hudManager;
	}

	/**
	 * Creates a new ModManager bound to the given configuration directory.
	 * Your mod should store this instance globally and register all its modules to it.
	 *
	 * @param directoryName The name of the config folder (e.g. "mymod")
	 * @return A modern ModuleManager instance
	 */
	public static ModManager createModManager(String directoryName) {
		if (directoryName.equals("lucent")) return new ModManager(OSUtils.getLucentDir());
		return new ModManager(new File(OSUtils.getLucentDir(), directoryName));
	}

	/**
	 * Returns the Lucent UI Screen for the specified ModuleManager.
	 * This can be hooked into ModMenu or launched via KeyBindings/Commands.
	 *
	 * @param manager Your mod's global ModuleManager instance
	 * @return The Minecraft Screen object containing the modern config UI
	 */
	public static Screen createConfigScreen(ModManager manager) {
		return new ConfigScreen(manager);
	}

	/**
	 * Returns the Lucent Edit HUD Screen for the specified ModuleManager.
	 *
	 * @param manager Your mod's global ModuleManager instance
	 * @return The Minecraft Screen object containing the Edit HUD UI
	 */
	public static Screen createEditHUDScreen(ModManager manager) {
		return new EditHUDScreen(manager);
	}

}