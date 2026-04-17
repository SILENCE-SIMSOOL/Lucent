package silence.simsool.lucent.ui.widget.components.color;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.utils.useful.UDisplay;
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
	private boolean pickerAbove = false;
	private ColorPicker colorPicker = null;
	private float hoverAnim = 0f;
	private static final float HOVER_SPEED = 8f;
	private Consumer<Integer> onChange;

	public ColorPickerButton(int x, int y, int width, int height, int initialColor) {
		super(x, y, width, height);
		this.color = initialColor;
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
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
	public void renderOverlay(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		if (pickerOpen && colorPicker != null) {
			syncPickerPosition();
			colorPicker.render(ctx, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!enabled || !visible || button != 0) return false;

		if (pickerOpen && colorPicker != null) {
			syncPickerPosition();
			if (colorPicker.mouseClicked(mouseX, mouseY, button)) return true;
			if (colorPicker == null) return true;

			if (!colorPicker.isMouseOver(mouseX, mouseY)) {
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
			syncPickerPosition();
			return colorPicker.mouseDragged(mouseX, mouseY, button, dx, dy);
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (pickerOpen && colorPicker != null) {
			syncPickerPosition();
			return colorPicker.mouseReleased(mouseX, mouseY, button);
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
		int pickerH = ColorPicker.getPreferredHeight();
	    int pickerW = ColorPicker.getPreferredWidth();
	    pickerAbove = false;

	    float screenH = UDisplay.getScreenHeight() / NVGRenderer.getStandardGuiScale();;
	    float screenW = UDisplay.getScreenWidth() / NVGRenderer.getStandardGuiScale();;

	    int py = y + height + 4;
	    if (py + pickerH > screenH) {
	        py = y - pickerH - 4;
	        if (py < 10) py = 10;
	        pickerAbove = true;
	    }

	    int px = x;
	    if (px + pickerW > screenW) px = (int) screenW - pickerW - 10;
	    if (px < 10) px = 10;

	    colorPicker = new ColorPicker(px, py, color);
	    colorPicker.setOnConfirm(newColor -> setColor(newColor));
	    colorPicker.setOnCancel(() -> closePicker(false));
	    pickerOpen = true;
	}

	private void syncPickerPosition() {
		if (colorPicker == null) return;
	    int pickerW = ColorPicker.getPreferredWidth();
	    int pickerH = ColorPicker.getPreferredHeight();

	    int px = this.x;
	    int py = pickerAbove ? this.y - pickerH - 4 : this.y + this.height + 4;

	    float screenW = UDisplay.getScreenWidth() / NVGRenderer.getStandardGuiScale();
	    if (px + pickerW > screenW - 10) px = (int) screenW - pickerW - 10;
	    if (px < 10) px = 10;

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