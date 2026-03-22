package silence.simsool.lucent.client.dev.examplemods;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import silence.simsool.lucent.general.abstracts.Mod;
import silence.simsool.lucent.general.data.KeyBind;
import silence.simsool.lucent.general.enums.ConfigType;
import silence.simsool.lucent.general.interfaces.ModConfig;
import silence.simsool.lucent.general.utils.UChat;

public class ChattingMod extends Mod {

	public ChattingMod() {
		super("Chatting Mod", "Enhances your chat experience with various QOL features.", "QOL", "minecraft, chat", "/assets/lucent/textures/modicons/chatmod.png");
	}

	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Clear Background",
		description = "Removes the default dark background from the chat window.",
		category = "Appearance"
	)
	public static boolean clearBackground = true;

	@ModConfig(
		type = ConfigType.SLIDER,
		name = "Chat Opacity",
		description = "Adjust the transparency level of the chat text and background.",
		category = "Appearance",
		min = 0.0,
		max = 1.0,
		step = 0.1
	)
	public static double chatOpacity = 0.8;

	@ModConfig(
		type = ConfigType.SELECTOR,
		name = "Chat Animation",
		description = "Select the animation style for incoming chat messages.",
		category = "General",
		options = {"Smooth", "Classic", "Slide"}
	)
	public static String chatAnimationStyle = "Smooth";

	@ModConfig(
		type = ConfigType.COLOR,
		name = "Mention Color",
		description = "The highlight color used when someone mentions your name.",
		category = "General"
	)
	public static Color mentionColor = new Color(85, 255, 85, 155);

	@ModConfig(
		type = ConfigType.KEYBIND,
		name = "Open Chat Settings",
		description = "Keybind to quickly open the mod configuration menu."
	)
	public KeyBind openMenuKey = KeyBind.ofKey(GLFW.GLFW_KEY_RIGHT_SHIFT, 0);

	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Test Switch 1",
		description = "This is test switch.",
		category = "Test Category"
	)
	public static boolean test1 = true;
	
	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Test Switch 2",
		description = "This is test switch.",
		category = "Test Category"
	)
	public static boolean test2 = true;

	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Test Switch 3",
		description = "This is test switch.",
		category = "Test Category"
	)
	public static boolean test3 = true;

	@ModConfig(
		type = ConfigType.TEXT,
		name = "Test Text Input",
		description = "This is a text input config.",
		category = "Test Category"
	)
	public static String testText = "Default Text";

	@ModConfig(
		type = ConfigType.BUTTON,
		name = "Test Button",
		display = "Click Me!",
		description = "This button runs a function when clicked.",
		category = "Test Category"
	)
	public void onTestButtonClicked() {
		UChat.chat("Button 1 clicked!");
	}

	@ModConfig(
		type = ConfigType.BUTTON,
		name = "Test Button 2",
		display = "",
		description = "This button runs a function when clicked.",
		category = "Test Category"
	)
	public void onTestButtonClicked2() {
		UChat.chat("Button 2 clicked!");
	}

}