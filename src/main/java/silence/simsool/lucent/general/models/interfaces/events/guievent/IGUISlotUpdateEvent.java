package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUISlotUpdateEvent;

@FunctionalInterface
public interface IGUISlotUpdateEvent {
	void onSlotUpdate(GUISlotUpdateEvent event);
}