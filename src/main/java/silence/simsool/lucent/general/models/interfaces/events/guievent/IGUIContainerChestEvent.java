package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUIContainerChestEvent;

@FunctionalInterface
public interface IGUIContainerChestEvent {
	void onChest(GUIContainerChestEvent event);
}