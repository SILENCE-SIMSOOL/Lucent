package silence.simsool.lucent.client.dev.examplemods;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import silence.simsool.lucent.general.abstracts.Mod;
import silence.simsool.lucent.general.data.KeyBind;
import silence.simsool.lucent.general.enums.ConfigType;
import silence.simsool.lucent.general.interfaces.ModConfig;

public class ChattingMod extends Mod {
	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Remove Chat Background",
		description = "채팅창 배경을 지웁니다.",
		category = "General"
	)
	public static boolean removeChatBackground = true;

	@ModConfig(
		type = ConfigType.SLIDER,
		name = "Chat Opacity",
		description = "투명도 조절",
		category = "General",
		min = 0.0,
		max = 1.0,
		step = 0.1
	)
	public static double chatOpacity = 0.8;

	@ModConfig(
		type = ConfigType.COLOR,
		name = "Background Color",
		description = "change background color",
		category = "Quality of Life"
	)
	public static Color testColor = new Color(85, 255, 85, 155);

	@ModConfig(
		type = ConfigType.KEYBIND,
		name = "Open Menu",
		description = "메뉴 열기 키"
	)
	public KeyBind openMenuKey = KeyBind.ofKey(GLFW.GLFW_KEY_RIGHT_SHIFT, 0);

	public ChattingMod() {
		super("Chatting Mod", "채팅 관련 여러 편의 기능을 제공합니다.", "QOL", "minecraft, chat", "/assets/lucent/textures/modicons/chatmod.png");
	}

}