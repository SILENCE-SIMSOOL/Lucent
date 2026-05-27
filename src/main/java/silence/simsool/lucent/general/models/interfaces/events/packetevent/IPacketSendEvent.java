package silence.simsool.lucent.general.models.interfaces.events.packetevent;

import silence.simsool.lucent.general.models.data.events.packetevent.PacketSendEvent;

@FunctionalInterface
public interface IPacketSendEvent {
	void onPacketSend(PacketSendEvent event);
}