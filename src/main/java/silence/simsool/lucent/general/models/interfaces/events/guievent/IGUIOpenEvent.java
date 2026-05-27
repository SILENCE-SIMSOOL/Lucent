package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUIOpenEvent;

@FunctionalInterface
public interface IGUIOpenEvent {
	void onOpen(GUIOpenEvent event);
}