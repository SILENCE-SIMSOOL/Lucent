package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.network.protocol.Packet;
import silence.simsool.lucent.general.models.interfaces.events.packetevent.IReceivePacketEvent;
import silence.simsool.lucent.general.models.interfaces.events.packetevent.ISendPacketEvent;

public final class PacketEvent {

	public static final Event<IReceivePacketEvent> RECEIVE = createArrayBacked(
		IReceivePacketEvent.class, listeners -> event -> {
			for (IReceivePacketEvent listener : listeners) {
				listener.onReceivePacket(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<ISendPacketEvent> SEND = createArrayBacked(
		ISendPacketEvent.class, listeners -> event -> {
			for (ISendPacketEvent listener : listeners) {
				listener.onSendPacket(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static class ReceiveEvent {
		public Packet<?> packet;
		private boolean canceled = false;

		public ReceiveEvent(Packet<?> packet) {
			this.packet = packet;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class SendEvent {
		public Packet<?> packet;
		private boolean canceled = false;

		public SendEvent(Packet<?> packet) {
			this.packet = packet;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

}