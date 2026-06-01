package silence.simsool.lucent.general.models.interfaces.events.inputevent;

import silence.simsool.lucent.events.impl.InputEvent;

@FunctionalInterface
public interface IKeyInputEvent {
	void onKeyInput(InputEvent.KeyInputEvent event);
}