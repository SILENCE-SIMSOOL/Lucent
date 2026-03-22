package silence.simsool.lucent.client.dev.examplemods;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.abstracts.LucentHUD;
import silence.simsool.lucent.general.enums.HudAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class ChattingHud extends LucentHUD {

	private static final float BASE_W = 160f;
	private static final float BASE_H = 24f;

	public ChattingHud() {
		super("chatting_hud", 0.01f, 0.05f, 1.0f, HudAlignment.LEFT);
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.NANOVG;
	}

	@Override
	public float getPreviewWidth() {
		return BASE_W;
	}

	@Override
	public float getPreviewHeight() {
		return BASE_H;
	}

	@Override
	public void draw() {
		if (Lucent.config.getModule(ChattingMod.class).isEnabled) {
			if (LucentHUD.isEditHudOpen || UDisplay.isDebugScreen()) return;
			renderPanel("Hello World");
		}
	}

	@Override
	public void preview() {
		if (Lucent.config.getModule(ChattingMod.class).isEnabled) {
			renderPanel("Chat & Emote");
		}
	}

	private void renderPanel(String text) {
		float rx = getRenderX(), ry = getRenderY();
		float sw = getScaledWidth(), sh = getScaledHeight();
		float fs = 14f * scale;

		NVGRenderer.rect(rx, ry, sw, sh, UIColors.withAlpha(UIColors.PURE_BLACK, 130), 4f * scale);
		NVGRenderer.text(text, rx + 8 * scale, ry + (sh - fs) / 2f, Fonts.PRETENDARD, UIColors.PURE_WHITE, fs);
	}

}