package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.network.protocol.Packet;

public final class PacketEvent {

	public static class PacketReceiveEvent {
		public Packet<?> packet;
		private boolean canceled = false;
		public PacketReceiveEvent(Packet<?> packet) { this.packet = packet; }
		public void cancel() { this.canceled = true; }
		public boolean isCanceled() { return canceled; }
	}

	public static class PacketSendEvent {
		public Packet<?> packet;
		private boolean canceled = false;
		public PacketSendEvent(Packet<?> packet) { this.packet = packet; }
		public void cancel() { this.canceled = true; }
		public boolean isCanceled() { return canceled; }
	}

	public static final Event<Receive> RECEIVE = createArrayBacked(
		Receive.class, listeners -> event -> {
			for (Receive listener : listeners) {
				listener.onPacketReceive(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<Send> SEND = createArrayBacked(
		Send.class, listeners -> event -> {
			for (Send listener : listeners) {
				listener.onPacketSend(event);
				if (event.isCanceled()) break;
			}
		}
	);

	@FunctionalInterface
	public interface Receive {
		void onPacketReceive(PacketReceiveEvent event);
	}

	@FunctionalInterface
	public interface Send {
		void onPacketSend(PacketSendEvent event);
	}

}