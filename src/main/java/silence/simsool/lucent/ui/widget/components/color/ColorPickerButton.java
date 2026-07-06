package silence.simsool.lucent.ui.widget.components.color;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.general.utils.useful.UScreen;
import silence.simsool.lucent.ui.screens.ConfigScreen;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.UIWidget;

public class ColorPickerButton extends UIWidget {
	private int color = 0xFFFF0000;
	private int borderColor      = 0xFF555555;
	private int borderHoverColor = 0xFF888888;
	private int borderFocusColor = 0xFF66AAFF;

	private boolean pickerOpen = false;
	//private boolean pickerAbove = false;
	private ColorPicker colorPicker = null;
	private float hoverAnim = 0f;
	private static final float HOVER_SPEED = 8f;
	private Consumer<Integer> onChange;

	public ColorPickerButton(int x, int y, int width, int height, int initialColor) {
		super(x, y, width, height);
		this.color = initialColor;
	}

	@Override
	protected void renderWidget(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
		NVGRenderer.push();
		hoverAnim = UAnimation.lerpSnap(hoverAnim, hovered ? 1f : 0f, HOVER_SPEED, delta);
		int round = 8;
		int border = pickerOpen ? borderFocusColor : UColor.lerpColor(borderColor, borderHoverColor, hoverAnim);

		NVGRenderer.drawCheckerboard(x, y, width, height, 4, 0xFFCCCCCC, 0xFF999999, round);
		NVGRenderer.rect(x, y, width, height, color, round);
		NVGRenderer.outlineRect(x, y, width, height, 1, border, round);

		if (hoverAnim > 0.01f) {
			int overlayAlpha = (int)(hoverAnim * 30);
			NVGRenderer.rect(x, y, width, height, UColor.withAlpha(0xFFFFFFFF, overlayAlpha), round);
		}
		NVGRenderer.pop();
	}

	@Override
	public void renderOverlay(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
		if (pickerOpen && colorPicker != null) {
			syncPickerPosition(true);
			colorPicker.render(ctx, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!enabled || !visible || button != 0) return false;

		if (pickerOpen && colorPicker != null) {
			double scrollOffset = 0;
			if (UScreen.getScreen() instanceof ConfigScreen configScreen) {
				scrollOffset = configScreen.getScrollOffset();
			}
			syncPickerPosition(false);
			if (colorPicker.mouseClicked(mouseX, mouseY - scrollOffset, button)) return true;
			if (colorPicker == null) return true;

			if (!colorPicker.isMouseOver(mouseX, mouseY - scrollOffset)) {
				closePicker(false);
				return true;
			}
		}

		if (isMouseOver(mouseX, mouseY)) {
			openPicker();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		if (pickerOpen && colorPicker != null) {
			double scrollOffset = 0;
			if (UScreen.getScreen()  instanceof ConfigScreen configScreen) {
				scrollOffset = configScreen.getScrollOffset();
			}
			syncPickerPosition(false);
			return colorPicker.mouseDragged(mouseX, mouseY - scrollOffset, button, dx, dy);
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (pickerOpen && colorPicker != null) {
			double scrollOffset = 0;
			if (UScreen.getScreen()  instanceof ConfigScreen configScreen) {
				scrollOffset = configScreen.getScrollOffset();
			}
			syncPickerPosition(false);
			return colorPicker.mouseReleased(mouseX, mouseY - scrollOffset, button);
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (pickerOpen && colorPicker != null) {
			return colorPicker.keyPressed(keyCode, scanCode, modifiers);
		}
		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (pickerOpen && colorPicker != null) {
			return colorPicker.charTyped(chr, modifiers);
		}
		return false;
	}

	private void openPicker() {
		colorPicker = new ColorPicker(x, y, color);
		colorPicker.setOnConfirm(newColor -> setColor(newColor));
		colorPicker.setOnCancel(() -> closePicker(false));
		pickerOpen = true;
		syncPickerPosition(false);
	}

	private void syncPickerPosition(boolean isRendering) {
		if (colorPicker == null) return;
		int pickerW = ColorPicker.getPreferredWidth();
		int pickerH = ColorPicker.getPreferredHeight();

		float uiScale = 1.0f;
		float scrollOffset = 0f;
		if (UScreen.getScreen()  instanceof ConfigScreen configScreen) {
			uiScale = configScreen.getUiScale();
			if (!isRendering) {
				scrollOffset = (float) configScreen.getScrollOffset();
			}
		}

		float scale = NVGRenderer.getStandardGuiScale() * uiScale;
		float screenW = UDisplay.getScreenWidth() / scale;
		float screenH = UDisplay.getScreenHeight() / scale;

		int px = this.x;
		int py;
		int actualY = this.y - (int) scrollOffset;

		if (actualY + this.height / 2 < screenH / 2) {
			py = actualY + this.height + 4;
			//pickerAbove = false;
		} else {
			py = actualY - pickerH - 4;
			//pickerAbove = true;
		}

		if (px + pickerW > screenW - 10) px = (int) screenW - pickerW - 10;
		if (px < 10) px = 10;

		if (py + pickerH > screenH - 10) py = (int) screenH - pickerH - 10;
		if (py < 10) py = 10;

		colorPicker.setPosition(px, py);
	}

	private void closePicker(boolean confirmed) {
		pickerOpen = false;
		colorPicker = null;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
		if (onChange != null) onChange.accept(color);
	}

	public void setOnChange(Consumer<Integer> onChange) {
		this.onChange = onChange;
	}

	public boolean isPickerOpen() {
		return pickerOpen;
	}
}