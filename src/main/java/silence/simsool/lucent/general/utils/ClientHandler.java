package silence.simsool.lucent.general.utils;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.utils.useful.UPacket;
import silence.simsool.lucent.general.utils.useful.USound;
import silence.simsool.lucent.general.utils.useful.UTitle;

public class ClientHandler {

	public static int clientTicks = 0;
	public static int serverTicks = 0;

	public static void init() {
		LucentEvent.TICK_EVENT.register(() -> clientTicks++);
		LucentEvent.SERVER_TICK_EVENT.register(() -> serverTicks++);
		LucentEvent.WORLD_LOAD_EVENT.register(() -> {
			clientTicks = 0;
			serverTicks = 0;
		});
	}

	public static void createTitle(String title, String lore, int fadeIn, int time, int fadeOut) {
		mc.schedule(() -> {
			UTitle.createTitle(title, lore, fadeIn, time, fadeOut);
		});
	}

	public static void sendPacket(Packet<?> packet) {
		mc.schedule(() -> {
			UPacket.sendPacket(packet);
		});
	}

	public static void playSound(SoundEvent sound) {
		mc.schedule(() -> {
			USound.playSound(sound);
		});
	}

	public static void playSound(SoundEvent sound, float volume, float pitch) {
		mc.schedule(() -> {
			USound.playSound(sound, volume, pitch);
		});
	}

}