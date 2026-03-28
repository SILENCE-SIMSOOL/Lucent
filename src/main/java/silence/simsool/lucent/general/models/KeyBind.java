package silence.simsool.lucent.general.models;

import org.lwjgl.glfw.GLFW;

/**
 * Represents a single key binding: one keyboard key or mouse button, plus optional modifier keys.
 *
 * <p>Exactly one of {@code keyCode} or {@code mouseButton} should be active at a time.
 * Use the factory methods {@link #ofKey}, {@link #ofMouse}, or {@link #none} instead of
 * constructing directly.</p>
 */
public class KeyBind {

	public static final int MOUSE_LEFT = 0;
	public static final int MOUSE_RIGHT = 1;
	public static final int MOUSE_MIDDLE = 2;

	/** GLFW key code. {@code GLFW_KEY_UNKNOWN} when this is a mouse binding. */
	public int keyCode;

	/** GLFW mouse button index. {@code -1} when this is a keyboard binding. */
	public int mouseButton;

	/** GLFW modifier bitmask (GLFW_MOD_SHIFT | GLFW_MOD_CONTROL | GLFW_MOD_ALT). */
	public int mods;

	/** Constructs an unbound (None) key bind. */
	public KeyBind() {
		this.keyCode = GLFW.GLFW_KEY_UNKNOWN;
		this.mouseButton = -1;
		this.mods = 0;
	}

	public KeyBind(int keyCode, int mouseButton, int mods) {
		this.keyCode = keyCode;
		this.mouseButton = mouseButton;
		this.mods = mods;
	}

	public static KeyBind ofKey(int keyCode, int mods) {
		return new KeyBind(keyCode, -1, mods);
	}

	public static KeyBind ofMouse(int mouseButton, int mods) {
		return new KeyBind(GLFW.GLFW_KEY_UNKNOWN, mouseButton, mods);
	}

	public static KeyBind none() {
		return new KeyBind();
	}

	public boolean isBound() {
		return isKey() || isMouse();
	}

	public boolean isKey() {
		return keyCode != GLFW.GLFW_KEY_UNKNOWN && mouseButton == -1;
	}

	public boolean isMouse() {
		return mouseButton >= 0;
	}

	// Checks both left/right variants so callers don't need to handle that.
	public static boolean isShift(int key) {
		return key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT;
	}

	public static boolean isControl(int key) {
		return key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL;
	}

	public static boolean isAlt(int key) {
		return key == GLFW.GLFW_KEY_LEFT_ALT || key == GLFW.GLFW_KEY_RIGHT_ALT;
	}

	/**
	 * Returns a human-readable label, e.g. {@code "Ctrl+Shift+R"}, {@code "Mouse2"}, {@code "None"}.
	 *
	 * <p>Modifier prefixes are suppressed when the main key itself is that modifier
	 * (e.g. binding Shift alone shows "Shift", not "Shift+Shift").</p>
	 */
	public String getDisplayName() {
		if (!isBound()) return "None";

		StringBuilder sb = new StringBuilder();
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
		} else sb.append(glfwKeyName(keyCode));

		return sb.toString();
	}

	/**
	 * Resolves a GLFW key code to a display name.
	 * Prefers {@code glfwGetKeyName} for printable keys; falls back to a hardcoded map
	 * for special keys that GLFW doesn't name, and finally "Key{code}" as a last resort.
	 */
	private static String glfwKeyName(int key) {
		String glfwName = GLFW.glfwGetKeyName(key, 0);
		if (glfwName != null && !glfwName.isEmpty()) {
			return glfwName.toUpperCase();
		}
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
			case GLFW.GLFW_KEY_LEFT_SHIFT,
			     GLFW.GLFW_KEY_RIGHT_SHIFT   -> "Shift";
			case GLFW.GLFW_KEY_LEFT_CONTROL,
			     GLFW.GLFW_KEY_RIGHT_CONTROL -> "Ctrl";
			case GLFW.GLFW_KEY_LEFT_ALT,
			     GLFW.GLFW_KEY_RIGHT_ALT     -> "Alt";
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