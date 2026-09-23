package silence.simsool.lucent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.lwjgl.glfw.GLFW;

import com.mojang.brigadier.Command;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.events.LucentEventRegister;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.InputEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.examplemod.huds.ExampleHUD;
import silence.simsool.lucent.general.managers.LucentManagerRegister;
import silence.simsool.lucent.general.utils.ClientHandler;
import silence.simsool.lucent.general.utils.LucentUtils;
import silence.simsool.lucent.general.utils.notification.NotificationRenderer;
import silence.simsool.lucent.general.utils.render.IrisCompatibility;
import silence.simsool.lucent.general.utils.render.ItemRenderer;
import silence.simsool.lucent.general.utils.render.Render3D;
import silence.simsool.lucent.general.utils.render.RoundRectPIPRenderer;
import silence.simsool.lucent.general.utils.useful.UChat;
import silence.simsool.lucent.general.utils.useful.ULog;
import silence.simsool.lucent.general.utils.useful.UScreen;
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.init.PremiumCosmetics;
import silence.simsool.lucent.mods.Translucent3DRenderFixMod;
import silence.simsool.lucent.skija.compositor.SkijaCompositor;
import silence.simsool.lucent.skija.natives.SkijaNatives;
import silence.simsool.lucent.ui.manager.LucentResourceManager;
import silence.simsool.lucent.ui.utils.skija.Fonts;

public class Lucent implements ClientModInitializer {

	public static final String ID = "lucent";
	public static final String NAME = "Lucent";
	public static final String VERSION = "1.5.3";
	public static String LATEST_VERSION = "Fetching...";

	public static Minecraft mc = Minecraft.getInstance();
	public static HUDManager hudManager = new HUDManager();
	public static ModManager config = LucentAPI.createModManager("lucent");
	public static ULog LOG = new ULog("Lucent");

	public static KeyMapping.Category KEYBINDING_CATEGORY = KeyMapping.Category.register(LucentUtils.id("main"));
	public static KeyMapping CONFIG_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.lucent.config",
			GLFW.GLFW_KEY_RIGHT_SHIFT, 
			KEYBINDING_CATEGORY
	));

	public static boolean devMode = false;
	public static boolean preview = false;
	public static final Identifier PREVIEW_BACKGROUND = LucentUtils.id("preview.png");

	static {
		updateLatestVersion();
	}

	@Override
	public void onInitializeClient() {
		LOG.info("Lucent library initializing..");
		SkijaNatives.ensure();
		PremiumCosmetics.init();

		Fonts.initAsync();
		LucentEventRegister.initialize();
		LucentManagerRegister.registerAll();
		ClientHandler.init();
		Render3D.init();
		IrisCompatibility.init();

		config.register(new Translucent3DRenderFixMod());

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			SkijaCompositor.INSTANCE.shutdown();
		});

		if (devMode) {
			config.registerExampleMods();
			LucentAPI.registerHUD(config, new ExampleHUD());
		}

		PictureInPictureRendererRegistry.register(context ->
			new RoundRectPIPRenderer()
		);

		PictureInPictureRendererRegistry.register(context ->
			new ItemRenderer()
		);

		LucentEvent.INIT_FINISHED_EVENT.register(() -> {
			config.loadGlobalConfig();
			ModManager.cleanupUnusedProfiles();
			config.loadConfigs();
		});

		LucentEvent.RESOURCES_READY_EVENT.register(() -> {
			LucentResourceManager.loadLucentIcons();
			LucentResourceManager.loadModIcons(config);
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommands.literal("config")
				.executes(context -> {
					UScreen.setScreenMC(LucentAPI.createEditHUDScreen(config));
					return Command.SINGLE_SUCCESS;
				})
			);

			dispatcher.register(ClientCommands.literal("lucent")
				.executes(context -> {
					UScreen.setScreenMC(LucentAPI.createEditHUDScreen(config));
					return Command.SINGLE_SUCCESS;
				})
				.then(ClientCommands.literal("refresh")
					.executes(context -> {
						PremiumCosmetics.loadPremiumData();
						UChat.chat("§aPremium cosmetics database reloaded.");
						return Command.SINGLE_SUCCESS;
					})
				)
				.then(ClientCommands.literal("reload")
					.executes(context -> {
						PremiumCosmetics.loadPremiumData();
						UChat.chat("§aPremium cosmetics database reloaded.");
						return Command.SINGLE_SUCCESS;
					})
				)
			);
		});

		InputEvent.KEY.register(event -> {
			if (event.state) {
				if (Lucent.CONFIG_KEY.matches(event.keyEvent)) {
					UScreen.setScreenMC(LucentAPI.createEditHUDScreen(config));
				}
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
			if (UScreen.getScreen() == null) {
				NotificationRenderer.render(event.graphics);
			}
		});

		LOG.info("Successfully loaded Lucent!");
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
						String value = body.substring(idx + 11);
						int quoteStart = value.indexOf("\"") + 1;
						int quoteEnd = value.indexOf("\"", quoteStart);
						String version = value.substring(quoteStart, quoteEnd);
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