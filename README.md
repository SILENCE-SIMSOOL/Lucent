# Lucent

**Lucent** is a Fabric library mod for Minecraft 1.21.11 that provides a modern, full-featured config management system and UI toolkit for mod developers.

> 🇰🇷 [한국어 문서 보기](./README_KR.md)

---

## Overview

Lucent gives you:

- **A polished config UI** — Register your mod and all options appear in Lucent's built-in screen. Developers only need to add annotations; no screen code required.
- **Standalone config option** — You can create your own separate config file instead of being merged into the Lucent screen.
- **Full UI toolkit** — If you want complete control, use Lucent's NanoVG rendering utilities and widget components to build your own screen from scratch.
- **HUD system** — Register draggable, scalable HUD elements rendered via Lucent's `EditHudScreen`.
- **Utility classes** — Convenience helpers for chat, display info, mouse, animation, color math, and logging.

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.18.4 |
| Fabric API | ≥ 0.139.5+1.21.11 |
| LWJGL NanoVG | 3.3.3 (bundled) |

---

## Integration

### Library Types

| Type | Description |
|---|---|
| **[0] mod** | Fully built mod. NanoVG is JIJ'd. Players must install the Lucent mod separately. Efficiently reduces the mod file size. |
| **[1] library** | Slim library build. No NanoVG included. Lucent is bundled via JIJ inside the mod, meaning players do not need to install it separately. |

### Version List

| Version | MC Version |
|---|---|
| 1.0.0 - 1.0.6 | 1.21.11 |

### Version Format

- Format: `<lucent_version>-<mc_version>-<type>`
- Example: `1.0.0-1.21.11-0`

### Gradle Example

```groovy
repositories {
    maven { url "https://SILENCE-SIMSOOL.github.io/maven-repo/" }
}

dependencies {
    // For fetching a [0] mod
    modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.0.1-1.21.11-0"

    // For fetching a [1] library
    modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.0.1-1.21.11-1"
    include "com.github.SILENCE-SIMSOOL:lucent:1.0.1-1.21.11-1"
}
```

---

## Usage

### Option A — Use the Lucent Config Screen (Recommended)

This is the fastest integration path. Lucent automatically generates a config UI from annotated fields.

#### Step 1 — Create a `ModManager`

```java
// In your main initializer
public static ModManager config = LucentAPI.createModManager("yourmodid");
```

This creates a config directory at `config/yourmodid/`.

#### Step 2 — Create a Module class

Extend `silence.simsool.lucent.general.models.Mod` and annotate your config fields with `@ModConfig`.

```java
import silence.simsool.lucent.general.models.Mod;
import silence.simsool.lucent.general.interfaces.ModConfig;
import silence.simsool.lucent.general.enums.ConfigType;

public class MyMod extends Mod {

    public MyMod() {
        super(
            "My Mod",           // Display name
            "Does cool things", // Description
            "Utility",          // Category (sidebar grouping)
            "utility, tool",    // Search tags
            null                // Icon path (optional, e.g. "/assets/mymod/icon.png")
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
        name = "Speed",
        description = "Controls movement speed.",
        category = "General",
        min = 0.5, max = 5.0, step = 0.5,
        priority = 90
    )
    public static double speed = 1.0;

    @ModConfig(
        type = ConfigType.SELECTOR,
        name = "Mode",
        options = {"Fast", "Normal", "Slow"},
        category = "General",
        priority = 80
    )
    public static String mode = "Normal";

    @ModConfig(
        type = ConfigType.COLOR,
        name = "Highlight Color",
        category = "Appearance",
        priority = 50
    )
    public static Color highlightColor = new Color(85, 255, 85, 200);

    @ModConfig(
        type = ConfigType.KEYBIND,
        name = "Activation Key",
        priority = 40
    )
    public KeyBind activationKey = KeyBind.ofKey(GLFW.GLFW_KEY_H, 0);

    @ModConfig(
        type = ConfigType.TEXT,
        name = "Label",
        description = "Custom label text.",
        category = "Appearance",
        priority = 30
    )
    public static String label = "Hello";

    @ModConfig(
        type = ConfigType.BUTTON,
        name = "Run Action",
        display = "Click Me!",
        description = "Runs an action when clicked.",
        priority = 20
    )
    public void onRunAction() {
        UChat.chat("Button clicked!");
    }
}
```

#### Step 3 — Register and load

```java
@Override
public void onInitializeClient() {
    config.register(new MyMod());
    config.loadGlobalConfig();
    config.loadConfigs();
}
```

#### Step 4 — Open the config screen

Via **ModMenu** or a keybind:

```java
// ModMenu integration
Screen screen = LucentAPI.createConfigScreen(config);
client.setScreen(screen);
```

---

### Option B — Your own config file (without Lucent screen)

If you want a separate config file but don't want the Lucent UI, simply create a `ModManager` with a different directory and never call `LucentAPI.createConfigScreen(...)`. You can save/load manually:

```java
ModManager myConfig = LucentAPI.createModManager("mymod");
myConfig.register(new MyMod());
myConfig.loadConfigs();

// Save when needed:
myConfig.saveConfigs();
```

Config files are saved as JSON under `config/mymod/profiles/default/MyMod.json`.

---

### Option C — Build a custom screen with Lucent utilities

You can completely bypass the Lucent config screen and build your own using the NanoVG renderer and widget classes.

#### NanoVG Rendering (`NVGRenderer`)

All NVG draw calls must happen inside a `NVGPIPRenderer.draw(...)` call (which handles framebuffer setup):

```java
NVGPIPRenderer.draw(guiGraphics, x, y, width, height, () -> {
    // Rectangles
    NVGRenderer.rect(x, y, w, h, color);
    NVGRenderer.rect(x, y, w, h, color, cornerRadius);
    NVGRenderer.rect(x, y, w, h, color, r1, r2, r3, r4); // per-corner radii

    // Outlined rectangles
    NVGRenderer.outlineRect(x, y, w, h, thickness, color, radius);

    // Gradient rectangles
    NVGRenderer.gradientRect(x, y, w, h, color1, color2, GradientType.TOP_TO_BOTTOM, radius);

    // Circles
    NVGRenderer.circle(cx, cy, radius, color);
    NVGRenderer.outlineCircle(cx, cy, radius, thickness, color);

    // Lines & Triangles
    NVGRenderer.line(x1, y1, x2, y2, thickness, color);
    NVGRenderer.triangle(x1, y1, x2, y2, x3, y3, color);
    NVGRenderer.arrowTriangle(cx, cy, w, h, Direction.DOWN, color);

    // Drop shadows
    NVGRenderer.dropShadow(x, y, w, h, blur, spread, radius);

    // Images
    NVGRenderer.image(image, x, y, w, h, radius);

    // SVG
    NVGRenderer.svg(svgImage, x, y, w, h);

    // Text
    NVGRenderer.text("Hello", x, y, Fonts.PRETENDARD, color, fontSize);
    NVGRenderer.textShadow("Hello", x, y, Fonts.PRETENDARD, color, fontSize);
    NVGRenderer.centerText("Hello", cx, y, Fonts.PRETENDARD, color, fontSize);
    float w = NVGRenderer.textWidth("Hello", Fonts.PRETENDARD, 14f);

    // State management
    NVGRenderer.push();           // save state
    NVGRenderer.scale(sx, sy);
    NVGRenderer.translate(dx, dy);
    NVGRenderer.rotate(radians);
    NVGRenderer.globalAlpha(0.5f);
    NVGRenderer.pop();            // restore state

    // Scissor / clip
    NVGRenderer.pushScissor(x, y, w, h);
    // ... draw clipped content ...
    NVGRenderer.popScissor();

    // Checkerboard (e.g. for transparent color preview)
    NVGRenderer.drawCheckerboard(x, y, w, h, radius);
});
```

#### Available Fonts (`Fonts`)

```java
Fonts.PRETENDARD_EXTRALIGHT
Fonts.PRETENDARD_LIGHT
Fonts.PRETENDARD          // Default
Fonts.PRETENDARD_MEDIUM
Fonts.PRETENDARD_SEMIBOLD
```

#### Pre-built Widgets

Located in `silence.simsool.lucent.ui.widget`:

| Widget | Description |
|---|---|
| `ToggleButton` | Animated on/off switch |
| `Slider` | Draggable range input |
| `Selector` | Cycle through options |
| `ColorPicker` | Full HSVA color picker with hex input |
| `ColorPickerButton` | Color preview swatch that opens a `ColorPicker` |
| `KeyBindButton` | Captures keyboard/mouse key binds |
| `TextBox` | Single-line text input |
| `ActionButton` | Clickable button that triggers a callback |

Each widget handles its own input events, animations, and rendering internally.

---

### HUD System

Register elements that appear in the Lucent `EditHudScreen` for drag/scale positioning.

#### Step 1 — Extend `LucentHUD`

```java
import silence.simsool.lucent.general.models.LucentHUD;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;

public class MyHUD extends LucentHUD {

    public MyHUD() {
        super("my_hud", 0.01f, 0.05f, 1.0f, HUDAlignment.LEFT);
        // id, defaultX (0~1 ratio), defaultY (0~1 ratio), scale, alignment
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.NANOVG; // or RenderType.MINECRAFT
    }

    @Override
    public float getPreviewWidth()  { return 160f; }

    @Override
    public float getPreviewHeight() { return 24f; }

    @Override
    public boolean isEnabled() {
        return myConfig.getModule(MyMod.class).isEnabled;
    }

    @Override
    public void draw() {
        if (LucentHUD.isEditHudOpen || UDisplay.isDebugScreen()) return;
        renderContent("Live Data");
    }

    @Override
    public void preview() {
        renderContent("Preview");
    }

    private void renderContent(String text) {
        float rx = getRenderX(), ry = getRenderY();
        float sw = getScaledWidth(), sh = getScaledHeight();
        NVGRenderer.rect(rx, ry, sw, sh, UIColors.withAlpha(UIColors.PURE_BLACK, 130), 4f * scale);
        NVGRenderer.text(text, rx + 8 * scale, ry + (sh - 14f * scale) / 2f, Fonts.PRETENDARD, UIColors.PURE_WHITE, 14f * scale);
    }
}
```

#### Step 2 — Register

```java
HUDManager.INSTANCE.register(new MyHUD());
HUDManager.INSTANCE.loadAll();
```

HUD position/scale/alignment is automatically persisted per-profile.

---

## Profiles

Lucent supports named configuration profiles. All module configs and HUD positions are stored per-profile under `config/<dir>/profiles/<name>/`.

```java
ModManager config = LucentAPI.createModManager("mymod");

// List profiles
List<String> profiles = config.getProfiles(); // always includes "default"

// Switch profile (reloads all configs)
config.setCurrentProfile("pvp");

// Create / delete / rename
config.createProfile("pvp");
config.deleteProfile("pvp");
config.renameProfile("pvp", "competitive");
```

---

## Themes

The config screen supports multiple visual themes. Users can switch themes in the UI; you can also apply one programmatically:

```java
ThemeManager.applyTheme(ThemeManager.findTheme("Midnight Cyan"));
```

Built-in themes: `Default`, `Glass Morphic`, `Midnight Cyan`, `Crimson Aurora`, `Emerald Mist`, `Amethyst Eclipse`.

---

## Utility Classes

### `UChat`
```java
UChat.chat("Hello!");          // Display client-side message
UChat.chat(42);                // Overloaded for int, long, double, float, boolean, Object
UChat.say("Hello server!");    // Send chat message to server
```

### `UDisplay`
```java
UDisplay.getWidth()            // Framebuffer width (pixels)
UDisplay.getScreenWidth()      // Physical screen width
UDisplay.getGuiScaledWidth()   // GUI-scaled width
UDisplay.isFullscreen()
UDisplay.isDebugScreen()       // true when F3 is open
```

### `UMouse`
```java
UMouse.getX()                           // Raw mouse X
UMouse.getY()                           // Raw mouse Y
UMouse.getScaledX(scale)               // Scaled by GUI scale * scale
UMouse.isAreaHovered(x, y, w, h)       // Hover check (raw coords)
UMouse.isAreaHovered(x, y, w, h, true) // Hover check (GUI-scaled)
UMouse.getQuadrant()                    // 1–4 screen quadrant
```

### `UAnimation`
```java
// Easing functions (input: 0~1, output: 0~1)
UAnimation.Easing.easeOut(t)
UAnimation.Easing.easeInOut(t)
UAnimation.Easing.spring(t)
UAnimation.Easing.elastic(t)
UAnimation.Easing.bounce(t)

// Lerp
UAnimation.lerp(a, b, t)
UAnimation.lerpColor(c1, c2, t)
UAnimation.lerpSnap(current, target, speed, delta)

// Clamp
UAnimation.clamp(val, min, max)

// Time-based
UAnimation.getPulseAlpha(speed)   // 0~1 sine wave
UAnimation.getWaveOffset(speed, amplitude)
UAnimation.getCycle(periodSeconds)

// Step progress (for per-frame animation state)
UAnimation.stepProgress(current, forward, speed, delta)

// Slider helpers
UAnimation.snapToStep(value, step)
UAnimation.formatForStep(value, step)
```

### `UColor`
```java
UColor.argb(a, r, g, b)
UColor.rgb(r, g, b)
UColor.withAlpha(color, alpha)        // alpha: 0–255
UColor.withAlphaF(color, alphaFloat)  // alpha: 0.0–1.0
UColor.lerpColor(c1, c2, t)
UColor.darken(color, 0.2f)
UColor.lighten(color, 0.2f)
UColor.toHSV(color)                   // float[] {h, s, v}
UColor.fromHSVA(h, s, v, a)
UColor.toHex(color)                   // "#RRGGBBAA"
UColor.fromHex("#FF5500")
```

### `ULog`
```java
ULog log = new ULog("MyMod");
log.info("Initialized");
log.warn("Something is off");
log.error("Failed!", exception);
```

### `URender`
```java
// Pre-built toggle button visuals (uses NVGRenderer internally)
URender.drawToggleButton(x, y, w, h, onProgress, hoverProgress);
```

### `KeyBind`
```java
KeyBind kb = KeyBind.ofKey(GLFW.GLFW_KEY_H, 0);           // keyboard key
KeyBind kb = KeyBind.ofMouse(KeyBind.MOUSE_RIGHT, 0);      // mouse button
KeyBind kb = KeyBind.none();                               // unbound

kb.getDisplayName(); // e.g. "Ctrl+Shift+H", "Mouse2", "None"
kb.isBound()
kb.isKey()
kb.isMouse()
```

---

## `@ModConfig` Reference

| Parameter | Type | Description |
|---|---|---|
| `type` | `ConfigType` | Widget type: `SWITCH`, `SLIDER`, `BUTTON`, `COLOR`, `SELECTOR`, `KEYBIND`, `TEXT` |
| `name` | `String` | Display name in the config UI |
| `display` | `String` | Button label text (only for `BUTTON` type) |
| `description` | `String` | Tooltip/description text |
| `category` | `String` | Tab grouping name (default: `"General"`) |
| `min` / `max` | `double` | Slider range (default: 0 / 10) |
| `step` | `double` | Slider step size (default: 1.0) |
| `options` | `String[]` | Selector options |
| `priority` | `int` | Higher = listed first within category |
| `parent` | `String` | Field name of a `SWITCH` parent — this item is hidden when the parent is off |

### Category Priority

Control the tab ordering with a class-level annotation:

```java
@ModConfig.CategoryPriority(name = "General",    priority = 1000)
@ModConfig.CategoryPriority(name = "Appearance", priority = 500)
public class MyMod extends Mod { ... }
```

### Child Config Items

Use `parent` to hide dependent options when a toggle is off:

```java
@ModConfig(type = ConfigType.SWITCH, name = "Enable X", priority = 100)
public static boolean enableX = true;

@ModConfig(type = ConfigType.SLIDER, name = "X Speed", parent = "enableX", priority = 90)
public static double xSpeed = 1.0;
```

---

## License

MIT License — see [LICENSE](./LICENSE) for details.
