package silence.simsool.lucent.general.models.interfaces.events.entityevent;

import silence.simsool.lucent.general.models.data.events.entityevent.RenderEntityEvent;

@FunctionalInterface
public interface IRenderEntityEvent {
	void onRenderEntity(RenderEntityEvent event);
}