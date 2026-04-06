package silence.simsool.lucent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.lwjgl.glfw.GLFW;

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
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.general.utils.ULog;
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;

public class Lucent implements ClientModInitializer {

	public static final String ID = "lucent";
	public static final String NAME = "Lucent";
	public static final String VERSION = "1.0.3";
	public static String LATEST_VERSION = "Fetching...";

	public static Minecraft mc = Minecraft.getInstance();
	public static ULog LOG = new ULog("Lucent");
	public static ModManager config = LucentAPI.createModManager("lucent");

	static {
		HttpClient.newHttpClient().sendAsync(
			HttpRequest.newBuilder(URI.create("https://api.github.com/repos/SILENCE-SIMSOOL/Lucent/releases/latest")).build(),
			HttpResponse.BodyHandlers.ofString()
		).thenAccept(res -> {
			try {
				if (res.statusCode() == 200) {
					String body = res.body();
					int idx = body.indexOf("\"tag_name\":");
					if (idx != -1) {
						String val = body.substring(idx + 11);
						int quoteStart = val.indexOf("\"") + 1;
						int quoteEnd = val.indexOf("\"", quoteStart);
						String version = val.substring(quoteStart, quoteEnd);
						if (version.startsWith("v")) version = version.substring(1);
						LATEST_VERSION = version;
					} else LATEST_VERSION = "Unknown";
				} else LATEST_VERSION = "Unknown";
			} catch(Exception e){
				LATEST_VERSION = "Unknown";
			}
		});
	}

	public static KeyMapping.Category KEYBINDING_CATEGORY = KeyMapping.Category.register(id("main"));
	private static KeyMapping CONFIG_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.lucent.config",
			GLFW.GLFW_KEY_RIGHT_SHIFT, 
			KEYBINDING_CATEGORY
	));

	public static void init() {
		
	}

	@Override
	public void onInitializeClient() {
		LOG.info("Lucent library initializing..");

		mc = Minecraft.getInstance();

		config.loadGlobalConfig();
		config.loadConfigs();

//		// Example mods
//		config.registerExampleMods();
//		HUDManager.INSTANCE.register(new ChattingHud());

		HUDManager.INSTANCE.loadAll();

		SpecialGuiElementRegistry.register(context ->
			new NVGPIPRenderer(context.vertexConsumers())
		);

		HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
			int sw = UDisplay.getWidth();
			int sh = UDisplay.getHeight();
			HUDManager.INSTANCE.render(guiGraphics, sw, sh);
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			String[] commands = {"lucent", "config"};

			for (String label : commands) {
				dispatcher.register(ClientCommandManager.literal(label)
					.executes(context -> {
						mc.schedule(() -> mc.setScreen(LucentAPI.createEditHUDScreen(config)));
						//ScreenOpenHelper.shouldOpen = true;
						return Command.SINGLE_SUCCESS;
					})
				);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (CONFIG_KEY.consumeClick()) {
				mc.schedule(() -> mc.setScreen(LucentAPI.createEditHUDScreen(config)));
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

}