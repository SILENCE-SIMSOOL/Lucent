package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.network.protocol.Packet;

public class UPacket {

	public static void sendPacket(Packet<?> packet) {
		if (mc.getConnection() != null) {
			mc.getConnection().send(packet);
		}
	}

}