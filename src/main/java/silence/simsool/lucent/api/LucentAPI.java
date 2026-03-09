package silence.simsool.lucent.api;

import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.client.ModuleManager;
import silence.simsool.lucent.client.dev.screens.LucentConfigScreenSecond;

import java.io.File;

/**
 * The main API entrypoint for integrating Lucent into your Fabric Mod.
 */
public class LucentAPI {

    /**
     * Creates a new ModuleManager bound to the given configuration directory.
     * Your mod should store this instance globally and register all its modules to it.
     *
     * @param directoryName The name of the config folder (e.g. "mymod")
     * @return A modern ModuleManager instance
     */
    public static ModuleManager createModuleManager(String directoryName) {
        return new ModuleManager(new File("config/" + directoryName));
    }

    /**
     * Returns the Lucent UI Screen for the specified ModuleManager.
     * This can be hooked into ModMenu or launched via KeyBindings/Commands.
     *
     * @param manager Your mod's global ModuleManager instance
     * @return The Minecraft Screen object containing the modern config UI
     */
    public static Screen createConfigScreen(ModuleManager manager) {
        return new LucentConfigScreenSecond(manager);
    }
}
