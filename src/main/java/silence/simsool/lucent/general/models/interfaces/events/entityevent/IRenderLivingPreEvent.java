package silence.simsool.lucent.general.models.interfaces.events.entityevent;

import silence.simsool.lucent.general.models.data.events.entityevent.RenderLivingPreEvent;

@FunctionalInterface
public interface IRenderLivingPreEvent {
	void onRenderLivingPre(RenderLivingPreEvent event);
}