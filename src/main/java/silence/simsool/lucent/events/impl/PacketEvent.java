package silence.simsool.lucent.events.impl;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.Packet;

public final class PacketEvent {

	public static final Event<Receive> RECEIVE = EventFactory.createArrayBacked(Receive.class,
		listeners -> packet -> {
			for (Receive listener : listeners) {
				if (listener.onPacketReceive(packet)) return true;
			}
			return false;
		}
	);

	public static final Event<Send> SEND = EventFactory.createArrayBacked(Send.class,
		listeners -> packet -> {
			for (Send listener : listeners) {
				if (listener.onPacketSend(packet)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface public interface Receive {
		/** @return true면 패킷 처리 취소 */
		boolean onPacketReceive(Packet<?> packet);
	}

	@FunctionalInterface public interface Send {
		/** @return true면 패킷 전송 취소 */
		boolean onPacketSend(Packet<?> packet);
	}

}