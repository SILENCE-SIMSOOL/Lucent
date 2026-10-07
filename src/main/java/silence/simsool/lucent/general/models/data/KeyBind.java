package silence.simsool.lucent.general.models.data;

import com.mojang.blaze3d.platform.InputConstants;
import silence.simsool.lucent.Lucent;

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

	public boolean isBound() {
		return isKey() || isMouse();
	}

	public boolean isKey() {
		return keyCode != InputConstants.UNKNOWN.getValue() && mouseButton == -1;
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
		if (isKey()) return InputConstants.isKeyDown(keyCode);
		if (isMouse() && Lucent.mc != null && Lucent.mc.mouseHandler != null) {
			if (mouseButton == MOUSE_LEFT) return Lucent.mc.mouseHandler.isLeftPressed();
			if (mouseButton == MOUSE_RIGHT) return Lucent.mc.mouseHandler.isRightPressed();
			if (mouseButton == MOUSE_MIDDLE) return Lucent.mc.mouseHandler.isMiddlePressed();
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
			sb.append(InputConstants.Type.KEYBOARD.getOrCreate(keyCode).getDisplayName().getString());
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}