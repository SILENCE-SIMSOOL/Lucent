package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUISlotClickEvent;

@FunctionalInterface
public interface IGUISlotClickEvent {
	void onSlotClick(GUISlotClickEvent event);
}