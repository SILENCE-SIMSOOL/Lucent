package silence.simsool.lucent.examplemod.mods;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.Align;
import silence.simsool.lucent.general.enums.ConfigType;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.data.KeyBind;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfigExtra;
import silence.simsool.lucent.general.utils.useful.UChat;
import silence.simsool.lucent.general.utils.useful.ULog;

//Category priority can be set; if not specified, "General" is placed at the top by default, followed by the rest.
@ModConfig.CategoryPriority(name = "General", priority = 1000)
@ModConfig.CategoryPriority(name = "Appearance", priority = 500)
@ModConfig.CategoryPriority(name = "Test Category", priority = 100)
public class ExampleMod extends Mod {

	public ExampleMod() {
		super(
				"Chatting Mod", "Enhances your chat experience with various QOL features.", // Name, Description
				"QOL", // Category
				"minecraft, chat", // Search Tags
				"/assets/lucent/textures/modicons/chatmod.png" // Icon Path
		);
	}

	/*
	 * Whether the mod itself is enabled. 
	 * You can use this to check the activation status of this mod from other classes.
	 */
	public static boolean isEnabled() {
		return Lucent.config.isModuleEnabled(ExampleMod.class);
	}

//───────────────────────────────── General ─────────────────────────────────────────
	// SWITCH
	@ModConfig(
		type = ConfigType.SWITCH, // (Type): boolean
		name = "Example Switch", // (Required): Name or Language ID (e.g., title.lucent.mod)
		description = "Enter a description here!", // (Required): Description or Language ID
		category = "General", // (Optional): Defaults to "General" if no category is specified
		priority = 900 // (Optional): Higher values represent higher priority and appear at the top of the list
	)
	public static boolean ExampleSwitch = true; // (Optional): Start with an uppercase letter to distinguish Lucent Config-dependent variables

	// SELECTOR
	@ModConfig(
		type = ConfigType.SELECTOR, // (Type): String
		name = "Chat Animation",
		description = "Select the animation style for incoming chat messages.",
		options = {"Smooth", "Classic", "Slide"}, // (Required)
		priority = 800
	)
	public static String ChatAnimationStyle = "Smooth";

	// COLOR
	@ModConfig(
		type = ConfigType.COLOR, // (Type): java.awt.Color
		name = "Mention Color",
		description = "The highlight color used when someone mentions your name.",
		priority = 700
	)
	public static Color MentionColor = new Color(85, 255, 85, 155);

	// KEYBIND
	@ModConfig(
		type = ConfigType.KEYBIND, // (Type): silence.simsool.lucent.general.models.data.KeyBind
		name = "Open Chat Keybind",
		description = "Keybind to quickly open the mod configuration menu.",
		priority = 600
	)
	public static KeyBind OpenChatKey = KeyBind.ofKey(GLFW.GLFW_KEY_RIGHT_SHIFT, 0);

	// SLIDER - Int
	@ModConfig(
		type = ConfigType.SLIDER, // (Type): int, float, double
		name = "Example int Selector",
		description = "",
		min = 0,  // (Required): Minimum value of the slider
		max = 10, // (Required): Maximum value of the slider
		step = 1, // (Optional): The amount of change per step (Default: 1)
		priority = 500
	)
	public static int ExampleSliderInt = 5;

	// SLIDER - Float
	@ModConfig(
		type = ConfigType.SLIDER,
		name = "Example Float Selector",
		description = "",
		min = 0.0f, max = 2.0f, step = 0.1f,
		priority = 400
	)
	public static float ExampleSliderFloat = 0f;

	// SLIDER - Double
	@ModConfig(
		type = ConfigType.SLIDER,
		name = "Example Double Selector",
		description = "",
		min = 0.0, max = 2.0, step = 0.1,
		priority = 300
	)
	public static double ExampleSliderDouble = 0;

	// TEXT
	@ModConfig(
		type = ConfigType.TEXT,
		name = "What your name?",
		description = "Typing your name.",
		priority = 200
	)
	public static String PlayerName = "Steve";

	// BUTTON
	@ModConfig(
		type = ConfigType.BUTTON,
		name = "Hello World",
		description = "This button runs a function when clicked.",
		display = "Click Me!", // (Optional): Specifies the button's display text. If omitted, an execution icon will be displayed.
		priority = 100
	)
	public void sendHelloWorld() {
		UChat.chat("(SimSool): Hello World!");
		ULog.print("hello world");
	}


//───────────────────────────────── Advanced ────────────────────────────────────────
	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Advanced Switch",
		description = "&cRed &9Blue &aGreen &rwith Color Code (&&). \nNew line &bnew line.",
		category = "Advanced"
	)
	public static boolean AdvancedConfig = false;

	@ModConfig(
		type = ConfigType.COLOR,
		name = "Advanced Parent",
		description = "",
		category = "Advanced",
		parent = "AdvancedConfig" // (Optional): Enter the variable name of the parent config; this item is displayed only when the parent is enabled.
	)
	public static Color AdvancedParent = new Color(85, 255, 85, 255);


//───────────────────────────────── Example ─────────────────────────────────────────
	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Clear Background",
		description = "Removes the default dark background from the chat window.",
		category = "Appearance",
		priority = 10
	)
	public static boolean ClearBackground = true;

	@ModConfig(
		type = ConfigType.SLIDER,
		name = "Chat Opacity",
		description = "Adjust the transparency level of the chat text and background.",
		category = "Appearance",
		min = 0.0, max = 1.0, step = 0.1,
		priority = 5
	)
	public static double ChatOpacity = 0.8;


//──────────────────────────── ModConfigExtra Example ──────────────────────────────
	@ModConfigExtra(
		type = ConfigType.SWITCH,
		name = "Extra Switch 1",
		description = "This is switch-1 description.",
		category = "Extra Example",
		priority = 5
	)
	public static boolean ExtraSwitch1 = true;

	@ModConfigExtra(
		type = ConfigType.SWITCH,
		name = "Extra Switch 2",
		description = "This is the description for switch 2.",
		category = "Extra Example",
		forceline = true, // (Optional): Forces a new line when enabled.
		forcewidget = true, // (Optional): Places the widget button before the title when enabled.
		align = Align.RIGHT, // (Optional): Aligns to the right and increases spacing between widget and title.
		priority = 4
	)
	public static boolean ExtraSwitch2 = true;

	@ModConfigExtra(
		type = ConfigType.SWITCH,
		name = "Extra Switch 3",
		category = "Extra Example",
		description = "This is switch-3 description.",
		forcewidget = true, align = Align.LEFT,
		priority = 3
	)
	public static boolean ExtraSwitch3 = true;

	@ModConfigExtra(
		type = ConfigType.SWITCH,
		name = "Extra Switch 4",
		description = "This is switch-4 description.",
		category = "Extra Example",
		forcewidget = true, align = Align.LEFT,
		priority = 2
	)
	public static boolean ExtraSwitch4 = true;

	@ModConfigExtra(
		type = ConfigType.SWITCH,
		name = "Extra Switch 5",
		description = "This is switch-5 description.",
		category = "Extra Example",
		forcewidget = true, align = Align.LEFT,
		priority = 1
	)
	public static boolean ExtraSwitch5 = true;

}