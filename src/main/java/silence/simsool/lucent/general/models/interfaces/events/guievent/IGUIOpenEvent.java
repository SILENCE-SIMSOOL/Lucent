package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.events.impl.GUIEvent;

@FunctionalInterface
public interface IGUIOpenEvent {
	void onOpen(GUIEvent.GUIOpenEvent event);
}