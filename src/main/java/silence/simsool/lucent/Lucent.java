package silence.simsool.lucent;

import org.lwjgl.glfw.GLFW;

import com.mojang.brigadier.Command;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.general.ULog;
import silence.simsool.lucent.ui.screens.ConfigScreen;
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

	public static void init() {
		
	}

	@Override
	public void onInitializeClient() {
		LOG.info("Lucent library initializing..");
		config.registerAll();
		config.loadGlobalConfig();
		config.loadConfigs();

		SpecialGuiElementRegistry.register(context -> 
	   		new NVGPIPRenderer(context.vertexConsumers())
		);

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
			if (ScreenOpenHelper.shouldOpen) {
				client.setScreen(new ConfigScreen(config));
				ScreenOpenHelper.shouldOpen = false;
			}
		});
	}

	private static class ScreenOpenHelper {
		static boolean shouldOpen = false;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

}