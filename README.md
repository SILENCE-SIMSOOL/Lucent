<h3 align="center">
	<img src="https://raw.githubusercontent.com/SILENCE-SIMSOOL/Lucent/main/docs/icon.png" alt="Lucent Icon" width="128" height="128" />
</h3>

<h1 align="center">Lucent</h1>

<p align="center">
	A modern config system, customizable HUD engine, and hardware-accelerated vector UI toolkit for Minecraft Fabric mods.
</p>

<p align="center">
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent/tree/main/docs">Documentation</a>
	·
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">Report Bug</a>
	·
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">Request Feature</a>
</p>

<p align="center">
	<a href="./README_KR.md">🇰🇷 한국어 문서</a>
</p>

---

## Documentation

Explore Lucent through focused documentation guides for each system and feature.

### ⚙️ Configuration
- **[Config Integration Guide](./docs/en/config.md)**  
  Annotation-driven config managers, modules, saving/loading, and settings GUI integration.  
  Example: [`ExampleMod.java`](https://github.com/SILENCE-SIMSOOL/Lucent/blob/main/src/main/java/silence/simsool/lucent/examplemod/mods/ExampleMod.java)
- **[Event System Guide](./docs/en/events.md)**  
  Subscribe to game events such as chat messages, ticks, rendering, network packets, and entities.

### 📺 HUD System
- **[HUD Configuration Guide](./docs/en/hud.md)**  
  Create draggable, scalable in-game HUD elements and widgets.  
  Example: [`ExampleHUD.java`](https://github.com/SILENCE-SIMSOOL/Lucent/blob/main/src/main/java/silence/simsool/lucent/examplemod/huds/ExampleHUD.java)

### 🎨 UI & Styling
- **[UI Widgets Guide](./docs/en/widgets.md)**  
  Ready-to-use widgets including Toggles, Sliders, Dropdowns, TextBoxes, and Color Pickers.

- **[Theme Configuration Guide](./docs/en/themes.md)**  
  Customize UI appearance with themes like Midnight Cyan, Glass Morphic, and Crimson Aurora.

### 🔤 Rendering
- **[Font Rendering Guide](./docs/en/fonts.md)**  
  Render bundled Pretendard fonts inside NanoVG and Minecraft rendering pipelines.

- **[Utility Classes & NanoVG Guide](./docs/en/utilities.md)**  
  GPU-accelerated rendering utilities, gradients, shadows, shapes, and Minecraft helper classes.

### 👥 Profiles
- **[Profile Management Guide](./docs/en/profiles.md)**  
  Manage configuration profiles and per-profile HUD layouts.

## Requirements

### 🟢 Minecraft 1.21.11 Environment
| Dependency | Version |
| :-- | :-- |
| Minecraft | `1.21.11` |
| Fabric Loader | `>= 0.18.4` |
| Fabric API | `>= 0.141.3+1.21.11` |
| LWJGL NanoVG | `3.3.3` *(bundled)* |

### 🟡 Minecraft 26.2 Environment
| Dependency | Version |
| :-- | :-- |
| Minecraft | `26.2` |
| Fabric Loader | `>= 0.19.3` |
| Fabric API | `>= 0.152.2+26.2` |
| LWJGL NanoVG | `3.3.4` *(bundled)* |

---

## Integration

### Build Types

| Type | Description |
| :-- | :-- |
| **[0]&nbsp;Mod** | Full standalone mod build with bundled NanoVG. Users must install Lucent separately. Reduces final mod size. |
| **[1]&nbsp;Library** | Embedded library build without NanoVG. Lucent is bundled directly into your mod using JIJ, so users do not need a separate installation. |

### Latest Version

[![Latest Release](https://img.shields.io/github/v/release/SILENCE-SIMSOOL/Lucent?color=E0E0E0&style=flat-square)](https://github.com/SILENCE-SIMSOOL/Lucent/releases)

### Version Format

> **`[Lucent Version]`**-**`[Minecraft Version]`**-**`[Build Type]`**

* **`Lucent Version`**: The unique distribution version of the library (e.g., `1.1.3`)
* **`Minecraft Version`**: The target Minecraft environment (e.g., `1.21.11` or `26.2`)
* **`Build Type`**: `0` for Standalone Mod, `1` for Embedded Library

---
**💡 Examples:**
* `1.1.3-1.21.11-0` (Standalone mod build for 1.21.11)
* `1.2.12-26.2-1` (Embedded library build for 26.2)

---

## Gradle Setup

Add the Lucent Maven repository to your `build.gradle`:

```groovy
repositories {
	maven {
		url "https://SILENCE-SIMSOOL.github.io/maven-repo/"
	}
}
```

Then add one of the following dependency setups:

### [0] Standalone Mod

```groovy
dependencies {
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-0"
}
```

### [1] Embedded Library (JIJ)

```groovy
dependencies {
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
	include "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
}
```

---

## Credits

- Config screen UI design was inspired by **[OneConfig](https://github.com/Polyfrost/OneConfig)** by Polyfrost.
- NanoVG rendering structures and utility drawing systems were inspired by **[Odin](https://github.com/odtheking/Odin)** by odtheking.
- Some event features were inspired by **[Devonian](https://github.com/Synnerz/devonian)** by Synnerz.
- Built for **[Minecraft](https://www.minecraft.net/)** using the **[Fabric Project](https://fabricmc.net/)** modding ecosystem.

---

## License

Licensed under the **MIT License**.  
See [`LICENSE`](./LICENSE) for more information.
