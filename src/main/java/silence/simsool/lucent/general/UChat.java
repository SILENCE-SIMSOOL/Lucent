package silence.simsool.lucent.general;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.network.chat.Component;

public class UChat {

	public static void chat(String text) {
		if (mc.player != null) mc.player.displayClientMessage(Component.literal(text), false);
	}

	public static void say(String text) {
		if (mc.player != null) mc.player.connection.sendChat(text);
	}

}