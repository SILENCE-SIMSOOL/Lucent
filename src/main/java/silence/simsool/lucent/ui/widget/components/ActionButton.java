package silence.simsool.lucent.ui.widget.components;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.UIWidget;

public class ActionButton extends UIWidget {
	private final String label;
	private Runnable onClick;
	private float hoverAnim = 0.0f;
	private static Image fallbackIcon;

	public ActionButton(int x, int y, int width, int height, String label) {
		super(x, y, width, height);
		this.label = label;
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		hoverAnim = UAnimation.stepProgress(hoverAnim, hovered, 12.0f, delta);

		int bgColor = UColor.withAlpha(UIColors.ACCENT_BLUE, (int)(40 + hoverAnim * 40));
		int borderColor = UColor.withAlpha(UIColors.ACCENT_BLUE, (int)(100 + hoverAnim * 155));
		int textColor = UIColors.TEXT_PRIMARY;

		// Subtle lift/glow on hover
		if (hoverAnim > 0) {
			NVGRenderer.rect(x - hoverAnim * 1, y - hoverAnim * 1, width + hoverAnim * 2, height + hoverAnim * 2, UColor.withAlpha(UIColors.ACCENT_BLUE, (int)(hoverAnim * 20)), 9f);
		}

		NVGRenderer.rect(x, y, width, height, bgColor, 8f);
		NVGRenderer.outlineRect(x, y, width, height, 1, borderColor, 8f);

		if (label == null || label.isEmpty()) {
			if (fallbackIcon == null) {
				try {
					fallbackIcon = NVGRenderer.createImage("/assets/lucent/textures/icons/run.png");
				} catch (Exception e) {}
			}
			if (fallbackIcon != null) NVGRenderer.image(fallbackIcon, x + (width - 16f) / 2f, y + (height - 16f) / 2f, 16f, 16f);
		} else {
			float tw = NVGRenderer.textWidth(label, Fonts.PRETENDARD_MEDIUM, 14f);
			float ty = y + (height - 14f) / 2f;
			NVGRenderer.text(label, x + (width - tw) / 2f, ty, Fonts.PRETENDARD_MEDIUM, textColor, 14f);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && isMouseOver(mouseX, mouseY)) {
			if (onClick != null) onClick.run();
			return true;
		}
		return false;
	}

	public void setOnClick(Runnable onClick) {
		this.onClick = onClick;
	}
}