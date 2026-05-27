package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.MessageSentEvent;

@FunctionalInterface
public interface IMessageSentEvent {
	void onMessageSent(MessageSentEvent event);
}