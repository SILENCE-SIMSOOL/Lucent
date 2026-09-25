package silence.simsool.lucent.config;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.InputEvent;
import silence.simsool.lucent.events.impl.PacketEvent;
import silence.simsool.lucent.events.impl.EntityEvent;
import silence.simsool.lucent.events.impl.ConfigEvent;
import silence.simsool.lucent.examplemod.mods.ExampleMod;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.data.KeyBind;
import silence.simsool.lucent.general.models.data.KeyBindFieldInfo;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfigExtra;
import silence.simsool.lucent.general.utils.OSUtils;
import silence.simsool.lucent.ui.font.LucentFont;
import silence.simsool.lucent.ui.theme.ThemeManager;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;

public class ModManager {

	private static final List<ModManager> INSTANCES = new ArrayList<>();
	private final List<KeyBindFieldInfo> keyBindFields = new ArrayList<>();

	public final List<Mod> modules = new ArrayList<>();
	private final Map<Class<? extends Mod>, Mod> moduleMap = new ConcurrentHashMap<>();
	private final File configDirectory;
	private static String currentProfile = "default";

	private String title = "LUCENT";
	private LucentFont titleFont = Fonts.PRETENDARD_SEMIBOLD;
	private int titleColor = UIColors.ACCENT_BLUE;
	private float titleSize = 20.f;
	private boolean themeColor = true;

	public void setThemeColor(boolean themeColor) {
		this.themeColor = themeColor;
	}

	public boolean isThemeColor() {
		return themeColor;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleFont(LucentFont titleFont) {
		this.titleFont = titleFont;
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	public void setTitleSize(float titleSize) {
		this.titleSize = titleSize;
	}

	public String getTitle() {
		return title;
	}

	public LucentFont getTitleFont() {
		return titleFont;
	}

	public int getTitleColor() {
		return titleColor;
	}

	public float getTitleSize() {
		return titleSize;
	}

	private File getGlobalLucentDir() {
		return OSUtils.getLucentDir();
	}

	private File getGlobalProfilesDir() {
		File f = new File(getGlobalLucentDir(), "profiles");
		if (!f.exists()) f.mkdirs();
		return f;
	}

	public File getHudConfigFile() {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, currentProfile);
		if (!profileDir.exists()) profileDir.mkdirs();
		return new File(profileDir, "hud.json");
	}

	public String getCurrentProfile() {
		return currentProfile;
	}

	public void setCurrentProfile(String profile) {
		currentProfile = profile;
		saveGlobalConfig();
		loadConfigs();
		LucentAPI.getHUDManager().loadAll();
	}

	public List<String> getProfiles() {
		File profilesDir = getGlobalProfilesDir();

		List<String> list = new ArrayList<>();
		File[] files = profilesDir.listFiles(File::isDirectory);
		if (files != null) {
			for (File f : files) {
				String name = f.getName();
				if (!name.equals("default")) list.add(name);
			}
		}
		Collections.sort(list);
		list.add(0, "default");

		new File(profilesDir, "default").mkdirs();

		return list;
	}

	public static void cleanupUnusedProfiles() {
		File globalProfilesDir = new File(OSUtils.getLucentDir(), "profiles");
		if (!globalProfilesDir.exists()) globalProfilesDir.mkdirs();

		File defaultProfile = new File(globalProfilesDir, "default");
		if (!defaultProfile.exists()) defaultProfile.mkdirs();

		File[] globalProfileDirs = globalProfilesDir.listFiles(File::isDirectory);
		java.util.Set<String> validProfiles = new java.util.HashSet<>();
		if (globalProfileDirs != null) {
			for (File f : globalProfileDirs) {
				validProfiles.add(f.getName());
			}
		}
		validProfiles.add("default");

		File configBaseDir = new File(OSUtils.getLucentDir(), "config");
		if (!configBaseDir.exists()) return;

		File[] configDirs = configBaseDir.listFiles(File::isDirectory);
		if (configDirs == null) return;

		for (File configDir : configDirs) {
			File profilesDir = new File(configDir, "profiles");
			if (!profilesDir.exists() || !profilesDir.isDirectory()) continue;

			File[] profileSubDirs = profilesDir.listFiles(File::isDirectory);
			if (profileSubDirs == null) continue;

			for (File profileSubDir : profileSubDirs) {
				String profileName = profileSubDir.getName();
				if (!validProfiles.contains(profileName)) {
					deleteDirectory(profileSubDir);
				}
			}
		}
	}

	public void createProfile(String name) {
		File profilesDir = getGlobalProfilesDir();
		File profileDir = new File(profilesDir, name);
		if (!profileDir.exists()) profileDir.mkdirs();
	}

	public void deleteProfile(String name) {
		if (name.equals("default")) return;

		File profilesDir = getGlobalProfilesDir();
		File profileDir = new File(profilesDir, name);
		if (profileDir.exists()) deleteDirectory(profileDir);

		File configBaseDir = new File(getGlobalLucentDir(), "config");
		if (configBaseDir.exists()) {
			File[] configDirs = configBaseDir.listFiles(File::isDirectory);
			if (configDirs != null) {
				for (File cDir : configDirs) {
					File targetDir = new File(cDir, "profiles/" + name);
					if (targetDir.exists()) {
						deleteDirectory(targetDir);
					}
				}
			}
		}

		if (currentProfile.equals(name)) setCurrentProfile("default");
	}

	public void renameProfile(String oldName, String newName) {
		if (oldName.equals("default") || newName.equals("default") || newName.isEmpty()) return;
		File profilesDir = getGlobalProfilesDir();
		File oldDir = new File(profilesDir, oldName);
		File newDir = new File(profilesDir, newName);

		if (oldDir.exists() && !newDir.exists()) oldDir.renameTo(newDir);

		File configBaseDir = new File(getGlobalLucentDir(), "config");
		if (configBaseDir.exists()) {
			File[] configDirs = configBaseDir.listFiles(File::isDirectory);
			if (configDirs != null) {
				for (File cDir : configDirs) {
					File cOldDir = new File(cDir, "profiles/" + oldName);
					File cNewDir = new File(cDir, "profiles/" + newName);
					if (cOldDir.exists() && !cNewDir.exists()) {
						cOldDir.renameTo(cNewDir);
					}
				}
			}
		}

		if (currentProfile.equals(oldName)) {
			currentProfile = newName;
			saveGlobalConfig();
		}
	}

	private static void deleteDirectory(File dir) {
		File[] children = dir.listFiles();
		if (children != null) {
			for (File child : children) {
				if (child.isDirectory()) deleteDirectory(child);
				else child.delete();
			}
		}
		dir.delete();
	}

	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(Color.class, new TypeAdapter<Color>() {
			@Override
			public void write(JsonWriter out, Color value) throws IOException {
				if (value == null) out.nullValue();
				else out.value(value.getRGB());
			}
			@Override
			public Color read(JsonReader in) throws IOException {
				if (in.peek() == JsonToken.NULL) {
					in.nextNull();
					return null;
				}
				return new Color(in.nextInt(), true);
			}
		})
		.registerTypeAdapter(KeyBind.class, new TypeAdapter<KeyBind>() {
			@Override
			public void write(JsonWriter out, KeyBind value) throws IOException {
				if (value == null) { out.nullValue(); return; }
				out.beginObject();
				out.name("key").value(value.keyCode);
				out.name("mouse").value(value.mouseButton);
				out.name("mods").value(value.mods);
				out.endObject();
			}
			@Override
			public KeyBind read(JsonReader in) throws IOException {
				if (in.peek() == JsonToken.NULL) {
					in.nextNull();
					return KeyBind.none();
				}
				KeyBind kb = KeyBind.none();
				in.beginObject();
				while (in.hasNext()) {
					switch (in.nextName()) {
						case "key"   -> kb.keyCode     = in.nextInt();
						case "mouse" -> kb.mouseButton = in.nextInt();
						case "mods"  -> kb.mods        = in.nextInt();
						default      -> in.skipValue();
					}
				}
				in.endObject();
				return kb;
			}
		})
		.setPrettyPrinting().create();

	public ModManager(File configDirectory) {
		INSTANCES.add(this);
		this.configDirectory = configDirectory;

		// Ensure local profiles directory exists
		File localProfilesDir = new File(configDirectory, "profiles");
		if (!localProfilesDir.exists()) localProfilesDir.mkdirs();

		// Ensure global profiles directory and default profile exist
		File defaultProfile = new File(getGlobalProfilesDir(), "default");
		if (!defaultProfile.exists()) defaultProfile.mkdirs();
	}

	private static boolean isOverridden(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
		try {
			return clazz.getMethod(methodName, parameterTypes).getDeclaringClass() != Mod.class;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	public void register(Mod module) {
		modules.add(module);
		moduleMap.put(module.getClass(), module);

		for (Field field : module.getClass().getDeclaredFields()) {
			if (field.getType() == KeyBind.class) {
				field.setAccessible(true);
				keyBindFields.add(new KeyBindFieldInfo(module, field));
			}
		}

		Class<?> clazz = module.getClass();

		if (isOverridden(clazz, "onInitFinished") || isOverridden(clazz, "onInitFinishedMod")) {
			LucentEvent.INIT_FINISHED_EVENT.register(() -> {
				module.onInitFinished();
				if (module.isEnabled) module.onInitFinishedMod();
			});
		}

		if (isOverridden(clazz, "onResourcesReady") || isOverridden(clazz, "onResourcesReadyMod")) {
			LucentEvent.RESOURCES_READY_EVENT.register(() -> {
				module.onResourcesReady();
				if (module.isEnabled) module.onResourcesReadyMod();
			});
		}

		if (isOverridden(clazz, "onTick")) {
			LucentEvent.TICK_EVENT.LOW.register(() -> {
				if (module.isEnabled) module.onTick();
			});
		}

		if (isOverridden(clazz, "onMediumTick")) {
			LucentEvent.TICK_EVENT.MEDIUM.register(() -> {
				if (module.isEnabled) module.onMediumTick();
			});
		}

		if (isOverridden(clazz, "onHighTick")) {
			LucentEvent.TICK_EVENT.HIGH.register(() -> {
				if (module.isEnabled) module.onHighTick();
			});
		}

		if (isOverridden(clazz, "onEverySecond")) {
			LucentEvent.EVERY_SECOND_EVENT.register(() -> {
				if (module.isEnabled) module.onEverySecond();
			});
		}

		if (isOverridden(clazz, "onServerTick")) {
			LucentEvent.SERVER_TICK_EVENT.register(() -> {
				if (module.isEnabled) module.onServerTick();
			});
		}

		if (isOverridden(clazz, "onChat", LucentEvent.MessageEvent.class)) {
			LucentEvent.CHAT_EVENT.register(event -> {
				if (module.isEnabled) module.onChat(event);
			});
		}

		if (isOverridden(clazz, "onModMessage", LucentEvent.ModMessageEvent.class)) {
			LucentEvent.MOD_MESSAGE_EVENT.register(event -> {
				if (module.isEnabled) module.onModMessage(event);
			});
		}

		if (isOverridden(clazz, "onActionBar", LucentEvent.MessageEvent.class)) {
			LucentEvent.ACTIONBAR_EVENT.register(event -> {
				if (module.isEnabled) module.onActionBar(event);
			});
		}

		if (isOverridden(clazz, "onServerJoin") || isOverridden(clazz, "onServerJoinMod")) {
			LucentEvent.SERVER_JOIN_EVENT.register(() -> {
				module.onServerJoin();
				if (module.isEnabled) module.onServerJoinMod();
			});
		}

		if (isOverridden(clazz, "onServerDisconnect") || isOverridden(clazz, "onServerDisconnectMod")) {
			LucentEvent.SERVER_DISCONNECT_EVENT.register(() -> {
				module.onServerDisconnect();
				if (module.isEnabled) module.onServerDisconnectMod();
			});
		}

		if (isOverridden(clazz, "onWorldLoad") || isOverridden(clazz, "onWorldLoadMod")) {
			LucentEvent.WORLD_LOAD_EVENT.register(() -> {
				module.onWorldLoad();
				if (module.isEnabled) module.onWorldLoadMod();
			});
		}

		if (isOverridden(clazz, "onBlockUpdate", LucentEvent.BlockUpdateEvent.class)) {
			LucentEvent.BLOCK_UPDATE_EVENT.register(event -> {
				if (module.isEnabled) module.onBlockUpdate(event);
			});
		}

		if (isOverridden(clazz, "onRenderWorld", LucentEvent.RenderWorldEvent.class)) {
			LucentEvent.WORLD_RENDER.register(event -> {
				if (module.isEnabled) module.onRenderWorld(event);
			});
		}

		if (isOverridden(clazz, "onRenderWorldLast", LucentEvent.RenderWorldLastEvent.class)) {
			LucentEvent.WORLD_RENDER_LAST.register(event -> {
				if (module.isEnabled) module.onRenderWorldLast(event);
			});
		}

		if (isOverridden(clazz, "onBlockInteract", LucentEvent.BlockInteractEvent.class)) {
			LucentEvent.BLOCK_INTERACT_EVENT.register(event -> {
				if (module.isEnabled) module.onBlockInteract(event);
			});
		}

		if (isOverridden(clazz, "onBlockOverlay", LucentEvent.BlockOverlayEvent.class)) {
			LucentEvent.BLOCK_OVERLAY_EVENT.register(event -> {
				if (module.isEnabled) module.onBlockOverlay(event);
			});
		}

		if (isOverridden(clazz, "onMessageSent", LucentEvent.MessageSentEvent.class)) {
			LucentEvent.MESSAGE_SENT_EVENT.register(event -> {
				if (module.isEnabled) module.onMessageSent(event);
			});
		}

		if (isOverridden(clazz, "onTabComplete", LucentEvent.TabCompletionEvent.class)) {
			LucentEvent.TAB_COMPLETION_EVENT.register(event -> {
				if (module.isEnabled) module.onTabComplete(event);
			});
		}

		if (isOverridden(clazz, "onRenderBossBar", LucentEvent.RenderBossBarEvent.class)) {
			LucentEvent.BOSS_BAR_RENDER_EVENT.register(event -> {
				if (module.isEnabled) module.onRenderBossBar(event);
			});
		}

		if (isOverridden(clazz, "onParticleSpawn", LucentEvent.ParticleSpawnEvent.class)) {
			LucentEvent.PARTICLE_SPAWN_EVENT.register(event -> {
				if (module.isEnabled) module.onParticleSpawn(event);
			});
		}

		if (isOverridden(clazz, "onKeybind", LucentEvent.KeybindEvent.class)) {
			LucentEvent.KEYBIND_EVENT.register(event -> {
				if (module.isEnabled) module.onKeybind(event);
			});
		}

		if (isOverridden(clazz, "onDropItem", LucentEvent.DropItemEvent.class)) {
			LucentEvent.DROP_ITEM_EVENT.register(event -> {
				if (module.isEnabled) module.onDropItem(event);
			});
		}

		if (isOverridden(clazz, "onItemPickup", LucentEvent.ItemPickupEvent.class)) {
			LucentEvent.ITEM_PICKUP_EVENT.register(event -> {
				if (module.isEnabled) module.onItemPickup(event);
			});
		}

		if (isOverridden(clazz, "onSound", LucentEvent.SoundEvent.class)) {
			LucentEvent.SOUND_EVENT.register(event -> {
				if (module.isEnabled) module.onSound(event);
			});
		}

		if (isOverridden(clazz, "onScoreboard", LucentEvent.ScoreboardEvent.class)) {
			LucentEvent.SCOREBOARD_EVENT.register(event -> {
				if (module.isEnabled) module.onScoreboard(event);
			});
		}

		if (isOverridden(clazz, "onTablistUpdate", LucentEvent.TablistUpdateEvent.class)) {
			LucentEvent.TABLIST_UPDATE_EVENT.register(event -> {
				if (module.isEnabled) module.onTablistUpdate(event);
			});
		}

		if (isOverridden(clazz, "onTabAdd", LucentEvent.TabAddEvent.class)) {
			LucentEvent.TAB_ADD_EVENT.register(event -> {
				if (module.isEnabled) module.onTabAdd(event);
			});
		}

		if (isOverridden(clazz, "onTabUpdate", LucentEvent.TabUpdateEvent.class)) {
			LucentEvent.TAB_UPDATE_EVENT.register(event -> {
				if (module.isEnabled) module.onTabUpdate(event);
			});
		}

		if (isOverridden(clazz, "onTabFooter", LucentEvent.TabFooterEvent.class)) {
			LucentEvent.TAB_FOOTER_EVENT.register(event -> {
				if (module.isEnabled) module.onTabFooter(event);
			});
		}

		if (isOverridden(clazz, "onTabHeader", LucentEvent.TabHeaderEvent.class)) {
			LucentEvent.TAB_HEADER_EVENT.register(event -> {
				if (module.isEnabled) module.onTabHeader(event);
			});
		}

		if (isOverridden(clazz, "onActionbar", LucentEvent.ActionbarEvent.class)) {
			LucentEvent.ACTIONBAR_TEXT_EVENT.register(event -> {
				if (module.isEnabled) module.onActionbar(event);
			});
		}

		if (isOverridden(clazz, "onScoreboardUpdate", LucentEvent.ScoreboardUpdateEvent.class)) {
			LucentEvent.SCOREBOARD_UPDATE_EVENT.register(event -> {
				if (module.isEnabled) module.onScoreboardUpdate(event);
			});
		}

		if (isOverridden(clazz, "onUseItemOn", LucentEvent.UseItemOnEvent.class)) {
			LucentEvent.USE_ITEM_ON_EVENT.register(event -> {
				if (module.isEnabled) module.onUseItemOn(event);
			});
		}

		if (isOverridden(clazz, "onUseItem", LucentEvent.UseItemEvent.class)) {
			LucentEvent.USE_ITEM_EVENT.register(event -> {
				if (module.isEnabled) module.onUseItem(event);
			});
		}

		if (isOverridden(clazz, "onLeftClickPre", LucentEvent.LeftClickPreEvent.class)) {
			LucentEvent.LEFT_CLICK_PRE_EVENT.register(event -> {
				if (module.isEnabled) module.onLeftClickPre(event);
			});
		}

		if (isOverridden(clazz, "onLeftClickPost", LucentEvent.LeftClickPostEvent.class)) {
			LucentEvent.LEFT_CLICK_POST_EVENT.register(event -> {
				if (module.isEnabled) module.onLeftClickPost(event);
			});
		}

		if (isOverridden(clazz, "onRightClickPre", LucentEvent.RightClickPreEvent.class)) {
			LucentEvent.RIGHT_CLICK_PRE_EVENT.register(event -> {
				if (module.isEnabled) module.onRightClickPre(event);
			});
		}

		if (isOverridden(clazz, "onRightClickPost", LucentEvent.RightClickPostEvent.class)) {
			LucentEvent.RIGHT_CLICK_POST_EVENT.register(event -> {
				if (module.isEnabled) module.onRightClickPost(event);
			});
		}

		if (isOverridden(clazz, "onMouseInput", InputEvent.MouseInputEvent.class)) {
			InputEvent.MOUSE.register(event -> {
				if (module.isEnabled) module.onMouseInput(event);
			});
		}

		if (isOverridden(clazz, "onKeyInput", InputEvent.KeyInputEvent.class)) {
			InputEvent.KEY.register(event -> {
				if (module.isEnabled) module.onKeyInput(event);
			});
		}

		if (isOverridden(clazz, "onRenderHUD", GUIEvent.RenderHUD.class)) {
			GUIEvent.RenderHUD.EVENT.register(event -> {
				if (module.isEnabled) module.onRenderHUD(event);
			});
		}

		if (isOverridden(clazz, "onGUIOpen", GUIEvent.GUIOpenEvent.class)) {
			GUIEvent.OPEN.EVENT.register(event -> {
				if (module.isEnabled) module.onGUIOpen(event);
			});
		}

		if (isOverridden(clazz, "onGUIOpenPre", GUIEvent.GUIOpenPreEvent.class)) {
			GUIEvent.OPEN_PRE.EVENT.register(event -> {
				if (module.isEnabled) module.onGUIOpenPre(event);
			});
		}

		if (isOverridden(clazz, "onGUIClose", GUIEvent.GUICloseEvent.class)) {
			GUIEvent.CLOSE.EVENT.register(event -> {
				if (module.isEnabled) module.onGUIClose(event);
			});
		}

		if (isOverridden(clazz, "onGUIClick", GUIEvent.GUIClickEvent.class)) {
			GUIEvent.CLICK.EVENT.register(event -> {
				if (module.isEnabled) module.onGUIClick(event);
			});
		}

		if (isOverridden(clazz, "onGUIKey", GUIEvent.GUIKeyEvent.class)) {
			GUIEvent.KEY.EVENT.register(event -> {
				if (module.isEnabled) module.onGUIKey(event);
			});
		}

		if (isOverridden(clazz, "onSlotClick", GUIEvent.SlotClickEvent.class)) {
			GUIEvent.SLOT.Click.EVENT.register(event -> {
				if (module.isEnabled) module.onSlotClick(event);
			});
		}

		if (isOverridden(clazz, "onSlotRenderPre", GUIEvent.RenderSlotPreEvent.class)) {
			GUIEvent.SLOT.RenderPre.EVENT.register(event -> {
				if (module.isEnabled) module.onSlotRenderPre(event);
			});
		}

		if (isOverridden(clazz, "onSlotRenderPost", GUIEvent.RenderSlotPostEvent.class)) {
			GUIEvent.SLOT.RenderPost.EVENT.register(event -> {
				if (module.isEnabled) module.onSlotRenderPost(event);
			});
		}

		if (isOverridden(clazz, "onHotbarRenderPre", GUIEvent.RenderHotbarPreEvent.class)) {
			GUIEvent.SLOT.RenderHotbarPre.EVENT.register(event -> {
				if (module.isEnabled) module.onHotbarRenderPre(event);
			});
		}

		if (isOverridden(clazz, "onHotbarRenderPost", GUIEvent.RenderHotbarPostEvent.class)) {
			GUIEvent.SLOT.RenderHotbarPost.EVENT.register(event -> {
				if (module.isEnabled) module.onHotbarRenderPost(event);
			});
		}

		if (isOverridden(clazz, "onSlotUpdate", GUIEvent.SlotUpdateEvent.class)) {
			GUIEvent.SLOT.Update.EVENT.register(event -> {
				if (module.isEnabled) module.onSlotUpdate(event);
			});
		}

		if (isOverridden(clazz, "onRenderContainer", GUIEvent.RenderContainer.class)) {
			GUIEvent.CONTAINER.All.EVENT.register(event -> {
				if (module.isEnabled) module.onRenderContainer(event);
			});
		}

		if (isOverridden(clazz, "onRenderInventory", GUIEvent.RenderInventory.class)) {
			GUIEvent.CONTAINER.Inventory.EVENT.register(event -> {
				if (module.isEnabled) module.onRenderInventory(event);
			});
		}

		if (isOverridden(clazz, "onRenderChest", GUIEvent.RenderChest.class)) {
			GUIEvent.CONTAINER.Chest.EVENT.register(event -> {
				if (module.isEnabled) module.onRenderChest(event);
			});
		}

		if (isOverridden(clazz, "onRenderTooltip", GUIEvent.TooltipEvent.class)) {
			GUIEvent.Tooltip.EVENT.register(event -> {
				if (module.isEnabled) module.onRenderTooltip(event);
			});
		}

		if (isOverridden(clazz, "onRenderEntityPre", EntityEvent.RenderEntityPreEvent.class)) {
			EntityEvent.hasRenderEntityPreListeners = true;
			EntityEvent.RENDER_ENTITY_PRE_EVENT.register(event -> {
				if (module.isEnabled) module.onRenderEntityPre(event);
			});
		}

		if (isOverridden(clazz, "onRenderEntity", EntityEvent.RenderEntityAllowEvent.class)) {
			EntityEvent.hasRenderEntityAllowListeners = true;
			EntityEvent.RENDER_ENTITY_ALLOW_EVENT.register(event -> {
				if (module.isEnabled) module.onRenderEntity(event);
			});
		}

		if (isOverridden(clazz, "onExtractRenderStatePre", EntityEvent.ExtractRenderStatePre.class)) {
			EntityEvent.hasExtractRenderStatePreListeners = true;
			EntityEvent.EXTRACT_RENDER_STATE_PRE.register(event -> {
				if (module.isEnabled) module.onExtractRenderStatePre(event);
			});
		}

		if (isOverridden(clazz, "onExtractRenderStatePost", EntityEvent.ExtractRenderStatePost.class)) {
			EntityEvent.hasExtractRenderStatePostListeners = true;
			EntityEvent.EXTRACT_RENDER_STATE_POST.register(event -> {
				if (module.isEnabled) module.onExtractRenderStatePost(event);
			});
		}

		if (isOverridden(clazz, "onEntityJoin", EntityEvent.EntityJoinEvent.class)) {
			EntityEvent.ENTITY_JOIN_EVENT.register(event -> {
				if (module.isEnabled) module.onEntityJoin(event);
			});
		}

		if (isOverridden(clazz, "onEntityLeave", EntityEvent.EntityLeaveEvent.class)) {
			EntityEvent.ENTITY_LEAVE_EVENT.register(event -> {
				if (module.isEnabled) module.onEntityLeave(event);
			});
		}

		if (isOverridden(clazz, "onEntityDeath", EntityEvent.EntityDeathEvent.class)) {
			EntityEvent.ENTITY_DEATH_EVENT.register(event -> {
				if (module.isEnabled) module.onEntityDeath(event);
			});
		}

		if (isOverridden(clazz, "onEntityData", EntityEvent.EntityDataEvent.class)) {
			EntityEvent.ENTITY_DATA_EVENT.register(event -> {
				if (module.isEnabled) module.onEntityData(event);
			});
		}

		if (isOverridden(clazz, "onNameChange", EntityEvent.NameChangeEvent.class)) {
			EntityEvent.NAME_CHANGE_EVENT.register(event -> {
				if (module.isEnabled) module.onNameChange(event);
			});
		}

		if (isOverridden(clazz, "onEntityEquipment", EntityEvent.EntityEquipmentEvent.class)) {
			EntityEvent.ENTITY_EQUIPMENT_EVENT.register(event -> {
				if (module.isEnabled) module.onEntityEquipment(event);
			});
		}

		if (isOverridden(clazz, "onEntityInteract", EntityEvent.EntityInteractEvent.class)) {
			EntityEvent.ENTITY_INTERACT_EVENT.register(event -> {
				if (module.isEnabled) module.onEntityInteract(event);
			});
		}

		if (isOverridden(clazz, "onReceivePacket", PacketEvent.ReceiveEvent.class)) {
			PacketEvent.RECEIVE.register(event -> {
				if (module.isEnabled) module.onReceivePacket(event);
			});
		}

		if (isOverridden(clazz, "onSendPacket", PacketEvent.SendEvent.class)) {
			PacketEvent.SEND.register(event -> {
				if (module.isEnabled) module.onSendPacket(event);
			});
		}

		if (isOverridden(clazz, "onToggleButtonChange", ConfigEvent.ToggleButtonEvent.class)) {
			ConfigEvent.TOGGLE_BUTTON.register(event -> {
				if (module.isEnabled) module.onToggleButtonChange(event);
			});
		}

		if (isOverridden(clazz, "onSliderChange", ConfigEvent.SliderEvent.class)) {
			ConfigEvent.SLIDER.register(event -> {
				if (module.isEnabled) module.onSliderChange(event);
			});
		}

		if (isOverridden(clazz, "onSelectorChange", ConfigEvent.SelectorEvent.class)) {
			ConfigEvent.SELECTOR.register(event -> {
				if (module.isEnabled) module.onSelectorChange(event);
			});
		}

		if (isOverridden(clazz, "onColorPickerChange", ConfigEvent.ColorPickerEvent.class)) {
			ConfigEvent.COLOR_PICKER.register(event -> {
				if (module.isEnabled) module.onColorPickerChange(event);
			});
		}

		if (isOverridden(clazz, "onTextBoxChange", ConfigEvent.TextBoxEvent.class)) {
			ConfigEvent.TEXT_BOX.register(event -> {
				if (module.isEnabled) module.onTextBoxChange(event);
			});
		}

		if (isOverridden(clazz, "onKeyBindChange", ConfigEvent.KeyBindEvent.class)) {
			ConfigEvent.KEY_BIND.register(event -> {
				if (module.isEnabled) module.onKeyBindChange(event);
			});
		}
	}

	public void registerExampleMods() {
		register(new ExampleMod());
	}

	@SuppressWarnings("unchecked")
	public <T extends Mod> T getModule(Class<T> moduleClass) {
		Mod cached = moduleMap.get(moduleClass);
		if (cached != null) {
			return (T) cached;
		}
		for (Mod mod : modules) {
			if (moduleClass.isAssignableFrom(mod.getClass())) {
				moduleMap.put(moduleClass, mod);
				return (T) mod;
			}
		}
		return null;
	}

	public boolean isModuleEnabled(Class<? extends Mod> moduleClass) {
		Mod mod = getModule(moduleClass);
		return mod != null && mod.isEnabled;
	}

	public void setModuleEnabled(Class<? extends Mod> moduleClass, boolean enabled) {
		Mod mod = getModule(moduleClass);
		if (mod != null) {
			if (mod.isLocked) return;
			mod.isEnabled = enabled;
			saveConfigs();
		}
	}

	public void loadConfigs() {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, currentProfile);

		if (!profileDir.exists()) profileDir.mkdirs();

		File configFile = new File(profileDir, "config.json");
		boolean needsResave = false;

		if (!configFile.exists()) {
			if (!modules.isEmpty()) saveConfigs();
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8)) {
			JsonObject root = GSON.fromJson(reader, JsonObject.class);
			if (root == null) return;
			JsonObject modulesJson = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();

			for (Mod module : modules) {
				String key = module.name;
				if (!modulesJson.has(key)) {
					needsResave = true;
					continue;
				}
				
				JsonObject json = modulesJson.getAsJsonObject(key);
				if (module.isLocked) {
					module.isEnabled = true;
				} else if (json.has("isEnabled")) {
					module.isEnabled = json.get("isEnabled").getAsBoolean();
				} else {
					module.isEnabled = false;
					needsResave = true;
				}
				if (json.has("isFavorite")) {
					module.isFavorite = json.get("isFavorite").getAsBoolean();
				} else {
					module.isFavorite = false;
				}

				for (Field field : module.getClass().getDeclaredFields()) {
					if (field.isAnnotationPresent(ModConfig.class) || field.isAnnotationPresent(ModConfigExtra.class)) {
						field.setAccessible(true);
						String fkey = field.getName();
						if (json.has(fkey)) {
							try {
								field.set(module, GSON.fromJson(json.get(fkey), field.getType()));
							} catch (Exception e) {}
						} else {
							// data not in JSON, keep variable default but mark for resave
							needsResave = true;
						}
					}
				}
			}
		} catch (Exception e) {}
		
		if (needsResave && !modules.isEmpty()) {
			saveConfigs();
		}
		
		LucentAPI.getHUDManager().loadAll();
	}

	public void saveConfigs() {
		File profilesDir = new File(configDirectory, "profiles");
		File profileDir = new File(profilesDir, currentProfile);
		if (!profileDir.exists()) profileDir.mkdirs();

		File configFile = new File(profileDir, "config.json");
		JsonObject root = null;

		if (configFile.exists()) {
			try (BufferedReader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8)) {
				root = GSON.fromJson(reader, JsonObject.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (root == null) root = new JsonObject();
		JsonObject modulesJson = root.has("modules") ? root.getAsJsonObject("modules") : new JsonObject();

		for (Mod module : modules) {
			JsonObject json = new JsonObject();
			json.addProperty("isEnabled", module.isEnabled);
			json.addProperty("isFavorite", module.isFavorite);
			for (Field field : module.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(ModConfig.class) || field.isAnnotationPresent(ModConfigExtra.class)) {
					field.setAccessible(true);
					try {
						Object val = field.get(module);
						if (val != null) {
							json.add(field.getName(), GSON.toJsonTree(val));
						}
					} catch (Exception e) {}
				}
			}
			modulesJson.add(module.name, json);
		}

		root.add("modules", modulesJson);

		try (BufferedWriter writer = Files.newBufferedWriter(configFile.toPath(), StandardCharsets.UTF_8)) {
			GSON.toJson(root, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		LucentAPI.getHUDManager().save(this);
	}

	public void loadGlobalConfig() {
		File file = new File(getGlobalLucentDir(), "lucent_global.json"); 
		if (!file.exists()) {
			saveGlobalConfig();
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class); if (json == null) return;

			if (json.has("currentProfile")) currentProfile = json.get("currentProfile").getAsString();

			if (json.has("theme")) {
				String themeName = json.get("theme").getAsString();
				ThemeManager.applyTheme(ThemeManager.findTheme(themeName));
			}

			if (json.has("openAnimation")) LucentConfig.openAnimation = json.get("openAnimation").getAsBoolean();
			if (json.has("uiBlur")) LucentConfig.uiBlur = json.get("uiBlur").getAsBoolean();
			if (json.has("uiBlurStrength")) LucentConfig.uiBlurStrength = json.get("uiBlurStrength").getAsFloat();
			if (json.has("setupLanguage")) LucentConfig.setupLanguage = json.get("setupLanguage").getAsString();
			if (json.has("uiScale")) LucentConfig.uiScale = json.get("uiScale").getAsFloat();
			if (json.has("renderPremiumHats")) LucentConfig.renderPremiumHats = json.get("renderPremiumHats").getAsBoolean();
			if (json.has("renderPremiumWings")) LucentConfig.renderPremiumWings = json.get("renderPremiumWings").getAsBoolean();
			if (json.has("renderPremiumCapes")) LucentConfig.renderPremiumCapes = json.get("renderPremiumCapes").getAsBoolean();
			if (json.has("playNotificationSound")) LucentConfig.playNotificationSound = json.get("playNotificationSound").getAsBoolean();

		} catch (Exception e) {}
	}

	public void saveGlobalConfig() {
		File globalFile = getGlobalLucentDir();
		if (!globalFile.exists()) globalFile.mkdirs();
		File file = new File(globalFile, "lucent_global.json");
		
		JsonObject json = null;
		if (file.exists()) {
			try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
				json = GSON.fromJson(reader, JsonObject.class);
			} catch (Exception e) {}
		}
		
		if (json == null) json = new JsonObject();

		json.addProperty("currentProfile", currentProfile);

		if (ThemeManager.currentTheme != null) json.addProperty("theme", ThemeManager.currentTheme.name);
		json.addProperty("openAnimation", LucentConfig.openAnimation);
		json.addProperty("uiBlur", LucentConfig.uiBlur);
		json.addProperty("uiBlurStrength", LucentConfig.uiBlurStrength);
		json.addProperty("setupLanguage", LucentConfig.setupLanguage);
		json.addProperty("uiScale", LucentConfig.uiScale);
		json.addProperty("renderPremiumHats", LucentConfig.renderPremiumHats);
		json.addProperty("renderPremiumWings", LucentConfig.renderPremiumWings);
		json.addProperty("renderPremiumCapes", LucentConfig.renderPremiumCapes);
		json.addProperty("playNotificationSound", LucentConfig.playNotificationSound);

		try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
			GSON.toJson(json, writer);
		} catch (Exception e) {}
	}

	public static void handleKeyInput(int key, int action) {
		boolean pressed = (action == 1);
		boolean keyDown = (action != 0);

		for (ModManager manager : INSTANCES) {
			for (KeyBindFieldInfo info : manager.keyBindFields) {
				if (!info.module.isEnabled) continue;
				KeyBind keybind = info.getKeyBind();
				if (keybind != null && keybind.isBound() && keybind.isKey() && keybind.keyCode == key) {
					LucentEvent.KEYBIND_EVENT.invoker().onKeybind(new LucentEvent.KeybindEvent(keybind, pressed, keyDown));
				}
			}
		}
	}

	public static void handleMouseInput(int button, int action) {
		boolean pressed = (action == 1);
		boolean keyDown = (action != 0);

		for (ModManager manager : INSTANCES) {
			for (KeyBindFieldInfo info : manager.keyBindFields) {
				if (!info.module.isEnabled) continue;
				KeyBind keybind = info.getKeyBind();
				if (keybind != null && keybind.isBound() && keybind.isMouse() && keybind.mouseButton == button) {
					LucentEvent.KEYBIND_EVENT.invoker().onKeybind(new LucentEvent.KeybindEvent(keybind, pressed, keyDown));
				}
			}
		}
	}

}