package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.network.protocol.Packet;

public final class PacketEvent {

	/**
	 * Event fired when a packet is received by the client.
	 */
	public static final Event<Receive> RECEIVE = createArrayBacked(
		Receive.class, listeners -> packet -> {
			for (Receive listener : listeners) {
				if (listener.onPacketReceive(packet)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a packet is about to be sent from the client.
	 */
	public static final Event<Send> SEND = createArrayBacked(
		Send.class, listeners -> packet -> {
			for (Send listener : listeners) {
				if (listener.onPacketSend(packet)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface
	public interface Receive {
		/**
		 * Called when a packet is received.
		 *
		 * @param packet The received packet
		 * @return true to cancel packet processing; false otherwise
		 */
		boolean onPacketReceive(Packet<?> packet);
	}

	@FunctionalInterface
	public interface Send {
		/**
		 * Called when a packet is being sent.
		 *
		 * @param packet The packet to be sent
		 * @return true to cancel the packet transmission; false otherwise
		 */
		boolean onPacketSend(Packet<?> packet);
	}

}