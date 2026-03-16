package silence.simsool.lucent.ui.widget;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class ActionButton extends UIWidget {
	private final String label;
	private Runnable onClick;
	private float hoverAnim = 0.0f;

	public ActionButton(int x, int y, int width, int height, String label) {
		super(x, y, width, height);
		this.label = label;
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		hoverAnim = UAnimation.stepProgress(hoverAnim, hovered, 8.0f, delta);

		int bgColor = UAnimation.lerpColor(0xFF35353A, 0xFF45454A, hoverAnim);
		int textColor = hovered ? UIColors.PURE_WHITE : 0xFFCECECE;

		NVGRenderer.rect(x, y, width, height, bgColor, 8f);
		NVGRenderer.outlineRect(x, y, width, height, 1, hovered ? UIColors.ACCENT_BLUE : 0xFF4A4A4F, 8f);

		float tw = NVGRenderer.textWidth(label, Fonts.PRETENDARD_MEDIUM, 14f);
		NVGRenderer.text(label, x + (width - tw) / 2f, y + (height - 14f) / 2f, Fonts.PRETENDARD_MEDIUM, textColor, 14f);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && isMouseOver(mouseX, mouseY)) {
			if (onClick != null) {
				onClick.run();
			}
			return true;
		}
		return false;
	}

	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}
}
