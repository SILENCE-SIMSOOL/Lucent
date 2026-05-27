package silence.simsool.lucent.general.models.data.events.packetevent;

import net.minecraft.network.protocol.Packet;

public class PacketSendEvent {

	public Packet<?> packet;
	private boolean canceled = false;

	public PacketSendEvent(Packet<?> packet) {
		this.packet = packet;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}