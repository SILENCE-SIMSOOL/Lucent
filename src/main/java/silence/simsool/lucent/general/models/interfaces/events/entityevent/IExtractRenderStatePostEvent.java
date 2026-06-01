package silence.simsool.lucent.general.models.interfaces.events.entityevent;

import silence.simsool.lucent.events.impl.EntityEvent;

@FunctionalInterface
public interface IExtractRenderStatePostEvent {
	void onExtractRenderStatePost(EntityEvent.ExtractRenderStatePost event);
}