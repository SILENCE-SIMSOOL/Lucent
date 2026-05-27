package silence.simsool.lucent.general.models.data.events.lucentevent;

import silence.simsool.lucent.general.models.data.KeyBind;

public class KeybindEvent {

	public final KeyBind keybind;
	private final boolean pressed;
	private final boolean keyDown;

	public KeybindEvent(KeyBind keybind, boolean pressed, boolean keyDown) {
		this.keybind = keybind;
		this.pressed = pressed;
		this.keyDown = keyDown;
	}

	public boolean isPressed() {
		return pressed;
	}

	public boolean isKeyDown() {
		return keyDown;
	}

}