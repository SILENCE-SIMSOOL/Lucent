package silence.simsool.lucent.client.dev.examplemods;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class ChattingHUD extends LucentHUD {

	private static final float BASE_W = 160f;
	private static final float BASE_H = 24f;

	public ChattingHUD() {
		super("chatting_hud", 0.01f, 0.05f, 1.0f, HUDAlignment.LEFT);
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
	public boolean isEnabled() {
		return Lucent.config.getModule(ChattingMod.class).isEnabled;
	}

	@Override
	public void disable() {
		Lucent.config.getModule(ChattingMod.class).isEnabled = false;
	}

	@Override
	public void draw() {
		if (LucentHUD.isEditHudOpen || UDisplay.isDebugScreen()) return;
		renderPanel("Hello World");
	}

	@Override
	public void preview() {
		renderPanel("Chat & Emote");
	}

	private void renderPanel(String text) {
		float rx = getRenderX(), ry = getRenderY();
		float sw = getScaledWidth(), sh = getScaledHeight();
		float fs = 14f * scale;

		NVGRenderer.rect(rx, ry, sw, sh, UIColors.withAlpha(UIColors.PURE_BLACK, 130), 4f * scale);
		NVGRenderer.text(text, rx + 8 * scale, ry + (sh - fs) / 2f, Fonts.PRETENDARD, UIColors.PURE_WHITE, fs);
	}

}