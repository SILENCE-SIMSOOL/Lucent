package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.events.impl.GUIEvent;

@FunctionalInterface
public interface IGUIKeyEvent {
	void onKey(GUIEvent.GUIKeyEvent event);
}