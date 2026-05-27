package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.BossBarRenderEvent;

@FunctionalInterface
public interface IBossBarRenderEvent {
	void onBossBarRender(BossBarRenderEvent event);
}