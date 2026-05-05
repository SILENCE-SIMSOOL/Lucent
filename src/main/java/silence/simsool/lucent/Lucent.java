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
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.events.LucentEventRegister;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.examplemod.huds.ExampleHUD;
import silence.simsool.lucent.general.managers.LucentManagerRegister;
import silence.simsool.lucent.general.utils.LucentUtils;
import silence.simsool.lucent.general.utils.render.ItemRenderer;
import silence.simsool.lucent.general.utils.render.RoundRectPIPRenderer;
import silence.simsool.lucent.general.utils.useful.ULog;
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.ui.manager.LucentResourceManager;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;

public class Lucent implements ClientModInitializer {

	public static final String ID = "lucent";
	public static final String NAME = "Lucent";
	public static final String VERSION = "1.0.21";
	public static String LATEST_VERSION = "Fetching...";

	public static Minecraft mc = Minecraft.getInstance();
	public static HUDManager hudManager = new HUDManager();
	public static ModManager config = LucentAPI.createModManager("lucent");
	public static ULog LOG = new ULog("Lucent");

	public static KeyMapping.Category KEYBINDING_CATEGORY = KeyMapping.Category.register(LucentUtils.id("main"));
	public static KeyMapping CONFIG_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key.lucent.config",
			GLFW.GLFW_KEY_RIGHT_SHIFT, 
			KEYBINDING_CATEGORY
	));

	public static boolean devMode = false;

	static {
		updateLatestVersion();
	}

	@Override
	public void onInitializeClient() {
		LOG.info("Lucent library initializing..");

		Fonts.initAsync();
		LucentEventRegister.initialize();
		LucentManagerRegister.registerAll();

		if (devMode) {
			config.registerExampleMods();
			LucentAPI.registerHUD(config, new ExampleHUD());
		}

		SpecialGuiElementRegistry.register(context ->
			new NVGPIPRenderer(context.vertexConsumers())
		);

		SpecialGuiElementRegistry.register (context ->
			new RoundRectPIPRenderer(context.vertexConsumers())
		);

		SpecialGuiElementRegistry.register (context ->
			new ItemRenderer(context.vertexConsumers())
		);

		LucentEvent.INIT_FINISHED_EVENT.register(() -> {
			config.loadGlobalConfig();
			config.loadConfigs();
		});

		LucentEvent.RESOURCES_READY_EVENT.register(() -> {
			LucentResourceManager.loadLucentIcons();
			LucentResourceManager.loadModIcons(config);
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			String[] commands = {"lucent", "config"};

			for (String label : commands) {
				dispatcher.register(ClientCommandManager.literal(label)
					.executes(context -> {
						mc.schedule(() -> mc.setScreen(LucentAPI.createEditHUDScreen(config)));
						return Command.SINGLE_SUCCESS;
					})
				);
			}
		});

		LucentEvent.KEY_INPUT_EVENT.register(event -> {
			if (KeyBindingHelper.getBoundKeyOf(Lucent.CONFIG_KEY).equals(event.key)) {
				mc.execute(() -> {
					mc.setScreen(LucentAPI.createEditHUDScreen(config));
				});
			}
		});

//		HudElementRegistry.attachElementAfter(
//		    VanillaHudElements.SUBTITLES,
//		    Identifier.fromNamespaceAndPath(ID, "hud_element"),
//		    (graphics, tickDelta) -> {
//		        hudManager.render(graphics, tickDelta);
//		    }
//		);

		GUIEvent.RenderHUD.EVENT.register(event -> {
			hudManager.render(event.graphics);
		});

	}

	private static void updateLatestVersion() {
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

}