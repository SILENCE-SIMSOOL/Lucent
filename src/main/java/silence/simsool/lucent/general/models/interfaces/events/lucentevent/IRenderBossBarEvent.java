package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.events.impl.LucentEvent;

@FunctionalInterface
public interface IRenderBossBarEvent {
	void onRenderBossBar(LucentEvent.RenderBossBarEvent event);
}