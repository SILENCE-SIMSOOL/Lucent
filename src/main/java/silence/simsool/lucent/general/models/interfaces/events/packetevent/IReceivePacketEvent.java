package silence.simsool.lucent.general.models.interfaces.events.packetevent;

import silence.simsool.lucent.events.impl.PacketEvent;

@FunctionalInterface
public interface IReceivePacketEvent {
	void onReceivePacket(PacketEvent.ReceiveEvent event);
}