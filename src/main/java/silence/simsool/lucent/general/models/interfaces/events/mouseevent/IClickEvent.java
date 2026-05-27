package silence.simsool.lucent.general.models.interfaces.events.mouseevent;

import silence.simsool.lucent.general.models.data.events.mouseevent.ClickEvent;

@FunctionalInterface
public interface IClickEvent {
	void onMouseClick(ClickEvent event);
}