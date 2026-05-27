package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.general.models.data.events.guievent.RenderHUD;

@FunctionalInterface
public interface IRenderHUD {
	void onRenderHUD(RenderHUD event);
}