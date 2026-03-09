package silence.simsool.lucent.ui.widget.base;

import net.minecraft.client.gui.GuiGraphics;

public abstract class UIWidget {
	protected int x, y, width, height;
	protected boolean hovered = false;
	protected boolean focused = false;
	protected boolean enabled = true;
	protected boolean visible = true;

	public UIWidget(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * 메인 렌더 (일반 렌더 패스)
	 * @param delta 틱 간격 (초 단위)
	 */
	public final void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		if (!visible) return;
		hovered = enabled && isMouseOver(mouseX, mouseY);
		renderWidget(ctx, mouseX, mouseY, delta);
	}

	protected abstract void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta);

	public void renderOverlay(GuiGraphics ctx, int mouseX, int mouseY, float delta) {}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!enabled || !visible) return false;
		return false;
	}

	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (!enabled || !visible) return false;
		return false;
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (!enabled || !visible) return false;
		return false;
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return false;
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!enabled || !focused) return false;
		return false;
	}

	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return false;
	}

	public boolean charTyped(char chr, int modifiers) {
		if (!enabled || !focused) return false;
		return false;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
		onFocusChanged(focused);
	}

	protected void onFocusChanged(boolean focused) {}

	public boolean isFocused() {
		return focused;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isHovered() {
		return hovered;
	}
}