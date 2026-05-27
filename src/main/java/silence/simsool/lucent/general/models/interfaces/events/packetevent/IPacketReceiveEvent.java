package silence.simsool.lucent.general.models.interfaces.events.packetevent;

import silence.simsool.lucent.general.models.data.events.packetevent.PacketReceiveEvent;

@FunctionalInterface
public interface IPacketReceiveEvent {
	void onPacketReceive(PacketReceiveEvent event);
}