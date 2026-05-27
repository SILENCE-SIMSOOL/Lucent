package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.KeyInputEvent;

@FunctionalInterface
public interface IKeyInputEvent {
	void onKeyInput(KeyInputEvent event);
}