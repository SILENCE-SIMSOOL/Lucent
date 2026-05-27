package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import silence.simsool.lucent.general.models.interfaces.events.packetevent.IPacketReceiveEvent;
import silence.simsool.lucent.general.models.interfaces.events.packetevent.IPacketSendEvent;

public final class PacketEvent {

	public static final Event<IPacketReceiveEvent> RECEIVE = createArrayBacked(
		IPacketReceiveEvent.class, listeners -> event -> {
			for (IPacketReceiveEvent listener : listeners) {
				listener.onPacketReceive(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IPacketSendEvent> SEND = createArrayBacked(
		IPacketSendEvent.class, listeners -> event -> {
			for (IPacketSendEvent listener : listeners) {
				listener.onPacketSend(event);
				if (event.isCanceled()) break;
			}
		}
	);

}