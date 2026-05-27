package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUIClickEvent;

@FunctionalInterface
public interface IGUIClickEvent {
	void onClick(GUIClickEvent event);
}