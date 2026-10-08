package silence.simsool.lucent.general.models.data;

import com.mojang.blaze3d.platform.InputConstants;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.utils.useful.UMouse;

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

	/** Key code. {@code UNKNOWN} when this is a mouse binding. */
	public int keyCode;

	/** Mouse button index. {@code -1} when this is a keyboard binding. */
	public int mouseButton;

	/** Modifier bitmask (MOD_SHIFT | MOD_CONTROL | MOD_ALT). */
	public int mods;

	private boolean wasPressed = false;

	/** Constructs an unbound (None) key bind. */
	public KeyBind() {
		this.keyCode = InputConstants.UNKNOWN.getValue();
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
		return new KeyBind(InputConstants.UNKNOWN.getValue(), mouseButton, mods);
	}

	public static KeyBind none() {
		return new KeyBind();
	}

	public static int fromGlfwKey(int glfwKey) {
		if (glfwKey <= 0) return 0;
		return switch (glfwKey) {
			case 32 -> InputConstants.KEY_SPACE;
			case 39 -> InputConstants.KEY_APOSTROPHE;
			case 44 -> InputConstants.KEY_COMMA;
			case 45 -> InputConstants.KEY_MINUS;
			case 46 -> InputConstants.KEY_PERIOD;
			case 47 -> InputConstants.KEY_SLASH;
			case 48 -> InputConstants.KEY_0;
			case 49 -> InputConstants.KEY_1;
			case 50 -> InputConstants.KEY_2;
			case 51 -> InputConstants.KEY_3;
			case 52 -> InputConstants.KEY_4;
			case 53 -> InputConstants.KEY_5;
			case 54 -> InputConstants.KEY_6;
			case 55 -> InputConstants.KEY_7;
			case 56 -> InputConstants.KEY_8;
			case 57 -> InputConstants.KEY_9;
			case 59 -> InputConstants.KEY_SEMICOLON;
			case 61 -> InputConstants.KEY_EQUALS;
			case 65 -> InputConstants.KEY_A;
			case 66 -> InputConstants.KEY_B;
			case 67 -> InputConstants.KEY_C;
			case 68 -> InputConstants.KEY_D;
			case 69 -> InputConstants.KEY_E;
			case 70 -> InputConstants.KEY_F;
			case 71 -> InputConstants.KEY_G;
			case 72 -> InputConstants.KEY_H;
			case 73 -> InputConstants.KEY_I;
			case 74 -> InputConstants.KEY_J;
			case 75 -> InputConstants.KEY_K;
			case 76 -> InputConstants.KEY_L;
			case 77 -> InputConstants.KEY_M;
			case 78 -> InputConstants.KEY_N;
			case 79 -> InputConstants.KEY_O;
			case 80 -> InputConstants.KEY_P;
			case 81 -> InputConstants.KEY_Q;
			case 82 -> InputConstants.KEY_R;
			case 83 -> InputConstants.KEY_S;
			case 84 -> InputConstants.KEY_T;
			case 85 -> InputConstants.KEY_U;
			case 86 -> InputConstants.KEY_V;
			case 87 -> InputConstants.KEY_W;
			case 88 -> InputConstants.KEY_X;
			case 89 -> InputConstants.KEY_Y;
			case 90 -> InputConstants.KEY_Z;
			case 91 -> InputConstants.KEY_LBRACKET;
			case 92 -> InputConstants.KEY_BACKSLASH;
			case 93 -> InputConstants.KEY_RBRACKET;
			case 96 -> InputConstants.KEY_GRAVE;
			case 256 -> InputConstants.KEY_ESCAPE;
			case 257 -> InputConstants.KEY_RETURN;
			case 258 -> InputConstants.KEY_TAB;
			case 259 -> InputConstants.KEY_BACKSPACE;
			case 260 -> InputConstants.KEY_INSERT;
			case 261 -> InputConstants.KEY_DELETE;
			case 262 -> InputConstants.KEY_RIGHT;
			case 263 -> InputConstants.KEY_LEFT;
			case 264 -> InputConstants.KEY_DOWN;
			case 265 -> InputConstants.KEY_UP;
			case 266 -> InputConstants.KEY_PAGEUP;
			case 267 -> InputConstants.KEY_PAGEDOWN;
			case 268 -> InputConstants.KEY_HOME;
			case 269 -> InputConstants.KEY_END;
			case 280 -> InputConstants.KEY_CAPSLOCK;
			case 281 -> InputConstants.KEY_SCROLLLOCK;
			case 282 -> InputConstants.KEY_NUMLOCK;
			case 283 -> InputConstants.KEY_PRINTSCREEN;
			case 284 -> InputConstants.KEY_PAUSE;
			case 290 -> InputConstants.KEY_F1;
			case 291 -> InputConstants.KEY_F2;
			case 292 -> InputConstants.KEY_F3;
			case 293 -> InputConstants.KEY_F4;
			case 294 -> InputConstants.KEY_F5;
			case 295 -> InputConstants.KEY_F6;
			case 296 -> InputConstants.KEY_F7;
			case 297 -> InputConstants.KEY_F8;
			case 298 -> InputConstants.KEY_F9;
			case 299 -> InputConstants.KEY_F10;
			case 300 -> InputConstants.KEY_F11;
			case 301 -> InputConstants.KEY_F12;
			case 302 -> InputConstants.KEY_F13;
			case 303 -> InputConstants.KEY_F14;
			case 304 -> InputConstants.KEY_F15;
			case 305 -> InputConstants.KEY_F16;
			case 306 -> InputConstants.KEY_F17;
			case 307 -> InputConstants.KEY_F18;
			case 308 -> InputConstants.KEY_F19;
			case 309 -> InputConstants.KEY_F20;
			case 310 -> InputConstants.KEY_F21;
			case 311 -> InputConstants.KEY_F22;
			case 312 -> InputConstants.KEY_F23;
			case 313 -> InputConstants.KEY_F24;
			case 320 -> InputConstants.KEY_NUMPAD0;
			case 321 -> InputConstants.KEY_NUMPAD1;
			case 322 -> InputConstants.KEY_NUMPAD2;
			case 323 -> InputConstants.KEY_NUMPAD3;
			case 324 -> InputConstants.KEY_NUMPAD4;
			case 325 -> InputConstants.KEY_NUMPAD5;
			case 326 -> InputConstants.KEY_NUMPAD6;
			case 327 -> InputConstants.KEY_NUMPAD7;
			case 328 -> InputConstants.KEY_NUMPAD8;
			case 329 -> InputConstants.KEY_NUMPAD9;
			case 330 -> InputConstants.KEY_NUMPADCOMMA;
			case 332 -> InputConstants.KEY_MULTIPLY;
			case 333 -> InputConstants.KEY_MINUS;
			case 334 -> InputConstants.KEY_ADD;
			case 335 -> InputConstants.KEY_NUMPADENTER;
			case 336 -> InputConstants.KEY_NUMPADEQUALS;
			case 340 -> InputConstants.KEY_LSHIFT;
			case 341 -> InputConstants.KEY_LCONTROL;
			case 342 -> InputConstants.KEY_LALT;
			case 343 -> InputConstants.KEY_LGUI;
			case 344 -> InputConstants.KEY_RSHIFT;
			case 345 -> InputConstants.KEY_RCONTROL;
			case 346 -> InputConstants.KEY_RALT;
			case 347 -> InputConstants.KEY_RGUI;
			default -> glfwKey;
		};
	}

	public static int fromGlfwMods(int glfwMods) {
		if (glfwMods <= 0) return 0;
		int result = 0;
		if ((glfwMods & 1) != 0) result |= InputConstants.MOD_SHIFT;
		if ((glfwMods & 2) != 0) result |= InputConstants.MOD_CONTROL;
		if ((glfwMods & 4) != 0) result |= InputConstants.MOD_ALT;
		if ((glfwMods & 8) != 0) result |= InputConstants.MOD_SUPER;
		return result;
	}

	public boolean isBound() {
		return isKey() || isMouse();
	}

	public boolean isKey() {
		return keyCode > 0 && mouseButton == -1;
	}

	public boolean isMouse() {
		return mouseButton >= 0;
	}

	public static boolean isShift(int key) {
		return key == InputConstants.KEY_LSHIFT || key == InputConstants.KEY_RSHIFT;
	}

	public static boolean isControl(int key) {
		return key == InputConstants.KEY_LCONTROL || key == InputConstants.KEY_RCONTROL;
	}

	public static boolean isAlt(int key) {
		return key == InputConstants.KEY_LALT || key == InputConstants.KEY_RALT;
	}

	public boolean isKeyDown() {
		if (!isBound()) return false;
		if (isKey()) {
			int key = keyCode;
			if (key <= 0 || key >= 512) return false;
			try {
				return InputConstants.isKeyDown(key);
			} catch (Exception e) {
				return false;
			}
		}
		if (isMouse()) {
			if (UMouse.isButtonDown(mouseButton)) return true;
			if (Lucent.mc != null && Lucent.mc.mouseHandler != null) {
				if (mouseButton == MOUSE_LEFT) return Lucent.mc.mouseHandler.isLeftPressed();
				if (mouseButton == MOUSE_RIGHT) return Lucent.mc.mouseHandler.isRightPressed();
				if (mouseButton == MOUSE_MIDDLE) return Lucent.mc.mouseHandler.isMiddlePressed();
			}
			return false;
		}
		return false;
	}

	public boolean isPressed() {
		boolean currentlyDown = isKeyDown();
		if (currentlyDown && !wasPressed) {
			wasPressed = true;
			return true;
		}
		if (!currentlyDown) wasPressed = false;
		return false;
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
		if ((mods & InputConstants.MOD_CONTROL) != 0 && !isControl(keyCode)) sb.append("Ctrl+");
		if ((mods & InputConstants.MOD_SHIFT)   != 0 && !isShift(keyCode))   sb.append("Shift+");
		if ((mods & InputConstants.MOD_ALT)     != 0 && !isAlt(keyCode))     sb.append("Alt+");

		if (isMouse()) {
			switch (mouseButton) {
				case MOUSE_LEFT   -> sb.append("Mouse1");
				case MOUSE_RIGHT  -> sb.append("Mouse2");
				case MOUSE_MIDDLE -> sb.append("Mouse3");
				default           -> sb.append("Mouse").append(mouseButton + 1);
			}
		} else {
			try {
				sb.append(InputConstants.Type.KEYBOARD.getOrCreate(keyCode).getDisplayName().getString());
			} catch (Exception e) {
				sb.append("None");
			}
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}