package silence.simsool.lucent.general.models.data;

import static silence.simsool.lucent.Lucent.mc;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.sdl.SDLScancode;

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
			case 330 -> SDLScancode.SDL_SCANCODE_KP_PERIOD;
			case 331 -> SDLScancode.SDL_SCANCODE_KP_DIVIDE;
			case 332 -> InputConstants.KEY_MULTIPLY;
			case 333 -> SDLScancode.SDL_SCANCODE_KP_MINUS;
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
			case 348 -> SDLScancode.SDL_SCANCODE_APPLICATION;
			default -> glfwKey;
		};
	}

	/** Converts runtime scan codes to the GLFW codes used by 26.2 config files. */
	public static int toGlfwKey(int key) {
		return switch (key) {
			case InputConstants.KEY_SPACE -> 32;
			case InputConstants.KEY_APOSTROPHE -> 39;
			case InputConstants.KEY_COMMA -> 44;
			case InputConstants.KEY_MINUS -> 45;
			case InputConstants.KEY_PERIOD -> 46;
			case InputConstants.KEY_SLASH -> 47;
			case InputConstants.KEY_0 -> 48;
			case InputConstants.KEY_1 -> 49;
			case InputConstants.KEY_2 -> 50;
			case InputConstants.KEY_3 -> 51;
			case InputConstants.KEY_4 -> 52;
			case InputConstants.KEY_5 -> 53;
			case InputConstants.KEY_6 -> 54;
			case InputConstants.KEY_7 -> 55;
			case InputConstants.KEY_8 -> 56;
			case InputConstants.KEY_9 -> 57;
			case InputConstants.KEY_SEMICOLON -> 59;
			case InputConstants.KEY_EQUALS -> 61;
			case InputConstants.KEY_A -> 65;
			case InputConstants.KEY_B -> 66;
			case InputConstants.KEY_C -> 67;
			case InputConstants.KEY_D -> 68;
			case InputConstants.KEY_E -> 69;
			case InputConstants.KEY_F -> 70;
			case InputConstants.KEY_G -> 71;
			case InputConstants.KEY_H -> 72;
			case InputConstants.KEY_I -> 73;
			case InputConstants.KEY_J -> 74;
			case InputConstants.KEY_K -> 75;
			case InputConstants.KEY_L -> 76;
			case InputConstants.KEY_M -> 77;
			case InputConstants.KEY_N -> 78;
			case InputConstants.KEY_O -> 79;
			case InputConstants.KEY_P -> 80;
			case InputConstants.KEY_Q -> 81;
			case InputConstants.KEY_R -> 82;
			case InputConstants.KEY_S -> 83;
			case InputConstants.KEY_T -> 84;
			case InputConstants.KEY_U -> 85;
			case InputConstants.KEY_V -> 86;
			case InputConstants.KEY_W -> 87;
			case InputConstants.KEY_X -> 88;
			case InputConstants.KEY_Y -> 89;
			case InputConstants.KEY_Z -> 90;
			case InputConstants.KEY_LBRACKET -> 91;
			case InputConstants.KEY_BACKSLASH -> 92;
			case InputConstants.KEY_RBRACKET -> 93;
			case InputConstants.KEY_GRAVE -> 96;
			case InputConstants.KEY_ESCAPE -> 256;
			case InputConstants.KEY_RETURN -> 257;
			case InputConstants.KEY_TAB -> 258;
			case InputConstants.KEY_BACKSPACE -> 259;
			case InputConstants.KEY_INSERT -> 260;
			case InputConstants.KEY_DELETE -> 261;
			case InputConstants.KEY_RIGHT -> 262;
			case InputConstants.KEY_LEFT -> 263;
			case InputConstants.KEY_DOWN -> 264;
			case InputConstants.KEY_UP -> 265;
			case InputConstants.KEY_PAGEUP -> 266;
			case InputConstants.KEY_PAGEDOWN -> 267;
			case InputConstants.KEY_HOME -> 268;
			case InputConstants.KEY_END -> 269;
			case InputConstants.KEY_CAPSLOCK -> 280;
			case InputConstants.KEY_SCROLLLOCK -> 281;
			case InputConstants.KEY_NUMLOCK -> 282;
			case InputConstants.KEY_PRINTSCREEN -> 283;
			case InputConstants.KEY_PAUSE -> 284;
			case InputConstants.KEY_F1 -> 290;
			case InputConstants.KEY_F2 -> 291;
			case InputConstants.KEY_F3 -> 292;
			case InputConstants.KEY_F4 -> 293;
			case InputConstants.KEY_F5 -> 294;
			case InputConstants.KEY_F6 -> 295;
			case InputConstants.KEY_F7 -> 296;
			case InputConstants.KEY_F8 -> 297;
			case InputConstants.KEY_F9 -> 298;
			case InputConstants.KEY_F10 -> 299;
			case InputConstants.KEY_F11 -> 300;
			case InputConstants.KEY_F12 -> 301;
			case InputConstants.KEY_F13 -> 302;
			case InputConstants.KEY_F14 -> 303;
			case InputConstants.KEY_F15 -> 304;
			case InputConstants.KEY_F16 -> 305;
			case InputConstants.KEY_F17 -> 306;
			case InputConstants.KEY_F18 -> 307;
			case InputConstants.KEY_F19 -> 308;
			case InputConstants.KEY_F20 -> 309;
			case InputConstants.KEY_F21 -> 310;
			case InputConstants.KEY_F22 -> 311;
			case InputConstants.KEY_F23 -> 312;
			case InputConstants.KEY_F24 -> 313;
			case InputConstants.KEY_NUMPAD0 -> 320;
			case InputConstants.KEY_NUMPAD1 -> 321;
			case InputConstants.KEY_NUMPAD2 -> 322;
			case InputConstants.KEY_NUMPAD3 -> 323;
			case InputConstants.KEY_NUMPAD4 -> 324;
			case InputConstants.KEY_NUMPAD5 -> 325;
			case InputConstants.KEY_NUMPAD6 -> 326;
			case InputConstants.KEY_NUMPAD7 -> 327;
			case InputConstants.KEY_NUMPAD8 -> 328;
			case InputConstants.KEY_NUMPAD9 -> 329;
			case SDLScancode.SDL_SCANCODE_KP_PERIOD -> 330;
			case SDLScancode.SDL_SCANCODE_KP_DIVIDE -> 331;
			case InputConstants.KEY_MULTIPLY -> 332;
			case SDLScancode.SDL_SCANCODE_KP_MINUS -> 333;
			case InputConstants.KEY_ADD -> 334;
			case InputConstants.KEY_NUMPADENTER -> 335;
			case InputConstants.KEY_NUMPADEQUALS -> 336;
			case InputConstants.KEY_LSHIFT -> 340;
			case InputConstants.KEY_LCONTROL -> 341;
			case InputConstants.KEY_LALT -> 342;
			case InputConstants.KEY_LGUI -> 343;
			case InputConstants.KEY_RSHIFT -> 344;
			case InputConstants.KEY_RCONTROL -> 345;
			case InputConstants.KEY_RALT -> 346;
			case InputConstants.KEY_RGUI -> 347;
			case SDLScancode.SDL_SCANCODE_APPLICATION -> 348;
			default -> -1;
		};
	}

	public static int fromGlfwMods(int glfwMods) {
		if (glfwMods <= 0) return 0;
		int result = 0;
		if ((glfwMods & 1) != 0) result |= InputConstants.MOD_SHIFT;
		if ((glfwMods & 2) != 0) result |= InputConstants.MOD_CONTROL;
		if ((glfwMods & 4) != 0) result |= InputConstants.MOD_ALT;
		if ((glfwMods & 8) != 0) result |= InputConstants.MOD_SUPER;
		if ((glfwMods & 16) != 0) result |= InputConstants.MOD_CAPS_LOCK;
		if ((glfwMods & 32) != 0) result |= InputConstants.MOD_NUM_LOCK;
		return result;
	}

	public static int toGlfwMods(int mods) {
		int result = 0;
		if ((mods & InputConstants.MOD_SHIFT) != 0) result |= 1;
		if ((mods & InputConstants.MOD_CONTROL) != 0) result |= 2;
		if ((mods & InputConstants.MOD_ALT) != 0) result |= 4;
		if ((mods & InputConstants.MOD_SUPER) != 0) result |= 8;
		if ((mods & InputConstants.MOD_CAPS_LOCK) != 0) result |= 16;
		if ((mods & InputConstants.MOD_NUM_LOCK) != 0) result |= 32;
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
			if (mc != null && mc.mouseHandler != null) {
				if (mouseButton == MOUSE_LEFT) return mc.mouseHandler.isLeftPressed();
				if (mouseButton == MOUSE_RIGHT) return mc.mouseHandler.isRightPressed();
				if (mouseButton == MOUSE_MIDDLE) return mc.mouseHandler.isMiddlePressed();
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