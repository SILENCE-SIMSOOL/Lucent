<a id="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
	<a href="https://github.com/SILENCE-SIMSOOL/Lucent">
		<img src="docs/icon.png" alt="Logo" width="128" height="128">
	</a>

	<h1 align="center">Lucent</h1>

	<p align="center">
		A modern, full-featured config management system, customizable HUD engine, and hardware-accelerated Vector UI toolkit for Minecraft Fabric mods.
		<br />
		<br />
		🇰🇷 <a href="./README_KR.md">한국어 문서 보기</a>
		·
		<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">Report Bug</a>
		·
		<a href="https://github.com/SILENCE-SIMSOOL/Lucent/issues">Request Feature</a>
	</p>
</div>

---

## Documentation Categories

To help you get started with specific features, the documentation has been split into dedicated guides:

- ⚙️ **[Config Integration Guide](./docs/en/config.md)** (Example: [ExampleMod.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/examplemod/mods/ExampleMod.java)) — Setting up annotation-driven configuration managers, modules, loading, saving, and launching the settings GUI screen.
- 📺 **[HUD Configuration Guide](./docs/en/hud.md)** (Example: [ExampleHUD.java](https://github.com/SILENCE-SIMSOOL/Lucent/src/main/java/silence/simsool/lucent/examplemod/huds/ExampleHUD.java)) — Creating custom draggable, scalable gameplay HUD elements and widgets.
- 🔤 **[Font Rendering Guide](./docs/en/fonts.md)** — Using the bundled Pretendard typography and printing fonts inside GPU pipelines or Minecraft draw contexts.
- 🧱 **[UI Widgets Guide](./docs/en/widgets.md)** — Catalog of ready-to-use widgets (Toggles, Sliders, Dropdowns, TextBoxes, Color Pickers) with code mapping.
- 👥 **[Profile Management Guide](./docs/en/profiles.md)** — Managing settings profiles and layout configurations per-profile.
- 🎨 **[Theme Configuration Guide](./docs/en/themes.md)** — Styling screens with custom aesthetics (Midnight Cyan, Glass Morphic, Crimson Aurora, etc.).
- 🛠️ **[Utility Classes & NanoVG Guide](./docs/en/utilities.md)** — Hardware-accelerated drawing commands (shapes, drop shadows, gradients) and rich collection of Minecraft helper utilities.

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.18.4 |
| Fabric API | ≥ 0.141.3+1.21.11 |
| LWJGL NanoVG | 3.3.3 (bundled) |

---

## Integration

### Library Types

| Type | Description |
|---|---|
| **[0] mod** | Fully built mod. NanoVG is JIJ'd. Players must install the Lucent mod separately. Efficiently reduces the mod file size. |
| **[1] library** | Slim library build. No NanoVG included. Lucent is bundled via JIJ inside the mod, meaning players do not need to install it separately. |

### Latest Version
- **`1.1.3`**

### Version Format

- Format: `<lucent_version>-<mc_version>-<type>`
- Example: `1.1.3-1.21.11-0`

### Gradle Setup

Add the maven repository and implementation dependency in your `build.gradle` file:

```groovy
repositories {
	maven { url "https://SILENCE-SIMSOOL.github.io/maven-repo/" }
}

dependencies {
	// To implement [0] mod type
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-0"

	// To implement [1] library type (JIJ bundle)
	modImplementation "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
	include "com.github.SILENCE-SIMSOOL:lucent:1.1.3-1.21.11-1"
}
```

---

## Credits & References

- This library references the NanoVG GPU rendering structures and utility drawing methods from the **[Odin](https://github.com/odtheking/Odin)** mod (developed by `odtheking` for Hypixel Skyblock modding).
- Developed for **[Minecraft](https://www.minecraft.net/)** using the **[Fabric Project](https://fabricmc.net/)** modding environment.

---

## License

MIT License — see [LICENSE](./LICENSE) for details.
