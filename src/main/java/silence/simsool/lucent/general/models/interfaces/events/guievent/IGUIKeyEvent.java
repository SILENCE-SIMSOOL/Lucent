package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUIKeyEvent;

@FunctionalInterface
public interface IGUIKeyEvent {
	void onKey(GUIKeyEvent event);
}