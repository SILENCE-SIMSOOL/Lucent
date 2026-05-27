package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.WorldRenderLastEvent;

@FunctionalInterface
public interface IWorldRenderLastEvent {
	void onWorldRenderLast(WorldRenderLastEvent event);
}
