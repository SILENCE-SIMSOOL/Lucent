package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUIContainerAllEvent;

@FunctionalInterface
public interface IGUIContainerAllEvent {
	void onContainer(GUIContainerAllEvent event);
}