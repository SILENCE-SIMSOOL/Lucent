package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.events.impl.GUIEvent;

@FunctionalInterface
public interface IRenderHUD {
	void onRenderHUD(GUIEvent.RenderHUD event);
}