package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.network.chat.Component;

public class UChat {

	public static void chat(String text) {
		if (mc.player != null) mc.player.displayClientMessage(Component.literal(applyColor(text)), false);
	}

	public static void chat(int value) {
		chat(String.valueOf(value));
	}

	public static void chat(long value) {
		chat(String.valueOf(value));
	}

	public static void chat(double value) {
		chat(String.valueOf(value));
	}

	public static void chat(float value) {
		chat(String.valueOf(value));
	}

	public static void chat(boolean value) {
		chat(value ? "true" : "false");
	}

	public static void chat(Object value) {
		chat(value != null ? value.toString() : "null");
	}

	public static void say(String text) {
		if (mc.player != null) mc.player.connection.sendChat(text);
	}

	public static void say(int value) {
		say(String.valueOf(value));
	}

	public static void say(long value) {
		say(String.valueOf(value));
	}

	public static void say(double value) {
		say(String.valueOf(value));
	}

	public static void say(float value) {
		say(String.valueOf(value));
	}

	public static void say(boolean value) {
		say(value ? "true" : "false");
	}

	public static void say(Object value) {
		say(value != null ? value.toString() : "null");
	}

	public static String cleanColor(String in) { 
		return in.replaceAll("(?i)\\u00A7.", "");
	}

//	public static String cleanColor(String in) { 
//		return in.replaceAll("§[0-9a-fk-orxX]", "");
//	}

	public static String applyColor(String text) {
		return text.replace("&&", "\u0000").replaceAll("&([0-9a-fA-Fk-orK-OR])", "§$1").replace("\u0000", "&");
	}

}