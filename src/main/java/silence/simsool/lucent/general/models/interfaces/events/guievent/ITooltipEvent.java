package silence.simsool.lucent.general.models.interfaces.events.guievent;

import silence.simsool.lucent.events.impl.GUIEvent;

public interface ITooltipEvent {
	void onRenderTooltip(GUIEvent.TooltipEvent event);
}