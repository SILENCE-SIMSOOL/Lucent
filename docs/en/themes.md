# Theme Configuration Guide

Lucent's config screen supports a series of beautifully crafted visual themes. Users can switch their preferred theme directly through the Settings GUI preferences section, or developers can apply them programmatically through code.

---

## 1. Built-in Themes

Lucent provides the following themes out of the box:

| Theme Name | Color Palette / Aesthetic Style |
|---|---|
| **Default** | Classic dark neutral theme with blue accents. |
| **Glass Morphic** | Modern frosted-glass visual panels with translucent overlays. |
| **Midnight Cyan** | Deep space dark navy theme with vibrant cyan highlight tones. |
| **Crimson Aurora** | Dark red and violet glow highlights simulating polar lights. |
| **Emerald Mist** | Forest themed dark environment with emerald green accents. |
| **Amethyst Eclipse** | Mystical purple design utilizing deep amethyst color schemes. |

---

## 2. API Usage

You can find, apply, and query visual themes using [ThemeManager](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/theme/ThemeManager.java).

### Applying a Theme Programmatically
To force a specific theme visual style to load on screen:

```java
import silence.simsool.lucent.ui.theme.ThemeManager;

// Find and apply "Midnight Cyan" theme layout colors
ThemeManager.applyTheme(ThemeManager.findTheme("Midnight Cyan"));
```

### Retrieving Active Theme Information
To check details of the current visual style:

```java
// Returns the currently active LucentTheme data object
LucentTheme currentTheme = ThemeManager.getCurrentTheme();

// Get theme identification name
String themeName = currentTheme.getName();
```
All UI colors inside [UIColors](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/ui/utils/UIColors.java) are automatically mapped and updated dynamically when a new theme is selected.
