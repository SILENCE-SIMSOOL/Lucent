package silence.simsool.lucent.guide;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.config.LucentConfig;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.general.utils.useful.UMouse;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/** A single anchored coach mark; opaque semantic surfaces keep text readable over the world. */
public final class GuideOverlay {

	// Current web guide: .modal-preview-card, .modal-desc and .btn-md.
	private static final int DARK_SURFACE = 0xFF131722;
	private static final int LIGHT_SURFACE = 0xFFFFFFFF;
	private static final int DARK_TEXT = 0xFFF5F5F5;
	private static final int LIGHT_TEXT = 0xFF111318;
	private static final int DARK_MUTED = 0xFF9CA3AF;
	private static final int LIGHT_MUTED = 0xFF374151;
	private static final int DARK_ACCENT = 0xFF2563EB;
	private static final int LIGHT_ACCENT = 0xFF0062FF;
	private static final float PANEL_RADIUS = 24;
	private static final float PANEL_PADDING = 26;
	private static final float BUTTON_HEIGHT = 44;
	private static final float BUTTON_RADIUS = 10;
	private static final float BODY_LINE_HEIGHT = 1.55f;
	private static GuideTarget card;
	private static GuideTarget skip;
	private static GuideTarget primary;
	private static float scale = 1;
	private static int focus = -1;
	private static boolean captured;
	private static long changedAt = System.nanoTime();
	private static long lastFrame = System.nanoTime();
	private static float hoverDelta;
	private static float skipHover;
	private static float primaryHover;

	private GuideOverlay() {}

	public static void resetFocus() {
		focus = -1;
		card = null;
		changedAt = System.nanoTime();
		skipHover = primaryHover = 0;
	}

	public static void render(GuiGraphics graphics) {
		GuideStep step = GuideManager.step();
		if (!GuideManager.inWorld() || step == null) { card = null; return; }
		float sw = UDisplay.getScreenWidth();
		float sh = UDisplay.getScreenHeight();
		if (sw <= 0 || sh <= 0) return;
		scale = NVGRenderer.getStandardGuiScale();
		float cw = sw / scale;
		float ch = sh / scale;
		// Preserve readable typography and hit targets on ordinary windows; fit very small windows as a unit.
		scale *= Math.min(1f, Math.min(cw / 400f, ch / 420f));
		cw = sw / scale;
		ch = sh / scale;
		final float canvasW = cw, canvasH = ch;
		NVGPIPRenderer.draw(graphics, 0, 0, UDisplay.getWidth(), UDisplay.getHeight(), () -> {
			NVGRenderer.push();
			NVGRenderer.scale(scale, scale);
			draw(step, canvasW, canvasH);
			NVGRenderer.pop();
		});
	}

	private static void draw(GuideStep step, float cw, float ch) {
		long now = System.nanoTime();
		hoverDelta = LucentConfig.openAnimation ? Math.min(1f, (now - lastFrame) / 150000000f) : 1f;
		lastFrame = now;
		boolean dark = !UIColors.isLightColor(UIColors.WIN_BG);
		int surface = dark ? DARK_SURFACE : LIGHT_SURFACE;
		int text = dark ? DARK_TEXT : LIGHT_TEXT;
		int muted = dark ? DARK_MUTED : LIGHT_MUTED;
		int accent = dark ? DARK_ACCENT : LIGHT_ACCENT;
		int border = dark ? 0xFF222630 : 0xFFE5E7EB;
		int caption = dark ? DARK_MUTED : 0xFF4B5563;
		boolean visible = step.visible().getAsBoolean();
		String title = GuideManager.text(step.title());
		String body = GuideManager.text(step.body()) + (visible ? "" : "\n\n" + GuideManager.text("guide.return"));
		float w = Math.min(375, cw - 32);
		float innerW = w - PANEL_PADDING * 2;
		String guideName = GuideManager.text(GuideManager.active().title());
		String progress = (GuideManager.index() + 1) + " / " + GuideManager.active().steps().size();
		float progressW = NVGRenderer.textWidth(progress, Fonts.PRETENDARD_MEDIUM, 13);
		float headerH = height(guideName, innerW - progressW - 12, 13, false, 1.3f);
		float titleH = height(title, innerW, 18, true, 1.3f);
		float bodyH = height(body, innerW, 15, false, BODY_LINE_HEIGHT);
		float errorH = GuideManager.storageError() ? height(GuideManager.text("guide.storage_error"), innerW, 13, false, BODY_LINE_HEIGHT) + 12 : 0;
		float h = PANEL_PADDING * 2 + headerH + 20 + titleH + 6 + bodyH + errorH + 20 + BUTTON_HEIGHT;
		float x = cw - w - 16, y = 16;
		GuideTarget target = visible ? step.target().get() : null;
		if (target != null) {
			float ratio = (float) UDisplay.getScreenWidth() / UDisplay.getGuiScaledWidth() / scale;
			target = new GuideTarget(target.x() * ratio, target.y() * ratio, target.width() * ratio, target.height() * ratio);
			float left = Math.max(0, target.x() - 6), top = Math.max(0, target.y() - 6);
			float right = Math.min(cw, target.x() + target.width() + 6), bottom = Math.min(ch, target.y() + target.height() + 6);
			NVGRenderer.rect(0, 0, cw, top, 0x66000000);
			NVGRenderer.rect(0, bottom, cw, ch - bottom, 0x66000000);
			NVGRenderer.rect(0, top, left, bottom - top, 0x66000000);
			NVGRenderer.rect(right, top, cw - right, bottom - top, 0x66000000);
			NVGRenderer.outlineRect(left, top, right - left, bottom - top, 2, accent, 12);
			// Try both sides before placing above/below, so the coach mark never covers its target.
			if (right + 16 + w <= cw - 16) x = right + 16;
			else if (left - 16 - w >= 16) x = left - 16 - w;
			else {
				x = Math.max(16, Math.min(cw - w - 16, left));
				y = bottom + 16 + h <= ch - 16 ? bottom + 16 : Math.max(16, top - h - 16);
			}
			if (right + 16 + w <= cw - 16 || left - 16 - w >= 16) y = Math.max(16, Math.min(ch - h - 16, top));
		}
		y = Math.max(16, Math.min(ch - h - 16, y));
		card = new GuideTarget(x, y, w, h);
		float t = LucentConfig.openAnimation ? Math.min(1f, (now - changedAt) / 240000000f) : 1f;
		NVGRenderer.globalAlpha(t * (dark ? 1f : 0.16f));
		NVGRenderer.dropShadow(x, y + 8, w, h, 28, 0, PANEL_RADIUS);
		NVGRenderer.globalAlpha(t);
		NVGRenderer.rect(x, y, w, h, surface, PANEL_RADIUS);
		NVGRenderer.outlineRect(x, y, w, h, 1, border, PANEL_RADIUS);
		float contentX = x + PANEL_PADDING, contentY = y + PANEL_PADDING;
		NVGRenderer.drawWrappedString(guideName, contentX, contentY, innerW - progressW - 12, Fonts.PRETENDARD_MEDIUM, 13, caption, 1.3f);
		NVGRenderer.text(progress, x + w - PANEL_PADDING - progressW, contentY, Fonts.PRETENDARD_MEDIUM, caption, 13);
		contentY += headerH + 20;
		NVGRenderer.drawWrappedString(title, contentX, contentY, innerW, Fonts.PRETENDARD_EXTRABOLD, 18, text, 1.3f);
		contentY += titleH + 6;
		NVGRenderer.drawWrappedString(body, contentX, contentY, innerW, Fonts.PRETENDARD, 15, muted, BODY_LINE_HEIGHT);
		if (errorH > 0) NVGRenderer.drawWrappedString(GuideManager.text("guide.storage_error"), contentX, contentY + bodyH + 12, innerW, Fonts.PRETENDARD, 13, muted, BODY_LINE_HEIGHT);
		float buttonW = (innerW - 10) / 2;
		float buttonY = y + h - PANEL_PADDING - BUTTON_HEIGHT;
		skip = new GuideTarget(contentX, buttonY, buttonW, BUTTON_HEIGHT);
		primary = new GuideTarget(contentX + buttonW + 10, buttonY, buttonW, BUTTON_HEIGHT);
		skipHover = button(skip, GuideManager.text("guide.skip"), false, true, focus == 0, dark, skipHover);
		boolean available = step.open() != null && (step.actionWhileVisible() || !visible);
		boolean enabled = step.completed() == null ? visible : available;
		String label = step.completed() == null
			? GuideManager.text(GuideManager.index() == GuideManager.active().steps().size() - 1 ? "guide.finish" : "guide.next")
			: GuideManager.text(available ? "guide.show" : "guide.wait");
		primaryHover = button(primary, label, true, enabled, focus == 1, dark, primaryHover);
		NVGRenderer.globalAlpha(1);
	}

	private static float height(String text, float width, float size, boolean heading, float lineHeight) {
		float[] bounds = NVGRenderer.wrappedTextBounds(text, width, size, heading ? Fonts.PRETENDARD_EXTRABOLD : Fonts.PRETENDARD, lineHeight);
		return Math.max(size * lineHeight, bounds[3] - bounds[1]);
	}

	private static float button(GuideTarget bounds, String label, boolean isPrimary, boolean enabled, boolean focused, boolean dark, float hover) {
		boolean hovered = enabled && bounds.contains(UMouse.getX() / scale, UMouse.getY() / scale);
		hover = Math.max(0, Math.min(1, hover + (hovered ? hoverDelta : -hoverDelta)));
		int background = isPrimary ? (dark ? DARK_ACCENT : LIGHT_ACCENT) : (dark ? DARK_SURFACE : LIGHT_SURFACE);
		int hoverBackground = isPrimary ? (dark ? 0xFF1D4ED8 : 0xFF0051D5) : (dark ? 0xFF1A1F2E : 0xFFF1F3F5);
		int text = isPrimary ? 0xFFF8F9FA : (dark ? DARK_TEXT : LIGHT_TEXT);
		int border = isPrimary ? background : (dark ? 0xFF2C313D : 0xFFD1D5DB);
		if (!enabled) {
			background = dark ? 0xFF1A1F2E : 0xFFEBEEF3;
			text = dark ? 0xFF4B5563 : 0xFF9AA2B5;
			border = dark ? 0xFF2C313D : 0xFFDCE0E8;
		} else {
			background = blend(background, hoverBackground, hover);
			border = isPrimary ? background : blend(border, dark ? 0xFF434A5B : 0xFF9CA3AF, hover);
		}
		float inset = captured && hovered ? bounds.width() * 0.015f : 0;
		float x = bounds.x() + inset, y = bounds.y() + (captured && hovered ? BUTTON_HEIGHT * 0.015f : 0);
		float w = bounds.width() - inset * 2, h = captured && hovered ? BUTTON_HEIGHT * 0.97f : BUTTON_HEIGHT;
		if (focused && enabled) NVGRenderer.outlineRect(x - 3, y - 3, w + 6, h + 6, 3.5f, 0x2E0062FF, BUTTON_RADIUS + 3);
		NVGRenderer.rect(x, y, w, h, background, BUTTON_RADIUS);
		NVGRenderer.outlineRect(x, y, w, h, 1, focused && enabled ? LIGHT_ACCENT : border, BUTTON_RADIUS);
		float tw = NVGRenderer.textWidth(label, Fonts.PRETENDARD_SEMIBOLD, 15);
		NVGRenderer.text(label, x + (w - tw) / 2, y + (h - 15) / 2, Fonts.PRETENDARD_SEMIBOLD, text, 15);
		return hover;
	}

	private static int blend(int from, int to, float t) {
		int r = Math.round(((from >> 16) & 255) * (1 - t) + ((to >> 16) & 255) * t);
		int g = Math.round(((from >> 8) & 255) * (1 - t) + ((to >> 8) & 255) * t);
		int b = Math.round((from & 255) * (1 - t) + (to & 255) * t);
		return 0xFF000000 | (r << 16) | (g << 8) | b;
	}

	public static boolean click(double mx, double my, int button) {
		if (!GuideManager.inWorld() || GuideManager.step() == null || card == null) return false;
		double ratio = (double) UDisplay.getScreenWidth() / UDisplay.getGuiScaledWidth() / scale;
		mx *= ratio; my *= ratio;
		if (!card.contains(mx, my)) { focus = -1; return false; }
		captured = true;
		if (button != GLFW_MOUSE_BUTTON_LEFT) return true;
		if (skip.contains(mx, my)) GuideManager.skip();
		else if (primary.contains(mx, my)) activate();
		return true;
	}

	public static boolean release(double mx, double my) {
		if (!captured) return false;
		captured = false;
		return true;
	}

	public static boolean key(int key, int modifiers) {
		if (!GuideManager.inWorld() || GuideManager.step() == null) return false;
		if (key == GLFW_KEY_ESCAPE) { GuideManager.skip(); return true; }
		if (key == GLFW_KEY_TAB) {
			focus = focus < 0 ? ((modifiers & GLFW_MOD_SHIFT) == 0 ? 0 : 1) : 1 - focus;
			return true;
		}
		if (focus >= 0 && (key == GLFW_KEY_ENTER || key == GLFW_KEY_SPACE)) {
			if (focus == 0) GuideManager.skip(); else activate();
			return true;
		}
		return false;
	}

	private static void activate() {
		if (GuideManager.step() == null) return;
		if (GuideManager.step().completed() == null) GuideManager.next();
		else GuideManager.open();
	}

}