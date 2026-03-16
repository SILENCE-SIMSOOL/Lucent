package silence.simsool.lucent.ui.widget;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.data.KeyBind;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class KeyBindButton extends UIWidget {
	private KeyBind value;
	private boolean waiting = false; // 키 입력 대기 중
	private Consumer<KeyBind> onChange;

	private float hoverAnim = 0f;
	private float waitAnim = 0f; // waiting 상태 펄스 (0→1 루프)
	private long waitStart = 0L;
	private static final float ANIM_SPEED = 8f;

	public KeyBindButton(int x, int y, int width, int height, KeyBind initialValue) {
		super(x, y, width, height);
		this.value = (initialValue != null) ? initialValue : KeyBind.none();
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		hoverAnim = UAnimation.stepProgress(hoverAnim, hovered && !waiting, ANIM_SPEED, delta);

		if (waiting) waitAnim = (float) ((System.currentTimeMillis() - waitStart) % 1500) / 1500f;
		else waitAnim = 0f;

		float radius = 8f;
		int bgColor = UColor.withAlpha(UIColors.PURE_BLACK, 100);
		int borderColor = waiting ? UIColors.ACCENT_BLUE : (hovered ? UIColors.withAlpha(UIColors.ACCENT_BLUE, 180) : UIColors.ITEM_BORDER);

		// Breathing Glow for waiting state
		if (waiting) {
			float pulse = 0.5f + 0.5f * (float) Math.sin(waitAnim * Math.PI * 2);
			NVGRenderer.rect(x - 2, y - 2, width + 4, height + 4, UColor.withAlpha(UIColors.ACCENT_BLUE, (int) (15 + pulse * 25)), radius + 2);
		}

		// Main Rect
		NVGRenderer.rect(x, y, width, height, bgColor, radius);

		// Striped Identity Pattern (Subtle)
		if (waiting || hovered) {
			NVGRenderer.pushScissor(x, y, width, height);
			for (int i = -width; i < width + height; i += 12) {
				NVGRenderer.rect(x + i + (waitAnim * 12), y, 2, height * 2, UColor.withAlpha(UIColors.PURE_WHITE, 10), 0);
			}
			NVGRenderer.popScissor();
		}

		NVGRenderer.outlineRect(x, y, width, height, 1f, borderColor, radius);

		// 텍스트
		String label;
		int textColor;
		if (waiting) {
			label = "Wait...";
			textColor = UIColors.ACCENT_BLUE;
		} else if (!value.isBound()) {
			label = "None";
			textColor = UIColors.MUTED;
		} else {
			label = value.getDisplayName();
			textColor = UIColors.TEXT_PRIMARY;
		}

		float fontSize = 13f;
		float tw = NVGRenderer.textWidth(label, Fonts.PRETENDARD_MEDIUM, fontSize);
		float tx = x + (width - tw) / 2f;
		float ty = y + (height - fontSize) / 2f;
		NVGRenderer.text(label, tx, ty, Fonts.PRETENDARD_MEDIUM, textColor, fontSize);

		// 대기 중일 때 ESC 힌트
		if (waiting) {
			String hint = "ESC to clear";
			float hw = NVGRenderer.textWidth(hint, Fonts.PRETENDARD, 10f);
			float hintAlpha = 0.5f + 0.3f * (float) Math.sin(waitAnim * Math.PI * 2);
			NVGRenderer.text(hint, x + (width - hw) / 2f, y + height + 6f, Fonts.PRETENDARD, UColor.withAlpha(UIColors.TEXT_PRIMARY, (int) (hintAlpha * 255)), 10f);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!enabled || !visible) return false;

		if (waiting) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE || button >= 3) {
				if (!isMouseOver(mouseX, mouseY) || button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					setBind(KeyBind.ofMouse(button, 0));
					return true;
				}
			}
			return false;
		}

		if (button == 0 && isMouseOver(mouseX, mouseY)) {
			startWaiting();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!waiting)
			return false;

		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			// ESC → 바인딩 해제
			setBind(KeyBind.none());
			return true;
		}

		// 수식키 단독 → 대기 중인 것으로 처리 (keyReleased 에서 단독 바인딩 체크)
		if (isModifierOnly(keyCode))
			return true;

		// 일반 키 + 현재 눌린 수식키
		setBind(KeyBind.ofKey(keyCode, modifiers));
		return true;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		if (!waiting)
			return false;

		// 수식키만 누었다가 뗀 경우 → 해당 수식키를 단독 바인딩으로 등록
		if (isModifierOnly(keyCode)) {
			setBind(KeyBind.ofKey(keyCode, modifiers));
			return true;
		}
		return false;
	}

	public KeyBind getValue() {
		return value;
	}

	public void setValue(KeyBind value) {
		this.value = (value != null) ? value : KeyBind.none();
		this.waiting = false;
	}

	public void setOnChange(Consumer<KeyBind> onChange) {
		this.onChange = onChange;
	}

	public boolean isWaiting() {
		return waiting;
	}

	private void startWaiting() {
		waiting = true;
		waitStart = System.currentTimeMillis();
	}

	private void setBind(KeyBind bind) {
		this.value = bind;
		this.waiting = false;
		if (onChange != null)
			onChange.accept(bind);
	}

	private static boolean isModifierOnly(int keyCode) {
		return (   keyCode == GLFW.GLFW_KEY_LEFT_SHIFT   || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
				|| keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
				|| keyCode == GLFW.GLFW_KEY_LEFT_ALT     || keyCode == GLFW.GLFW_KEY_RIGHT_ALT
				|| keyCode == GLFW.GLFW_KEY_LEFT_SUPER   || keyCode == GLFW.GLFW_KEY_RIGHT_SUPER
		);
	}
}