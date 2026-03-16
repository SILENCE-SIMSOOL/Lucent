package silence.simsool.lucent.config.api;

import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.ui.screens.ConfigScreen;

import java.io.File;

/**
 * The main API entrypoint for integrating Lucent into your Fabric Mod.
 */
public class LucentAPI {

    /**
     * Creates a new ModManager bound to the given configuration directory.
     * Your mod should store this instance globally and register all its modules to it.
     *
     * @param directoryName The name of the config folder (e.g. "mymod")
     * @return A modern ModuleManager instance
     */
    public static ModManager createModManager(String directoryName) {
        return new ModManager(new File("config/" + directoryName));
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
}