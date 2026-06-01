package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.events.impl.GUIEvent;

@FunctionalInterface
public interface IRenderHotbarPostEvent {
	void onHotbarRenderPost(GUIEvent.RenderHotbarPostEvent event);
}
