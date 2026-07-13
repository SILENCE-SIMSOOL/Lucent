package silence.simsool.lucent.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.LucentConfig;
import silence.simsool.lucent.events.impl.EntityEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.models.data.cosmetics.CachedDisplayName;
import silence.simsool.lucent.general.models.data.cosmetics.CosmeticSetting;
import silence.simsool.lucent.general.models.data.cosmetics.UserCosmetic;
import silence.simsool.lucent.general.utils.APIHandler;
import silence.simsool.lucent.general.utils.OSUtils;

public class PremiumCosmetics {

	private static final String PREMIUM_URL = "https://gist.githubusercontent.com/SILENCE-SIMSOOL/7658533b89b3ccd69397a9fbf62b5190/raw/lucent_information.json";
	private static final Map<UUID, UserCosmetic> cache = new ConcurrentHashMap<>();
	private static final Map<Integer, UUID> idToUuidMap = new ConcurrentHashMap<>();

	private static final String DOWNLOAD_URL = "https://raw.githubusercontent.com/SILENCE-SIMSOOL/Lucent-Loader/main/cosmetics/cosmetics.zip";
	private static final String VERSION_URL = "https://raw.githubusercontent.com/SILENCE-SIMSOOL/Lucent-Loader/refs/heads/main/cosmetics/cosmetics_manager.json";
	private static final long DOWNLOAD_COOLDOWN_MS = 10_000; // 10 seconds
	private static volatile long lastDownloadAttempt = 0;
	private static volatile boolean isDownloading = false;

	public static final Map<UUID, CachedDisplayName> nameTagCache = new ConcurrentHashMap<>();
	public static final Map<UUID, CachedDisplayName> tabListCache = new ConcurrentHashMap<>();

	public static boolean isPreviewActive = false;
	public static final Map<String, String> localPreviews = new ConcurrentHashMap<>();

	private static final Map<String, Identifier> forcedTextureCache = new ConcurrentHashMap<>();
	private static final Map<String, Identifier> previewTextureCache = new ConcurrentHashMap<>();
	private static final String PRICES_URL = "https://gist.githubusercontent.com/SILENCE-SIMSOOL/fca65e88b36c057898fd8f47ad9f5230/raw/lucent_cosmetics_prices.json";
	private static final Map<String, Integer> cosmeticPrices = new ConcurrentHashMap<>();

	public static void init() {
		loadPremiumData();
		loadCosmeticPrices();
		checkAndDownloadCosmetics();

		LucentEvent.WORLD_LOAD_EVENT.register(() -> {
			idToUuidMap.clear();
			nameTagCache.clear();
			tabListCache.clear();
		});

		EntityEvent.ENTITY_JOIN_EVENT.register(event -> {
			if (event.entity instanceof Player player) {
				idToUuidMap.put(event.entity.getId(), player.getUUID());
			}
		});

		EntityEvent.ENTITY_LEAVE_EVENT.register(event -> {
			if (event.entity instanceof Player player) {
				UUID uuid = player.getUUID();
				idToUuidMap.remove(event.entity.getId(), player.getUUID());
				nameTagCache.remove(uuid);
				tabListCache.remove(uuid);
			}
		});

		LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityType == EntityTypes.PLAYER) {
				@SuppressWarnings("unchecked")
				RenderLayerParent<AvatarRenderState, PlayerModel> parent = (RenderLayerParent<AvatarRenderState, PlayerModel>) entityRenderer;
				registrationHelper.register(new WingLayer(parent));
			}
		});
	}

	public static UUID getUuidFromState(int stateId) {
		return idToUuidMap.get(stateId);
	}

	public static boolean isPremium(UUID uuid) {
		if (uuid == null) return false;
		return cache.containsKey(uuid);
	}

	public static boolean hasCosmetic(UUID uuid, String cosmeticName) {
		if (uuid == null) return false;
		UserCosmetic info = cache.get(uuid); if (info == null) return false;
		
		CosmeticSetting setting = info.cosmetics.get(cosmeticName);
		return setting != null && setting.enabled;
	}

	public static String getCosmeticMode(UUID uuid, String cosmeticName) {
		if (uuid == null) return null;
		UserCosmetic info = cache.get(uuid); if (info == null) return null;
		
		CosmeticSetting setting = info.cosmetics.get(cosmeticName);
		return setting != null ? setting.mode : null;
	}

	public static void loadPremiumData() {
		CompletableFuture.runAsync(() -> {
			try {
				JsonObject jsonResponse = APIHandler.getObjectResponse(PREMIUM_URL + "?t=" + System.currentTimeMillis());
				if (jsonResponse != null) parseJson(jsonResponse);
				else Lucent.LOG.error("Failed fetch premium data.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static UUID parseUuid(String hex) {
		if (hex.contains("-")) return UUID.fromString(hex);
		String formatted = hex.substring(0, 8) + "-" +
				hex.substring(8, 12) + "-" +
				hex.substring(12, 16) + "-" +
				hex.substring(16, 20) + "-" +
				hex.substring(20);
		return UUID.fromString(formatted);
	}

	private static void parseJson(JsonObject json) {
		try {
			JsonObject data = json.getAsJsonObject("data"); if (data == null) return;
			JsonObject premium = data.getAsJsonObject("premium"); if (premium == null) return;

			Map<UUID, UserCosmetic> tempCache = new HashMap<>();
			for (Map.Entry<String, JsonElement> entry : premium.entrySet()) {
				JsonObject userObj = entry.getValue().getAsJsonObject();
				if (!userObj.has("uuid")) continue;
				String uuidStr = userObj.get("uuid").getAsString().toLowerCase();
				UUID uuid = parseUuid(uuidStr);
				
				JsonObject cosmetics = userObj.getAsJsonObject("cosmetics");
				Map<String, CosmeticSetting> cosmeticMap = new HashMap<>();
				if (cosmetics != null) {
					for (Map.Entry<String, JsonElement> cosEntry : cosmetics.entrySet()) {
						JsonObject cosObj = cosEntry.getValue().getAsJsonObject();
						boolean enabled = cosObj.has("enabled") && cosObj.get("enabled").getAsBoolean();
						String mode = cosObj.has("mode") ? cosObj.get("mode").getAsString() : "";
						cosmeticMap.put(cosEntry.getKey(), new CosmeticSetting(enabled, mode));
					}
				}
				tempCache.put(uuid, new UserCosmetic(cosmeticMap));
			}

			cache.clear();
			cache.putAll(tempCache);
			nameTagCache.clear();
			tabListCache.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearTextureCache() {
		for (UserCosmetic info : cache.values()) {
			for (CosmeticSetting setting : info.cosmetics.values()) {
				setting.resolvedTexture = null;
			}
		}
		forcedTextureCache.clear();
		previewTextureCache.clear();
	}

	public static void downloadCosmetics() {
		CompletableFuture.runAsync(PremiumCosmetics::downloadCosmeticsSync);
	}

	public static Identifier getCosmeticTexture(String type, CosmeticSetting setting) {
		if (setting.resolvedTexture != null) {
			return setting.resolvedTexture;
		}

		String filename = setting.mode.toLowerCase() + "_" + type.toLowerCase() + ".png";
		File cosmeticFile = new File(OSUtils.getLucentDir(), "resources/cosmetics/" + type.toLowerCase() + "/" + filename);

		if (!cosmeticFile.exists()) {
			tryDownloadWithCooldown();
			return null;
		}

		try {
			Identifier id = Identifier.fromNamespaceAndPath(Lucent.ID, "cosmetics_" + type.toLowerCase() + "_" + setting.mode.toLowerCase());
			try (InputStream is = new FileInputStream(cosmeticFile)) {
				NativeImage nativeImage = NativeImage.read(is);
				DynamicTexture dynamicTexture = new DynamicTexture(() -> id.toString(), nativeImage);
				Minecraft.getInstance().getTextureManager().register(id, dynamicTexture);
				setting.resolvedTexture = id;
				return id;
			}
		} catch (Exception e) {
			e.printStackTrace();
			tryDownloadWithCooldown();
		}
		return null;
	}

	private static void tryDownloadWithCooldown() {
	    long now = System.currentTimeMillis();
	    if (now - lastDownloadAttempt > DOWNLOAD_COOLDOWN_MS) {
	        lastDownloadAttempt = now;
	        downloadCosmetics();
	    }
	}

	private static synchronized void downloadCosmeticsSync() {
		if (isDownloading) return;
		isDownloading = true;

		Lucent.LOG.info("Downloading cosmetics resources...");

		try {
			File resourcesDir = new File(OSUtils.getLucentDir(), "resources");
			if (!resourcesDir.exists()) resourcesDir.mkdirs();

			File cosmeticsDir = new File(resourcesDir, "cosmetics");
			if (cosmeticsDir.exists()) {
				deleteDirectory(cosmeticsDir);
			}

			URL url = URI.create(DOWNLOAD_URL).toURL();

			try (InputStream in = url.openStream();
				 ZipInputStream zipIn = new ZipInputStream(in)) {

				ZipEntry entry;
				byte[] buffer = new byte[4096];
				while ((entry = zipIn.getNextEntry()) != null) {
					String entryName = entry.getName();
					File targetFile;
					if (entryName.startsWith("cosmetics/")) targetFile = new File(resourcesDir, entryName);
					else targetFile = new File(cosmeticsDir, entryName);

					if (entry.isDirectory()) {
						targetFile.mkdirs();
					}
					else {
						File parent = targetFile.getParentFile();
						if (parent != null && !parent.exists()) {
							parent.mkdirs();
						}
						try (FileOutputStream fos = new FileOutputStream(targetFile)) {
							int len;
							while ((len = zipIn.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}
						}
					}
					zipIn.closeEntry();
				}
			}
			clearTextureCache();
			Lucent.LOG.info("Cosmetics resource download complete!");

		} catch (Exception e) {
			Lucent.LOG.error("Failed to download cosmetics resources.", e);
		} finally {
			isDownloading = false;
		}
	}

	private static void deleteDirectory(File file) {
		if (file.isDirectory()) {
			File[] contents = file.listFiles();
			if (contents != null) {
				for (File f : contents) {
					deleteDirectory(f);
				}
			}
		}
		file.delete();
	}

	public static void checkAndDownloadCosmetics() {
		CompletableFuture.runAsync(() -> {
			try {
				File resourcesDir = new File(OSUtils.getLucentDir(), "resources");
				File cosmeticsDir = new File(resourcesDir, "cosmetics");
				File managerFile = new File(cosmeticsDir, "cosmetics_manager.json");

				boolean needDownload = false;

				if (!cosmeticsDir.exists() || !managerFile.exists()) {
					needDownload = true;
				} else {
					String localVersion = null;
					try (FileReader reader = new FileReader(managerFile)) {
						JsonObject localJson = JsonParser.parseReader(reader).getAsJsonObject();
						if (localJson.has("version")) {
							localVersion = localJson.get("version").getAsString();
						}
					} catch (Exception e) {
						needDownload = true;
					}

					if (!needDownload && localVersion != null) {
						JsonObject remoteJson = APIHandler.getObjectResponse(VERSION_URL + "?t=" + System.currentTimeMillis());
						if (remoteJson != null && remoteJson.has("version")) {
							String remoteVersion = remoteJson.get("version").getAsString();
							if (isVersionOlder(localVersion, remoteVersion)) {
								needDownload = true;
							}
						}
					}
				}
				if (needDownload) downloadCosmeticsSync();

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static boolean isVersionOlder(String localVersion, String remoteVersion) {
		try {
			String[] localParts = localVersion.split("\\.");
			String[] remoteParts = remoteVersion.split("\\.");
			int length = Math.max(localParts.length, remoteParts.length);
			for (int i = 0; i < length; i++) {
				int localPart = i < localParts.length ? Integer.parseInt(localParts[i].trim()) : 0;
				int remotePart = i < remoteParts.length ? Integer.parseInt(remoteParts[i].trim()) : 0;
				if (localPart < remotePart) return true;
				if (localPart > remotePart) return false;
			}
		} catch (Exception e) {
			return true;
		}
		return false;
	}

	public static CosmeticSetting getCosmeticSetting(UUID uuid, String cosmeticName) {
		if (uuid == null) return null;
		if (cosmeticName.equalsIgnoreCase("wings") && !LucentConfig.renderPremiumWings) return null;
		if (cosmeticName.equalsIgnoreCase("hat") && !LucentConfig.renderPremiumHats) return null;
		if (cosmeticName.equalsIgnoreCase("cape") && !LucentConfig.renderPremiumCapes) return null;
		if (isPreviewActive && uuid.equals(Minecraft.getInstance().getUser().getProfileId())) {
			String mode = localPreviews.get(cosmeticName);
			if (mode == null || mode.isEmpty() || mode.equalsIgnoreCase("none")) {
				return new CosmeticSetting(false, "");
			}
			return new CosmeticSetting(true, mode);
		}
		UserCosmetic info = cache.get(uuid); if (info == null) return null;
		return info.cosmetics.get(cosmeticName);
	}

	public static Component getFormattedDisplayName(Map<UUID, CachedDisplayName> cacheMap, UUID uuid, Component original) {
		CachedDisplayName cached = cacheMap.get(uuid);
		if (cached != null) {
			return cached.formatted;
		}
		MutableComponent formatted = original.copy().append(" §d+§r");
		cacheMap.put(uuid, new CachedDisplayName(original, formatted));
		return formatted;
	}

	public static Identifier getCosmeticTextureForced(String type, String mode) {
		String key = type + "_" + mode;
		Identifier cached = forcedTextureCache.get(key);
		if (cached != null) {
			return cached;
		}

		String filename = mode.toLowerCase() + "_" + type.toLowerCase() + ".png";
		File cosmeticFile = new File(OSUtils.getLucentDir(), "resources/cosmetics/" + type.toLowerCase() + "/" + filename);
		if (!cosmeticFile.exists()) {
			tryDownloadWithCooldown();
			return null;
		}

		try {
			Identifier id = Identifier.fromNamespaceAndPath(Lucent.ID, "cosmetics_debug_" + key.toLowerCase());
			try (InputStream is = new FileInputStream(cosmeticFile)) {
				NativeImage nativeImage = NativeImage.read(is);
				DynamicTexture dynamicTexture = new DynamicTexture(() -> id.toString(), nativeImage);
				Minecraft.getInstance().getTextureManager().register(id, dynamicTexture);
				forcedTextureCache.put(key, id);
				return id;
			}
		} catch (Exception e) {
			e.printStackTrace();
			tryDownloadWithCooldown();
		}
		return null;
	}

	public static Identifier getCosmeticTexturePreview(String type, String mode) {
		String key = type + "_" + mode + "_preview";
		Identifier cached = previewTextureCache.get(key);
		if (cached != null) {
			return cached;
		}

		String filename = mode.toLowerCase() + "_" + type.toLowerCase() + "_preview.png";
		File cosmeticFile = new File(OSUtils.getLucentDir(), "resources/cosmetics/" + type.toLowerCase() + "/" + filename);
		if (!cosmeticFile.exists()) {
			tryDownloadWithCooldown();
			return null;
		}

		try {
			Identifier id = Identifier.fromNamespaceAndPath(Lucent.ID, "cosmetics_preview_" + key.toLowerCase());
			try (InputStream is = new FileInputStream(cosmeticFile)) {
				NativeImage nativeImage = NativeImage.read(is);
				DynamicTexture dynamicTexture = new DynamicTexture(() -> id.toString(), nativeImage);
				Minecraft.getInstance().getTextureManager().register(id, dynamicTexture);
				previewTextureCache.put(key, id);
				return id;
			}
		} catch (Exception e) {
			e.printStackTrace();
			tryDownloadWithCooldown();
		}
		return null;
	}

	public static void loadCosmeticPrices() {
		CompletableFuture.runAsync(() -> {
			try {
				JsonObject jsonResponse = APIHandler.getObjectResponse(PRICES_URL + "?t=" + System.currentTimeMillis());
				if (jsonResponse != null) {
					Map<String, Integer> tempPrices = new HashMap<>();
					for (Map.Entry<String, JsonElement> entry : jsonResponse.entrySet()) {
						tempPrices.put(entry.getKey().toLowerCase(), entry.getValue().getAsInt());
					}
					cosmeticPrices.clear();
					cosmeticPrices.putAll(tempPrices);
				} else {
					Lucent.LOG.error("Failed fetch cosmetic prices data.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static int getCosmeticPrice(String type, String mode) {
		String key = (mode + "_" + type).toLowerCase();
		return cosmeticPrices.getOrDefault(key, 0);
	}

}