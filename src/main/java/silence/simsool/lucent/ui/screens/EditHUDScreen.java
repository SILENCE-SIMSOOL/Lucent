package silence.simsool.lucent.ui.screens;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.LucentConfig;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.LucentUtils;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.general.utils.UMouse;
import silence.simsool.lucent.hud.HUDManager;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class EditHUDScreen extends Screen {
	private static final int SNAP_PX        = 10;
	private static final int C_DIM          = 0x88000000;
	private static final int C_BORDER       = 0x4DFFFFFF;
	private static final int C_BORDER_HOV   = 0x80FFFFFF;
	private static final int C_SCALE_BORDER = 0xCCFFFFFF;
	private static final int C_FILL         = 0x1AFFFFFF;
	private static final int C_TOOLTIP_BG   = 0xF01A1A1E;
	private static final int C_TEXT         = 0xFFFFFFFF;
	private static final int C_TEXT_DIM     = 0xFF9CA3AF;
	private static final int C_TEXT_ACCENT  = 0xFF60A5FA;
	private static final int C_MENU_BG      = 0xF01A1A1E;

	private LucentHUD draggingMove  = null;
	private LucentHUD draggingScale = null;
	private float dragOffsetX, dragOffsetY;
	private float scaleDragStartX, scaleDragBaseScale;

	private LucentHUD contextMenuHud = null;
	private float contextMenuX, contextMenuY;

	private final boolean showModsButton;
	private boolean iconsLoaded = false;
	private Image iconProfiles, iconEditHud;

	private long startTime = -1L;
	private long closeStartTime = -1L;
	private boolean closing = false;

	private final silence.simsool.lucent.config.ModManager parentManager;

	public EditHUDScreen() {
		this(null);
	}

	public EditHUDScreen(silence.simsool.lucent.config.ModManager parentManager) {
		super(Component.literal("Edit HUD"));
		this.parentManager = parentManager;
		this.showModsButton = true;
		LucentHUD.isEditHudOpen = true;
	}

	@Override
	protected void init() {
		super.init();
		this.startTime = System.currentTimeMillis();
		this.closing = false;
		this.closeStartTime = -1L;
	}

	@Override
	public void onClose() {
		if (LucentConfig.openAnimation && !closing) {
			closing = true;
			closeStartTime = System.currentTimeMillis();
		} else {
			exit();
		}
	}

	private void exit() {
		super.onClose();
	}

	@Override
	public void removed() {
		super.removed();
		LucentHUD.isEditHudOpen = false;
		HUDManager.INSTANCE.save();
	}

	@Override
	public void renderBackground(GuiGraphics ctx, int mx, int my, float delta) {
		ctx.fill(0, 0, width, height, 0x11000000);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		float animP;
		if (LucentConfig.openAnimation) {
			if (closing) {
				if (closeStartTime == -1L) closeStartTime = System.currentTimeMillis();
				float elapsed = (float) (System.currentTimeMillis() - closeStartTime);
				animP = 1f - Math.min(1f, elapsed / 300f);
				if (animP <= 0f) {
					exit();
					return;
				}
			} else {
				float elapsed = (float) (System.currentTimeMillis() - startTime);
				animP = Math.min(1f, elapsed / 450f);
			}
		} else {
			animP = 1f;
			if (closing) {
				exit();
				return;
			}
		}

		for (LucentHUD hud : HUDManager.INSTANCE.getHuds()) {
			if (hud.isEnabled() && hud.getRenderType() == RenderType.MINECRAFT) {
				hud.preview();
			}
		}
		NVGPIPRenderer.draw(guiGraphics, 0, 0, width, height, () -> {
			float gs = NVGRenderer.getStandardGuiScale();
			NVGRenderer.push();
			NVGRenderer.scale(gs, gs);

			if (LucentConfig.openAnimation) {
				NVGRenderer.globalAlpha(UAnimation.clamp(animP * 1.5f, 0, 1));
			}

		for (LucentHUD hud : HUDManager.INSTANCE.getHuds()) {
			if (hud.isEnabled() && hud.getRenderType() == RenderType.NANOVG) {
				hud.preview();
			}
		}
		renderOverlay(animP);

			NVGRenderer.pop();
		});
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}

	private void renderOverlay(float animP) {
		float gs  = NVGRenderer.getStandardGuiScale();
		float vw  = UDisplay.getScreenWidth()  / gs;
		float vh  = UDisplay.getScreenHeight() / gs;
		float mx  = UMouse.getScaledX(1f);
		float my  = UMouse.getScaledY(1f);

		NVGRenderer.rect(0, 0, vw, vh, C_DIM);

		drawCrosshair(vw, vh);

		for (LucentHUD hud : HUDManager.INSTANCE.getHuds()) {
			if (hud.isEnabled()) drawHudBorder(hud, mx, my);
		}

		drawCrosshair(vw, vh);

		if (draggingMove == null && draggingScale == null && contextMenuHud == null) {
			for (LucentHUD hud : HUDManager.INSTANCE.getHuds()) {
				if (hud.isEnabled() && isInsideHud(hud, mx, my)) {
					drawTooltip(hud, mx, my, vw, vh);
					break;
				}
			}
		}
		if (draggingScale != null) drawScaleBadge(draggingScale);
		if (contextMenuHud != null) drawContextMenu(vw, vh);
		if (showModsButton)  drawModsButton(vw, vh, mx, my, animP);
	}

	private void drawModsButton(float vw, float vh, float mx, float my, float animP) {
		if (!iconsLoaded) {
			try {
				iconProfiles = LucentUtils.createIcon("profiles");
				iconEditHud  = LucentUtils.createIcon("edithud");
				iconsLoaded = true;
			} catch (Exception ignored) {}
		}

		float bw = 164f, bh = 48f;
		float bx = (vw - bw) / 2f, by = ((vh - bh) / 2f);
		int round = 8;
		float sideS = 48f;
		float gap = 10f;

		float logoY = by - 100;
		// Staggered progress based on animP
		float logoP = UAnimation.clamp(animP * 1.4f, 0f, 1f);
		float btn1P = UAnimation.clamp((animP - 0.15f) * 1.5f, 0f, 1f);
		float btn2P = UAnimation.clamp((animP - 0.3f) * 1.5f, 0f, 1f);
		float btn3P = UAnimation.clamp((animP - 0.45f) * 1.5f, 0f, 1f);

		float logoEase = UAnimation.Easing.spring(logoP);
		float alpha = UAnimation.clamp(logoP * 2f, 0f, 1f);

		// 1. Logo Pop
		NVGRenderer.push();
		NVGRenderer.translate(vw / 2f, logoY);
		NVGRenderer.scale(0.7f + 0.3f * logoEase, 0.7f + 0.3f * logoEase);
		drawBrandLogo(0, 0, alpha);
		NVGRenderer.pop();

		// 2. Buttons Staggered Slide
		if (btn1P > 0) {
			float e = UAnimation.Easing.spring(btn1P);
			drawIconButton(bx - sideS - gap - (1f - e) * 60f, by, sideS, sideS, iconEditHud, mx, my, btn1P);
		}
		if (btn3P > 0) {
			float e = UAnimation.Easing.spring(btn3P);
			drawIconButton(bx + bw + gap + (1f - e) * 60f, by, sideS, sideS, iconProfiles, mx, my, btn3P);
		}
		if (btn2P > 0) {
			float e = UAnimation.Easing.spring(btn2P);
			float curBy = by + (1f - e) * 40f;
			
			boolean hov = mx >= bx && mx <= bx + bw && my >= curBy && my <= curBy + bh;
			int bg = UIColors.withAlpha(hov ? UIColors.CARD_HOVER : UIColors.CARD_BG, (int) (180 * btn2P));

			NVGRenderer.rect(bx, curBy, bw, bh, bg, round);
			NVGRenderer.outlineRect(bx, curBy, bw, bh, 1.5f, UIColors.withAlpha(0xFFFFFFFF, (int) ((hov ? 60 : 30) * btn2P)), round);

			int size = 18;
			float tw = NVGRenderer.textWidth("M O D S", Fonts.PRETENDARD_SEMIBOLD, size);
			NVGRenderer.text("M O D S", bx + (bw - tw) / 2f, curBy + (bh - size) / 2f + 1, Fonts.PRETENDARD_SEMIBOLD, UIColors.withAlpha(UIColors.PURE_WHITE, (int) ((hov ? 255 : 185) * btn2P)), size);
		}
	}

	private void drawIconButton(float x, float y, float w, float h, Image icon, float mx, float my, float alpha) {
		boolean hov = mx >= x && mx <= x + w && my >= y && my <= y + h;
		int bg = UIColors.withAlpha(hov ? UIColors.CARD_HOVER : UIColors.CARD_BG, (int) (180 * alpha));

		NVGRenderer.rect(x, y, w, h, bg, 8f);
		NVGRenderer.outlineRect(x, y, w, h, 1.5f, UIColors.withAlpha(0xFFFFFFFF, (int) ((hov ? 60 : 30) * alpha)), 8f);

		if (icon != null) {
			float is = 20f;
			NVGRenderer.image(icon, x + (w - is) / 2f, y + (h - is) / 2f, is, is, alpha);
		}
	}

	private void drawBrandLogo(float cx, float cy, float alpha) {
		float size = 50f;

		NVGRenderer.outlineCircle(cx, cy, size / 2f + 5, 2.5f, UIColors.withAlpha(UIColors.ACCENT_BLUE, (int) (180 * alpha)));
		NVGRenderer.circle(cx, cy, size / 2f - 2, UIColors.withAlpha(UIColors.ACCENT_BLUE, (int) (40 * alpha)));

		float fs = 32f;
		float tw = NVGRenderer.textWidth("LUCENT", Fonts.PRETENDARD_SEMIBOLD, fs);
		NVGRenderer.text("LUCENT", cx - tw / 2f, cy + 44, Fonts.PRETENDARD_SEMIBOLD, UIColors.withAlpha(UIColors.PURE_WHITE, (int) (255 * alpha)), fs);

		float sfs = 10f;
		float stw = NVGRenderer.textWidth("U L T I M A T E   C O N F I G", Fonts.PRETENDARD_MEDIUM, sfs);
		NVGRenderer.text("U L T I M A T E   C O N F I G", cx - stw / 2f, cy + 76, Fonts.PRETENDARD_MEDIUM, UIColors.withAlpha(UIColors.PURE_WHITE, (int) (120 * alpha)), sfs);
	}

	private void drawCrosshair(float vw, float vh) {
		if (draggingMove == null) return;

		float cx = vw / 2f;
		float cy = vh / 2f;

		float rx = draggingMove.getRenderX(), ry = draggingMove.getRenderY();
		float rw = draggingMove.getScaledWidth(), rh = draggingMove.getScaledHeight();

		float hcx = rx + rw / 2f;
		float hcy = ry + rh / 2f;

		float eps = SNAP_PX;
		boolean matchX = Math.abs(hcx - cx) < eps;
		boolean matchY = Math.abs(hcy - cy) < eps;

		if (matchY) NVGRenderer.line(0, cy, vw, cy, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 150));
		if (matchX) NVGRenderer.line(cx, 0, cx, vh, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 150));

		// Requirement 2: Alignment with other HUDs
		for (LucentHUD other : HUDManager.INSTANCE.getHuds()) {
			if (!other.isEnabled() || other == draggingMove) continue;

			float orx = other.getRenderX(), ory = other.getRenderY();
			float orw = other.getScaledWidth(), orh = other.getScaledHeight();
			float ohcx = orx + orw / 2f;
			float ohcy = ory + orh / 2f;

			// Match X (Left, Center, Right)
			if (Math.abs(rx - orx) < eps) {
				NVGRenderer.line(orx, 0, orx, vh, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 100));
			} else if (Math.abs(hcx - ohcx) < eps) {
				NVGRenderer.line(ohcx, 0, ohcx, vh, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 100));
			} else if (Math.abs((rx + rw) - (orx + orw)) < eps) {
				NVGRenderer.line(orx + orw, 0, orx + orw, vh, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 100));
			}

			// Match Y (Top, Center, Bottom)
			if (Math.abs(ry - ory) < eps) {
				NVGRenderer.line(0, ory, vw, ory, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 100));
			} else if (Math.abs(hcy - ohcy) < eps) {
				NVGRenderer.line(0, ohcy, vw, ohcy, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 100));
			} else if (Math.abs((ry + rh) - (ory + orh)) < eps) {
				NVGRenderer.line(0, ory + orh, vw, ory + orh, 1f, UIColors.withAlpha(UIColors.ACCENT_BLUE, 100));
			}
		}

		if (matchX && matchY) NVGRenderer.outlineCircle(cx, cy, 4f, 2f, UIColors.PURE_WHITE);
	}

	private void drawHudBorder(LucentHUD hud, float mx, float my) {
		float rx = hud.getRenderX(), ry = hud.getRenderY();
		float rw = hud.getScaledWidth(), rh = hud.getScaledHeight();

		int handleIndex = getHandleUnderMouse(hud, mx, my);
		boolean scaleDrag  = draggingScale == hud;
		boolean moveDrag   = draggingMove  == hud;

		NVGRenderer.rect(rx, ry, rw, rh, C_FILL, 3f);

		int   bc; float bt;
		if (scaleDrag)        { bc = C_SCALE_BORDER; bt = 2.5f; }
		else if (moveDrag)    { bc = C_BORDER_HOV;   bt = 2.0f; }
		else if (handleIndex != -1) { bc = C_BORDER_HOV;   bt = 2.0f; }
		else                  { bc = C_BORDER;        bt = 1.5f; }

		NVGRenderer.outlineRect(rx, ry, rw, rh, bt, bc, 3f);

		boolean hl = (handleIndex != -1) || scaleDrag;
		drawCornerHandles(rx, ry, rw, rh, hl);
	}

	private void drawCornerHandles(float rx, float ry, float rw, float rh, boolean highlight) {
		int c  = highlight ? C_BORDER_HOV : C_BORDER;
		float hs = 7f, off = hs / 2f;
		NVGRenderer.rect(rx - off,      ry - off,      hs, hs, c, 1.5f);
		NVGRenderer.rect(rx + rw - off, ry - off,      hs, hs, c, 1.5f);
		NVGRenderer.rect(rx - off,      ry + rh - off, hs, hs, c, 1.5f);
		NVGRenderer.rect(rx + rw - off, ry + rh - off, hs, hs, c, 1.5f);
	}

	private void drawTooltip(LucentHUD hud, float mx, float my, float vw, float vh) {
		String[] lines = {
			"X: " + Math.round(hud.x) + "  Y: " + Math.round(hud.y),
			"Scale: " + String.format("%.1f", hud.scale),
			"Align: " + hud.alignment.displayName()
		};

		float fs = 11f, padX = 10f, padY = 7f, lineH = 15f;
		float tw = 0;
		for (String l : lines) tw = Math.max(tw, NVGRenderer.textWidth(l, Fonts.PRETENDARD, fs));
		float bw = tw + padX * 2, bh = lineH * lines.length + padY * 2;

		float tx = mx + 14, ty = my + 10;
		if (tx + bw > vw - 4) tx = mx - bw - 6;
		if (ty + bh > vh - 4) ty = vh - bh - 4;

		NVGRenderer.rect(tx, ty, bw, bh, C_TOOLTIP_BG, 6f);
		NVGRenderer.outlineRect(tx, ty, bw, bh, 1f, UIColors.withAlpha(0xFFFFFFFF, 18), 6f);

		for (int i = 0; i < lines.length; i++) {
			int col = (i == 0) ? C_TEXT : (i == 2 ? C_TEXT_ACCENT : C_TEXT_DIM);
			NVGRenderer.text(lines[i], tx + padX, ty + padY + i * lineH, Fonts.PRETENDARD, col, fs);
		}
	}

	private void drawScaleBadge(LucentHUD hud) {
		String label = String.format("%.1f×", hud.scale);
		float fs = 13f;
		float tw = NVGRenderer.textWidth(label, Fonts.PRETENDARD, fs);
		float bw = tw + 20f, bh = 26f;
		float bx = hud.getRenderX() + hud.getScaledWidth() / 2f - bw / 2f;
		float by = hud.getRenderY() - bh - 8f;

		NVGRenderer.rect(bx, by, bw, bh, C_TOOLTIP_BG, 6f);
		NVGRenderer.rect(bx + 4, by + bh - 2.5f, bw - 8, 2.5f, C_SCALE_BORDER, 1.5f);
		NVGRenderer.outlineRect(bx, by, bw, bh, 1f, UIColors.withAlpha(C_SCALE_BORDER, 80), 6f);
		NVGRenderer.text(label, bx + 10, by + (bh - fs) / 2f, Fonts.PRETENDARD, C_TEXT, fs);
	}

	private void drawContextMenu(float vw, float vh) {
		HUDAlignment[] opts = HUDAlignment.values();
		float iH = 28f, iW = 120f, pad = 8f;
		float totalH = iH * opts.length + iH + pad * 2 + 5f; // +iH+5f for separator and Delete option

		float cx = clamp(contextMenuX, 0, vw - iW - 4);
		float cy = clamp(contextMenuY, 0, vh - totalH - 4);

		NVGRenderer.rect(cx, cy, iW, totalH, C_MENU_BG, 8f);
		NVGRenderer.outlineRect(cx, cy, iW, totalH, 1f, UIColors.withAlpha(0xFFFFFFFF, 15), 8f);

		for (int i = 0; i < opts.length; i++) {
			HUDAlignment opt = opts[i];
			float iy  = cy + pad + i * iH;
			boolean selected = contextMenuHud.alignment == opt;

			if (selected) NVGRenderer.rect(cx + 6, iy + 2, iW - 12, iH - 4, UIColors.withAlpha(UIColors.ACCENT_BLUE, 50), 5f);

			int tc = selected ? UIColors.ACCENT_BLUE : C_TEXT_DIM;
			if (selected) NVGRenderer.circle(cx + 14, iy + iH / 2f, 3f, UIColors.ACCENT_BLUE);
			NVGRenderer.text(opt.displayName(), cx + 24, iy + (iH - 11f) / 2f, Fonts.PRETENDARD, tc, 11f);
		}

		// Separator
		float sepY = cy + pad + opts.length * iH + 2.5f;
		NVGRenderer.rect(cx + 8, sepY, iW - 16, 1f, UIColors.withAlpha(UIColors.PURE_WHITE, 15));

		// Delete Option
		float delY = sepY + 2.5f;
		NVGRenderer.text("Delete", cx + 24, delY + (iH - 11f) / 2f, Fonts.PRETENDARD, UIColors.RED, 11f);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		int button = event.button();
		float gs   = NVGRenderer.getStandardGuiScale();
		float vw   = UDisplay.getScreenWidth()  / gs;
		float vh   = UDisplay.getScreenHeight() / gs;
		float mx   = UMouse.getScaledX(1f);
		float my   = UMouse.getScaledY(1f);

		// 컨텍스트 메뉴 처리
		if (contextMenuHud != null) {
			handleContextMenuClick(mx, my);
			contextMenuHud = null;
			return true;
		}

		List<LucentHUD> huds = HUDManager.INSTANCE.getHuds();

		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			for (int i = huds.size() - 1; i >= 0; i--) {
				LucentHUD hud = huds.get(i);
				if (!hud.isEnabled()) continue;
				
				int handle = getHandleUnderMouse(hud, mx, my);
				if (handle != -1) {
					draggingScale      = hud;
					scaleDragStartX    = mx;
					scaleDragBaseScale = hud.scale;
					return true;
				}
				if (isInsideInner(hud, mx, my)) {
					draggingMove = hud;
					dragOffsetX  = mx - hud.x * vw;
					dragOffsetY  = my - hud.y * vh;
					return true;
				}
			}

			if (showModsButton) {
				float bw = 164f, bh = 48f;
				float bx = (vw - bw) / 2f, by = ((vh - bh) / 2f);
				float sideS = 48f, gap = 10f;

				// Center Mods Button
				if (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh) {
					minecraft.setScreen(new ConfigScreen(parentManager != null ? parentManager : Lucent.config));
					return true;
				}

				// Left Button
				if (mx >= bx - sideS - gap && mx <= bx - gap && my >= by && my <= by + sideS) {
					minecraft.setScreen(new ConfigScreen(parentManager != null ? parentManager : Lucent.config));
					return true;
				}

				// Right Button (Profiles)
				if (mx >= bx + bw + gap && mx <= bx + bw + gap + sideS && my >= by && my <= by + sideS) {
					ConfigScreen cs = new ConfigScreen(parentManager != null ? parentManager : Lucent.config);
					minecraft.setScreen(cs);
					return true;
				}
			}
		}
		else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			for (int i = huds.size() - 1; i >= 0; i--) {
				LucentHUD hud = huds.get(i);
				if (isInsideHud(hud, mx, my)) {
					contextMenuHud = hud;
					contextMenuX   = mx;
					contextMenuY   = my;
					return true;
				}
			}
		}
		return super.mouseClicked(event, isDoubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (draggingMove != null || draggingScale != null) HUDManager.INSTANCE.save();
			draggingMove  = null;
			draggingScale = null;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
		if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return super.mouseDragged(event, mouseX, mouseY);

		float gs = NVGRenderer.getStandardGuiScale();
		float vw = UDisplay.getScreenWidth()  / gs;
		float vh = UDisplay.getScreenHeight() / gs;
		float mx = UMouse.getScaledX(1f);
		float my = UMouse.getScaledY(1f);

		if (draggingMove != null) {
			float rawX = mx - dragOffsetX, rawY = my - dragOffsetY;

			float snappedX = (float)(Math.round(rawX / SNAP_PX) * SNAP_PX);
			float snappedY = (float)(Math.round(rawY / SNAP_PX) * SNAP_PX);

			float scaledW = draggingMove.getScaledWidth();
			float scaledH = draggingMove.getScaledHeight();
			
			float minRx = 0, maxRx = vw - scaledW;
			float minRy = 0, maxRy = vh - scaledH;

			float renderX;
			switch (draggingMove.alignment) {
				case CENTER -> {
					renderX = snappedX - scaledW / 2f;
					if (renderX < minRx) snappedX = scaledW / 2f;
					else if (renderX > maxRx) snappedX = vw - scaledW / 2f;
				}
				case RIGHT -> {
					renderX = snappedX - scaledW;
					if (renderX < minRx) snappedX = scaledW;
					else if (renderX > maxRx) snappedX = vw;
				}
				default -> { // LEFT
					snappedX = clamp(snappedX, minRx, maxRx);
				}
			}
			snappedY = clamp(snappedY, minRy, maxRy);

			float hcx_raw = 0, hcy_raw = 0;
			switch (draggingMove.alignment) {
				case CENTER -> hcx_raw = snappedX;
				case RIGHT  -> hcx_raw = snappedX - scaledW / 2f;
				default     -> hcx_raw = snappedX + scaledW / 2f;
			}
			hcy_raw = snappedY + scaledH / 2f;

			if (Math.abs(hcx_raw - vw / 2f) < SNAP_PX) {
				switch (draggingMove.alignment) {
					case CENTER -> snappedX = vw / 2f;
					case RIGHT  -> snappedX = vw / 2f + scaledW / 2f;
					default     -> snappedX = vw / 2f - scaledW / 2f;
				}
			}
			if (Math.abs(hcy_raw - vh / 2f) < SNAP_PX) snappedY = vh / 2f - scaledH / 2f;

			// Snapping to other HUDs
			for (LucentHUD other : HUDManager.INSTANCE.getHuds()) {
				if (other == draggingMove) continue;

				float orx = other.getRenderX(), ory = other.getRenderY();
				float orw = other.getScaledWidth(), orh = other.getScaledHeight();
				float ohcx = orx + orw / 2f;
				float ohcy = ory + orh / 2f;

				// Current renderX with snappedX
				float curRx;
				switch (draggingMove.alignment) {
					case CENTER: curRx = snappedX - scaledW / 2f; break;
					case RIGHT:  curRx = snappedX - scaledW;      break;
					default:     curRx = snappedX;                break;
				}

				// Snap X
				if (Math.abs(curRx - orx) < SNAP_PX) {
					switch (draggingMove.alignment) {
						case CENTER: snappedX = orx + scaledW / 2f; break;
						case RIGHT:  snappedX = orx + scaledW;      break;
						default:     snappedX = orx;                break;
					}
				} else if (Math.abs((curRx + scaledW / 2f) - ohcx) < SNAP_PX) {
					switch (draggingMove.alignment) {
						case CENTER: snappedX = ohcx;                break;
						case RIGHT:  snappedX = ohcx + scaledW / 2f; break;
						default:     snappedX = ohcx - scaledW / 2f; break;
					}
				} else if (Math.abs((curRx + scaledW) - (orx + orw)) < SNAP_PX) {
					switch (draggingMove.alignment) {
						case CENTER: snappedX = orx + orw - scaledW / 2f; break;
						case RIGHT:  snappedX = orx + orw;                break;
						default:     snappedX = orx + orw - scaledW;      break;
					}
				}

				// Snap Y
				if (Math.abs(snappedY - ory) < SNAP_PX) snappedY = ory;
				else if (Math.abs((snappedY + scaledH / 2f) - ohcy) < SNAP_PX) snappedY = ohcy - scaledH / 2f;
				else if (Math.abs((snappedY + scaledH) - (ory + orh)) < SNAP_PX) snappedY = ory + orh - scaledH;
			}

			draggingMove.x = snappedX / vw;
			draggingMove.y = snappedY / vh;
			return true;
		}

		if (draggingScale != null) {
			float diff = (mx - scaleDragStartX) / 100f;
			draggingScale.scale = LucentHUD.clampScale(scaleDragBaseScale + diff);
			return true;
		}

		return super.mouseDragged(event, mouseX, mouseY);
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (input.key() == GLFW.GLFW_KEY_ESCAPE) {
			this.onClose();
			return true;
		}
		return super.keyPressed(input);
	}

	private boolean isInsideHud(LucentHUD h, float mx, float my) {
		float rx = h.getRenderX(), ry = h.getRenderY(), rw = h.getScaledWidth(), rh = h.getScaledHeight();
		return mx >= rx && mx <= rx + rw && my >= ry && my <= ry + rh;
	}

	private int getHandleUnderMouse(LucentHUD h, float mx, float my) {
		float rx = h.getRenderX(), ry = h.getRenderY(), rw = h.getScaledWidth(), rh = h.getScaledHeight();
		float hs = 10f, off = hs / 2f; // 히트박스는 시각적 크기보다 크게 (10px)

		if (mx >= rx - off      && mx <= rx + off      && my >= ry - off      && my <= ry + off)      return 0;
		if (mx >= rx + rw - off && mx <= rx + rw + off && my >= ry - off      && my <= ry + off)      return 1;
		if (mx >= rx - off      && mx <= rx + off      && my >= ry + rh - off && my <= ry + rh + off) return 2;
		if (mx >= rx + rw - off && mx <= rx + rw + off && my >= ry + rh - off && my <= ry + rh + off) return 3;

		return -1;
	}

	private boolean isInsideInner(LucentHUD h, float mx, float my) {
		float rx = h.getRenderX(), ry = h.getRenderY(), rw = h.getScaledWidth(), rh = h.getScaledHeight();
		return mx > rx && mx < rx + rw && my > ry && my < ry + rh && getHandleUnderMouse(h, mx, my) == -1;
	}

	private void handleContextMenuClick(float mx, float my) {
		if (contextMenuHud == null) return;
		HUDAlignment[] opts = HUDAlignment.values();
		float iH = 28f, iW = 120f, pad = 8f;
		float totalH = iH * opts.length + iH + pad * 2 + 5f;

		float gs = NVGRenderer.getStandardGuiScale();
		float vw = UDisplay.getScreenWidth()  / gs;
		float vh = UDisplay.getScreenHeight() / gs;
		float cx = clamp(contextMenuX, 0, vw - iW - 4);
		float cy = clamp(contextMenuY, 0, vh - totalH - 4);

		if (mx < cx || mx > cx + iW || my < cy || my > cy + totalH) return;

		int idx = -1;
		if (my < cy + pad + opts.length * iH) {
			idx = (int)((my - cy - pad) / iH);
		} else if (my >= cy + pad + opts.length * iH + 5f) {
			idx = opts.length;
		}

		if (idx >= 0 && idx < opts.length) {
			contextMenuHud.alignment = opts[idx];
			HUDManager.INSTANCE.save();
		} else if (idx == opts.length) {
			contextMenuHud.disable();
			HUDManager.INSTANCE.save();
		}
	}

	private static float clamp(float v, float min, float max) {
		return Math.max(min, Math.min(max, v));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}