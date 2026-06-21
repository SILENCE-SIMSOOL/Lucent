package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.events.impl.GUIEvent;

@FunctionalInterface
public interface IGUIClickEvent {
	void onClick(GUIEvent.GUIClickEvent event);
}