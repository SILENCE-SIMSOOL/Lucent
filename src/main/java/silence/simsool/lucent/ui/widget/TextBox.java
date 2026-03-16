package silence.simsool.lucent.ui.widget;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class TextBox extends UIWidget {
	private String value;
	private Consumer<String> onChange;
	private boolean focused = false;
	private int cursorPosition;
	private float scrollOffset = 0;

	public TextBox(int x, int y, int width, int height, String initialValue) {
		super(x, y, width, height);
		this.value = initialValue == null ? "" : initialValue;
		this.cursorPosition = this.value.length();
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		int bgColor = 0xFF141416; // C_SEARCHBAR_BG
		int borderColor = focused ? UIColors.ACCENT_BLUE : 0xFF2A2A2D; // C_SEARCHBAR_BDR

		NVGRenderer.rect(x, y, width, height, bgColor, 8f);
		NVGRenderer.outlineRect(x, y, width, height, 1, borderColor, 8f);

		float textAreaX = x + 10;
		float textAreaW = width - 20;
		float textY = y + (height - 14f) / 2f;

		NVGRenderer.push();
		org.lwjgl.nanovg.NanoVG.nvgIntersectScissor(NVGRenderer.getVG(), (int) textAreaX, (int) y, (int) textAreaW, (int) height);
		
		String visibleText = value;
		float cursorX = NVGRenderer.textWidth(value.substring(0, Math.min(cursorPosition, value.length())), Fonts.PRETENDARD_MEDIUM, 14f);
		
		if (cursorX - scrollOffset > textAreaW) {
			scrollOffset = cursorX - textAreaW + 4f;
		} else if (cursorX - scrollOffset < 0) {
			scrollOffset = cursorX;
		}
		
		NVGRenderer.text(visibleText, textAreaX - scrollOffset, textY, Fonts.PRETENDARD_MEDIUM, UIColors.PURE_WHITE, 14f);

		if (focused && (System.currentTimeMillis() / 500) % 2 == 0) {
			float cx = textAreaX + cursorX - scrollOffset;
			NVGRenderer.rect(cx + 1f, textY - 1f, 1.5f, 16f, UIColors.PURE_WHITE, 0f);
		}
		
		NVGRenderer.pop();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			focused = true;
			return true;
		}
		focused = false;
		return false;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (!focused) return false;

		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			if (!value.isEmpty() && cursorPosition > 0) {
				value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
				cursorPosition--;
				if (onChange != null) onChange.accept(value);
			}
			return true;
		} else if (key == GLFW.GLFW_KEY_DELETE) {
			if (!value.isEmpty() && cursorPosition < value.length()) {
				value = value.substring(0, cursorPosition) + value.substring(cursorPosition + 1);
				if (onChange != null) onChange.accept(value);
			}
			return true;
		} else if (key == GLFW.GLFW_KEY_LEFT) {
			if (cursorPosition > 0) cursorPosition--;
			return true;
		} else if (key == GLFW.GLFW_KEY_RIGHT) {
			if (cursorPosition < value.length()) cursorPosition++;
			return true;
		} else if (key == GLFW.GLFW_KEY_HOME) {
			cursorPosition = 0;
			return true;
		} else if (key == GLFW.GLFW_KEY_END) {
			cursorPosition = value.length();
			return true;
		} else if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
			focused = false;
			return true;
		}

		return false;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (!focused) return false;
		
		value = value.substring(0, cursorPosition) + codePoint + value.substring(cursorPosition);
		cursorPosition++;
		if (onChange != null) onChange.accept(value);
		return true;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
		this.cursorPosition = Math.min(cursorPosition, value.length());
	}

	public void setOnChange(Consumer<String> onChange) {
		this.onChange = onChange;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}
}
