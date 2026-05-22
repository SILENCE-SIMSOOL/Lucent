# Utility Classes & NanoVG Guide

[🏠 Main README](../../README.md) | [⚙️ Config](config.md) | [📺 HUD](hud.md) | [🔤 Fonts](fonts.md) | [🧱 Widgets](widgets.md) | [👥 Profiles](profiles.md) | [🎨 Themes](themes.md) | [🛠️ Utilities](utilities.md)

Lucent bundles a powerful suite of graphic renderers and helper utilities that can speed up development for any Minecraft Fabric mod.

---

## 1. NanoVG Rendering (Priority #1)

Lucent includes a bundled LWJGL NanoVG binding for hardware-accelerated vector drawing. All NanoVG draws are clipped and composited using a Picture-in-Picture framebuffer pipeline.

### Picture-in-Picture Pipeline ([NVGPIPRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/nvg/NVGPIPRenderer.java))
All NanoVG draw calls **MUST** reside within the `NVGPIPRenderer.draw(...)` callback. This sets up framebuffers and restores GL state automatically.

```java
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

// Draw inside standard GuiGraphics draw phase
NVGPIPRenderer.draw(guiGraphics, x, y, width, height, () -> {
	// NanoVG coordinates are local inside this block [0, 0] to [width, height]
	NVGRenderer.rect(0, 0, width, height, 0xE618181C, 8f); // Round box
	NVGRenderer.outlineRect(0, 0, width, height, 1f, 0xFFFFFFFF, 8f);
});
```

### NanoVG Drawing Functions ([NVGRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/nvg/NVGRenderer.java))
Use these methods inside the drawing pipeline to draw shapes:

```java
// Rectangles (Filled, Round, and Per-corner radii)
NVGRenderer.rect(x, y, w, h, color);
NVGRenderer.rect(x, y, w, h, color, radius);
NVGRenderer.rect(x, y, w, h, color, tl, tr, bl, br);

// Outlined Rectangles
NVGRenderer.outlineRect(x, y, w, h, thickness, color, radius);
NVGRenderer.outlineRect(x, y, w, h, thickness, color, tl, tr, bl, br);

// Gradients
NVGRenderer.gradientRect(x, y, w, h, color1, color2, GradientType.TOP_TO_BOTTOM, radius);

// Circles
NVGRenderer.circle(cx, cy, radius, color);
NVGRenderer.outlineCircle(cx, cy, radius, thickness, color);

// Lines & Triangles
NVGRenderer.line(x1, y1, x2, y2, thickness, color);
NVGRenderer.triangle(x1, y1, x2, y2, x3, y3, color);
NVGRenderer.arrowTriangle(cx, cy, w, h, Direction.DOWN, color); // Dropdown arrows

// Drop Shadows (Simulates modern card depth elevations)
NVGRenderer.dropShadow(x, y, w, h, blur, spread, radius);

// Text Drawing
NVGRenderer.text("Sample Text", x, y, Fonts.PRETENDARD, color, fontSize);
NVGRenderer.textShadow("Text Shadow", x, y, Fonts.PRETENDARD_MEDIUM, color, fontSize);
NVGRenderer.centerText("Centered", cx, y, Fonts.PRETENDARD_SEMIBOLD, color, fontSize);

// State Transformations
NVGRenderer.push(); // Save translation state
NVGRenderer.scale(sx, sy);
NVGRenderer.translate(dx, dy);
NVGRenderer.rotate(radians);
NVGRenderer.globalAlpha(0.5f);
NVGRenderer.pop(); // Restore translation state

// Scissor Clip Masking
NVGRenderer.pushScissor(x, y, w, h);
// ... Draw elements (clipped outside x,y,w,h) ...
NVGRenderer.popScissor();

// Transparent Checkerboard background
NVGRenderer.drawCheckerboard(x, y, w, h, radius);
```

---

## 2. UI Utilities (`silence.simsool.lucent.ui.utils.*`)

These classes provide coordinate geometry, coloring, layouts, and easing variables:

### [UAnimation](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/UAnimation.java)
Handles math transitions, interpolation curves, easing algorithms, and delta timing operations.
- **Easing**: `Easing.easeOut(t)`, `Easing.easeInOut(t)`, `Easing.spring(t)` (rebound bounce), `Easing.elastic(t)`.
- **Lerp**: `lerp(a, b, t)`, `lerpColor(c1, c2, t)`, `lerpSnap(current, target, speed, delta)` (snaps close values).
- **Time Waves**: `getPulseAlpha(speed)`, `getWaveOffset(speed, amplitude)`.
- **Progress**: `stepProgress(current, forward, speed, delta)` (computes progression tick by tick).

### [UColor](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/UColor.java)
Color math converter for ARGB integer transformations.
- `argb(a, r, g, b)`, `withAlpha(color, alpha)`, `withAlphaF(color, floatAlpha)`.
- `darken(color, amount)`, `brighten(color, amount)`.
- `toHSV(color)`, `fromHSVA(h, s, v, a)`.
- `toHex(color)` (outputs `"#RRGGBBAA"`), `fromHex(String hex)`.

### [UCorner](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/UCorner.java)
A lightweight Java Record specifying four distinct corner rounding radii.
- `UCorner.of(radius)` (uniform), `UCorner.top(radius)`, `UCorner.bottom(radius)`.
- `clampToBox(w, h)`: Safely prevents radii values from causing layout distortions.

### [UIColors](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/UIColors.java)
Stores colors and theme configuration parameters (e.g. `PURE_WHITE`, `ACCENT_BLUE`, `WIN_BG`). Automatically maps values dynamically when themes are changed.

### [ULayout](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/ULayout.java)
Helps position layout elements.
- `centerX(cX, cW, childW)` / `centerY(cY, cH, childH)`.
- `isHovered(mouseX, mouseY, x, y, w, h)`: Bounds hover check.
- `fitInside(srcW, srcH, maxW, maxH)` / `fillCover(srcW, srcH, targetW, targetH)`.
- Includes an `Insets` record (margins and paddings offset provider).

### [URender](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/URender.java)
Helpers for drawing using standard Minecraft GUI layers.
- `drawRect(graphics, x, y, w, h, color)`.
- `drawBorder(graphics, x, y, w, h, borderW, color)`.
- `drawPlayerHead(graphics, x, y, size, UUID)`: Renders player skins.

---

## 3. General & Rendering Utilities (`silence.simsool.lucent.general.utils.*`)

These are backend helpers for rendering, maths, structures, and minecraft overlays.

### Tuple Structs
- **[Pair](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/Pair.java)**: Generic pair `(A, B)`.
- **[Triple](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/Triple.java)**: Generic triple `(A, B, C)`.

### Core Helpers
- **[ClientHandler](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/ClientHandler.java)**: Safely schedules code blocks on the main client thread.
- **[MinecraftColor](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/MinecraftColor.java)**: Maps Minecraft formatting codes (`§`) to RGB colors.
- **[NumberUtils](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/NumberUtils.java)**: Provides safe parsing (`tryParseInt`, `tryParseDouble`) and math operations.
- **[ScoreboardUtils](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/ScoreboardUtils.java)**: Queries scoreboard objectives, sidebar titles, and player scores.

### Graphics Rendering Helpers
- **[RenderUtils](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/render/RenderUtils.java)**: Handles glState blends, scissor clips, and coordinate translations.
- **[DrawContextRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/render/DrawContextRenderer.java)** / **[DrawContextUtils](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/render/DrawContextUtils.java)**: Standardizes Minecraft `GuiGraphics` calls.
- **[ItemRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/render/ItemRenderer.java)**: Renders 3D ItemStacks onto 2D GUI layers (handles overlays and durability).
- **[RoundRectPIPRenderer](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/render/RoundRectPIPRenderer.java)**: Picture-in-picture shader mask that overlays rounded corners on textures.

---

## 4. Useful Shortcuts (`silence.simsool.lucent.general.utils.useful.*`)

Convenient shortcuts prefixing standard API bindings:

- **[UChat](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UChat.java)**: Sends messages to chat (`chat(msg)`) and commands to the server (`say(command)`).
- **[UDesktop](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UDesktop.java)**: Opens URLs in browsers, edits files, manages system clipboards, and triggers native OS alerts.
- **[UDisplay](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UDisplay.java)**: Fetches physical resolution, GUI dimensions, and checks if F3 debug menu is active.
- **[UFile](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UFile.java)**: Downloader helper that fetches bytes from URLs asynchronously.
- **[UInventory](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UInventory.java)**: Scans inventories, tallies item stacks, and manages user equipment.
- **[UKeyboard](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UKeyboard.java)**: Quick checks for active keyboard key presses.
- **[ULog](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/ULog.java)**: Lightweight logger wrapper formatting messages with a custom Mod identifier.
- **[UMouse](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UMouse.java)**: Retrieves mouse coordinates (both raw and scaled), and performs hover boundary checks.
- **[UObject](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UObject.java)**: Safe null operations and casting helpers.
- **[UPacket](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UPacket.java)**: Sends client-to-server connection packets.
- **[URender](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/URender.java)**: Specialized helper that uses NanoVG to draw components (e.g. `drawToggleButton`).
- **[UScreen](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UScreen.java)**: Opens screen instances safely.
- **[USlot](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/USlot.java)**: Interacts with slot items in container screens.
- **[USound](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/USound.java)**: Triggers UI sound effects.
- **[UText](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UText.java)**: Strips color codes and formats chat texts.
- **[UThread](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UThread.java)**: Thread pool configuration executor.
- **[UTitle](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UTitle.java)**: Displays overlays and subtitles.
- **[UWorld](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/general/utils/useful/UWorld.java)**: Interacts with world blocks, local players, and loaded entities.
