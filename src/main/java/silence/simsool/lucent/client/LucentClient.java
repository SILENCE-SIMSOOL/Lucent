package silence.simsool.lucent.client;

import java.io.File;

import com.mojang.brigadier.Command;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import silence.simsool.lucent.client.dev.examplemods.ChattingMod;
import silence.simsool.lucent.config.ModuleManager;
import silence.simsool.lucent.general.UChat;
import silence.simsool.lucent.ui.screens.ConfigScreen;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;

public class LucentClient implements ClientModInitializer {
	
	public static final ModuleManager moduleManager = new ModuleManager(new File("config/lucent"));

	@Override
	public void onInitializeClient() {
		moduleManager.registerAll();
		moduleManager.loadConfigs();

		SpecialGuiElementRegistry.register(context -> 
	   		new NVGPIPRenderer(context.vertexConsumers())
		);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("screen")
				.executes(context -> {
					//UText.init();
					UChat.chat(ChattingMod.removeChatBackground);
					ScreenOpenHelper.shouldOpen = true;
					return Command.SINGLE_SUCCESS;
				}));
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (ScreenOpenHelper.shouldOpen) {
				client.setScreen(new ConfigScreen(moduleManager));
				//client.setScreen(new LucentConfigScreen(moduleManager));
				ScreenOpenHelper.shouldOpen = false;
			}
		});
	}

	private static class ScreenOpenHelper {
		static boolean shouldOpen = false;
	}
}