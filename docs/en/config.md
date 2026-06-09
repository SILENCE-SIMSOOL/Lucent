# Config Integration Guide

[🏠 Main README](../../README.md) | [⚙️ Config](config.md) | [📺 HUD](hud.md) | [🔤 Fonts](fonts.md) | [🧱 Widgets](widgets.md) | [👥 Profiles](profiles.md) | [🎨 Themes](themes.md) | [🛠️ Utilities](utilities.md)

This guide explains how to set up, load, save, and display configuration menus for your Fabric mod using Lucent.

---

## 1. Creating or Using a Config Manager

Lucent uses [ModManager](../../src/main/java/silence/simsool/lucent/config/ModManager.java) to manage all configurations. You can create a standalone config directory for your own mod, or access the default Lucent configuration.

### Creating a Standalone Config Manager (Recommended)
Use [LucentAPI.createModManager](../../src/main/java/silence/simsool/lucent/config/api/LucentAPI.java) to initialize a manager bound to your unique Mod ID.

```java
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.config.api.LucentAPI;

public class MyModInitializer {
	public static ModManager config = LucentAPI.createModManager("yourmodid");
}
```
This automatically sets up the directory layout inside `config/yourmodid/`.

---

## 2. Creating a Module Class

To add settings, create a module class extending [Mod](../../src/main/java/silence/simsool/lucent/general/models/abstracts/Mod.java). Annotate the configurable fields with `@ModConfig` or `@ModConfigExtra`.

Refer to the built-in [ExampleMod.java](../../src/main/java/silence/simsool/lucent/examplemod/mods/ExampleMod.java) for a complete template.

### Example Module Configuration:

```java
import java.awt.Color;
import org.lwjgl.glfw.GLFW;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.general.enums.ConfigType;
import silence.simsool.lucent.general.models.data.KeyBind;

@ModConfig.CategoryPriority(name = "General", priority = 1000)
public class MyFeatureModule extends Mod {

	public MyFeatureModule() {
		super(
			"My Feature",           // Module display name
			"Does awesome things!", // Description tooltip
			"CategoryName",         // Sidebar category
			"tag1, tag2",           // Search tags
			null                    // Optional icon path (e.g. "/assets/mymod/icon.png")
		);
	}

	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Enable Feature",
		description = "Toggles the main feature on or off.",
		category = "General",
		priority = 100
	)
	public static boolean enableFeature = true;

	@ModConfig(
		type = ConfigType.SLIDER,
		name = "Multiplier",
		min = 0.0, max = 5.0, step = 0.1,
		category = "General",
		priority = 90
	)
	public static double multiplier = 1.0;
}
```

### Annotation Options
List of common properties available in `@ModConfig` and `@ModConfigExtra`:

- `type`: Config field input type (e.g. `ConfigType.SWITCH`, `ConfigType.SLIDER`, `ConfigType.SELECTOR`, etc.)
- `name`: Display name shown on the GUI.
- `description`: Detailed description shown as a hover tooltip.
- `category`: Category grouping for this item (default: `"General"`).
- `parent`: The field name of another configuration this option depends on. This option will only be visible when the parent is active (Prefix with `!` to invert the logic).
- `hidden`: If `true`, the option remains functional in code but is hidden from the ConfigScreen UI (default: `false`).
- `selector`: Applicable only when a `parent` is specified and that parent is a selector (`ConfigType.SELECTOR`). This option will only be visible when the parent selector's value matches the `selector` string.
- `priority`: Ordering weight (higher priority options are rendered first).

---

## 3. Registering, Loading, and Saving Configurations

Register your modules to your [ModManager](../../src/main/java/silence/simsool/lucent/config/ModManager.java) during your mod's client initialization block.

### Client Initialization Code:

```java
@Override
public void onInitializeClient() {
	// Register module instance
	config.register(new MyFeatureModule());

	// Load stored configurations from disk
	config.loadGlobalConfig();
	config.loadConfigs();
}
```

### Saving Configuration Programmatically:

Settings are loaded automatically, but if you programmatically modify fields or want to force a save manually:

```java
// Writes configuration to config/yourmodid/profiles/default/<ModuleName>.json
config.saveConfigs();
```

---

## 4. Opening the Config Screen

You can integrate Lucent's modern GUI screen into **ModMenu** or launch it directly via custom keybinds.

Use [LucentAPI.createConfigScreen](../../src/main/java/silence/simsool/lucent/config/api/LucentAPI.java) to instantiate the Minecraft `Screen` component.

```java
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.config.api.LucentAPI;

// Generate screen instance and apply it to Minecraft Client
Screen configScreen = LucentAPI.createConfigScreen(config);
Minecraft.getInstance().setScreen(configScreen);
```

---

## 5. LucentAPI Reference

The [LucentAPI](../../src/main/java/silence/simsool/lucent/config/api/LucentAPI.java) provides simple shortcuts for common tasks:

- `createModManager(String directoryName)`: Instantiates a config manager.
- `createConfigScreen(ModManager manager)`: Generates the settings GUI.
- `createEditHUDScreen(ModManager manager)`: Generates the drag-and-drop HUD layout screen.
- `registerHUD(ModManager manager, LucentHUD hud)`: Registers a HUD component.
