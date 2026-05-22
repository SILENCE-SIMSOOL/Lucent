# HUD Configuration Guide

This guide explains how to create custom draggable, scalable Head-Up Display (HUD) overlay elements inside your mod using Lucent's HUD system.

---

## 1. Extending the HUD Base Class

To define a custom HUD element, create a class that extends [LucentHUD](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/general/models/abstracts/LucentHUD.java).

Refer to the built-in [ExampleHUD.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/examplemod/huds/ExampleHUD.java) for a clean reference design.

### Example HUD Code:

```java
import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.enums.Align;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class MyStatusHUD extends LucentHUD {

	public MyStatusHUD() {
		super(
			"my_hud_id",      // Key name saved in the configuration file
			MyMod.class,      // (Optional) Dependency module class. Shows HUD only if the module is enabled
			0.05f, 0.1f,      // Default coordinates ratio on screen (0.0 ~ 1.0)
			1.0f,             // Default display scale multiplier
			Align.LEFT        // Default snap alignment
		);
	}

	@Override
	public RenderType getRenderType() {
		// Set to RenderType.NANOVG for GPU pipeline rendering, or RenderType.MINECRAFT for vanilla texturing
		return RenderType.NANOVG;
	}

	@Override
	public float getPreviewWidth() {
		return 150f; // Width of the hitbox element in the editor screen
	}

	@Override
	public float getPreviewHeight() {
		return 20f;  // Height of the hitbox element in the editor screen
	}

	@Override
	public void draw(GuiGraphics graphics) {
		// Suppress HUD rendering if the layout editor screen is open, or F3 debug overlay is active
		if (LucentHUD.isEditHudOpen) return;
		
		// Rendering logic during active gameplay
		drawHUDContent(graphics, "Speed: 10 m/s");
	}

	@Override
	public void preview(GuiGraphics graphics) {
		// Placeholder logic drawn only inside the layout editor screen
		drawHUDContent(graphics, "HUD Preview");
	}

	private void drawHUDContent(GuiGraphics graphics, String text) {
		float rx = getRenderX(); // Raw computed X screen coordinate
		float ry = getRenderY(); // Raw computed Y screen coordinate
		float sw = getScaledWidth();  // Computed hitbox width (width * scale)
		float sh = getScaledHeight(); // Computed hitbox height (height * scale)

		// Draw semi-transparent background using NVGRenderer
		NVGRenderer.rect(rx, ry, sw, sh, UIColors.withAlpha(UIColors.PURE_BLACK, 150), 6f * scale);

		// Draw text
		NVGRenderer.text(
			text, 
			rx + 6 * scale, 
			ry + (sh - 14f * scale) / 2f, 
			Fonts.PRETENDARD, 
			UIColors.PURE_WHITE, 
			14f * scale
		);
	}
}
```

---

## 2. Registering and Loading HUD elements

Use the [HUDManager](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/hud/HUDManager.java) inside your client initialization block to register your layout element and load stored user adjustments.

```java
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.config.api.LucentAPI;

@Override
public void onInitializeClient() {
	// Register the HUD to your specific ModManager config profile
	LucentAPI.registerHUD(config, new MyStatusHUD());

	// Load layout states (position/scale adjustments)
	HUDManager.INSTANCE.loadAll();
}
```

---

## 3. Customizing HUD Positions in-game

To adjust the positions, alignment, and scale, users need to open the HUD Editor GUI.

Generate the Minecraft `Screen` instance using `LucentAPI` and apply it:

```java
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.config.api.LucentAPI;

// Opens the drag-and-drop overlay interface. Changes are saved automatically on close.
Screen editHudScreen = LucentAPI.createEditHUDScreen(config);
Minecraft.getInstance().setScreen(editHudScreen);
```
Inside this screen, users can:
- **Left Click & Drag** elements to move them across the screen layout.
- **Scroll Mouse Wheel** over elements to scale them up or down.
- **Right-Click** elements to snap align them to sides or toggle element specific settings.
