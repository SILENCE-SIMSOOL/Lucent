package silence.simsool.lucent.client.dev.examplehuds;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.client.dev.examplemods.ChattingMod;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class ChattingHUD extends LucentHUD {

	public ChattingHUD() {
		super("chatting", ChattingMod.class, 0.01f, 0.05f, 1.0f, HUDAlignment.LEFT);
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.NANOVG;
	}

	@Override
	public float getPreviewWidth() {
		return 160;
	}

	@Override
	public float getPreviewHeight() {
		return 24;
	}

	@Override
	public void draw(GuiGraphics graphics) {
		if (LucentHUD.isEditHudOpen || UDisplay.isDebugScreen()) return;
		renderPanel(graphics, "Hello World");
	}

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