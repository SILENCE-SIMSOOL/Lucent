package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.GUISlotRenderEvent;

@FunctionalInterface
public interface IGUISlotRenderEvent {
	void onSlotRender(GUISlotRenderEvent event);
}