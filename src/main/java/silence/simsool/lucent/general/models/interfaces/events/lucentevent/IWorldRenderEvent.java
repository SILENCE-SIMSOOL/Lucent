package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.WorldRenderEvent;

@FunctionalInterface
public interface IWorldRenderEvent {
	void onWorldRender(WorldRenderEvent event);
}
