package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.TabCompletionEvent;

@FunctionalInterface
public interface ITabCompleteEvent {
	void onTabComplete(TabCompletionEvent event);
}