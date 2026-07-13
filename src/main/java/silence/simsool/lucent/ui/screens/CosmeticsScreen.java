package silence.simsool.lucent.ui.screens;

import static silence.simsool.lucent.Lucent.mc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import silence.simsool.lucent.config.LucentConfig;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.general.models.data.nvg.NVGImageEntry;
import silence.simsool.lucent.general.utils.MinecraftColor;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.general.utils.useful.UMouse;
import silence.simsool.lucent.general.utils.useful.UScreen;
import silence.simsool.lucent.init.PremiumCosmetics;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.URender;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class CosmeticsScreen extends Screen {

	private static final int WINDOW_W = 1100;
	private static final int WINDOW_H = 680;
	private static final int LEFT_W   = 700;
	private static final int RIGHT_W  = 400;
	private static final int PAD      = 24;

	private int winX, winY;
	private float uiScale = 1.0f;
	private float openAnimationProgress = 0f;
	private long startTime = -1L;
	private int lastScreenWidth = -1;
	private int lastScreenHeight = -1;

	private float dragYaw = 0.0f;
	private float lastDragX = 0.0f;
	private boolean isDraggingPlayer = false;

	private final ModManager moduleManager;
	private String currentCategory = "Wings"; // Hats, Wings, Capes

	private final Map<Identifier, NVGImageEntry> nvgImageCache = new HashMap<>();

	private long openTime = -1L;

	public CosmeticsScreen(ModManager moduleManager) {
		super(Component.literal("Lucent Cosmetics"));
		this.moduleManager = moduleManager;
	}

	@Override
	protected void init() {
		super.init();
		openTime = System.currentTimeMillis() + 150L;
		PremiumCosmetics.isPreviewActive = true;
		lastScreenWidth = UDisplay.getScreenWidth();
		lastScreenHeight = UDisplay.getScreenHeight();
		if (lastScreenWidth <= 0 || lastScreenHeight <= 0) {
			winX = 0;
			winY = 0;
			uiScale = 1.0f;
			return;
		}

		float standardScale = NVGRenderer.getStandardGuiScale();
		if (standardScale <= 0f) standardScale = 1f;
		float screenW = UDisplay.getScreenWidth() / standardScale;
		float screenH = UDisplay.getScreenHeight() / standardScale;

		float pad = 40f;
		float scaleX = (screenW - pad) / WINDOW_W;
		float scaleY = (screenH - pad) / WINDOW_H;
		float autoFit = Math.max(0.1f, Math.min(1.0f, Math.min(scaleX, scaleY)));
		
		uiScale = Math.max(0.1f, autoFit * LucentConfig.uiScale);
		if (!Float.isFinite(uiScale) || uiScale <= 0.01f || uiScale > 10.0f) {
			uiScale = 1.0f;
		}

		float virtScreenW = screenW / uiScale;
		float virtScreenH = screenH / uiScale;

		if (!Float.isFinite(virtScreenW) || !Float.isFinite(virtScreenH)) {
			winX = 0;
			winY = 0;
		}
		else {
			winX = (int)((virtScreenW - WINDOW_W) / 2f);
			winY = (int)((virtScreenH - WINDOW_H) / 2f);
		}

		if (winX < -2000 || winX > 10000 || winY < -2000 || winY > 10000) {
			winX = 0;
			winY = 0;
		}

		UUID localUuid = mc.getUser().getProfileId();
		PremiumCosmetics.localPreviews.clear();
		String currentWing = PremiumCosmetics.getCosmeticMode(localUuid, "wing");
		if (currentWing != null) PremiumCosmetics.localPreviews.put("wing", currentWing);
		else PremiumCosmetics.localPreviews.put("wing", "none");
	}

	@Override
	public void onClose() {
		PremiumCosmetics.isPreviewActive = false;

		for (NVGImageEntry entry : nvgImageCache.values()) {
			NanoVG.nvgDeleteImage(NVGRenderer.getVG(), entry.nvgImageHandle);
		}
		nvgImageCache.clear();

		super.onClose();
	}

	private int getOrCreateNvgImage(Identifier id, int glId, int w, int h) {
		if (id == null || glId == -1) return -1;
		NVGImageEntry entry = nvgImageCache.get(id);
		if (entry != null && entry.glId == glId) {
			return entry.nvgImageHandle;
		}
		if (entry != null) {
			NanoVG.nvgDeleteImage(NVGRenderer.getVG(), entry.nvgImageHandle);
			nvgImageCache.remove(id);
		}
		int handle = NVGRenderer.createNVGImage(glId, w, h);
		if (handle != -1) {
			nvgImageCache.put(id, new NVGImageEntry(glId, handle));
		}
		return handle;
	}

	private int getGlTextureId(Identifier id) {
		if (id == null) return -1;
		try {
			var tex = mc.getTextureManager().getTexture(id);
			if (tex == null) return -1;
			GpuTexture gpuTex = tex.getTexture();
			if (gpuTex instanceof GlTexture glTex) return glTex.glId();
			return -1;
		} catch (Throwable t) {
			return -1;
		}
	}

	@Override
	public void render(GuiGraphics graphics, int mx, int my, float delta) {
		int currentW = UDisplay.getScreenWidth();
		int currentH = UDisplay.getScreenHeight();
		if (currentW > 0 && currentH > 0 && (currentW != lastScreenWidth || currentH != lastScreenHeight)) {
			if (lastScreenWidth != -1 && lastScreenHeight != -1) {
				this.resize(UDisplay.getGuiScaledWidth(), UDisplay.getGuiScaledHeight());
			}
			lastScreenWidth = currentW;
			lastScreenHeight = currentH;
		}

		if (currentW <= 0 || currentH <= 0 || width <= 0 || height <= 0) return;

		if (startTime == -1L) startTime = System.currentTimeMillis();

		float elapsed = (float)(System.currentTimeMillis() - startTime);
		openAnimationProgress = Math.min(1f, elapsed / 400f);

		NVGPIPRenderer.draw(graphics, 0, 0, width, height, () -> {
			float gs  = NVGRenderer.getStandardGuiScale();
			float smx = UMouse.getNvgScaledX(uiScale);
			float smy = UMouse.getNvgScaledY(uiScale);

			NVGRenderer.push();
			
			// Open Animation
			float ease = UAnimation.Easing.spring(openAnimationProgress);
			NVGRenderer.translate(width / 2f, height / 2f);
			NVGRenderer.scale(0.9f + 0.1f * ease, 0.9f + 0.1f * ease);
			NVGRenderer.translate(-width / 2f, -height / 2f);
			NVGRenderer.globalAlpha(UAnimation.clamp(openAnimationProgress * 1.5f, 0f, 1f));
			NVGRenderer.scale(gs * uiScale, gs * uiScale);

			// 1. Draw Frame
			drawFrame();

			// 2. Draw Header & Tabs
			renderHeaderAndTabs(smx, smy);

			// 3. Draw Showcase & Player
			renderPlayerShowcase(smx, smy);

			// 4. Draw Album Grid
			renderItemAlbum(smx, smy);

			NVGRenderer.pop();
		});

		if (mc.player != null && openTime < System.currentTimeMillis()) {
			graphics.pose().pushMatrix();
			graphics.pose().scale(uiScale, uiScale);

			int sx = winX + LEFT_W + PAD;
			int sy = winY + PAD;
			int sw = RIGHT_W - PAD * 2;
			int sh = WINDOW_H - PAD * 2;

			float px = sx + sw / 2f;
			float py = sy + sh / 2f - 20f;

			float currentScale = (float) mc.options.guiScale().get();
			if (currentScale == 0) currentScale = 4.0f;
			float guiFactor = 2.0f / currentScale;

			int xp = 120;
			int yp = 140;
			float scaledX = (px - xp) * uiScale * guiFactor;
			float scaledY = (py - yp) * uiScale * guiFactor;
			float scaledWidth = (xp * 2) * uiScale * guiFactor;
			float scaledHeight = (yp * 2) * uiScale * guiFactor;

			int x1 = (int) scaledX / 2;
			int y1 = (int) scaledY / 2;
			int x2 = (int) (scaledX + scaledWidth) / 2;
			int y2 = (int) (scaledY + scaledHeight) / 2;

			if (isDraggingPlayer) {
				float currentMouseX = UMouse.getScaledX();
				dragYaw -= (currentMouseX - lastDragX) * 1.5f;
				lastDragX = currentMouseX;
			}

			float yawOffset = (currentCategory.equalsIgnoreCase("Wings") || currentCategory.equalsIgnoreCase("Capes")) ? 0f : 180f;
			float yaw = yawOffset + dragYaw;
			float pitch = 0.0f;

			float invScale = 1.0f / uiScale;

			Matrix3x2fStack pose = graphics.pose();
			pose.pushMatrix();
			pose.scale(invScale, invScale);

			URender.drawPlayerWithRotation(
				graphics, x1, y1, x2, y2, (int) (68 * uiScale * guiFactor), yaw, pitch, mc.player
			);

			pose.popMatrix();

			graphics.pose().popMatrix();
		}

		super.render(graphics, mx, my, delta);
	}

	private void drawFrame() {
		int round = 20;
		NVGRenderer.dropShadow(winX, winY, WINDOW_W, WINDOW_H, 30f, 0f, round);
		NVGRenderer.rect(winX, winY, WINDOW_W, WINDOW_H, 0xD9151821, round);
		NVGRenderer.outlineRect(winX, winY, WINDOW_W, WINDOW_H, 1.5f, 0x1AFFFFFF, round);

		NVGRenderer.line(winX + LEFT_W, winY + 40, winX + LEFT_W, winY + WINDOW_H - 40, 1f, 0x10FFFFFF);
	}

	private void renderHeaderAndTabs(float smx, float smy) {
		int ix = winX + PAD;
		NVGRenderer.text("Wardrobe", ix, winY + 30f, Fonts.PRETENDARD_SEMIBOLD, UIColors.PURE_WHITE, 22f);
		
		int tabX = winX + 300;
		int tabY = winY + 28;
		
		drawCategoryTab(tabX, tabY, "Hats", currentCategory.equals("Hats"), smx, smy);
		drawCategoryTab(tabX + 90, tabY, "Wings", currentCategory.equals("Wings"), smx, smy);
		drawCategoryTab(tabX + 180, tabY, "Capes", currentCategory.equals("Capes"), smx, smy);
	}

	private void drawCategoryTab(int x, int y, String label, boolean active, float smx, float smy) {
		int w = 76;
		int h = 32;
		boolean hov = smx >= x && smx <= x + w && smy >= y && smy <= y + h;
		
		int bg = active ? 0xFF3182F6 : (hov ? 0x1AFFFFFF : 0x0AFFFFFF);
		int fg = active ? UIColors.PURE_WHITE : (hov ? UIColors.TEXT_PRIMARY : UIColors.TEXT_SECONDARY);
		
		NVGRenderer.rect(x, y, w, h, bg, 16f);
		
		float tw = NVGRenderer.textWidth(label, Fonts.PRETENDARD_MEDIUM, 13f);
		NVGRenderer.text(label, x + (w - tw) / 2f, y + (h - 13f) / 2f, Fonts.PRETENDARD_MEDIUM, fg, 13f);
	}

	private void renderPlayerShowcase(float smx, float smy) {
		int sx = winX + LEFT_W + PAD;
		int sy = winY + PAD;
		int sw = RIGHT_W - PAD * 2;
		int sh = WINDOW_H - PAD * 2;

		float px = sx + sw / 2f;
		float py = sy + sh / 2f - 20f;
		
		NVGRenderer.circle(px, py, 110f, 0x083182F6);
		NVGRenderer.circle(px, py, 70f, 0x0B3182F6);

		boolean isPremium = PremiumCosmetics.isPremium(mc.getUser().getProfileId());
		String btnText = isPremium ? "Edit Custom Cosmetic" : "Get Premium Wardrobe";
		
		int btnW = sw - 20;
		int btnH = 46;
		int bx = sx + (sw - btnW) / 2;
		int by = sy + sh - 110;

		boolean hov = smx >= bx && smx <= bx + btnW && smy >= by && smy <= by + btnH;
		int btnBg = hov ? 0xFF1B64DA : 0xFF3182F6;
		NVGRenderer.rect(bx, by, btnW, btnH, btnBg, 12f);
		
		float tw = NVGRenderer.textWidth(btnText, Fonts.PRETENDARD_SEMIBOLD, 14f);
		NVGRenderer.text(btnText, bx + (btnW - tw) / 2f, by + (btnH - 14f) / 2f, Fonts.PRETENDARD_SEMIBOLD, UIColors.PURE_WHITE, 14f);

		int bW = (sw - 30) / 2;
		int backX = sx + 10;
		int closeX = sx + 10 + bW + 10;
		int bY = sy + sh - 46;
		int bH = 36;

		boolean hovBack = smx >= backX && smx <= backX + bW && smy >= bY && smy <= bY + bH;
		int backBg = hovBack ? 0x15FFFFFF : 0x08FFFFFF;
		NVGRenderer.rect(backX, bY, bW, bH, backBg, 8f);
		float btw = NVGRenderer.textWidth("Back", Fonts.PRETENDARD_MEDIUM, 13f);
		NVGRenderer.text("Back", backX + (bW - btw) / 2f, bY + (bH - 13f) / 2f, Fonts.PRETENDARD_MEDIUM, UIColors.TEXT_SECONDARY, 13f);

		boolean hovClose = smx >= closeX && smx <= closeX + bW && smy >= bY && smy <= bY + bH;
		int closeBg = hovClose ? 0x15FFFFFF : 0x08FFFFFF;
		NVGRenderer.rect(closeX, bY, bW, bH, closeBg, 8f);
		float ctw = NVGRenderer.textWidth("Close", Fonts.PRETENDARD_MEDIUM, 13f);
		NVGRenderer.text("Close", closeX + (bW - ctw) / 2f, bY + (bH - 13f) / 2f, Fonts.PRETENDARD_MEDIUM, UIColors.TEXT_SECONDARY, 13f);
	}

	private void renderItemAlbum(float smx, float smy) {
		int ax = winX + PAD;
		int ay = winY + 85;

		int gridY = ay;
		int colW = 148;
		int rowH = 176;
		int gap = 20;

		int index = 0;
		if (currentCategory.equals("Wings")) {
			drawAlbumCard(ax, gridY, colW, rowH, "None", "none", index++, smx, smy);
			drawAlbumCard(ax + colW + gap, gridY, colW, rowH, "Ender Wings", "ender", index++, smx, smy);
			drawAlbumCard(ax + (colW + gap) * 2, gridY, colW, rowH, "Mage Wings", "mage", index++, smx, smy);
		}
		else {
			drawAlbumCard(ax, gridY, colW, rowH, "None", "none", index++, smx, smy);
		}
	}

	private void drawAlbumCard(int x, int y, int w, int h, String name, String mode, int index, float smx, float smy) {
		boolean selected = false;
		String curMode = PremiumCosmetics.localPreviews.get(currentCategory.toLowerCase().substring(0, currentCategory.length() - 1));
		if (curMode == null && mode.equals("none")) selected = true;
		else if (curMode != null && curMode.equalsIgnoreCase(mode)) selected = true;

		boolean equipped = false;
		String equippedMode = PremiumCosmetics.getCosmeticMode(mc.getUser().getProfileId(), currentCategory.toLowerCase().substring(0, currentCategory.length() - 1));
		if (equippedMode == null || equippedMode.isEmpty()) equippedMode = "none";
		if (equippedMode.equalsIgnoreCase(mode)) equipped = true;

		boolean hov = smx >= x && smx <= x + w && smy >= y && smy <= y + h;

		int bg;
		int border;
		if (selected) {
			bg = 0x1A3182F6;
			border = 0xFF3182F6;
		}
		else if (equipped) {
			bg = hov ? 0x25FFFFFF : 0x18FFFFFF;
			border = hov ? 0x50FFFFFF : 0x35FFFFFF;
		}
		else {
			bg = hov ? 0x15FFFFFF : 0x08FFFFFF;
			border = hov ? 0x2AFFFFFF : 0x10FFFFFF;
		}

		NVGRenderer.rect(x, y, w, h, bg, 16f);
		NVGRenderer.outlineRect(x, y, w, h, 1.5f, border, 16f);

		if (mode.equals("none")) {
			float tw = NVGRenderer.textWidth("None", Fonts.PRETENDARD_SEMIBOLD, 15f);
			NVGRenderer.text("None", x + (w - tw) / 2f, y + (h - 40) / 2f, Fonts.PRETENDARD_SEMIBOLD, UIColors.TEXT_SECONDARY, 15f);
		}
		else {
			if (equipped) {
				float tw = NVGRenderer.textWidth("Equipped", Fonts.PRETENDARD_SEMIBOLD, 14f);
				NVGRenderer.text("Equipped", x + w - tw - 12, y + 12, Fonts.PRETENDARD_SEMIBOLD, MinecraftColor.GREEN.getRGB(), 14f);
			}
			Identifier wingId = PremiumCosmetics.getCosmeticTexturePreview("wings", mode);
			if (wingId != null) {
				int wingGlId = getGlTextureId(wingId);
				int nvgWingImg = wingGlId != -1 ? getOrCreateNvgImage(wingId, wingGlId, 64, 64) : -1;

				if (nvgWingImg != -1) {
					float cx = x + w / 2f;
					float cy = y + (h - 40) / 2f;
					float cardScale = 2.0f;

					NVGRenderer.push();
					NVGRenderer.translate(cx, cy);
					NVGRenderer.image(nvgWingImg, 64, 64, 24, 0, 24, 32, -24f, -24f * cardScale, 35f * cardScale, 40f * cardScale, 0f);
					NVGRenderer.pop();
				}
			}
		}

		float tw = NVGRenderer.textWidth(name, Fonts.PRETENDARD_MEDIUM, 13f);
		int fontColor = selected ? 0xFF3182F6 : (equipped ? 0xFF3182F6 : UIColors.TEXT_PRIMARY);
		NVGRenderer.text(name, x + (w - tw) / 2f, y + h - 38, Fonts.PRETENDARD_MEDIUM, fontColor, 13f);

		if (!mode.equals("none")) {
			int priceVal = PremiumCosmetics.getCosmeticPrice(currentCategory.toLowerCase(), mode);
			String price = "$" + String.format("%.2f", (float) priceVal);
			float ptw = NVGRenderer.textWidth(price, Fonts.PRETENDARD_SEMIBOLD, 11f);
			NVGRenderer.text(price, x + (w - ptw) / 2f, y + h - 21, Fonts.PRETENDARD_SEMIBOLD, UIColors.TEXT_SECONDARY, 11f);
		}
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		float mx = UMouse.getNvgScaledX(uiScale);
		float my = UMouse.getNvgScaledY(uiScale);

		// Category Tabs Click
		int tabX = winX + 300;
		int tabY = winY + 28;
		if (my >= tabY && my <= tabY + 32) {
			if (mx >= tabX && mx <= tabX + 76) { currentCategory = "Hats"; return true; }
			if (mx >= tabX + 90 && mx <= tabX + 90 + 76) { currentCategory = "Wings"; return true; }
			if (mx >= tabX + 180 && mx <= tabX + 180 + 76) { currentCategory = "Capes"; return true; }
		}

		// Player Hitbox Drag Check
		if (event.button() == 0) {
			int sx = winX + LEFT_W + PAD;
			int sy = winY + PAD;
			int sw = RIGHT_W - PAD * 2;
			int sh = WINDOW_H - PAD * 2;
			float px = sx + sw / 2f;
			float py = sy + sh / 2f - 20f;
			int xp = 120;
			int yp = 140;

			float bx1 = px - xp;
			float bx2 = px + xp;
			float by1 = py - yp;
			float by2 = py + yp;

			if (mx >= bx1 && mx <= bx2 && my >= by1 && my <= by2) {
				isDraggingPlayer = true;
				lastDragX = UMouse.getScaledX();
				return true;
			}
		}

		// Discord / Purchase Button Click
		int btnW = (RIGHT_W - PAD * 2) - 20;
		int bx = winX + LEFT_W + PAD + ((RIGHT_W - PAD * 2) - btnW) / 2;
		int by = winY + PAD + (WINDOW_H - PAD * 2) - 110;
		if (mx >= bx && mx <= bx + btnW && my >= by && my <= by + 46) {
			Util.getPlatform().openUri(LucentConfig.DISCORD_LINK);
			return true;
		}

		// Back and Close Buttons Click
		int bW = ((RIGHT_W - PAD * 2) - 30) / 2;
		int backX = winX + LEFT_W + PAD + 10;
		int closeX = winX + LEFT_W + PAD + 10 + bW + 10;
		int bY = winY + PAD + (WINDOW_H - PAD * 2) - 46;
		if (my >= bY && my <= bY + 36) {
			if (mx >= backX && mx <= backX + bW) {
				UScreen.setScreen(new EditHUDScreen(moduleManager));
				return true;
			}
			if (mx >= closeX && mx <= closeX + bW) {
				this.onClose();
				return true;
			}
		}

		// Album Card Click
		int ax = winX + PAD;
		int ay = winY + 85;
		int colW = 148;
		int rowH = 176;
		int gap = 20;

		if (currentCategory.equals("Wings")) {
			if (mx >= ax && mx <= ax + colW && my >= ay && my <= ay + rowH) { selectCosmetic("none"); return true; }
			if (mx >= ax + colW + gap && mx <= ax + colW + gap + colW && my >= ay && my <= ay + rowH) { selectCosmetic("ender"); return true; }
			if (mx >= ax + (colW + gap) * 2 && mx <= ax + (colW + gap) * 2 + colW && my >= ay && my <= ay + rowH) { selectCosmetic("mage"); return true; }
		} else {
			if (mx >= ax && mx <= ax + colW && my >= ay && my <= ay + rowH) { selectCosmetic("none"); return true; }
		}

		return super.mouseClicked(event, isDoubleClick);
	}

	private void selectCosmetic(String mode) {
		PremiumCosmetics.localPreviews.put(currentCategory.toLowerCase().substring(0, currentCategory.length() - 1), mode);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (event.button() == 0) {
			isDraggingPlayer = false;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
			this.onClose();
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

}