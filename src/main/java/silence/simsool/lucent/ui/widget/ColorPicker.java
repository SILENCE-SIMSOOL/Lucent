package silence.simsool.lucent.ui.widget;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.enums.GradientType;
import silence.simsool.lucent.ui.font.LucentFont;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.ULayout;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class ColorPicker extends UIWidget {
	private static int PADDING        = 24;
	private static int SV_W           = 360;
	private static int SV_H           = 260;
	private static int BAR_H          = 22;
	private static int PREVIEW_H      = 36;
	private static int INPUT_H        = 24;
	private static int GAP            = 16;
	private static float FONT_SIZE    = 14f;

	private static int INPUT_INNER_PAD = 10;
	private static int LABEL_W         = 20;
	private static int LABEL_GAP       = 6;
	private static int INPUT_ROW_GAP   = 8;

	private static int TOTAL_W = PADDING * 2 + SV_W;
	private static int TOTAL_H = PADDING + SV_H + GAP + BAR_H + GAP + BAR_H + GAP + PREVIEW_H + GAP
			+ INPUT_INNER_PAD + INPUT_H + INPUT_ROW_GAP + INPUT_H + INPUT_INNER_PAD + PADDING;

	private float hue = 0f;
	private float saturation = 1f;
	private float value = 1f;
	private float alpha = 1f;

	private final int originalColor;

	private enum DragTarget { NONE, SV_PANEL, HUE_BAR, ALPHA_BAR }
	private DragTarget dragging = DragTarget.NONE;

	private enum InputField { NONE, R, G, B, A, HEX }
	private InputField activeInput = InputField.NONE;
	private String inputBuffer = "";
	private int inputCursor = 0;
	private boolean inputError = false;

	private int svX, svY;
	private int hueX, hueY;
	private int alphaX, alphaY;

	private int panelBg     = 0xFF141517;
	private int panelBorder = 0xFF444444;
	private int inputBorder = 0x883D4049;
	private int inputFocus  = 0xFF66AAFF;
	private int inputErr    = 0xFFFF4444;

	private Consumer<Integer> onConfirm;
	private Runnable onCancel;

	public ColorPicker(int x, int y, int initialColor) {
		super(x, y, TOTAL_W, TOTAL_H);
		this.originalColor = initialColor;
		setFromARGB(initialColor);
		recalcLayout();
	}

	private void recalcLayout() {
		svX    = x + PADDING;
		svY    = y + PADDING;
		hueX   = svX;
		hueY   = svY + SV_H + GAP;
		alphaX = svX;
		alphaY = hueY + BAR_H + GAP;
	}

	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		recalcLayout();
	}

	// ── 인풋 박스 rect의 절대 Y, H를 한 곳에서 관리 ──────────────────
	private int getInputBoxY() {
		return alphaY + BAR_H + GAP + PREVIEW_H + GAP;
	}

	private int getInputBoxH() {
		return INPUT_INNER_PAD + INPUT_H + INPUT_ROW_GAP + INPUT_H + INPUT_INNER_PAD;
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		NVGRenderer.rect(x, y, width, height, panelBg, 18);
		NVGRenderer.outlineRect(x, y, width, height, 1, UIColors.DARK_GRAY, 18);
		renderSVPanel();
		renderHueBar();
		renderAlphaBar();
		renderPreview();
		renderInputFields();
	}

	private void renderSVPanel() {
		int round = 12;
		int hueColor = UColor.fromHSV(hue, 1f, 1f);
		int transparentHue = UColor.withAlpha(hueColor, 0);

		NVGRenderer.rect(svX, svY, SV_W, SV_H, 0xFFFFFFFF, round);
		NVGRenderer.gradientRect(svX, svY, SV_W, SV_H, transparentHue, hueColor, GradientType.LEFT_TO_RIGHT, round);
		NVGRenderer.gradientRect(svX, svY, SV_W, SV_H, 0x00000000, 0xFF000000, GradientType.TOP_TO_BOTTOM, round);
		NVGRenderer.outlineRect(svX, svY, SV_W, SV_H, 1, UIColors.MUTED, round);

		int cx = svX + (int)(saturation * SV_W);
		int cy = svY + (int)((1f - value) * SV_H);
		cx = UAnimation.clamp(cx, svX, svX + SV_W);
		cy = UAnimation.clamp(cy, svY, svY + SV_H);

		NVGRenderer.circle(cx, cy, 11, UIColors.BLUE_ECLIPSE_DARK);
		NVGRenderer.circle(cx, cy, 10, UIColors.PURE_WHITE);
		NVGRenderer.circle(cx, cy, 8, UColor.fromHSV(hue, saturation, value));
	}

	private void renderHueBar() {
		int round = 8;
		float segW = SV_W / 6f;
		for (int i = 0; i < 6; i++) {
			int c1 = UColor.fromHSV(i * 60f, 1f, 1f);
			int c2 = UColor.fromHSV((i + 1) * 60f, 1f, 1f);

			float rTL = (i == 0) ? round : 0f;
			float rBL = (i == 0) ? round : 0f;
			float rTR = (i == 5) ? round : 0f;
			float rBR = (i == 5) ? round : 0f;

			NVGRenderer.gradientRect(hueX + i * segW, hueY, (float)Math.ceil(segW), BAR_H, c1, c2, GradientType.LEFT_TO_RIGHT, rTL, rTR, rBR, rBL);
		}

		NVGRenderer.outlineRect(hueX, hueY, SV_W, BAR_H, 1, UIColors.MUTED, round);

		int cx = hueX + (int)(hue / 360f * SV_W);
		cx = UAnimation.clamp(cx, hueX, hueX + SV_W);
		float cy = hueY + BAR_H / 2f;

		NVGRenderer.circle(cx, cy, BAR_H / 2f + 4, UIColors.BLUE_ECLIPSE_DARK);
		NVGRenderer.circle(cx, cy, BAR_H / 2f + 3, UIColors.PURE_WHITE);
		NVGRenderer.circle(cx, cy, BAR_H / 2f, UColor.fromHSV(hue, 1f, 1f));
	}

	private void renderAlphaBar() {
		int round = 8;
		int colorOpaque = UColor.withAlpha(getCurrentARGB(), 255);
		int colorTrans  = UColor.withAlpha(getCurrentARGB(), 0);

		NVGRenderer.drawCheckerboard(alphaX, alphaY, SV_W, BAR_H, round);
		NVGRenderer.gradientRect(alphaX, alphaY, SV_W, BAR_H, colorTrans, colorOpaque, GradientType.LEFT_TO_RIGHT, round);
		NVGRenderer.outlineRect(alphaX, alphaY, SV_W, BAR_H, 1, UIColors.MUTED, round);

		int cx = alphaX + (int)(alpha * SV_W);
		cx = UAnimation.clamp(cx, alphaX, alphaX + SV_W);
		float cy = alphaY + BAR_H / 2f;

		NVGRenderer.circle(cx, cy, BAR_H / 2f + 4, UIColors.BLUE_ECLIPSE_DARK);
		NVGRenderer.circle(cx, cy, BAR_H / 2f + 3, UIColors.PURE_WHITE);
		NVGRenderer.circle(cx, cy, BAR_H / 2f, UColor.withAlpha(colorOpaque, (int)(alpha * 255)));
	}

	private void renderPreview() {
		int round = 10;
		int prevY = alphaY + BAR_H + GAP;
		int halfW = SV_W / 2;
		NVGRenderer.drawCheckerboard(svX, prevY, SV_W, PREVIEW_H, round);
		NVGRenderer.pushScissor(svX, prevY, halfW, PREVIEW_H);
		NVGRenderer.rect(svX, prevY, SV_W, PREVIEW_H, originalColor, round);
		NVGRenderer.popScissor();
		NVGRenderer.pushScissor(svX + halfW, prevY, SV_W - halfW, PREVIEW_H);
		NVGRenderer.rect(svX, prevY, SV_W, PREVIEW_H, getCurrentARGB(), round);
		NVGRenderer.popScissor();
		NVGRenderer.outlineRect(svX, prevY, SV_W, PREVIEW_H, 1, UIColors.MUTED, round);
	}

	private void renderInputFields() {
	    // ── 감싸는 rect ───────────────────────────────────────────────
	    int boxX = svX;
	    int boxY = getInputBoxY();
	    int boxW = SV_W;
	    int boxH = getInputBoxH();

	    NVGRenderer.rect(boxX, boxY, boxW, boxH, UIColors.EXTRA_DARK, 12);
	    NVGRenderer.outlineRect(boxX, boxY, boxW, boxH, 1, UIColors.DARK, 12);

	    // ── rect 내부 좌표 (모두 box 기준) ─────────
	    int COL_GAP = 14;  // 열 사이 여백
	    int INPUT_SHRINK = 8;  // 인풋 박스 양쪽에서 줄일 여백

	    int innerX = boxX + INPUT_INNER_PAD;
	    int innerW = boxW - INPUT_INNER_PAD * 2;
	    int row1Y  = boxY + INPUT_INNER_PAD;
	    int row2Y  = row1Y + INPUT_H + INPUT_ROW_GAP;

	    // 행 1: R / G / B — 열 간격 포함 3등분
	    int colW3 = (innerW - COL_GAP * 2) / 3;
	    int[] rgb = hsvToRGB();
	    renderLabeledInput(InputField.R, innerX,                          row1Y, colW3, INPUT_SHRINK, String.valueOf(rgb[0]), "R:");
	    renderLabeledInput(InputField.G, innerX + colW3 + COL_GAP,        row1Y, colW3, INPUT_SHRINK, String.valueOf(rgb[1]), "G:");
	    renderLabeledInput(InputField.B, innerX + (colW3 + COL_GAP) * 2,  row1Y, colW3, INPUT_SHRINK, String.valueOf(rgb[2]), "B:");

	    // 행 2: A (1열분) + HEX (2열분 + gap 포함)
	    renderLabeledInput(InputField.A,   innerX,                   row2Y, colW3,              INPUT_SHRINK, String.valueOf((int) Math.round(alpha * 100)), "A:");
	    renderLabeledInput(InputField.HEX, innerX + colW3 + COL_GAP, row2Y, colW3 * 2 + COL_GAP, 0,          getHexString(), "H:");
	}

	/**
	 * 셀(cellX, cellY, cellW) 안에 라벨 + 인풋을 배치.
	 * 라벨: 왼쪽 LABEL_W 고정 / 인풋: 나머지 공간 전부
	 */
	private void renderLabeledInput(InputField field, int cellX, int cellY, int cellW, int shrink, String defaultValue, String label) {
	    float labelTy = cellY + (INPUT_H - FONT_SIZE) / 2f;
	    NVGRenderer.text(label, cellX, labelTy, Fonts.PRETENDARD_MEDIUM, UIColors.GRAY, FONT_SIZE);

	    int inputX = cellX + LABEL_W + LABEL_GAP;
	    int inputW = cellW - LABEL_W - LABEL_GAP - shrink;
	    renderSmallInput(field, inputX, cellY, inputW, defaultValue);
	}

	private void renderSmallInput(InputField field, int ix, int iy, int iw, String defaultValue) {
		boolean active      = (activeInput == field);
		String  displayText = active ? inputBuffer : defaultValue;
		int     border      = active ? (inputError ? inputErr : inputFocus) : inputBorder;

		NVGRenderer.rect(ix, iy, iw, INPUT_H, UIColors.LIGHT_BLACK, 8);
		if (active) NVGRenderer.outlineRect(ix, iy, iw, INPUT_H, 1, border, 8);

		String clipped = fitText(displayText, iw - 6, Fonts.PRETENDARD_MEDIUM);
		float tx = ix + (iw - NVGRenderer.textWidth(clipped, Fonts.PRETENDARD_MEDIUM, FONT_SIZE)) / 2f;
		float ty = iy + (INPUT_H - FONT_SIZE) / 2f;

		NVGRenderer.text(clipped, tx, ty, Fonts.PRETENDARD_MEDIUM, UIColors.MUTED, FONT_SIZE);

		if (active && System.currentTimeMillis() % 1000 < 500) {
			float curX = tx + NVGRenderer.textWidth(
				inputBuffer.substring(0, Math.min(inputCursor, inputBuffer.length())),
				Fonts.PRETENDARD_MEDIUM, FONT_SIZE);
			NVGRenderer.rect(curX, ty, 1, FONT_SIZE, UIColors.MUTED);
		}
	}

	private String fitText(String text, int maxWidth, LucentFont font) {
		if (NVGRenderer.textWidth(text, font, FONT_SIZE) <= maxWidth) return text;
		String suffix  = "...";
		String current = text;
		while (current.length() > 0 && NVGRenderer.textWidth(current + suffix, font, FONT_SIZE) > maxWidth)
			current = current.substring(0, current.length() - 1);
		return current + suffix;
	}

	// ── 클릭 감지: renderInputFields 와 완전히 동일한 좌표 계산 ──────
	private boolean clickInputField(int mx, int my) {
	    int COL_GAP = 14;
	    int INPUT_SHRINK = 8;

	    int boxX   = svX;
	    int boxY   = getInputBoxY();
	    int boxW   = SV_W;
	    int innerX = boxX + INPUT_INNER_PAD;
	    int innerW = boxW - INPUT_INNER_PAD * 2;
	    int row1Y  = boxY + INPUT_INNER_PAD;
	    int row2Y  = row1Y + INPUT_H + INPUT_ROW_GAP;
	    int colW3  = (innerW - COL_GAP * 2) / 3;
	    int[] rgb  = hsvToRGB();

	    if (clickLabeledField(mx, my, InputField.R, innerX,                         row1Y, colW3,              INPUT_SHRINK, String.valueOf(rgb[0]))) return true;
	    if (clickLabeledField(mx, my, InputField.G, innerX + colW3 + COL_GAP,       row1Y, colW3,              INPUT_SHRINK, String.valueOf(rgb[1]))) return true;
	    if (clickLabeledField(mx, my, InputField.B, innerX + (colW3 + COL_GAP) * 2, row1Y, colW3,              INPUT_SHRINK, String.valueOf(rgb[2]))) return true;
	    if (clickLabeledField(mx, my, InputField.A, innerX,                          row2Y, colW3,              INPUT_SHRINK, String.valueOf((int) Math.round(alpha * 100)))) return true;
	    if (clickLabeledField(mx, my, InputField.HEX, innerX + colW3 + COL_GAP,     row2Y, colW3 * 2 + COL_GAP, 0,           getHexString())) return true;

	    return false;
	}

	private boolean clickLabeledField(int mx, int my, InputField field, int cellX, int cellY, int cellW, int shrink, String defaultVal) {
	    int inputX = cellX + LABEL_W + LABEL_GAP;
	    int inputW = cellW - LABEL_W - LABEL_GAP - shrink;
	    if (ULayout.isHovered(mx, my, inputX, cellY, inputW, INPUT_H)) {
	        activeInput = field;
	        inputBuffer = defaultVal;
	        inputCursor = inputBuffer.length();
	        inputError  = false;
	        return true;
	    }
	    return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!visible || button != 0) return false;

		int mx = (int) mouseX;
		int my = (int) mouseY;

		if (ULayout.isHovered(mouseX, mouseY, svX, svY, SV_W, SV_H)) {
			dragging = DragTarget.SV_PANEL; updateSVFromMouse(mx, my); closeActiveInput(); return true;
		}
		if (ULayout.isHovered(mouseX, mouseY, hueX, hueY, SV_W, BAR_H)) {
			dragging = DragTarget.HUE_BAR; updateHueFromMouse(mx); closeActiveInput(); return true;
		}
		if (ULayout.isHovered(mouseX, mouseY, alphaX, alphaY, SV_W, BAR_H)) {
			dragging = DragTarget.ALPHA_BAR; updateAlphaFromMouse(mx); closeActiveInput(); return true;
		}
		if (clickInputField(mx, my)) return true;
		if (activeInput != InputField.NONE) confirmInput();

		return isMouseOver(mouseX, mouseY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
		int mx = (int) mouseX, my = (int) mouseY;
		switch (dragging) {
			case SV_PANEL  -> { updateSVFromMouse(mx, my); return true; }
			case HUE_BAR   -> { updateHueFromMouse(mx);    return true; }
			case ALPHA_BAR -> { updateAlphaFromMouse(mx);  return true; }
			default -> {}
		}
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (dragging != DragTarget.NONE) { dragging = DragTarget.NONE; return true; }
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (activeInput == InputField.NONE) {
			if (keyCode == InputConstants.KEY_ESCAPE) { cancel(); return true; }
			return false;
		}
		if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) { confirmInput(); return true; }
		if (keyCode == InputConstants.KEY_ESCAPE)    { closeActiveInput(); return true; }
		if (keyCode == InputConstants.KEY_BACKSPACE && inputCursor > 0) {
			inputBuffer = inputBuffer.substring(0, inputCursor - 1) + inputBuffer.substring(inputCursor);
			inputCursor--;
			inputError = false;
			applyInputPreview();
			return true;
		}
		if (keyCode == InputConstants.KEY_DELETE && inputCursor < inputBuffer.length()) {
			inputBuffer = inputBuffer.substring(0, inputCursor) + inputBuffer.substring(inputCursor + 1);
			inputError = false;
			applyInputPreview();
			return true;
		}
		if (keyCode == InputConstants.KEY_LEFT)  { inputCursor = Math.max(0, inputCursor - 1);                   return true; }
		if (keyCode == InputConstants.KEY_RIGHT) { inputCursor = Math.min(inputBuffer.length(), inputCursor + 1); return true; }
		if (keyCode == InputConstants.KEY_HOME)  { inputCursor = 0;                      return true; }
		if (keyCode == InputConstants.KEY_END)   { inputCursor = inputBuffer.length();   return true; }
		return false;
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (activeInput == InputField.NONE) return false;
		boolean valid = switch (activeInput) {
			case HEX -> isHexChar(chr);
			default  -> Character.isDigit(chr) || chr == '.';
		};
		if (valid) {
			inputBuffer = inputBuffer.substring(0, inputCursor) + chr + inputBuffer.substring(inputCursor);
			inputCursor++;
			inputError = false;
			applyInputPreview();
			return true;
		}
		return false;
	}

	private void applyInputPreview() {
		try { applyInputValue(); } catch (Exception ignored) {}
	}

	private void confirmInput() {
		try {
			applyInputValue();
			inputError = false;
			closeActiveInput();
			confirm();
		} catch (Exception e) {
			inputError = true;
		}
	}

	private void applyInputValue() throws NumberFormatException {
		switch (activeInput) {
			case R -> { int[] rgb = hsvToRGB(); rgb[0] = UAnimation.clamp((int) Float.parseFloat(inputBuffer), 0, 255); setFromRGB(rgb[0], rgb[1], rgb[2]); }
			case G -> { int[] rgb = hsvToRGB(); rgb[1] = UAnimation.clamp((int) Float.parseFloat(inputBuffer), 0, 255); setFromRGB(rgb[0], rgb[1], rgb[2]); }
			case B -> { int[] rgb = hsvToRGB(); rgb[2] = UAnimation.clamp((int) Float.parseFloat(inputBuffer), 0, 255); setFromRGB(rgb[0], rgb[1], rgb[2]); }
			case A -> alpha = UAnimation.clamp(Float.parseFloat(inputBuffer) / 100f, 0, 1);
			case HEX -> {
				String hex = inputBuffer.replace("#", "").trim();
				if (hex.length() == 6) hex = "FF" + hex;
				int c = (int) Long.parseLong(hex, 16);
				setFromARGB(c);
			}
			default -> {}
		}
		confirm();
	}

	private void closeActiveInput() {
		activeInput = InputField.NONE;
		inputBuffer = "";
		inputError  = false;
	}

	private void updateSVFromMouse(int mx, int my) {
		saturation = UAnimation.clamp((float)(mx - svX) / SV_W, 0, 1);
		value      = 1f - UAnimation.clamp((float)(my - svY) / SV_H, 0, 1);
		confirm();
	}

	private void updateHueFromMouse(int mx) {
		hue = UAnimation.clamp((float)(mx - hueX) / SV_W * 360f, 0, 360);
		confirm();
	}

	private void updateAlphaFromMouse(int mx) {
		alpha = UAnimation.clamp((float)(mx - alphaX) / SV_W, 0, 1);
		confirm();
	}

	private int getCurrentARGB() { return UColor.fromHSVA(hue, saturation, value, alpha); }

	private void setFromARGB(int color) {
		float[] hsv = UColor.toHSV(color);
		hue        = hsv[0];
		saturation = hsv[1];
		value      = hsv[2];
		alpha      = UColor.getAlphaF(color);
	}

	private void setFromRGB(int r, int g, int b) {
		float[] hsv = UColor.toHSV(UColor.rgb(r, g, b));
		hue        = hsv[0];
		saturation = hsv[1];
		value      = hsv[2];
	}

	private int[] hsvToRGB() {
		int c = UColor.fromHSV(hue, saturation, value);
		return new int[]{UColor.getRed(c), UColor.getGreen(c), UColor.getBlue(c)};
	}

	private String getHexString() {
		int[] rgb = hsvToRGB();
		int a = (int)(alpha * 255);
		return String.format("%02X%02X%02X%02X", a, rgb[0], rgb[1], rgb[2]);
	}

	private boolean isHexChar(char c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}

	private void confirm() {
		if (onConfirm != null)
			onConfirm.accept(getCurrentARGB());
	}

	private void cancel() {
		if (onCancel != null)
			onCancel.run();
	}

	public int getColor() {
		return getCurrentARGB();
	}

	public void setColor(int color) {
		setFromARGB(color);
	}

	public void setOnConfirm(Consumer<Integer> cb) {
		this.onConfirm = cb;
	}

	public void setOnCancel(Runnable cb) {
		this.onCancel = cb;
	}

	public static int getPreferredWidth() {
		return TOTAL_W;
	}

	public static int getPreferredHeight() {
		return TOTAL_H;
	}
}