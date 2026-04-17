package silence.simsool.lucent.ui.widget.components;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.ULayout;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.UIWidget;

public class Slider extends UIWidget {
	public enum SliderType {
		DOUBLE, FLOAT, INT
	}

	private final int   TRACK_H =          6;
	private final int   THUMB_R =         10;
	private final int   THUMB_HOVER_R =   11;
	private final int   LABEL_TO_TRACK =  14; // min/max 텍스트 ~ 트랙 사이 여백
	private final int   TRACK_TO_LABEL =  14; // 트랙 ~ max 텍스트 사이 여백
	private final int   LABEL_TO_BOX =    22;   // max 텍스트 ~ 값 상자 사이 여백
	private final float LABEL_FONT =      16;
	private final float VALUE_FONT =      16;
	private final int   VALUE_BOX_H =     22;
	private final int   VALUE_BOX_PAD =   12;

	private double min;
	private double max;
	private double step;
	private double value;
	private SliderType type = SliderType.DOUBLE;

	private float thumbScale = 0.0f;
	private float displayX = -1;
	private static final float THUMB_ANIM_SPEED = 12f;
	private static final float SCALE_ANIM_SPEED = 8f;

	private boolean isDragging = false;

	private boolean inputMode = false;
	private String inputBuffer = "";
	private int inputCursor = 0;

	private Consumer<Double> onChange;
	private Consumer<Double> onRelease;
	private int trackX, trackW;

	public Slider(int x, int y, int width, int height, double min, double max, double step, double initialValue) {
		this(x, y, width, height, min, max, step, initialValue, SliderType.DOUBLE);
	}

	public Slider(int x, int y, int width, int height, double min, double max, double step, double initialValue, SliderType type) {
		super(x, y, width, height);
		this.type = type;
		this.min = min;
		this.max = max;
		this.step = (type == SliderType.INT) ? 1.0 : step;
		this.value = UAnimation.clamp(initialValue, min, max);
	}

	public static Slider SLIDER_INT(int x, int y, int w, int h, int min, int max, int initial) {
		return new Slider(x, y, w, h, min, max, 1.0, initial, SliderType.INT);
	}

	public static Slider SLIDER_FLOAT(int x, int y, int w, int h, float min, float max, float step, float initial) {
		return new Slider(x, y, w, h, min, max, step, initial, SliderType.FLOAT);
	}

	public static Slider SLIDER_DOUBLE(int x, int y, int w, int h, double min, double max, double step, double initialValue) {
		return new Slider(x, y, w, h, min, max, step, initialValue, SliderType.DOUBLE);
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		String currentValText = inputMode ? inputBuffer : formatValue(value);
		int VALUE_BOX_W = (int) NVGRenderer.textWidth(currentValText, Fonts.PRETENDARD_MEDIUM, VALUE_FONT) + VALUE_BOX_PAD * 2;
		int minLabelW = (int) NVGRenderer.textWidth(formatValue(min), Fonts.PRETENDARD, LABEL_FONT);
		int maxLabelW = (int) NVGRenderer.textWidth(formatValue(max), Fonts.PRETENDARD, LABEL_FONT);

		// 좌→우:
		// [minLabel][LABEL_TO_TRACK][track][TRACK_TO_LABEL][maxLabel][LABEL_TO_BOX][valueBox]
		trackX = x + minLabelW + LABEL_TO_TRACK;
		trackW = width - minLabelW - LABEL_TO_TRACK - TRACK_TO_LABEL - maxLabelW - LABEL_TO_BOX - VALUE_BOX_W;
		int trackY = y + height / 2 - TRACK_H / 2;
		int labelY = y + (height - (int) LABEL_FONT) / 2;
		int valueBoxX = trackX + trackW + TRACK_TO_LABEL + maxLabelW + LABEL_TO_BOX;
		int valueBoxY = y + (height - VALUE_BOX_H) / 2;

		// ── 원 위치 애니메이션 ────────────────────────────────────────
		float targetThumbX = (float) ULayout.valueToTrackX(value, min, max, trackX, trackW);
		if (displayX < 0) displayX = targetThumbX;
		displayX = UAnimation.lerpSnap(displayX, targetThumbX, THUMB_ANIM_SPEED, delta, 0.5f);

		float targetScale = (isDragging || (hovered && isMouseOverThumb(mouseX, mouseY))) ? 1f : 0f;
		thumbScale = UAnimation.lerpSnap(thumbScale, targetScale, SCALE_ANIM_SPEED, delta);
		float currentRadius = UAnimation.lerp(THUMB_R, THUMB_HOVER_R, thumbScale);

		// ── 트랙 ─────────────────────────────────────────────────────
		NVGRenderer.rect(trackX, trackY, trackW, TRACK_H, UIColors.MUTED, TRACK_H / 2f);
		int fillW = (int) (displayX - trackX);
		if (fillW > 0) NVGRenderer.rect(trackX, trackY, fillW, TRACK_H, UIColors.ACCENT_BLUE, TRACK_H / 2);

		// ── 원 ───────────────────────────────────────────────────────
		int thumbCy = y + height / 2;
		
//      Shadow effect, but I don't think it's good
//		float sr = currentRadius + 1;
//		NVGRenderer.dropShadow(displayX - sr, thumbCy - sr, sr * 2, sr * 2, 4f, 0f, sr);
		NVGRenderer.circle(displayX, thumbCy, currentRadius, UIColors.PURE_WHITE);

		// ── 드래그 중 현재값 말풍선 ──────────────────────────────────
		if (isDragging || thumbScale > 0.1f) {
			String curValText = formatValue(value);
			int curValW = (int) NVGRenderer.textWidth(curValText, Fonts.PRETENDARD_MEDIUM, LABEL_FONT);
			int curValX = (int) displayX - curValW / 2;
			int curValY = thumbCy - (int) currentRadius - (int) LABEL_FONT - 4;
			int bgPad = 4;
			NVGRenderer.rect(curValX - bgPad, curValY - bgPad / 2, curValW + bgPad * 2, LABEL_FONT + bgPad - 1, 0xB2000000, 4);
			NVGRenderer.text(curValText, curValX, curValY, Fonts.PRETENDARD_MEDIUM, UIColors.LIGHT_GRAY, LABEL_FONT);
		}

		// ── min / max 라벨 ───────────────────────────────────────────
		NVGRenderer.text(formatValue(min), x, labelY, Fonts.PRETENDARD, UIColors.MUTED, LABEL_FONT);
		NVGRenderer.text(formatValue(max), trackX + trackW + TRACK_TO_LABEL, labelY, Fonts.PRETENDARD, UIColors.MUTED, LABEL_FONT);

		// ── 값 상자 ──────────────────────────────────────────────────
		renderValueBox(valueBoxX, valueBoxY, VALUE_BOX_W, VALUE_BOX_H, VALUE_FONT);
	}

	private void renderValueBox(int bx, int by, int bw, int bh, float fontSize) {
		NVGRenderer.outlineRect(bx, by, bw, bh, 1, UIColors.DARK, 8f);

		String displayText = inputMode ? inputBuffer : formatValue(value);
		int tx = bx + (bw - (int) NVGRenderer.textWidth(displayText, Fonts.PRETENDARD_MEDIUM, fontSize)) / 2;
		int ty = by + (bh - (int) fontSize) / 2;
		NVGRenderer.text(displayText, tx, ty, Fonts.PRETENDARD_MEDIUM, UIColors.GRAY, fontSize);

		if (inputMode && System.currentTimeMillis() % 1000 < 500) {
			int cursorX = tx + (int) NVGRenderer.textWidth(inputBuffer.substring(0, Math.min(inputCursor, inputBuffer.length())), Fonts.PRETENDARD_MEDIUM, fontSize);
			NVGRenderer.rect(cursorX, ty, 1, fontSize, UIColors.PURE_WHITE);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!enabled || !visible) return false;

		// ── value box 좌표를 renderWidget 과 동일하게 계산 ──────────
		String currentValText = inputMode ? inputBuffer : formatValue(value);
		int vbw = (int) NVGRenderer.textWidth(currentValText, Fonts.PRETENDARD_MEDIUM, VALUE_FONT) + VALUE_BOX_PAD * 2;

		int minLabelW = (int) NVGRenderer.textWidth(formatValue(min), Fonts.PRETENDARD, LABEL_FONT);
		int maxLabelW = (int) NVGRenderer.textWidth(formatValue(max), Fonts.PRETENDARD, LABEL_FONT);
		int tX = x + minLabelW + LABEL_TO_TRACK;
		int tW = width - minLabelW - LABEL_TO_TRACK - TRACK_TO_LABEL - maxLabelW - LABEL_TO_BOX - vbw;

		int vbx = tX + tW + TRACK_TO_LABEL + maxLabelW + LABEL_TO_BOX;
		int vby = y + (height - VALUE_BOX_H) / 2;
		// ────────────────────────────────────────────────────────────

		if (button == 0 && ULayout.isHovered(mouseX, mouseY, vbx, vby, vbw, VALUE_BOX_H)) {
			startInputMode();
			return true;
		}

		if (inputMode) {
			confirmInput();
			return false;
		}

		if (button == 0 && isMouseOver(mouseX, mouseY)) {
			isDragging = true;
			setValueFromMouse(mouseX);
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (!enabled || !isDragging)
			return false;
		setValueFromMouse(mouseX);
		return true;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (isDragging) {
			isDragging = false;
			if (onRelease != null) onRelease.accept(value);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!inputMode) return false;

		if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
			confirmInput();
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			cancelInput();
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
			if (!inputBuffer.isEmpty() && inputCursor > 0) {
				inputBuffer = inputBuffer.substring(0, inputCursor - 1) + inputBuffer.substring(inputCursor);
				inputCursor--;
			}
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_DELETE) {
			if (inputCursor < inputBuffer.length()) {
				inputBuffer = inputBuffer.substring(0, inputCursor) + inputBuffer.substring(inputCursor + 1);
			}
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_LEFT) {
			inputCursor = Math.max(0, inputCursor - 1);
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_RIGHT) {
			inputCursor = Math.min(inputBuffer.length(), inputCursor + 1);
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_HOME) {
			inputCursor = 0;
			return true;
		}

		if (keyCode == GLFW.GLFW_KEY_END) {
			inputCursor = inputBuffer.length();
			return true;
		}

		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (!inputMode) return false;
		if (Character.isDigit(chr) || chr == '.' || (chr == '-' && inputCursor == 0)) {
			inputBuffer = inputBuffer.substring(0, inputCursor) + chr + inputBuffer.substring(inputCursor);
			inputCursor++;
			return true;
		}
		return false;
	}

	private boolean isMouseOverThumb(double mx, double my) {
		int thumbCy = y + height / 2;
		int targetX = (int) displayX;
		double dx = mx - targetX;
		double dy = my - thumbCy;
		return dx * dx + dy * dy <= (THUMB_R + 2) * (THUMB_HOVER_R + 2);
	}

	private void setValueFromMouse(double mouseX) {
		double raw = ULayout.trackXToValue((int) mouseX, trackX, trackW, min, max);
		setValue(UAnimation.snapToStep(raw, step), true);
	}

	private void startInputMode() {
		inputMode = true;
		inputBuffer = formatValue(value);
		inputCursor = inputBuffer.length();
		setFocused(true);
	}

	private void confirmInput() {
		try {
			double parsed = Double.parseDouble(inputBuffer);
			double clamped = UAnimation.clamp(parsed, min, max);
			setValue(UAnimation.snapToStep(clamped, step), true);
			if (onRelease != null) onRelease.accept(this.value);
			inputMode = false;
			setFocused(false);
		} catch (NumberFormatException e) {
		}
	}

	private void cancelInput() {
		inputMode = false;
		setFocused(false);
	}

	private String formatValue(double v) {
		if (type == SliderType.INT) return String.valueOf((int) Math.round(v));
		return UAnimation.formatForStep(v, step);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		setValue(value, false);
	}

	public void setValue(double value, boolean notify) {
		double clamped = UAnimation.clamp(UAnimation.snapToStep(value, step), min, max);
		if (this.value == clamped)
			return;
		this.value = clamped;
		if (notify && onChange != null)
			onChange.accept(clamped);
	}

	public void setRange(double min, double max, double step) {
		this.min = min;
		this.max = max;
		this.step = (type == SliderType.INT) ? 1.0 : step;
		this.value = UAnimation.clamp(this.value, min, max);
		this.displayX = -1;
	}

	public int getIntValue() {
		return (int) Math.round(value);
	}

	public float getFloatValue() {
		return (float) value;
	}

	public double getDoubleValue() {
		return value;
	}

	public SliderType getType() {
		return type;
	}

	public void setType(SliderType type) {
		this.type = type;
		if (type == SliderType.INT) this.step = 1.0;
	}

	public void setOnChange(Consumer<Double> onChange) {
		this.onChange = onChange;
	}

	public void setOnRelease(Consumer<Double> onRelease) {
		this.onRelease = onRelease;
	}

// Later..
//	public Slider setTrackColors(int bg, int fill) {
//		this.trackBgColor   = bg;
//		this.trackFillColor = fill;
//		return this;
//	}
}