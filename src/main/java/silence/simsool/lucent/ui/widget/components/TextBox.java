package silence.simsool.lucent.ui.widget.components;

import java.util.Stack;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import silence.simsool.lucent.general.utils.useful.UDesktop;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.UIWidget;

public class TextBox extends UIWidget {
	private String value;
	private Consumer<String> onChange;
	private boolean focused = false;
	private int cursorPosition;
	private int highlightPosition;
	private float scrollOffset = 0;

	private static class TextState {
		final String value;
		final int cursorPosition;
		final int highlightPosition;

		TextState(String value, int cursorPosition, int highlightPosition) {
			this.value = value;
			this.cursorPosition = cursorPosition;
			this.highlightPosition = highlightPosition;
		}
	}

	private final Stack<TextState> undoStack = new Stack<>();
	private final Stack<TextState> redoStack = new Stack<>();

	public TextBox(int x, int y, int width, int height, String initialValue) {
		super(x, y, width, height);
		this.value = initialValue == null ? "" : initialValue;
		this.cursorPosition = this.value.length();
		this.highlightPosition = this.cursorPosition;
	}

	@Override
	protected void renderWidget(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
		int bgColor = UIColors.withAlpha(UIColors.PURE_BLACK, 60);
		int borderColor = focused ? UIColors.ACCENT_BLUE : UIColors.ITEM_BORDER;

		// Focus glow
		if (focused) NVGRenderer.rect(x - 2, y - 2, width + 4, height + 4, UIColors.withAlpha(UIColors.ACCENT_BLUE, 30), 10f);

		NVGRenderer.rect(x, y, width, height, bgColor, 8f);
		NVGRenderer.outlineRect(x, y, width, height, 1, borderColor, 8f);

		float textAreaX = x + 10;
		float textAreaW = width - 20;
		float textY = y + (height - 14f) / 2f;

		NVGRenderer.push();
		NanoVG.nvgIntersectScissor(NVGRenderer.getVG(), (int) textAreaX, (int) y, (int) textAreaW, (int) height);
		
		String visibleText = value;
		float cursorX = NVGRenderer.textWidth(value.substring(0, Math.min(cursorPosition, value.length())), Fonts.PRETENDARD_MEDIUM, 14f);
		
		if (cursorX - scrollOffset > textAreaW) scrollOffset = cursorX - textAreaW + 4f;
		else if (cursorX - scrollOffset < 0) scrollOffset = cursorX;
		
		// Draw selection highlight
		int selMin = Math.min(cursorPosition, highlightPosition);
		int selMax = Math.max(cursorPosition, highlightPosition);
		if (focused && selMin != selMax) {
			float selectionMinX = NVGRenderer.textWidth(value.substring(0, Math.min(selMin, value.length())), Fonts.PRETENDARD_MEDIUM, 14f);
			float selectionMaxX = NVGRenderer.textWidth(value.substring(0, Math.min(selMax, value.length())), Fonts.PRETENDARD_MEDIUM, 14f);
			float x1 = textAreaX + selectionMinX - scrollOffset;
			float x2 = textAreaX + selectionMaxX - scrollOffset;
			NVGRenderer.rect(x1, textY - 1f, x2 - x1, 16f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 100), 0f);
		}

		NVGRenderer.text(visibleText, textAreaX - scrollOffset, textY, Fonts.PRETENDARD_MEDIUM, UIColors.TEXT_PRIMARY, 14f);

		if (focused && (System.currentTimeMillis() / 500) % 2 == 0) {
			float cx = textAreaX + cursorX - scrollOffset;
			NVGRenderer.rect(cx + 1f, textY - 1f, 1.5f, 16f, UIColors.ACCENT_BLUE, 0f);
		}
		
		NVGRenderer.pop();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			focused = true;

			float clickX = (float) mouseX - (x + 10) + scrollOffset;
			int bestPos = 0;
			float minDiff = Float.MAX_VALUE;

			for (int i = 0; i <= value.length(); i++) {
				float w = NVGRenderer.textWidth(value.substring(0, i), Fonts.PRETENDARD_MEDIUM, 14f);
				float diff = Math.abs(w - clickX);
				if (diff < minDiff) {
					minDiff = diff;
					bestPos = i;
				}
			}
			cursorPosition = bestPos;
			highlightPosition = cursorPosition;

			return true;
		}
		focused = false;
		scrollOffset = 0;
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (focused) {
			float clickX = (float) mouseX - (x + 10) + scrollOffset;
			int bestPos = 0;
			float minDiff = Float.MAX_VALUE;

			for (int i = 0; i <= value.length(); i++) {
				float w = NVGRenderer.textWidth(value.substring(0, i), Fonts.PRETENDARD_MEDIUM, 14f);
				float diff = Math.abs(w - clickX);
				if (diff < minDiff) {
					minDiff = diff;
					bestPos = i;
				}
			}
			cursorPosition = bestPos;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (!focused) return false;

		boolean isShiftDown = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
		boolean isCtrlDown = (modifiers & GLFW.GLFW_MOD_CONTROL) != 0;

		if (isCtrlDown) {
			if (key == GLFW.GLFW_KEY_A) {
				highlightPosition = 0;
				cursorPosition = value.length();
				return true;
			} else if (key == GLFW.GLFW_KEY_C) {
				String selText = getSelectedText();
				if (!selText.isEmpty()) {
					UDesktop.setClipboardString(selText);
				}
				return true;
			} else if (key == GLFW.GLFW_KEY_V) {
				String clipboard = UDesktop.getClipboardString();
				if (clipboard != null) {
					writeText(clipboard);
				}
				return true;
			} else if (key == GLFW.GLFW_KEY_X) {
				String selText = getSelectedText();
				if (!selText.isEmpty()) {
					UDesktop.setClipboardString(selText);
					writeText("");
				}
				return true;
			} else if (key == GLFW.GLFW_KEY_Z) {
				undo();
				return true;
			} else if (key == GLFW.GLFW_KEY_Y) {
				redo();
				return true;
			}
		}

		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			deleteText(true);
			return true;

		} else if (key == GLFW.GLFW_KEY_DELETE) {
			deleteText(false);
			return true;

		} else if (key == GLFW.GLFW_KEY_LEFT) {
			int nextPos = isCtrlDown ? getWordSkipPosition(-1) : cursorPosition - 1;
			moveCursorTo(nextPos, isShiftDown);
			return true;

		} else if (key == GLFW.GLFW_KEY_RIGHT) {
			int nextPos = isCtrlDown ? getWordSkipPosition(1) : cursorPosition + 1;
			moveCursorTo(nextPos, isShiftDown);
			return true;

		} else if (key == GLFW.GLFW_KEY_HOME) {
			moveCursorTo(0, isShiftDown);
			return true;

		} else if (key == GLFW.GLFW_KEY_END) {
			moveCursorTo(value.length(), isShiftDown);
			return true;

		} else if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER || key == GLFW.GLFW_KEY_ESCAPE) {
			focused = false;
			scrollOffset = 0;
			return true;
		}

		return false;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if (!focused) return false;
		writeText(String.valueOf(codePoint));
		return true;
	}

	@Override
	protected void onFocusChanged(boolean focused) {
		this.focused = focused;
		if (!focused) {
			scrollOffset = 0;
		}
	}

	private void writeText(String text) {
		saveToUndo();
		int selMin = Math.min(cursorPosition, highlightPosition);
		int selMax = Math.max(cursorPosition, highlightPosition);
		value = value.substring(0, selMin) + text + value.substring(selMax);
		cursorPosition = selMin + text.length();
		highlightPosition = cursorPosition;
		if (onChange != null) onChange.accept(value);
	}

	private void deleteText(boolean backspace) {
		int selMin = Math.min(cursorPosition, highlightPosition);
		int selMax = Math.max(cursorPosition, highlightPosition);
		if (selMin != selMax) {
			saveToUndo();
			value = value.substring(0, selMin) + value.substring(selMax);
			cursorPosition = selMin;
			highlightPosition = cursorPosition;
			if (onChange != null) onChange.accept(value);
		} else {
			if (backspace) {
				if (cursorPosition > 0) {
					saveToUndo();
					value = value.substring(0, cursorPosition - 1) + value.substring(cursorPosition);
					cursorPosition--;
					highlightPosition = cursorPosition;
					if (onChange != null) onChange.accept(value);
				}
			} else {
				if (cursorPosition < value.length()) {
					saveToUndo();
					value = value.substring(0, cursorPosition) + value.substring(cursorPosition + 1);
					highlightPosition = cursorPosition;
					if (onChange != null) onChange.accept(value);
				}
			}
		}
	}

	private int getWordSkipPosition(int wordCount) {
		int pos = cursorPosition;
		boolean flag = wordCount < 0;
		int count = Math.abs(wordCount);
		for (int i = 0; i < count; ++i) {
			if (flag) {
				while (pos > 0 && value.charAt(pos - 1) == ' ') {
					--pos;
				}
				while (pos > 0 && value.charAt(pos - 1) != ' ') {
					--pos;
				}
			} else {
				int len = value.length();
				while (pos < len && value.charAt(pos) == ' ') {
					++pos;
				}
				while (pos < len && value.charAt(pos) != ' ') {
					++pos;
				}
			}
		}
		return pos;
	}

	private void moveCursorTo(int newPosition, boolean select) {
		cursorPosition = Math.max(0, Math.min(newPosition, value.length()));
		if (!select) {
			highlightPosition = cursorPosition;
		}
	}

	private String getSelectedText() {
		int selMin = Math.min(cursorPosition, highlightPosition);
		int selMax = Math.max(cursorPosition, highlightPosition);
		return value.substring(Math.min(selMin, value.length()), Math.min(selMax, value.length()));
	}

	private void saveToUndo() {
		if (undoStack.isEmpty() || !undoStack.peek().value.equals(value)) {
			undoStack.push(new TextState(value, cursorPosition, highlightPosition));
			redoStack.clear();
		}
	}

	private void undo() {
		if (!undoStack.isEmpty()) {
			redoStack.push(new TextState(value, cursorPosition, highlightPosition));
			TextState state = undoStack.pop();
			this.value = state.value;
			this.cursorPosition = state.cursorPosition;
			this.highlightPosition = state.highlightPosition;
			if (onChange != null) onChange.accept(value);
		}
	}

	private void redo() {
		if (!redoStack.isEmpty()) {
			undoStack.push(new TextState(value, cursorPosition, highlightPosition));
			TextState state = redoStack.pop();
			this.value = state.value;
			this.cursorPosition = state.cursorPosition;
			this.highlightPosition = state.highlightPosition;
			if (onChange != null) onChange.accept(value);
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? "" : value;
		this.cursorPosition = Math.min(cursorPosition, this.value.length());
		this.highlightPosition = Math.min(highlightPosition, this.value.length());
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