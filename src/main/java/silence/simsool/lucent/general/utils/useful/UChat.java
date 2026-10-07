package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import java.util.Optional;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent.ChatMethod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class UChat {

	// --- chat ---
	public static void chat(Component component) {
//      This method is detected in LucentEvent.CHAT_EVENT and will not be used.
//		if (mc.player != null) {
//			mc.player.displayClientMessage(component, false);
//		}
		if (mc.gui != null && mc.gui.hud != null && mc.gui.hud.getChat() != null) {
			mc.gui.hud.getChat().addClientSystemMessage(component);
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
			mc.player.sendOverlayMessage(component);
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

	public static void clear(boolean history) {
		mc.gui.hud.getChat().clearMessages(history);
	}

	public static void clear() {
		clear(false);
	}

	public static void open(ChatMethod chat) {
		mc.gui.openChatScreen(chat);
	}

	public static void open() {
		open(ChatMethod.MESSAGE);
	}

	public static void openCommand() {
		open(ChatMethod.COMMAND);
	}

	// --- Color Utils ---
//	public static String cleanColor(String in) { 
//		return in.replaceAll("(?i)\\u00A7.", "");
//	}
//
//	public static String applyColor(String text) {
//		return text.replace("&&", "\u0000").replaceAll("&([0-9a-fA-Fk-orK-OR])", "§$1").replace("\u0000", "&");
//	}

	/**
	 * Fast Clean Color
	 *
	 * Removes Minecraft color codes without using regular expressions.
	 * This avoids regex overhead and reduces unnecessary allocations.
	 *
	 * @author SimSool
	 */
	public static String cleanColor(String in) {
		int first = in.indexOf('\u00A7'); if (first == -1) return in;
		char[] result = new char[in.length()];
		in.getChars(0, first, result, 0);
		int len = first;
		for (int i = first; i < in.length(); i++) {
			if (in.charAt(i) == '\u00A7') {
				i++;
				continue;
			}
			result[len++] = in.charAt(i);
		}
		return new String(result, 0, len);
	}

	/**
	 * Fast Apply Color
	 *
	 * Applies Minecraft color codes without using regular expressions.
	 * Handles escaped ampersands (&&) while avoiding regex overhead
	 * and unnecessary intermediate String allocations.
	 *
	 * @author SimSool
	 */
	public static String applyColor(String text) {
		int first = text.indexOf('&'); if (first == -1) return text;
		char[] result = new char[text.length()];
		text.getChars(0, first, result, 0);
		int len = first;
		for (int i = first; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '&' && i + 1 < text.length()) {
				char next = text.charAt(i + 1);
				if (next == '&') {
					result[len++] = '&';
					i++;
					continue;
				}
				if (
						(next >= '0' && next <= '9') || (next >= 'a' && next <= 'f') ||
						(next >= 'A' && next <= 'F') || (next >= 'k' && next <= 'o') ||
						(next >= 'K' && next <= 'O') || next == 'r' || next == 'R'
				) {
					result[len++] = '§';
					result[len++] = next;
					i++;
					continue;
				}
			}
			result[len++] = c;
		}
		return new String(result, 0, len);
	}

	// --- Component ---
	public static String getString(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return "";
		return componentToLegacy(stack.getHoverName());
	}

	public static String getString(Component component) {
		return componentToLegacy(component);
	}

	public static String componentToLegacy(Component component) {
		if (component == null) return "";
		
		StringBuilder sb = new StringBuilder();

		component.visit((style, text) -> {
			TextColor color = style.getColor();
			
			if (color != null) {
				for (ChatFormatting formatting : ChatFormatting.values()) {
					TextColor legacyColor = TextColor.fromLegacyFormat(formatting);
					if (legacyColor != null && legacyColor.getValue() == color.getValue()) {
						sb.append(formatting.toString());
						break;
					}
				}
			}

			if (style.isBold()) sb.append("§l");
			if (style.isItalic()) sb.append("§o");
			if (style.isUnderlined()) sb.append("§n");
			if (style.isStrikethrough()) sb.append("§m");
			if (style.isObfuscated()) sb.append("§k");

			sb.append(text);
			return Optional.<String>empty();
		}, Style.EMPTY);

		return sb.toString();
	}

}