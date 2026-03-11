package silence.simsool.lucent.ui.widget;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.data.KeyBind;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.base.UIWidget;

/**
 * KeyBind 설정 버튼 위젯.
 * <p>클릭하면 "대기" 상태로 전환되고, 다음으로 누르는 키보드 키나 마우스 버튼을 바인딩으로 등록합니다.</p>
 * <ul>
 *   <li>ESC 를 누르면 현재 바인딩 해제(None 상태)로 설정됩니다.</li>
 *   <li>Ctrl/Shift/Alt 단독 키는 수식키로 처리되며 메인 키와 조합됩니다.</li>
 * </ul>
 */
public class KeyBindButton extends UIWidget {

    // ─── 색상 ─────────────────────────────────────────────────────────────────
    private static final int C_BG          = 0xFF2B2B30;
    private static final int C_BG_HOVER    = 0xFF35353A;
    private static final int C_BG_WAITING  = 0xFF1A2A4A; // 대기 중 → 파란 계열
    private static final int C_BORDER      = 0xFF3A3A40;
    private static final int C_BORDER_WAIT = 0xFF3B82F6;
    private static final int C_TEXT        = 0xFFFFFFFF;
    private static final int C_TEXT_NONE   = 0xFF6B6B70;
    private static final int C_TEXT_WAIT   = 0xFF74A9F5;

    // ─── 상태 ─────────────────────────────────────────────────────────────────
    private KeyBind value;
    private boolean waiting = false;   // 키 입력 대기 중
    private Consumer<KeyBind> onChange;

    // ─── 애니메이션 ────────────────────────────────────────────────────────────
    private float hoverAnim  = 0f;
    private float waitAnim   = 0f;   // waiting 상태 펄스 (0→1 루프)
    private long  waitStart  = 0L;
    private static final float ANIM_SPEED = 8f;

    // ─── 생성자 ───────────────────────────────────────────────────────────────

    public KeyBindButton(int x, int y, int width, int height, KeyBind initialValue) {
        super(x, y, width, height);
        this.value = (initialValue != null) ? initialValue : KeyBind.none();
    }

    // ─── 렌더 ─────────────────────────────────────────────────────────────────

    @Override
    protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
        hoverAnim = UAnimation.stepProgress(hoverAnim, hovered && !waiting, ANIM_SPEED, delta);

        // waiting 상태 펄스 (0 → 2π 루프)
        if (waiting) {
            waitAnim = (float)((System.currentTimeMillis() - waitStart) % 2000) / 2000f;
        } else {
            waitAnim = 0f;
        }

        // 배경
        int bg;
        if (waiting) {
            // 알파값으로 펄스 효과 (0.6 ~ 1.0)
            float pulse = 0.7f + 0.3f * (float) Math.abs(Math.sin(waitAnim * Math.PI * 2));
            bg = UAnimation.lerpColor(C_BG, C_BG_WAITING, pulse);
        } else {
            bg = UAnimation.lerpColor(C_BG, C_BG_HOVER, hoverAnim);
        }

        float radius = height / 4.0f;
        NVGRenderer.rect(x, y, width, height, UIColors.EXTRA_DARK, radius);
        NVGRenderer.outlineRect(x, y, width, height, 1f, UIColors.DARK_GRAY, radius);

        // 테두리
        int border = waiting ? C_BORDER_WAIT : UIColors.LIGHT_GRAY;
        float borderAlpha = waiting
                ? 0.7f + 0.3f * (float) Math.abs(Math.sin(waitAnim * Math.PI * 2))
                : (hoverAnim * 0.5f);
        NVGRenderer.rect(x, y, width, height, applyAlpha(UIColors.EXTRA_DARK, borderAlpha), radius);
        NVGRenderer.outlineRect(x, y, width, height, 1f, applyAlpha(border, borderAlpha), radius);

        // 텍스트
        String label;
        int textColor;
        if (waiting) {
            label     = "Press any key...";
            textColor = C_TEXT_WAIT;
        } else if (!value.isBound()) {
            label     = "None";
            textColor = C_TEXT_NONE;
        } else {
            label     = value.getDisplayName();
            textColor = C_TEXT;
        }

        float fontSize = 12f;
        float tw = NVGRenderer.textWidth(label, Fonts.PRETENDARD_MEDIUM, fontSize);
        float tx = x + (width - tw) / 2f;
        float ty = y + (height - fontSize) / 2f;
        NVGRenderer.text(label, tx, ty, Fonts.PRETENDARD_MEDIUM, textColor, fontSize);

        // 대기 중일 때 ESC 힌트
        if (waiting) {
            String hint = "ESC to clear";
            float hw = NVGRenderer.textWidth(hint, Fonts.PRETENDARD, 9f);
            float hintAlpha = 0.4f + 0.2f * (float) Math.abs(Math.sin(waitAnim * Math.PI * 2));
            NVGRenderer.text(hint, x + (width - hw) / 2f, y + height + 4f,
                    Fonts.PRETENDARD, applyAlpha(C_TEXT_WAIT, hintAlpha), 9f);
        }
    }

    // ─── 이벤트 ───────────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!enabled || !visible) return false;

        if (waiting) {
            // 대기 중에 마우스 클릭 → 수식키 조합 후 바인딩
            // Ctrl/Shift/Alt 는 마우스 클릭 때 mods를 직접 알 수 없으므로 0 처리
            // (실제 수식키 조합이 필요하면 Screen 레벨에서 GLFWKey 이벤트를 직접 전달해야 함)
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT
                    || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT
                    || button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE
                    || button >= 3) {
                // 좌클릭으로 자기 자신을 클릭한 건 바인딩 시작으로 이미 처리했으므로
                // 대기 중 다른 마우스 버튼이면 바인딩
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

    /**
     * ConfigScreen 의 keyPressed 에서 직접 호출해야 합니다.
     * (UIWidget.keyPressed 는 focused 상태를 요구하지만 KeyBind 는 waiting 으로 처리)
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!waiting) return false;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            // ESC → 바인딩 해제
            setBind(KeyBind.none());
            return true;
        }

        // 수식키 단독 → 대기 중인 것으로 처리 (keyReleased 에서 단독 바인딩 체크)
        if (isModifierOnly(keyCode)) return true;

        // 일반 키 + 현재 눌린 수식키
        setBind(KeyBind.ofKey(keyCode, modifiers));
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!waiting) return false;

        // 수식키만 누었다가 뗀 경우 → 해당 수식키를 단독 바인딩으로 등록
        if (isModifierOnly(keyCode)) {
            setBind(KeyBind.ofKey(keyCode, modifiers));
            return true;
        }
        return false;
    }

    // ─── 공개 API ─────────────────────────────────────────────────────────────

    public KeyBind getValue() {
        return value;
    }

    public void setValue(KeyBind value) {
        this.value   = (value != null) ? value : KeyBind.none();
        this.waiting = false;
    }

    public void setOnChange(Consumer<KeyBind> onChange) {
        this.onChange = onChange;
    }

    public boolean isWaiting() {
        return waiting;
    }

    // ─── 내부 ─────────────────────────────────────────────────────────────────

    private void startWaiting() {
        waiting   = true;
        waitStart = System.currentTimeMillis();
    }

    private void setBind(KeyBind bind) {
        this.value   = bind;
        this.waiting = false;
        if (onChange != null) onChange.accept(bind);
    }

    private static boolean isModifierOnly(int keyCode) {
        return keyCode == GLFW.GLFW_KEY_LEFT_SHIFT
            || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
            || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL
            || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
            || keyCode == GLFW.GLFW_KEY_LEFT_ALT
            || keyCode == GLFW.GLFW_KEY_RIGHT_ALT
            || keyCode == GLFW.GLFW_KEY_LEFT_SUPER
            || keyCode == GLFW.GLFW_KEY_RIGHT_SUPER;
    }

    /** ARGB 색상에 추가 알파 계수를 곱함. */
    private static int applyAlpha(int argb, float alpha) {
        int a = (int)(((argb >> 24) & 0xFF) * Math.min(1f, Math.max(0f, alpha)));
        return (a << 24) | (argb & 0x00FFFFFF);
    }
}
