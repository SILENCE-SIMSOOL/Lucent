package silence.simsool.lucent;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.Command;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.client.dev.examplemods.ChattingHud;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.general.utils.ULog;
import silence.simsool.lucent.hud.HudManager;
import silence.simsool.lucent.ui.screens.EditHudScreen;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;

public class Lucent implements ClientModInitializer {

	public static final String ID = "lucent";
	public static final String NAME = "Lucent";
	public static final String VERSION = "0.0.1";

	public static Minecraft mc = Minecraft.getInstance();
	public static final ULog LOG = new ULog("Lucent");
	public static final ModManager config = LucentAPI.createModManager("lucent");

	public static final KeyMapping.Category KEYBINDING_CATEGORY = KeyMapping.Category.register(id("main"));
	private static final KeyMapping CONFIG_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.lucent.config",
			GLFW.GLFW_KEY_RIGHT_SHIFT, 
			KEYBINDING_CATEGORY
	));
	private static final KeyMapping EDIT_HUD_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.lucent.edithud",
			InputConstants.UNKNOWN.getValue(),
			KEYBINDING_CATEGORY
	));

	public static void init() {
		
	}

	private static void registerHuds() {
		HudManager.INSTANCE.register(new ChattingHud());
	}

	@Override
	public void onInitializeClient() {
		LOG.info("Lucent library initializing..");
		config.registerAll();
		config.loadGlobalConfig();
		config.loadConfigs();

		registerHuds();
		HudManager.INSTANCE.loadAll();

		SpecialGuiElementRegistry.register(context ->
			new NVGPIPRenderer(context.vertexConsumers())
		);

		HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
			int sw = UDisplay.getWidth();
			int sh = UDisplay.getHeight();
			HudManager.INSTANCE.render(guiGraphics, sw, sh);
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			String[] commands = {"lucent", "config"};

			for (String label : commands) {
				dispatcher.register(ClientCommandManager.literal(label)
					.executes(context -> {
						ScreenOpenHelper.shouldOpen = true;
						return Command.SINGLE_SUCCESS;
					})
				);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (CONFIG_KEY.consumeClick()) {
				ScreenOpenHelper.shouldOpen = true;
			}
			while (EDIT_HUD_KEY.consumeClick()) {
				ScreenOpenHelper.shouldOpenEditHud = true;
			}
			if (ScreenOpenHelper.shouldOpen) {
				client.setScreen(new EditHudScreen(true));
				ScreenOpenHelper.shouldOpen = false;
			}
			if (ScreenOpenHelper.shouldOpenEditHud) {
				client.setScreen(new EditHudScreen());
				ScreenOpenHelper.shouldOpenEditHud = false;
			}
		});
	}

	private static class ScreenOpenHelper {
		static boolean shouldOpen = false;
		static boolean shouldOpenEditHud = false;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

}