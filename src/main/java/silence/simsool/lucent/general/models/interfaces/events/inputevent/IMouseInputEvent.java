package silence.simsool.lucent.general.models.interfaces.events.inputevent;

import silence.simsool.lucent.events.impl.InputEvent;

@FunctionalInterface
public interface IMouseInputEvent {
	void onMouseInput(InputEvent.MouseInputEvent event);
}