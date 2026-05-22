# Font Rendering Guide

[🏠 Main README](../../README.md) | [⚙️ Config](config.md) | [📺 HUD](hud.md) | [🔤 Fonts](fonts.md) | [🧱 Widgets](widgets.md) | [👥 Profiles](profiles.md) | [🎨 Themes](themes.md) | [🛠️ Utilities](utilities.md)

This guide covers how to display custom typography in Lucent, detailing the pre-loaded font variants and the two distinct methods of rendering text.

---

## 1. Loaded Fonts

Lucent uses the modern, highly readable **Pretendard** font family. The system handles asynchronous font downloading and initializes these variations:

### Available Fonts (via `Fonts` Utility)
Access these static references defined in [Fonts](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/nvg/Fonts.java):

- `Fonts.PRETENDARD_LIGHT` (Lightweight weight)
- `Fonts.PRETENDARD` (Regular/Default weight)
- `Fonts.PRETENDARD_MEDIUM` (Medium weight)
- `Fonts.PRETENDARD_SEMIBOLD` (Semi-bold weight)

The backend enum listing all metadata is available at [FontList](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/enums/FontList.java).

---

## 2. Text Rendering Methods

Lucent provides two rendering paths depending on your active graphics framework:

### Path A: NanoVG GPU Rendering (Recommended for Custom Screens)
If you are drawing inside a NanoVG context via `NVGPIPRenderer`, use the static draw methods on [NVGRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/nvg/NVGRenderer.java).

```java
// Regular Text
NVGRenderer.text("Hello World", x, y, Fonts.PRETENDARD, UIColors.PURE_WHITE, 14f);

// Centered Text
NVGRenderer.centerText("Centered Title", centerX, y, Fonts.PRETENDARD_SEMIBOLD, UIColors.ACCENT_BLUE, 18f);

// Text with Drop Shadow
NVGRenderer.textShadow("Shadow Effect", x, y, Fonts.PRETENDARD_MEDIUM, UIColors.PURE_WHITE, 14f);

// Get Text Width (useful for layout spacing)
float width = NVGRenderer.textWidth("Measure Me", Fonts.PRETENDARD, 14f);
```

---

### Path B: Minecraft Blit Rendering (Vanilla GuiGraphics Integration)
If you want to render custom typography directly inside standard Minecraft GUI overlays or screen elements without NanoVG, use [CFontRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/font/CFontRenderer.java). This method parses standard formatting color codes (e.g. `§a`, `§c`).

#### Step 1: Initialize your Font Renderer
Create a standard Java `Font` instance and wrap it using `CFontRenderer`:

```java
import silence.simsool.lucent.ui.font.CFontRenderer;
import java.awt.Font;

// Load a fallback AWT Font or load your TTF stream manually
Font baseFont = new Font("Pretendard", Font.PLAIN, 18);
CFontRenderer fontRenderer = new CFontRenderer(baseFont, true); // true = anti-aliasing active
```

#### Step 2: Draw on GuiGraphics Context

```java
import net.minecraft.client.gui.GuiGraphics;

// drawString(context, text, x, y, ARGBColor, drawShadow, fontSizePx)
int textWidth = fontRenderer.drawString(
	graphics, 
	"§aGreen Text §rwith standard formatting", 
	x, 
	y, 
	0xFFFFFFFF, 
	true, 
	16f
);
```

#### Helper Methods:
- `fontRenderer.getStringWidth(String text, float size)`: Calculates width of formatted strings (automatically skips `§` color code characters).
- `fontRenderer.getFontHeight(float size)`: Returns the scaled line height.
