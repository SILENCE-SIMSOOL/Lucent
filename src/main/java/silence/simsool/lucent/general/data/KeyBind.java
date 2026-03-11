package silence.simsool.lucent.general.data;

import org.lwjgl.glfw.GLFW;

/**
 * KeyBind 데이터 클래스.
 * <p>키보드 키 또는 마우스 버튼 하나와 수식키(Ctrl/Shift/Alt)를 함께 저장합니다.</p>
 *
 * <ul>
 *   <li>{@code keyCode}  : GLFW 키코드 (예: GLFW_KEY_R). 마우스 버튼이면 -1.</li>
 *   <li>{@code mouseButton} : GLFW 마우스 버튼 인덱스 (0=Left, 1=Right, 2=Middle...). 키보드면 -1.</li>
 *   <li>{@code mods}     : GLFW modifier 비트마스크 (SHIFT | CONTROL | ALT | SUPER).</li>
 * </ul>
 */
public class KeyBind {

    public static final int MOUSE_LEFT   = 0;
    public static final int MOUSE_RIGHT  = 1;
    public static final int MOUSE_MIDDLE = 2;

    /** GLFW 키코드. 마우스 바인딩인 경우 -1. */
    public int keyCode;

    /** GLFW 마우스 버튼. 키보드 바인딩인 경우 -1. */
    public int mouseButton;

    /** GLFW modifier 비트마스크 (GLFW_MOD_SHIFT | GLFW_MOD_CONTROL | GLFW_MOD_ALT). */
    public int mods;

    /** 바인딩 없음 상태 (None) */
    public KeyBind() {
        this.keyCode     = GLFW.GLFW_KEY_UNKNOWN;
        this.mouseButton = -1;
        this.mods        = 0;
    }

    public KeyBind(int keyCode, int mouseButton, int mods) {
        this.keyCode     = keyCode;
        this.mouseButton = mouseButton;
        this.mods        = mods;
    }

    // ─── Factory ──────────────────────────────────────────────────────────────

    public static KeyBind ofKey(int keyCode, int mods) {
        return new KeyBind(keyCode, -1, mods);
    }

    public static KeyBind ofMouse(int mouseButton, int mods) {
        return new KeyBind(GLFW.GLFW_KEY_UNKNOWN, mouseButton, mods);
    }

    public static KeyBind none() {
        return new KeyBind();
    }

    // ─── Query ────────────────────────────────────────────────────────────────

    public boolean isBound() {
        return isKey() || isMouse();
    }

    public boolean isKey() {
        return keyCode != GLFW.GLFW_KEY_UNKNOWN && mouseButton == -1;
    }

    public boolean isMouse() {
        return mouseButton >= 0;
    }

    public static boolean isShift(int key) {
        return key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT;
    }

    public static boolean isControl(int key) {
        return key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL;
    }

    public static boolean isAlt(int key) {
        return key == GLFW.GLFW_KEY_LEFT_ALT || key == GLFW.GLFW_KEY_RIGHT_ALT;
    }

    // ─── Display ──────────────────────────────────────────────────────────────

    /**
     * 사람이 읽을 수 있는 키 이름 반환.
     * 예: "Ctrl+Shift+R", "Mouse4", "None"
     */
    public String getDisplayName() {
        if (!isBound()) return "None";

        StringBuilder sb = new StringBuilder();

        // 수식키 접두사 (메인 키가 해당 수식키인 경우 중복 출력 방지)
        if ((mods & GLFW.GLFW_MOD_CONTROL) != 0 && !isControl(keyCode)) sb.append("Ctrl+");
        if ((mods & GLFW.GLFW_MOD_SHIFT)   != 0 && !isShift(keyCode))   sb.append("Shift+");
        if ((mods & GLFW.GLFW_MOD_ALT)     != 0 && !isAlt(keyCode))     sb.append("Alt+");

        if (isMouse()) {
            switch (mouseButton) {
                case MOUSE_LEFT   -> sb.append("Mouse1");
                case MOUSE_RIGHT  -> sb.append("Mouse2");
                case MOUSE_MIDDLE -> sb.append("Mouse3");
                default           -> sb.append("Mouse").append(mouseButton + 1);
            }
        } else {
            sb.append(glfwKeyName(keyCode));
        }

        return sb.toString();
    }

    // ─── Internals ────────────────────────────────────────────────────────────

    private static String glfwKeyName(int key) {
        // GLFW 자체 이름이 있으면 사용
        String glfwName = GLFW.glfwGetKeyName(key, 0);
        if (glfwName != null && !glfwName.isEmpty()) {
            return glfwName.toUpperCase();
        }
        // 특수 키 매핑
        return switch (key) {
            case GLFW.GLFW_KEY_SPACE         -> "Space";
            case GLFW.GLFW_KEY_ENTER         -> "Enter";
            case GLFW.GLFW_KEY_TAB           -> "Tab";
            case GLFW.GLFW_KEY_BACKSPACE     -> "Backspace";
            case GLFW.GLFW_KEY_DELETE        -> "Delete";
            case GLFW.GLFW_KEY_INSERT        -> "Insert";
            case GLFW.GLFW_KEY_HOME          -> "Home";
            case GLFW.GLFW_KEY_END           -> "End";
            case GLFW.GLFW_KEY_PAGE_UP       -> "PgUp";
            case GLFW.GLFW_KEY_PAGE_DOWN     -> "PgDn";
            case GLFW.GLFW_KEY_UP            -> "↑";
            case GLFW.GLFW_KEY_DOWN          -> "↓";
            case GLFW.GLFW_KEY_LEFT          -> "←";
            case GLFW.GLFW_KEY_RIGHT         -> "→";
            case GLFW.GLFW_KEY_LEFT_SHIFT    -> "Shift";
            case GLFW.GLFW_KEY_RIGHT_SHIFT   -> "Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL  -> "Ctrl";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT      -> "Alt";
            case GLFW.GLFW_KEY_RIGHT_ALT     -> "Alt";
            case GLFW.GLFW_KEY_ESCAPE        -> "Esc";
            case GLFW.GLFW_KEY_CAPS_LOCK     -> "CapsLk";
            case GLFW.GLFW_KEY_F1            -> "F1";
            case GLFW.GLFW_KEY_F2            -> "F2";
            case GLFW.GLFW_KEY_F3            -> "F3";
            case GLFW.GLFW_KEY_F4            -> "F4";
            case GLFW.GLFW_KEY_F5            -> "F5";
            case GLFW.GLFW_KEY_F6            -> "F6";
            case GLFW.GLFW_KEY_F7            -> "F7";
            case GLFW.GLFW_KEY_F8            -> "F8";
            case GLFW.GLFW_KEY_F9            -> "F9";
            case GLFW.GLFW_KEY_F10           -> "F10";
            case GLFW.GLFW_KEY_F11           -> "F11";
            case GLFW.GLFW_KEY_F12           -> "F12";
            default                          -> "Key" + key;
        };
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
