package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.MessageEvent;

@FunctionalInterface
public interface IChatEvent {
	void onChat(MessageEvent event);
}