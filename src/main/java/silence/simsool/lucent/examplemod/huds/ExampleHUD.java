package silence.simsool.lucent.examplemod.huds;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.examplemod.mods.ExampleMod;
import silence.simsool.lucent.general.enums.Align;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class ExampleHUD extends LucentHUD {

	public ExampleHUD() {
		super(
				"chatting", // (Required): Name to be saved in the config
				ExampleMod.class, // (Optional): Depends on the activation status of the specified mod class. If no class is specified, isEnabled() and disable() must be overridden.
				0.01f, 0.05f, // (Required): Initial position
				1.0f, // (Required): Initial scale
				Align.LEFT // (Required): Initial alignment
		);
	}

	// (Required): Set the rendering type. Use NANOVG for NVG pipelines (NVGRenderer); otherwise, use MINECRAFT.
	@Override
	public RenderType getRenderType() {
		return RenderType.NANOVG;
		// return RenderType.MINECRAFT;
	}

	// (Required): Set the hitbox width of this HUD item in the EditHUDScreen.
	@Override
	public float getPreviewWidth() {
		return 160;
	}

	// (Required): Set the hitbox height of this HUD item in the EditHUDScreen.
	@Override
	public float getPreviewHeight() {
		return 24;
	}

// (Optional): Set the visibility of the HUD rendering. Recommended when conditions other than the module's activation status are needed.
//	@Override
//	public boolean isEnabled() {
//		return Lucent.config.isModuleEnabled(ExampleMod.class) && ChattingMod.AdvancedConfig;
//	}

// (Optional): Set the action to perform when the Delete button is pressed in the HUD menu.
//	@Override
//	public void disable() {
//		Lucent.config.getModule(ExampleMod.class).isEnabled = false;
//	}

	// (Required): Write the logic for HUD rendering.
	@Override
	public void draw(GuiGraphics graphics) {
		if (LucentHUD.isEditHudOpen || UDisplay.isDebugScreen()) return;
		renderPanel(graphics, "Hello World");
	}

	// (Required): Write the preview logic to be rendered in the EditHUDScreen.
	@Override
	public void preview(GuiGraphics graphics) {
		renderPanel(graphics, "Preview Text");
	}

	private void renderPanel(GuiGraphics guiGraphics, String text) {
		float rx = getRenderX(), ry = getRenderY();
		float sw = getScaledWidth(), sh = getScaledHeight();
		float fs = 14f * scale;
		NVGRenderer.rect(rx, ry, sw, sh, UIColors.withAlpha(UIColors.PURE_BLACK, 130), 4f * scale);
		NVGRenderer.text(text, rx + 8 * scale, ry + (sh - fs) / 2f, Fonts.PRETENDARD, UIColors.PURE_WHITE, fs);
	}

}