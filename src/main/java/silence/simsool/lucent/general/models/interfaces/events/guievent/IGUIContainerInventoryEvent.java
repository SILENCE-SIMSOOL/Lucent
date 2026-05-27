package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUIContainerInventoryEvent;

@FunctionalInterface
public interface IGUIContainerInventoryEvent {
	void onInventory(GUIContainerInventoryEvent event);
}