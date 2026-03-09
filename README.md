# Lucent

Lucent is a powerful and lightweight library mod for Minecraft Fabric that makes creating beautiful, high-performance UIs and managing mod configurations effortless. Built with modern aesthetics in mind, Lucent empowers developers to focus on features while the library takes care of the visual heavy lifting and boilerplate code.

Similar to frameworks like Essential or OneConfig, Lucent provides a unified, customizable configuration screen that feels incredibly seamless. It also provides handy utility classes for common modding tasks like Client Chat rendering, Logging, and Screen Animations.

## Features

* **Beautiful UI Rendering (NVG):** Out-of-the-box support for NanoVG-based rendering (`NVGRenderer`). Draw perfect anti-aliased shadows, gradients, rounded rectangles, and loaded TTF/OTF fonts with zero pixelation at any GUI Scale.
* **Unified Config UI Engine:** A full-fledged settings UI built in! By simply passing your custom `@ModConfig` annotated Java classes, Lucent generates an interactive UI (Switches, Sliders, Dropdowns, Color Pickers) with a modern sidebar layout and search functionality.
* **Developer Utilities:** Save time with built-in utils:
  * `UChat`: Easily send client-sided messages or execute commands without long vanilla boilerplate.
  * `ULog`: A unified logger wrapper to standardize logging across your mod.
  * `UAnimation`: Handful of interpolation and snap functions for buttery-smooth GUI animations.

---

## 🚀 Getting Started for Developers

### 1. Setting Up Your Mod Configuration

Lucent uses an easy Annotation-based system to detect your settings. Create a class extending `Module` (or register an object directly, depending on your setup) and annotate fields with `@ModConfig`.

```java
import silence.simsool.lucent.general.abstracts.Module;
import silence.simsool.lucent.general.interfaces.ModConfig;
import silence.simsool.lucent.general.interfaces.ModConfig.ConfigType;

public class MyAwesomeModModule extends Module {

    public MyAwesomeModModule() {
        // name, description, category, search tags, icon path
        super("Awesome Mod", "Does awesome things", "GENERAL", "awesome, cool", "/assets/mymod/icon.png");
    }

    @ModConfig(
        name = "Enable Super Jump",
        description = "Makes the player jump really high.",
        category = "Movement",
        type = ConfigType.SWITCH
    )
    public boolean superJump = false;

    @ModConfig(
        name = "Jump Power",
        description = "How high you go.",
        category = "Movement",
        type = ConfigType.SLIDER,
        min = 1.0, max = 10.0, step = 0.5
    )
    public double jumpPower = 2.5;
}
```

### 2. Registering with Lucent

In your mod's Client Initialization (`ClientModInitializer`), simply instantiate your config class and register it to Lucent's `ModuleManager`.

```java
import net.fabricmc.api.ClientModInitializer;
import silence.simsool.lucent.client.ModuleManager;

public class MyModClient implements ClientModInitializer {
    
    // Create or retrieve Lucent's global ModuleManager instance here
    public static final ModuleManager moduleManager = new ModuleManager(new File("config/mymod"));

    @Override
    public void onInitializeClient() {
        // Register your module
        moduleManager.register(new MyAwesomeModModule());
        
        // Load existing config values from file
        moduleManager.loadConfigs();
    }
}
```

### 3. Opening the Config Screen

You can easily open the Lucent generated UI screen by passing your `ModuleManager` into the `LucentConfigScreenSecond` (the primary modern config screen API). 

```java
import silence.simsool.lucent.client.dev.screens.LucentConfigScreenSecond;

// E.g., inside a KeyBinding callback or a command
Minecraft.getInstance().setScreen(new LucentConfigScreenSecond(MyModClient.moduleManager));
```

### 4. Utilities

Stop writing long boilerplate for logging and chat!

```java
import silence.simsool.lucent.general.UChat;
import silence.simsool.lucent.general.ULog;

// Client Chat
UChat.chat("§aConfig saved successfully!"); // Only shows to the client
UChat.say("/gamemode creative"); // Sends as if the player typed it

// Logging
ULog.info("My Awesome Mod has loaded!");
ULog.warn("Something looks weird...");
```

---

## 🎨 UI Component Showcase

When you register properties using `@ModConfig`, Lucent automatically generates the following matching Modern UI Widgets:

- `ConfigType.SWITCH`: A smooth, animated Toggle Button.
- `ConfigType.SLIDER`: An interactive slider with min/max/step bounds, custom input box, and drop-shadow thumb.
- `ConfigType.SELECTOR`: A dropdown selection box for enums/strings.
- `ConfigType.COLOR`: A real-time PolyUI-like Color Picker modal.
