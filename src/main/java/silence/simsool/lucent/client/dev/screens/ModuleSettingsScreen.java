package silence.simsool.lucent.client.dev.screens;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.ColorPickerButton;
import silence.simsool.lucent.ui.widget.Selector;
import silence.simsool.lucent.ui.widget.Slider;
import silence.simsool.lucent.ui.widget.ToggleButton;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class ModuleSettingsScreen extends Screen {

	// ── 색상 테마 ──────────────────────────────────
	private static final int COLOR_WINDOW_BG      = 0xCC121418;
	private static final int COLOR_SIDEBAR_BG     = 0xE60B0D10;
	private static final int COLOR_ACCENT         = 0xFF3B82F6;
	private static final int COLOR_TEXT_PRIMARY   = 0xFFFFFFFF;
	private static final int COLOR_TEXT_SECONDARY = 0xFF9CA3AF;
	private static final int COLOR_ITEM_BG        = 0xFF1A1D21;
	private static final int COLOR_ITEM_BORDER    = 0xFF2C3036;

	// ── 레이아웃 상수 ──────────────────────────────
	private static final int WINDOW_WIDTH   = 1400;
	private static final int WINDOW_HEIGHT  = 900;
	private static final int SIDEBAR_WIDTH  = 360;
	private static final int HEADER_HEIGHT  = 200;
	private static final int PADDING        = 40;
	private static final float CORNER_RADIUS = 24f;

	private int windowX, windowY;
	private int contentX, contentY, contentW, contentH;

	private final List<UIWidget> widgets = new ArrayList<>();
	private final List<UIWidget> overlayWidgets = new ArrayList<>();
	private EditBox searchField;

	private double scrollOffset = 0;
	private double maxScroll = 0;

	// ── 설정값 예시 ────────────────────────────────
	private boolean enableBlur = false;
	private boolean customGuiScale = true;
	private double scaleValue = 1.0;
	private int themeColor = 0xFF5D3FD3;
	private String releaseChannel = "Pre-Releases";

	public ModuleSettingsScreen() {
		super(Component.literal("Module Settings"));
	}

	// ── ★ Odin 스타일 마우스 물리 좌표 가져오기 ──
	private float getScaledMouseX() {
		return (float) (this.minecraft.mouseHandler.xpos() / NVGRenderer.getStandardGuiScale());
	}

	private float getScaledMouseY() {
		return (float) (this.minecraft.mouseHandler.ypos() / NVGRenderer.getStandardGuiScale());
	}

	@Override
	protected void init() {
		super.init();

		float guiScale = NVGRenderer.getStandardGuiScale();

		float screenW = this.minecraft.getWindow().getScreenWidth() / guiScale;
		float screenH = this.minecraft.getWindow().getScreenHeight() / guiScale;

		windowX = (int) ((screenW - WINDOW_WIDTH) / 2.0f);
		windowY = (int) ((screenH - WINDOW_HEIGHT) / 2.0f);

		contentX = windowX + SIDEBAR_WIDTH;
		contentY = windowY + HEADER_HEIGHT;
		contentW = WINDOW_WIDTH - SIDEBAR_WIDTH;
		contentH = WINDOW_HEIGHT - HEADER_HEIGHT;

		initWidgets();
	}

	private void initWidgets() {
		widgets.clear();
		overlayWidgets.clear();

		int searchW = 400;
		int searchX = windowX + WINDOW_WIDTH - PADDING - searchW - 60;
		int searchY = windowY + 40;
		searchField = new EditBox(font, searchX, searchY, searchW, 40, Component.literal("Search"));
		searchField.setHint(Component.literal("Search preferences..."));
		searchField.setBordered(false);

		int itemW = contentW - PADDING * 2;
		int currentY = contentY + PADDING + 48;

		addSettingRow(currentY, "Enable blur", "Toggle the background blur when the GUI is open", itemW, (x, y) -> {
			ToggleButton toggle = new ToggleButton(x + itemW - 48 - PADDING, y + 24, 48, 24, enableBlur);
			toggle.setOnChange(v -> enableBlur = v);
			return toggle;
		});
		currentY += 140;

		addSettingRowBig(currentY, 200, "Custom GUI Scale", "Change the overall scale of the interface", itemW, (x, y) -> {
			ToggleButton toggle = new ToggleButton(x + itemW - 48 - PADDING, y + 24, 48, 24, customGuiScale);
			toggle.setOnChange(v -> customGuiScale = v);
			widgets.add(toggle);

			Slider slider = new Slider(x + PADDING + 100, y + 110, 400, 48, 0.5, 2.0, 0.1, scaleValue);
			slider.setOnChange(v -> scaleValue = v);
			widgets.add(slider);
			return null;
		});
		currentY += 220;

		addSettingRow(currentY, "Theme Color", "Customize the primary accent color", itemW, (x, y) -> {
			ColorPickerButton cp = new ColorPickerButton(x + itemW - 80 - PADDING, y + 40, 80, 36, themeColor);
			cp.setOnChange(c -> themeColor = c);
			overlayWidgets.add(cp);
			return cp;
		});
		currentY += 140;

		addSettingRow(currentY, "Release channel", "Change the branch to pre-releases, etc", itemW, (x, y) -> {
			Selector selector = new Selector(x + itemW - 240 - PADDING, y + 20, 240, 38, List.of("Stable", "Pre-Releases", "Dev"));
			selector.setValue(releaseChannel);
			selector.setOnChange(v -> releaseChannel = v);
			overlayWidgets.add(selector);
			return selector;
		});

		int totalH = currentY - (contentY + PADDING) + 200;
		this.maxScroll = Math.max(0, totalH - contentH);
	}

	private void addSettingRow(int y, String label, String desc, int w, WidgetFactory factory) {
		int rowX = contentX + PADDING;
		widgets.add(new SettingsRow(rowX, y, w, 120, label, desc));
		UIWidget child = factory.create(rowX, y);
		if (child != null) widgets.add(child);
	}

	private void addSettingRowBig(int y, int h, String label, String desc, int w, WidgetFactory factory) {
		int rowX = contentX + PADDING;
		widgets.add(new SettingsRow(rowX, y, w, h, label, desc));
		factory.create(rowX, y);
	}

	interface WidgetFactory { UIWidget create(int x, int y); }

	@Override
	public void renderBackground(GuiGraphics ctx, int mouseX, int mouseY, float delta) {

	}

	@Override
	public void render(GuiGraphics ctx, int mouseX, int mouseY, float delta) {

		NVGPIPRenderer.draw(ctx, 0, 0, width, height, () -> {
			float guiScale = NVGRenderer.getStandardGuiScale();
			float scaledMouseX = getScaledMouseX();
			float scaledMouseY = getScaledMouseY();

			NVGRenderer.push();
			NVGRenderer.scale(guiScale, guiScale);

			NVGRenderer.rect(windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT, COLOR_WINDOW_BG, CORNER_RADIUS);
			NVGRenderer.rect(windowX, windowY, SIDEBAR_WIDTH, WINDOW_HEIGHT, COLOR_SIDEBAR_BG, CORNER_RADIUS);

			renderSidebarNVG();
			renderHeaderNVG();
			renderSearchBarBackgroundNVG();

			NVGRenderer.pushScissor(contentX, contentY, contentW, contentH);
			NVGRenderer.push();
			NVGRenderer.translate(0, (float) -scrollOffset);

			int startY = contentY + PADDING;
			NVGRenderer.text("General", contentX + PADDING, startY, Fonts.PRETENDARD_SEMIBOLD, COLOR_TEXT_PRIMARY, 28f);
			int verTitleY = startY + 48 + 140 + 220 + 140 + 20;
			NVGRenderer.text("Versions", contentX + PADDING, verTitleY, Fonts.PRETENDARD_SEMIBOLD, COLOR_TEXT_PRIMARY, 28f);

			for (UIWidget widget : widgets) {
				widget.render(ctx, (int)scaledMouseX, (int)(scaledMouseY + scrollOffset), delta);
			}
			for (UIWidget widget : overlayWidgets) {
				widget.render(ctx, (int)scaledMouseX, (int)(scaledMouseY + scrollOffset), delta);
			}

			NVGRenderer.pop();
			NVGRenderer.popScissor();

			for (UIWidget widget : overlayWidgets) {
				int originalY = widget.getY();
				widget.setPosition(widget.getX(), (int)(originalY - scrollOffset));
				widget.renderOverlay(ctx, (int)scaledMouseX, (int)scaledMouseY, delta);
				widget.setPosition(widget.getX(), originalY);
			}

			NVGRenderer.pop();
		});

		if (searchField != null) {
			ctx.pose().pushMatrix();
			ctx.pose().translate(20, 0);
			searchField.render(ctx, mouseX, mouseY, delta);
			ctx.pose().popMatrix();
		}
	}

	private void renderSidebarNVG() {
		int x = windowX + PADDING;
		int y = windowY + PADDING;
		NVGRenderer.text("SILENCE UTILS", x, y + 20, Fonts.PRETENDARD_SEMIBOLD, COLOR_ACCENT, 28f);

		y += 100;
		renderMenuSectionNVG("MODS & OPTIONS", new String[]{ "Mods", "Profiles", "Keybinds" }, -1, x, y);
		y += 240;
		renderMenuSectionNVG("PERSONALIZATION", new String[]{ "Themes", "Preferences" }, 1, x, y);
	}

	private void renderMenuSectionNVG(String title, String[] items, int activeIdx, int x, int y) {
		NVGRenderer.text(title, x, y, Fonts.PRETENDARD_MEDIUM, 0xFF555555, 18f);
		y += 36;

		for (int i = 0; i < items.length; i++) {
			boolean active = (i == activeIdx);
			int color = active ? COLOR_TEXT_PRIMARY : COLOR_TEXT_SECONDARY;

			if (active) {
				NVGRenderer.rect(windowX + 16, y - 10, SIDEBAR_WIDTH - 32, 52, UColor.withAlpha(COLOR_ACCENT, 40), 10f);
				NVGRenderer.rect(windowX + 6, y - 8, 5, 46, COLOR_ACCENT, 2.5f);
			}

			NVGRenderer.text(items[i], x + 44, y + 6, Fonts.PRETENDARD, color, 20f);
			y += 60;
		}
	}

	private void renderHeaderNVG() {
		int x = contentX + PADDING;
		int y = windowY + PADDING;
		NVGRenderer.text("←  →", x, y + 10, Fonts.PRETENDARD, COLOR_TEXT_SECONDARY, 22f);
		NVGRenderer.text("Preferences", x + 96, y + 10, Fonts.PRETENDARD_SEMIBOLD, COLOR_TEXT_PRIMARY, 26f);

		int tabY = y + 80;
		renderTabNVG("General", x, tabY, true);
		renderTabNVG("HUD", x + 160, tabY, false);
		renderTabNVG("Animations", x + 280, tabY, false);
		renderTabNVG("Hypixel", x + 460, tabY, false);

		int closeX = windowX + WINDOW_WIDTH - PADDING - 20;
		NVGRenderer.text("✕", closeX, windowY + PADDING + 10, Fonts.PRETENDARD, COLOR_TEXT_SECONDARY, 22f);
	}

	private void renderTabNVG(String text, int x, int y, boolean active) {
		float w = NVGRenderer.textWidth(text, Fonts.PRETENDARD, 20f) + 36f;
		int h = 46;

		int bg = active ? COLOR_ACCENT : 0x00000000;
		int border = active ? COLOR_ACCENT : 0xFF333333;
		int textC = active ? 0xFFFFFFFF : COLOR_TEXT_SECONDARY;

		if (active) {
			NVGRenderer.rect(x, y, w, h, bg, 10f);
		} else {
			NVGRenderer.outlineRect(x, y, w, h, 1, border, 10f);
		}

		float tx = x + (w - NVGRenderer.textWidth(text, Fonts.PRETENDARD, 20f)) / 2f;
		float ty = y + (h - 20f) / 2f;
		NVGRenderer.text(text, tx, ty, Fonts.PRETENDARD, textC, 20f);
	}

	private void renderSearchBarBackgroundNVG() {
		if (searchField == null) return;
		int x = searchField.getX();
		int y = searchField.getY();
		int w = searchField.getWidth();
		int h = searchField.getHeight();

		NVGRenderer.rect(x, y, w, h, 0xFF1A1D21, 10f);
		NVGRenderer.outlineRect(x, y, w, h, 1, 0xFF333333, 10f);
		NVGRenderer.text("🔍", x + 12, y + 12, Fonts.PRETENDARD, 0xFF555555, 20f);
	}

	// ── 입력 처리 ───────────────────────────────────

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		float scaledMouseX = getScaledMouseX();
		float scaledMouseY = getScaledMouseY();
		int button = event.button();

		for (UIWidget widget : overlayWidgets) {
			if (shouldSkipOverlay(widget)) continue;
			if (widget.mouseClicked(scaledMouseX, scaledMouseY + scrollOffset, button)) return true;
		}

		int closeX = windowX + WINDOW_WIDTH - PADDING - 20;
		if (scaledMouseX >= closeX - 20 && scaledMouseX <= closeX + 40 && scaledMouseY >= windowY + PADDING - 10 && scaledMouseY <= windowY + PADDING + 50) {
			onClose();
			return true;
		}

		if (scaledMouseX >= contentX && scaledMouseX <= contentX + contentW && scaledMouseY >= contentY && scaledMouseY <= contentY + contentH) {
			for (UIWidget widget : widgets) {
				if (widget.mouseClicked(scaledMouseX, scaledMouseY + scrollOffset, button)) return true;
			}
		}

		if (searchField != null && searchField.mouseClicked(event, isDoubleClick)) return true;

		return super.mouseClicked(event, isDoubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
		float scaledMouseX = getScaledMouseX();
		float scaledMouseY = getScaledMouseY();
		int button = event.button();
		double deltaX = event.x();
		double deltaY = event.y();

		for (UIWidget widget : overlayWidgets) {
			if (shouldSkipOverlay(widget)) continue;
			if (widget.mouseDragged(scaledMouseX, scaledMouseY + scrollOffset, button, deltaX, deltaY)) return true;
		}

		if (scaledMouseX >= contentX && scaledMouseX <= contentX + contentW && scaledMouseY >= contentY && scaledMouseY <= contentY + contentH) {
			for (UIWidget widget : widgets) {
				if (widget.mouseDragged(scaledMouseX, scaledMouseY + scrollOffset, button, deltaX, deltaY)) return true;
			}
		}

		return super.mouseDragged(event, mouseX, mouseY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		float scaledMouseX = getScaledMouseX();
		float scaledMouseY = getScaledMouseY();
		int button = event.button();

		for (UIWidget widget : overlayWidgets) {
			if (shouldSkipOverlay(widget)) continue;
			if (widget.mouseReleased(scaledMouseX, scaledMouseY + scrollOffset, button)) return true;
		}

		if (scaledMouseX >= contentX && scaledMouseX <= contentX + contentW && scaledMouseY >= contentY && scaledMouseY <= contentY + contentH) {
			for (UIWidget widget : widgets) {
				if (widget.mouseReleased(scaledMouseX, scaledMouseY + scrollOffset, button)) return true;
			}
		}

		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		float scaledMouseX = getScaledMouseX();
		float scaledMouseY = getScaledMouseY();

		if (scaledMouseX >= contentX && scaledMouseX <= contentX + contentW && scaledMouseY >= contentY && scaledMouseY <= contentY + contentH) {
			for (UIWidget widget : overlayWidgets) {
				if (shouldSkipOverlay(widget)) continue;
				if (widget.mouseScrolled(scaledMouseX, scaledMouseY + scrollOffset, horizontalAmount, verticalAmount)) return true;
			}
			scrollOffset -= verticalAmount * 40;
			scrollOffset = UAnimation.clamp(scrollOffset, 0, maxScroll);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		char chr = (char) event.codepoint();
		int modifiers = event.modifiers();

		if (searchField != null && searchField.charTyped(event)) return true;

		for (UIWidget widget : widgets) {
			if (widget.charTyped(chr, modifiers)) return true;
		}

		for (UIWidget widget : overlayWidgets) {
			if (shouldSkipOverlay(widget)) continue;
			if (widget.charTyped(chr, modifiers)) return true;
		}

		return super.charTyped(event);
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		int keyCode = input.key();
		int scanCode = input.scancode();
		int modifiers = input.modifiers();

		if (searchField != null && searchField.keyPressed(input)) return true;

		if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
			this.onClose();
			return true;
		}

		for (UIWidget widget : widgets) {
			if (widget.keyPressed(keyCode, scanCode, modifiers)) return true;
		}

		for (UIWidget widget : overlayWidgets) {
			if (shouldSkipOverlay(widget)) continue;
			if (widget.keyPressed(keyCode, scanCode, modifiers)) return true;
		}

		return super.keyPressed(input);
	}

	private boolean shouldSkipOverlay(UIWidget widget) {
		if (widget instanceof Selector && !((Selector) widget).isOpen()) return true;
		if (widget instanceof ColorPickerButton && !((ColorPickerButton) widget).isPickerOpen()) return true;
		return false;
	}

	private class SettingsRow extends UIWidget {
		private String label;
		private String description;

		public SettingsRow(int x, int y, int w, int h, String label, String desc) {
			super(x, y, w, h);
			this.label = label;
			this.description = desc;
		}

		@Override
		protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
			boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
			int bg = hovered ? UColor.lighten(COLOR_ITEM_BG, 0.05f) : COLOR_ITEM_BG;

			NVGRenderer.rect(x, y, width, height, bg, 14f);
			NVGRenderer.outlineRect(x, y, width, height, 1, COLOR_ITEM_BORDER, 14f);
			NVGRenderer.outlineRect(x + 20, y + 20, 64, 64, 1, 0xFF333333, 14f);

			NVGRenderer.text(label, x + 104, y + 22, Fonts.PRETENDARD, COLOR_TEXT_PRIMARY, 22f);
			NVGRenderer.text(description, x + 104, y + 52, Fonts.PRETENDARD, COLOR_TEXT_SECONDARY, 18f);
		}
	}
}