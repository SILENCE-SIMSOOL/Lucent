package silence.simsool.lucent.general.utils.useful;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import static silence.simsool.lucent.Lucent.mc;

public class UChat {

	// --- chat ---
	public static void chat(Component component) {
//      This method is detected in LucentEvent.CHAT_EVENT and will not be used.
//		if (mc.player != null) {
//			mc.player.displayClientMessage(component, false);
//		}
		if (mc.gui != null && mc.gui.getChat() != null) {
			mc.gui.getChat().addMessage(component);
		}
	}

	public static void chat(String text) {
		chat(Component.literal(applyColor(text)));
	}

	public static void chat(int value) { chat(String.valueOf(value)); }
	public static void chat(long value) { chat(String.valueOf(value)); }
	public static void chat(double value) { chat(String.valueOf(value)); }
	public static void chat(float value) { chat(String.valueOf(value)); }
	public static void chat(boolean value) { chat(value ? "true" : "false"); }
	public static void chat(Object value) { chat(value != null ? value.toString() : "null"); }

	// --- actionbar ---
	public static void actionbar(Component component) {
		if (mc.player != null) {
			mc.player.displayClientMessage(component, true);
		}
	}

	public static void actionbar(String text) {
		actionbar(Component.literal(applyColor(text)));
	}

	// --- say ---
	public static void say(String text) {
		if (mc.player != null && mc.player.connection != null) {
			mc.player.connection.sendChat(text);
		}
	}

	public static void say(int value) { say(String.valueOf(value)); }
	public static void say(long value) { say(String.valueOf(value)); }
	public static void say(double value) { say(String.valueOf(value)); }
	public static void say(float value) { say(String.valueOf(value)); }
	public static void say(boolean value) { say(value ? "true" : "false"); }
	public static void say(Object value) { say(value != null ? value.toString() : "null"); }

	// --- Component Stylers ---
	public static MutableComponent onHover(MutableComponent component, Component hoverText) {
		return component.withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(hoverText)));
	}

	public static MutableComponent onHover(MutableComponent component, String hoverText) {
		return onHover(component, Component.literal(applyColor(hoverText)));
	}

	public static MutableComponent color(MutableComponent component, int rgb) {
		return component.withStyle(style -> style.withColor(rgb));
	}

	public static MutableComponent color(MutableComponent component, TextColor textColor) {
		return component.withStyle(style -> style.withColor(textColor));
	}

	// --- Utilities ---
	public static int getChatWidth() {
		return Mth.floor(mc.options.chatWidth().get() * (double) UDisplay.getGuiScaledWidth());
	}

	public static String getChatBreak() {
		int chatWidth = getChatWidth();
		Font font = mc.font;
		int dashWidth = font.width("-");
		if (dashWidth <= 0) return "-----------------";
		return "-".repeat(Math.max(0, chatWidth / dashWidth));
	}

	public static String getCenteredText(String text) {
		int chatWidth = getChatWidth();
		Font font = mc.font;
		int textWidth = font.width(text);

		if (textWidth >= chatWidth) return text;

		int spaceWidth = font.width(" ");
		if (spaceWidth <= 0) return text;

		int padding = Math.round((chatWidth - textWidth) / 2.0f / spaceWidth);
		return " ".repeat(Math.max(0, padding)) + text;
	}

	// --- Color Utils ---
	public static String cleanColor(String in) { 
		return in.replaceAll("(?i)\\u00A7.", "");
	}

	public static String applyColor(String text) {
		if (text == null) return null;
		return text.replace("&&", "\u0000").replaceAll("&([0-9a-fA-Fk-orK-OR])", "§$1").replace("\u0000", "&");
	}

}