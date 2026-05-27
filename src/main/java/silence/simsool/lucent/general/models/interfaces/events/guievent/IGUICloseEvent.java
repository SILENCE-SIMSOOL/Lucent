package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUICloseEvent;

@FunctionalInterface
public interface IGUICloseEvent {
	void onClose(GUICloseEvent event);
}