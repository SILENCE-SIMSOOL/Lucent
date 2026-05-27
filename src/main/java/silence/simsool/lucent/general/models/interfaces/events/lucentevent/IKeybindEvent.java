package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.KeybindEvent;

@FunctionalInterface
public interface IKeybindEvent {
	void onKeybind(KeybindEvent event);
}