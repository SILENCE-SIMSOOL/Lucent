package silence.simsool.lucent.ui.screens;

import static silence.simsool.lucent.Lucent.mc;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NanoVG;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.LucentConfig;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.data.KeyBind;
import silence.simsool.lucent.general.models.data.LucentTheme;
import silence.simsool.lucent.general.models.data.NavState;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.general.utils.LucentUtils;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.general.utils.UMouse;
import silence.simsool.lucent.ui.theme.ThemeManager;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.ActionButton;
import silence.simsool.lucent.ui.widget.ColorPickerButton;
import silence.simsool.lucent.ui.widget.KeyBindButton;
import silence.simsool.lucent.ui.widget.Selector;
import silence.simsool.lucent.ui.widget.Slider;
import silence.simsool.lucent.ui.widget.TextBox;
import silence.simsool.lucent.ui.widget.ToggleButton;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class ConfigScreen extends Screen {

	public ConfigScreen(ModManager moduleManager) {
		super(Component.literal("Lucent Config"));
		this.moduleManager = moduleManager;
	}

	private static final int WINDOW_W  = 1100;
	private static final int WINDOW_H  = 680;
	private static final int SIDEBAR_W = 210;
	private static final int TOPBAR_H  = 72;
	private static final int PAD       = 24;

	private int winX, winY;
	private int contentX, contentY, contentW, contentH;
	private int scissorY, scissorH;

	private final Map<String, Image> modIconsMap = new HashMap<>();
	private Image iconMods, iconProfiles, iconThemes, iconPreferences, iconEditHud, iconClose, iconSearch, iconSettings, iconDelete, iconEdit;
	private boolean iconsLoaded = false;

	private final Map<String, Float> toggleAnimCache = new HashMap<>();
	private final ModManager moduleManager;
	private Mod currentModSettings = null;
	private String currentCategory = "All";
	private String currentSidebarPage = "Mods";
	private String lastSearchQuery = "";

	private final Stack<NavState> history = new Stack<>();
	private final Stack<NavState> forwardHistory = new Stack<>();

	private final List<UIWidget> widgets = new ArrayList<>();
	private final List<UIWidget> overlayWidgets = new ArrayList<>();

	private EditBox searchField;
	private boolean searchFocused = false;

	private double scrollOffset = 0;
	private double maxScroll    = 0;

	private float uiScale = 1.0f;
	private float openAnimationProgress = 0f;
	private long startTime = -1L;
	private long closeStartTime = -1L;
	private boolean closing = false;

	private class ThemeCardWidget extends UIWidget {
		private final LucentTheme theme;
		private float hoverAnim = 0f;

		public ThemeCardWidget(int x, int y, int w, int h, LucentTheme theme) {
			super(x, y, w, h);
			this.theme = theme;
		}

		@Override
		protected void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
			hoverAnim = UAnimation.stepProgress(hoverAnim, hovered, 12f, delta);
			boolean current = ThemeManager.currentTheme == theme;

			// Glow/Shadow effect on hover
			if (hoverAnim > 0) {
				NVGRenderer.rect(x - hoverAnim * 2, y - hoverAnim * 2, width + hoverAnim * 4, height + hoverAnim * 4, UColor.withAlpha(theme.accent, (int)(hoverAnim * 25)), 14f);
			}

			// Main Background
			NVGRenderer.rect(x, y, width, height, theme.itemBg, 12f);
			if (hoverAnim > 0) {
				int targetHover = UColor.withAlpha(theme.itemHover, (int)(hoverAnim * UColor.getAlpha(theme.itemHover)));
				NVGRenderer.rect(x, y, width, height, targetHover, 12f);
			}

			// Theme name
			NVGRenderer.text(theme.name, x + 15, y + 18, Fonts.PRETENDARD_SEMIBOLD, theme.textPrimary, 15f);

			// Palette Preview circles
			float size = 10f;
			float gap = 6f;
			float px = x + 15;
			float py = y + height - 20;

			int[] colors = { theme.accent, theme.textPrimary, theme.sidebarBg, theme.winBg };
			for (int i = 0; i < colors.length; i++) {
				NVGRenderer.circle(px + i * (size + gap), py, size / 2f, colors[i]);
				NVGRenderer.outlineCircle(px + i * (size + gap), py, size / 2f + 1, 0.5f, theme.itemBorder);
			}

			// Active Indicator
			if (current) {
				NVGRenderer.circle(x + width - 20, y + 20, 5, theme.accent);
				NVGRenderer.outlineCircle(x + width - 20, y + 20, 7, 1, theme.accent);
			}

			// Border
			NVGRenderer.outlineRect(x, y, width, height, 1.5f, current ? theme.accent : theme.itemBorder, 12f);
		}

		@Override
		public boolean mouseClicked(double mx, double my, int btn) {
			if (btn == 0 && isMouseOver(mx, my)) {
				applyTheme(theme);
				return true;
			}
			return false;
		}
	}

	private class ProfileHeaderWidget extends UIWidget {
		private final TextBox input;
		private final ActionButton createBtn;

		public ProfileHeaderWidget(int x, int y, int w) {
			super(x, y, w, 44);
			this.input = new TextBox(x, y, w - 110, 38, "");
			this.createBtn = new ActionButton(x + w - 100, y, 100, 38, "Create");
			this.createBtn.setOnClick(this::handleCreate);
		}

		private void handleCreate() {
			String name = input.getValue().trim();
			if (!name.isEmpty()) {
				moduleManager.createProfile(name);
				moduleManager.setCurrentProfile(name);
				input.setValue("");
				refreshUI();
			}
		}

		@Override
		protected void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
			input.render(ctx, mx, my, delta);
			createBtn.render(ctx, mx, my, delta);
		}

		@Override
		public boolean mouseClicked(double mx, double my, int btn) {
			return input.mouseClicked(mx, my, btn) || createBtn.mouseClicked(mx, my, btn);
		}

		@Override
		public boolean keyPressed(int key, int scancode, int mods) {
			if (input.isFocused() && (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)) {
				handleCreate();
				return true;
			}
			return input.keyPressed(key, scancode, mods);
		}

		@Override
		public boolean charTyped(char chr, int mods) {
			return input.charTyped(chr, mods);
		}
	}

	private class ProfileCardWidget extends UIWidget {
		private String profileName;
		private final boolean active;
		private static final int BAR_H = 34;
		private boolean editing = false;
		private final TextBox renameBox;

		public ProfileCardWidget(int x, int y, int w, int h, String name, boolean active) {
			super(x, y, w, h);
			this.profileName = name;
			this.active = active;
			this.renameBox = new TextBox(x + 10, y + (h - BAR_H) / 2 - 15, w - 20, 30, name);
			this.renameBox.setFocused(false);
			this.renameBox.setVisible(false);
		}

		@Override
		protected void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
			boolean hov = isMouseOver(mx, my);
			
			int topBg = hov ? UIColors.CARD_HOVER : UIColors.CARD_BG;
			int barBg = active ? UIColors.ACCENT_BLUE : UIColors.TAB_BG;
			if (hov && !active) barBg = UIColors.withAlphaFloat(UIColors.TAB_BG, 0.75f);

			// Backgrounds
			NVGRenderer.rect(x, y, width, height - BAR_H, topBg, 12, 12, 0, 0);
			NVGRenderer.rect(x, y + height - BAR_H, width, BAR_H, barBg, 0, 0, 12, 12);

			if (editing) {
				renameBox.render(ctx, mx, my, delta);
			} else {
				float fontSize = 20f;
				float tw = NVGRenderer.textWidth(profileName, Fonts.PRETENDARD_SEMIBOLD, fontSize);
				NVGRenderer.text(profileName, x + (width - tw) / 2f, y + (height - BAR_H) / 2f - fontSize / 2f, Fonts.PRETENDARD_SEMIBOLD, UIColors.TEXT_PRIMARY, fontSize);
			}

			// Footer Content (Split into two halves)
			float barTop = y + height - BAR_H;
			float midX = x + width / 2f;
			float iconS = 16f;

			if (!editing) {
				if (profileName.equals("default")) {
					// No divider, center Edit icon
					boolean hovEdit = mx > x && mx < x + width && my > barTop && my < barTop + BAR_H;
					NVGRenderer.image(iconEdit, midX - (iconS/2f), barTop + (BAR_H - iconS) / 2f, iconS, iconS, 0f, hovEdit ? 1.0f : 0.6f);
				} else {
					// Divider line
					NVGRenderer.rect(midX - 0.5f, barTop + 6f, 1f, BAR_H - 12f, 0x33FFFFFF, 0f);

					// Left Half: Edit
					boolean hovEdit = mx > x && mx < midX && my > barTop && my < barTop + BAR_H;
					NVGRenderer.image(iconEdit, x + (width/4f) - (iconS/2f), barTop + (BAR_H - iconS) / 2f, iconS, iconS, 0f, hovEdit ? 1.0f : 0.6f);

					// Right Half: Delete
					boolean hovDel = mx > midX && mx < x + width && my > barTop && my < barTop + BAR_H;
					NVGRenderer.image(iconDelete, x + (3*width/4f) - (iconS/2f), barTop + (BAR_H - iconS) / 2f, iconS, iconS, 0f, hovDel ? 1.0f : 0.6f);
				}
			}

			NVGRenderer.outlineRect(x, y, width, height, 1.5f, active ? UIColors.ACCENT_BLUE : UIColors.ITEM_BORDER, 12f);
		}

		@Override
		public boolean mouseClicked(double mx, double my, int btn) {
			if (editing) {
				if (renameBox.mouseClicked(mx, my, btn)) return true;
				if (btn == 0 && !isMouseOver(mx, my)) { finishEditing(); return true; }
				return false;
			}

			if (btn == 0 && isMouseOver(mx, my)) {
				float barTop = y + height - BAR_H;
				float midX = x + width / 2f;

				if (my > barTop && !editing) {
					if (profileName.equals("default")) {
						// Entire bar click (Edit)
						editing = true;
						renameBox.setVisible(true);
						renameBox.setFocused(true);
						return true;
					} else {
						// Left half click (Edit)
						if (mx < midX) {
							editing = true;
							renameBox.setVisible(true);
							renameBox.setFocused(true);
							return true;
						}
						// Right half click (Delete)
						if (mx >= midX) {
							moduleManager.deleteProfile(profileName);
							refreshUI();
							return true;
						}
					}
				}

				if (!active) {
					moduleManager.setCurrentProfile(profileName);
					refreshUI();
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean keyPressed(int key, int scancode, int mods) {
			if (editing) {
				if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) { finishEditing(); return true; }
				if (key == GLFW.GLFW_KEY_ESCAPE) { editing = false; renameBox.setVisible(false); return true; }
				return renameBox.keyPressed(key, scancode, mods);
			}
			return false;
		}

		@Override
		public boolean charTyped(char chr, int mods) {
			return editing && renameBox.charTyped(chr, mods);
		}

		private void finishEditing() {
			String newName = renameBox.getValue().trim();
			if (!newName.isEmpty() && !newName.equals(profileName)) {
				moduleManager.renameProfile(profileName, newName);
				profileName = newName;
			}
			editing = false;
			renameBox.setVisible(false);
			refreshUI();
		}
	}

	@Override
	protected void init() {
		super.init();

		float standardScale = NVGRenderer.getStandardGuiScale();
		float screenW = UDisplay.getScreenWidth() / standardScale;
		float screenH = UDisplay.getScreenHeight() / standardScale;

		float pad = 40f;
		float scaleX = (screenW - pad) / WINDOW_W;
		float scaleY = (screenH - pad) / WINDOW_H;
		float autoFit = Math.min(1.0f, Math.min(scaleX, scaleY));
		
		uiScale = autoFit * LucentConfig.uiScale;

		float virtScreenW = screenW / uiScale;
		float virtScreenH = screenH / uiScale;

		winX = (int)((virtScreenW - WINDOW_W) / 2f);
		winY = (int)((virtScreenH - WINDOW_H) / 2f);

		contentX = winX + SIDEBAR_W;
		contentY = winY + TOPBAR_H;
		contentW = WINDOW_W - SIDEBAR_W;
		contentH = WINDOW_H - TOPBAR_H;

		searchField = new EditBox(font, 0, 0, 0, 0, Component.literal("Search"));
		searchField.setMaxLength(64);
		addWidget(searchField);
		searchField.setFocused(false);

		refreshUI();
	}

	private void refreshUI() {
		refreshUI(false);
	}

	private void refreshUI(boolean keepScroll) {
		// Save animation states
		for (UIWidget w : widgets) {
			if (w instanceof ToggleButton tb && tb.getId() != null) {
				toggleAnimCache.put(tb.getId(), tb.getAnimProgress());
			}
		}

		widgets.clear();
		overlayWidgets.clear();
		if (!keepScroll) scrollOffset = 0;

		if (currentSidebarPage.equals("Mods")) {
			if (currentModSettings == null) buildMainWidgets();
			else buildSettingsWidgets();
		}
		else if (currentSidebarPage.equals("Themes")) buildThemesWidgets();
		else if (currentSidebarPage.equals("Profiles")) buildProfilesWidgets();
		else if (currentSidebarPage.equals("Preferences")) buildPreferencesWidgets();
		else buildPlaceholderWidgets(currentSidebarPage);
	}

	private void loadIcon() {
		if (iconsLoaded) return;
		iconsLoaded = true;

		try {
			iconMods        = LucentUtils.createIcon("mods");
			iconProfiles    = LucentUtils.createIcon("profiles");
			iconThemes      = LucentUtils.createIcon("themes");
			iconPreferences = LucentUtils.createIcon("preferences");
			iconEditHud     = LucentUtils.createIcon("edithud");
			iconClose       = LucentUtils.createIcon("close");
			iconSearch      = LucentUtils.createIcon("search");
			iconSettings    = LucentUtils.createIcon("settings");
			iconDelete      = LucentUtils.createIcon("delete");
			iconEdit        = LucentUtils.createIcon("edit");

			if (moduleManager != null) {
				for (Mod m : moduleManager.modules) {
					if (m.icon != null && !m.icon.isEmpty() && !modIconsMap.containsKey(m.name)) {
						try {
							modIconsMap.put(m.name, NVGRenderer.createImage(m.icon));
						} catch (Exception ex) {}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void applyTheme(LucentTheme theme) {
		ThemeManager.applyTheme(theme);
		if (searchField != null) {
			searchField.setTextColor(UIColors.TEXT_PRIMARY);
			searchField.setTextColorUneditable(UIColors.TEXT_SECONDARY);
		}
		moduleManager.saveConfigs();
	}

	private void pushNav(String page, Mod mod, String cat) {
		if (currentSidebarPage.equals("Mods")) moduleManager.saveConfigs();

		NavState current = new NavState(currentSidebarPage, currentModSettings, currentCategory);
		if (history.isEmpty() || !isSameState(history.peek(), current)) history.push(current);

		forwardHistory.clear();
		currentSidebarPage = page;
		currentModSettings = mod;
		currentCategory = cat;

		if (page.equals("Mods")) moduleManager.loadConfigs();

		refreshUI();
	}

	private boolean isSameState(NavState a, NavState b) {
		if (a == null || b == null) return a == b;
		return Objects.equals(a.page, b.page) && a.mod == b.mod && Objects.equals(a.category, b.category);
	}

	private void goBack() {
		if (history.isEmpty()) return;

		if (currentSidebarPage.equals("Mods")) moduleManager.saveConfigs();

		forwardHistory.push(new NavState(currentSidebarPage, currentModSettings, currentCategory));
		NavState prev = history.pop();
		currentSidebarPage = prev.page;
		currentModSettings = prev.mod;
		currentCategory = prev.category;

		if (currentSidebarPage.equals("Mods")) moduleManager.loadConfigs();

		refreshUI();
	}

	private void goForward() {
		if (forwardHistory.isEmpty()) return;

		if (currentSidebarPage.equals("Mods")) moduleManager.saveConfigs();

		history.push(new NavState(currentSidebarPage, currentModSettings, currentCategory));
		NavState next = forwardHistory.pop();
		currentSidebarPage = next.page;
		currentModSettings = next.mod;
		currentCategory = next.category;

		if (currentSidebarPage.equals("Mods")) moduleManager.loadConfigs();

		refreshUI();
	}

	private void buildThemesWidgets() {
		scissorY = contentY + 20; 
		scissorH = contentH - 20;

		int cols  = 2; 
		int gap   = 20;
		int cardW = (contentW - PAD * 2 - gap * (cols - 1)) / cols;
		int cardH = 120;
		int sx    = contentX + PAD;
		int sy    = scissorY;
		int maxRow = 0;

		List<LucentTheme> themes = ThemeManager.AVAILABLE_THEMES;
		for (int i = 0; i < themes.size(); i++) {
			int col = i % cols;
			int row = i / cols;
			maxRow  = Math.max(maxRow, row);

			widgets.add(new ThemeCardWidget(
					sx + col * (cardW + gap),
					sy + row * (cardH + gap),
					cardW, cardH, themes.get(i)
			));
		}
		maxScroll = Math.max(0, (maxRow + 1) * (cardH + gap) + 40 - scissorH);
	}

	private void buildProfilesWidgets() {
		int sx = contentX + PAD;
		int sy = contentY + 20;
		int fullW = contentW - PAD * 2;
		
		// 1. Create Profile Header (Long input bar + Create button)
		overlayWidgets.add(new ProfileHeaderWidget(sx, sy, fullW));

		// 2. Profile Grid below
		int gridY = sy + 64;
		scissorY = gridY;
		scissorH = contentY + contentH - gridY - 20;

		int cols = 3; 
		int gap = 16;
		int cardW = (fullW - gap * (cols - 1)) / cols;
		int cardH = 130; 
		int maxRow = 0;

		List<String> profiles = moduleManager.getProfiles();
		String current = moduleManager.getCurrentProfile();

		for (int i = 0; i < profiles.size(); i++) {
			int col = i % cols;
			int row = i / cols;
			maxRow = Math.max(maxRow, row);
			widgets.add(new ProfileCardWidget(sx + col * (cardW + gap), gridY + row * (cardH + gap), cardW, cardH, profiles.get(i), profiles.get(i).equals(current)));
		}
		
		maxScroll = Math.max(0, (maxRow + 1) * (cardH + gap) + 40 - scissorH);
	}

	private void buildPreferencesWidgets() {
		scissorY = contentY + 20;
		scissorH = contentH - 20;
		int sx = contentX + PAD;
		int sy = scissorY;
		int itemW = contentW - PAD * 2;
		
		// 1. Open Animation
		widgets.add(new SettingRowWidget(sx, sy, itemW, 74, "Open Animation", "Enable iPhone-style scale animation when opening this screen."));
		ToggleButton animBtn = new ToggleButton(sx + itemW - PAD - 48, sy + 25, 48, 24, LucentConfig.openAnimation);
		animBtn.setOnChange(v -> { LucentConfig.openAnimation = v; Lucent.config.saveGlobalConfig(); });
		widgets.add(animBtn);

		// 2. UI Blur
		widgets.add(new SettingRowWidget(sx, sy + 84, itemW, 74, "UI Blur", "Enable background blur effect for a premium frosted glass look."));
		ToggleButton blurBtn = new ToggleButton(sx + itemW - PAD - 48, sy + 25 + 84, 48, 24, LucentConfig.uiBlur);
		blurBtn.setOnChange(v -> { LucentConfig.uiBlur = v; Lucent.config.saveGlobalConfig(); });
		widgets.add(blurBtn);

		// 3. UI Blur Strength
		widgets.add(new SettingRowWidget(sx, sy + 168, itemW, 74, "UI Blur Strength", "Adjust the intensity of the background blur."));
		Slider blurSlider = new Slider(sx + itemW - PAD - 200, sy + 25 + 168, 200, 24, 0, 20, 1, LucentConfig.uiBlurStrength);
		blurSlider.setOnChange(v -> { LucentConfig.uiBlurStrength = (float)(double)v; Lucent.config.saveGlobalConfig(); });
		widgets.add(blurSlider);

		// 4. Setup Language
		widgets.add(new SettingRowWidget(sx, sy + 252, itemW, 74, "Setup Language", "Choose the primary language for the configuration UI."));
		Selector langSel = new Selector(sx + itemW - PAD - 148, sy + 17 + 252, 148, 38, List.of("English", "Korean", "Chinese", "Japanese", "Russian"));
		langSel.setValue(LucentConfig.setupLanguage);
		langSel.setOnChange(v -> { LucentConfig.setupLanguage = v; Lucent.config.saveGlobalConfig(); });
		overlayWidgets.add(langSel);

		// 5. UI Scale
		widgets.add(new SettingRowWidget(sx, sy + 336, itemW, 74, "UI Scale", "Adjust the overall size of the configuration interface."));
		Slider scaleSlider = new Slider(sx + itemW - PAD - 200, sy + 25 + 336, 200, 24, 1.0, 2.0, 0.1, (double) LucentConfig.uiScale);
		scaleSlider.setOnRelease(v -> { 
			LucentConfig.uiScale = (float)(double)v; 
			Lucent.config.saveGlobalConfig();
			init(); // Re-calculate dimensions immediately
		});
		widgets.add(scaleSlider);

		// Action Buttons
		int btnW = 120;
		int rowY = sy + 420 + 20;
		
		ActionButton openConfig = new ActionButton(sx, rowY, btnW, 36, "Open Config");
		openConfig.setOnClick(() -> {
			Util.getPlatform().openPath(new File(mc.gameDirectory, "config/lucent").toPath());
		});
		widgets.add(openConfig);

		ActionButton loadConfig = new ActionButton(sx + btnW + 12, rowY, btnW, 36, "Load Config");
		loadConfig.setOnClick(() -> {
			Lucent.config.loadConfigs();
			refreshUI();
		});
		widgets.add(loadConfig);

		ActionButton discord = new ActionButton(sx + (btnW + 12) * 2, rowY, btnW, 36, "Discord");
		discord.setOnClick(() -> {
			Util.getPlatform().openUri(LucentConfig.DISCORD_LINK);
		});
		widgets.add(discord);

		// Next row of buttons
		rowY += 48;
		ActionButton github = new ActionButton(sx, rowY, btnW, 36, "GitHub");
		github.setOnClick(() -> {
			Util.getPlatform().openUri(LucentConfig.GITHUB_LINK);
		});
		widgets.add(github);

		ActionButton license = new ActionButton(sx + btnW + 12, rowY, btnW, 36, "License");
		license.setOnClick(() -> {
			Util.getPlatform().openUri(LucentConfig.LICENSE_LINK);
		});
		widgets.add(license);
		
		maxScroll = Math.max(0, rowY + 60 - scissorY - scissorH);
	}

	private void buildPlaceholderWidgets(String pageName) {
		scissorY = contentY;
		scissorH = contentH;
		maxScroll = 0;
		// Placeholder UI will be rendered in empty state
	}

	private void buildMainWidgets() {
		scissorY = contentY + 54;
		scissorH = contentH - 54;

		List<Mod> mods = getFilteredMods();

		int cols  = 4;
		int gap   = 16;
		int cardW = (contentW - PAD * 2 - gap * (cols - 1)) / cols;
		int cardH = 120;
		int sx    = contentX + PAD;
		int sy    = scissorY + 10;
		int maxRow = 0;

		for (int i = 0; i < mods.size(); i++) {
			int col = i % cols;
			int row = i / cols;
			maxRow  = Math.max(maxRow, row);
			widgets.add(new ModCardWidget(
				sx + col * (cardW + gap),
				sy + row * (cardH + gap),
				cardW, cardH, mods.get(i)
			));
		}
		maxScroll = Math.max(0, (maxRow + 1) * (cardH + gap) + 20 - scissorH);
	}

	private List<Mod> getFilteredMods() {
		String q = (searchField == null) ? "" : searchField.getValue().trim().toLowerCase();
		List<Mod> out = new ArrayList<>();
		for (Mod m : moduleManager.modules) {
			boolean catOk = currentCategory.equals("All") || m.category.equals(currentCategory);
			boolean qOk = q.isEmpty()
				|| m.name.toLowerCase().contains(q)
				|| m.searchTags.toLowerCase().contains(q);
			if (catOk && qOk) out.add(m);
		}
		return out;
	}

	private void buildSettingsWidgets() {
		scissorY = contentY;
		scissorH = contentH;

		int sx    = contentX + PAD;
		int curY  = scissorY + 66;
		int itemW = contentW - PAD * 2;

		for (Map.Entry<String, List<Object>> entry : groupConfigMembers().entrySet()) {
			curY += 36; 

			for (Object member : entry.getValue()) {
				ModConfig cfg = (member instanceof Field f) ? f.getAnnotation(ModConfig.class) : ((Method)member).getAnnotation(ModConfig.class);
				widgets.add(new SettingRowWidget(sx, curY, itemW, 74, cfg.name(), cfg.description()));

				try {
					Object val = null;
					if (member instanceof Field f) {
						f.setAccessible(true);
						val = f.get(currentModSettings);
					}
					
					int ux = sx + itemW - PAD; 

					switch (cfg.type()) {

						case SWITCH -> {
							ToggleButton toggleButton = new ToggleButton(ux - 48, curY + 25, 48, 24, (boolean) val);
							final Field field = (member instanceof Field f) ? f : null;
							if (field != null) {
								String fid = field.getName();
								toggleButton.setId(fid);
								if (toggleAnimCache.containsKey(fid)) {
									toggleButton.setAnimProgress(toggleAnimCache.get(fid));
								}
							}
							toggleButton.setOnChange(v -> {
								try {
									if (field != null) field.set(currentModSettings, v);
									refreshUI(true); // Preserve scroll position and animation state
								} catch (Exception e) {}
							});
							widgets.add(toggleButton);
						}

						case SLIDER -> {
							Slider slider = new Slider(ux - 290, curY + 25, 290, 24, cfg.min(), cfg.max(), cfg.step(), (double) val);
							final Field field = (member instanceof Field f) ? f : null;
							slider.setOnChange(v -> {
								try {
									if (field != null) field.set(currentModSettings, v);
								} catch (Exception e) {}
							});
							widgets.add(slider);
						}

						case SELECTOR -> {
							Selector selector = new Selector(ux - 148, curY + 17, 148, 38, List.of(cfg.options()));
							selector.setValue((String) val);
							final Field field = (member instanceof Field f) ? f : null;
							selector.setOnChange(v -> {
								try {
									if (field != null) field.set(currentModSettings, v);
								} catch (Exception e) {}
							});
							overlayWidgets.add(selector);
						}

						case COLOR -> {
							int initialColor = 0xFFFFFFFF;
							if (val instanceof Color cObj) initialColor = cObj.getRGB();
							else if (val instanceof Number nObj) initialColor = nObj.intValue();

							int width = 64;
							ColorPickerButton cp = new ColorPickerButton(ux - width, curY + 17, width, 38, initialColor);
							final Field f = (member instanceof Field field) ? field : null;
							cp.setOnChange(c -> { 
								try { 
									if (f != null) {
										if (f.getType() == Color.class) f.set(currentModSettings, new Color(c, true));
										else f.set(currentModSettings, c);
									}
								} catch (Exception e) {} 
							});
							overlayWidgets.add(cp);
						}

						case BUTTON -> {
							String btnText = cfg.display();
							int btnW = (btnText == null || btnText.isEmpty()) ? 44 : 100;
							ActionButton actionButton = new ActionButton(ux - btnW, curY + 19, btnW, 34, btnText);
							if (member instanceof Method m) {
								actionButton.setOnClick(() -> {
									try {
										m.setAccessible(true);
										m.invoke(currentModSettings);
									} catch (Exception e) {}
								});
							} else if (member instanceof @SuppressWarnings("unused") Field f && val instanceof Runnable r) {
								actionButton.setOnClick(r);
							}
							widgets.add(actionButton);
						}

						case KEYBIND -> {
							KeyBind initialBind = null;
							if (val instanceof KeyBind kb) initialBind = kb;
							KeyBindButton kbb = new KeyBindButton(ux - 100, curY + 19, 100, 34, initialBind);
							final Field field = (member instanceof Field f) ? f : null;
							kbb.setOnChange(v -> {
								try {
									if (field != null) field.set(currentModSettings, v);
								} catch (Exception e) {}
							});
							overlayWidgets.add(kbb);
						}

						case TEXT -> {
							TextBox tb = new TextBox(ux - 200, curY + 19, 200, 34, (String) val);
							final Field field = (member instanceof Field f) ? f : null;
							tb.setOnChange(v -> {
								try {
									if (field != null) field.set(currentModSettings, v);
								} catch (Exception e) {}
							});
							widgets.add(tb);
						}

					}
				} catch (Exception e) {}

				curY += 84;
			}
			curY += 16;
		}
		maxScroll = Math.max(0, curY - scissorY + 10 - scissorH);
	}

	private Map<String, List<Object>> groupConfigMembers() {
		String query = (searchField == null) ? "" : searchField.getValue().trim().toLowerCase();
		Class<?> clazz = currentModSettings.getClass();
		
		List<Object> allMembers = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(ModConfig.class)) allMembers.add(field);
		}
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(ModConfig.class)) allMembers.add(method);
		}

		// Sort by priority (higher first), then name
		allMembers.sort((a, b) -> {
			ModConfig c1 = (a instanceof Field f) ? f.getAnnotation(ModConfig.class) : ((Method)a).getAnnotation(ModConfig.class);
			ModConfig c2 = (b instanceof Field f) ? f.getAnnotation(ModConfig.class) : ((Method)b).getAnnotation(ModConfig.class);
			if (c1.priority() != c2.priority()) return Integer.compare(c2.priority(), c1.priority());
			return c1.name().compareToIgnoreCase(c2.name());
		});

		Map<String, List<Object>> map = new LinkedHashMap<>();
		for (Object member : allMembers) {
			ModConfig cfg = (member instanceof Field f) ? f.getAnnotation(ModConfig.class) : ((Method)member).getAnnotation(ModConfig.class);
			
			// Handle Search
			if (!matchesSearch(cfg, query)) continue;

			// Handle Parent Dependency
			if (!cfg.parent().isEmpty()) {
				if (!isParentActive(cfg.parent())) continue;
			}

			map.computeIfAbsent(cfg.category(), k -> new ArrayList<>()).add(member);
		}

		// Sort Categories by their max element priority
		List<String> sortedCats = new ArrayList<>(map.keySet());
		sortedCats.sort((cat1, cat2) -> {
			int p1 = getCategoryPriority(cat1, map.get(cat1));
			int p2 = getCategoryPriority(cat2, map.get(cat2));
			if (p1 != p2) return Integer.compare(p2, p1);
			return cat1.compareToIgnoreCase(cat2);
		});

		Map<String, List<Object>> finalMap = new LinkedHashMap<>();
		for (String cat : sortedCats) finalMap.put(cat, map.get(cat));
		
		return finalMap;
	}

	private int getCategoryPriority(String category, List<Object> members) {
		// 1. Check for explicit @CategoryPriority on the class
		Class<?> clazz = currentModSettings.getClass();
		ModConfig.CategoryPriority[] annos = clazz.getAnnotationsByType(ModConfig.CategoryPriority.class);
		for (ModConfig.CategoryPriority cp : annos) {
			if (cp.name().equalsIgnoreCase(category)) return cp.priority();
		}

		// 2. Fallback to max element priority
		int max = Integer.MIN_VALUE;
		for (Object m : members) {
			ModConfig cfg = (m instanceof Field f) ? f.getAnnotation(ModConfig.class) : ((Method)m).getAnnotation(ModConfig.class);
			if (cfg.priority() > max) max = cfg.priority();
		}
		return max == Integer.MIN_VALUE ? 0 : max;
	}

	private boolean isParentActive(String parentFieldName) {
		try {
			Field f = currentModSettings.getClass().getDeclaredField(parentFieldName);
			f.setAccessible(true);
			Object val = f.get(currentModSettings);
			if (val instanceof Boolean b) return b;
		} catch (Exception e) {}
		return false;
	}

	private boolean matchesSearch(ModConfig cfg, String q) {
		if (q == null || q.isEmpty()) return true;
		return (   cfg.name().toLowerCase().contains(q)
				|| cfg.description().toLowerCase().contains(q)
				|| cfg.category().toLowerCase().contains(q)
		);
	}

	@Override
	public void renderBackground(GuiGraphics ctx, int mx, int my, float delta) {
		float alpha = LucentConfig.openAnimation ? openAnimationProgress : 1f;
		if (LucentConfig.uiBlur) {
			ctx.fill(0, 0, width, height, UIColors.withAlpha(0x25000000, (int)(20 * alpha))); 
			super.renderBackground(ctx, mx, my, delta);
		}
		else {
			ctx.fill(0, 0, width, height, UIColors.withAlpha(0x80000000, (int)(80 * alpha)));
		}
	}

	@Override
	public void render(GuiGraphics ctx, int mx, int my, float delta) {
		if (startTime == -1L) startTime = System.currentTimeMillis();

		if (LucentConfig.openAnimation) {
			if (closing) {
				if (closeStartTime == -1L) closeStartTime = System.currentTimeMillis();
				float elapsed = (float)(System.currentTimeMillis() - closeStartTime);
				openAnimationProgress = 1f - Math.min(1f, elapsed / 300f);
				if (openAnimationProgress <= 0f) {
					super.onClose();
					return;
				}
			} else {
				float elapsed = (float)(System.currentTimeMillis() - startTime);
				openAnimationProgress = Math.min(1f, elapsed / 400f);
			}
		} else {
			openAnimationProgress = 1f;
			if (closing) {
				super.onClose();
				return;
			}
		}

		if (searchField != null) {
			boolean showSearch = currentSidebarPage.equals("Mods");

			if (searchField.visible != showSearch) searchField.visible = showSearch;
			if (!searchField.getValue().equals(lastSearchQuery)) {
				lastSearchQuery = searchField.getValue();
				if (showSearch) refreshUI();
			}
		}

		NVGPIPRenderer.draw(ctx, 0, 0, width, height, () -> {
			float gs  = NVGRenderer.getStandardGuiScale();
			float smx = UMouse.getScaledX(uiScale);
			float smy = UMouse.getScaledY(uiScale);

			NVGRenderer.push();
			
			// Apply Animation
			if (LucentConfig.openAnimation) {
				float ease = UAnimation.Easing.spring(openAnimationProgress);
				NVGRenderer.translate(width / 2f, height / 2f);
				NVGRenderer.scale(0.9f + 0.1f * ease, 0.9f + 0.1f * ease);
				NVGRenderer.translate(-width / 2f, -height / 2f);
				NVGRenderer.globalAlpha(UAnimation.clamp(openAnimationProgress * 1.5f, 0f, 1f));
			}

			NVGRenderer.scale(gs * uiScale, gs * uiScale);

			loadIcon();

			drawFrame();
			renderSidebar();
			renderTopBar();

			if (currentSidebarPage.equals("Mods")) {
				renderSearchBarBg();
				if (currentModSettings == null) renderCategoryTabs();
			} 
 
			NVGRenderer.pushScissor(contentX - 2, scissorY - 2, contentW + 4, scissorH + 4);
			if (currentSidebarPage.equals("Mods") && currentModSettings != null) renderSettingsHeader();
			
			NVGRenderer.push();
			NVGRenderer.translate(0, (float) -scrollOffset);
			for (UIWidget w : widgets) w.render(ctx, (int) smx, (int) (smy + scrollOffset), delta);
			// Also render those that stay in the list when closed
			for (UIWidget w : overlayWidgets) {
				if (shouldSkipOverlay(w)) w.render(ctx, (int) smx, (int) (smy + scrollOffset), delta);
			}
			NVGRenderer.pop();
			NVGRenderer.popScissor();

			// For OPEN overlays, render them without scissor so dropdown is visible
			for (UIWidget w : overlayWidgets) {
				if (!shouldSkipOverlay(w)) {
					int oy = w.getY();
					w.setPosition(w.getX(), (int)(oy - scrollOffset));
					w.render(ctx, (int) smx, (int) (smy + scrollOffset), delta);
					w.renderOverlay(ctx, (int) smx, (int) (smy + scrollOffset), delta);
					w.setPosition(w.getX(), oy);
				}
			}

			NVGRenderer.pop();
		});

		super.render(ctx, mx, my, delta);
	}

	@Override
	public void onClose() {
		if (LucentConfig.openAnimation && !closing) {
			closing = true;
			closeStartTime = System.currentTimeMillis();
		} else {
			super.onClose();
		}
	}

	@Override
	public void removed() {
		super.removed();
		if (moduleManager != null) moduleManager.saveConfigs();
	}

	private void drawFrame() {
		int round = 14;
		NVGRenderer.rect(winX + SIDEBAR_W, winY, WINDOW_W - SIDEBAR_W, WINDOW_H, UIColors.WIN_BG, 0,round,round,0);
		NVGRenderer.rect(winX, winY, SIDEBAR_W, WINDOW_H, UIColors.SIDEBAR_BG, round,0,0,round);
	}

	private void renderSidebar() {
		int ix = winX + PAD;

		NVGRenderer.text("LUCENT", ix, winY + 26f, Fonts.PRETENDARD_SEMIBOLD, UIColors.ACCENT_BLUE, 20f);

		int sy = winY + 44;
		
		sy += 36;
		NVGRenderer.text("MOD CONFIG", ix, sy, Fonts.PRETENDARD_SEMIBOLD, UIColors.MUTED, 10f);
		sy += 16;
		sy = sidebarItem(ix, sy, iconMods, "Mods", currentSidebarPage.equals("Mods"));
		sy = sidebarItem(ix, sy, iconProfiles, "Profiles", currentSidebarPage.equals("Profiles"));

		sy += 16;
		NVGRenderer.text("PERSONALIZATION", ix, sy, Fonts.PRETENDARD_SEMIBOLD, UIColors.MUTED, 10f);
		sy += 16;
		sy = sidebarItem(ix, sy, iconThemes, "Themes", currentSidebarPage.equals("Themes"));
		sy = sidebarItem(ix, sy, iconPreferences, "Preferences", currentSidebarPage.equals("Preferences"));

		int bY = winY + WINDOW_H - 100;
		sidebarItem(ix, bY, iconEditHud, "Edit HUD", false);
		sidebarItem(ix, bY + 38, iconClose, "Close", false);
	}

	private int sidebarItem(int x, int y, Image icon, String label, boolean active) {
		final int itemH = 34;
		if (active) {
			NVGRenderer.rect(winX + 12, y, SIDEBAR_W - 24, itemH, UIColors.SIDEBAR_SEL, 8f);
			NVGRenderer.rect(winX + 12, y + 6, 3, itemH - 12, UIColors.ACCENT_BLUE, 1.5f);
		}
		int fg = active ? UIColors.TEXT_PRIMARY : UIColors.TEXT_SECONDARY;
		NVGRenderer.image(icon, x + 4, y + (itemH - 16) / 2f, 16);
		NVGRenderer.text(label, x + 30, y + 10f, Fonts.PRETENDARD_MEDIUM, fg, 14f);
		return y + itemH + 2;
	}

	private void renderTopBar() {
		int cx = contentX + PAD;

		boolean canGoBack = !history.isEmpty();
		boolean canGoForward = !forwardHistory.isEmpty();

		int backColor = canGoBack ? UIColors.TEXT_PRIMARY : UIColors.MUTED;
		int fwdColor = canGoForward  ? UIColors.TEXT_PRIMARY : UIColors.MUTED;

		NVGRenderer.text("←", cx, winY + 26f,  Fonts.PRETENDARD_MEDIUM, backColor, 20f);
		NVGRenderer.text("→", cx + 32, winY + 26f,  Fonts.PRETENDARD_MEDIUM, fwdColor, 20f);
		
		String title = currentSidebarPage;
		if (currentSidebarPage.equals("Mods") && currentModSettings != null) {
			title = currentModSettings.name;
		}
		NVGRenderer.text(title, cx + 70, winY + 25f,  Fonts.PRETENDARD_SEMIBOLD, UIColors.TEXT_PRIMARY, 22f);
	}

	private void renderSearchBarBg() {
		int bw = 200, bh = 32;
		int bx = winX + WINDOW_W - PAD - bw;
		int by = winY + (TOPBAR_H - bh) / 2;

		NVGRenderer.rect(bx, by, bw, bh, UIColors.SEARCHBAR_BG, 8f);
		NVGRenderer.outlineRect(bx, by, bw, bh, 1, searchFocused ? UIColors.ACCENT_BLUE : UIColors.ITEM_BORDER, 8f);
		NVGRenderer.image(iconSearch, bx + 10, by + (bh - 16) / 2f, 16);

		if (searchField != null) {
			String txt = searchField.getValue();
			float textY = by + (bh - 14f) / 2f;

			float textAreaX = bx + 34;
			float textAreaW = bw - 34 - 8;

			if (txt.isEmpty() && !searchFocused) {
				NVGRenderer.push();
				NanoVG.nvgIntersectScissor(NVGRenderer.getVG(), (int) textAreaX, by, (int) textAreaW, bh);
				NVGRenderer.text("Search...", textAreaX, textY, Fonts.PRETENDARD_MEDIUM, UIColors.TEXT_SECONDARY, 14f);
				NVGRenderer.pop();
			} else {
				int cpos = searchField.getCursorPosition();
				String beforeCursor = txt.substring(0, Math.min(cpos, txt.length()));
				float cursorX = NVGRenderer.textWidth(beforeCursor, Fonts.PRETENDARD_MEDIUM, 14f);

				float scrollX = 0f;
				if (cursorX > textAreaW) {
					scrollX = cursorX - textAreaW + 4f;
				}

				NVGRenderer.push();
				NanoVG.nvgIntersectScissor(NVGRenderer.getVG(), (int) textAreaX, by, (int) textAreaW, bh);
				NVGRenderer.text(txt, textAreaX - scrollX, textY, Fonts.PRETENDARD_MEDIUM, UIColors.TEXT_PRIMARY, 14f);

				if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
					float cx = textAreaX + cursorX - scrollX;
					NVGRenderer.rect(cx + 1f, textY - 1f, 1.5f, 16f, UIColors.TEXT_PRIMARY, 0f);
				}
				NVGRenderer.pop();
			}
		}
	}

	private void renderCategoryTabs() {
		List<String> cats = getCategories();
		
		float cx = contentX + PAD;
		float cy = contentY; 

		for (String cat : cats) {
			float tw = NVGRenderer.textWidth(cat, Fonts.PRETENDARD_MEDIUM, 13f);
			boolean active = cat.equals(currentCategory);
			
			int bg = active ? UIColors.ACCENT_BLUE : UIColors.TAB_BG;
			int fg = active ? UIColors.TEXT_PRIMARY : 0xFFCCCCCC;
			
			int padX = 16;
			int tabW = (int)(tw + padX * 2);
			int tabH = 28;
			
			NVGRenderer.rect(cx, cy, tabW, tabH, bg, 8f);
			float textY = cy + (tabH - 13f) / 2f;
			NVGRenderer.text(cat, cx + padX, textY, Fonts.PRETENDARD_MEDIUM, fg, 13f);
			
			cx += tabW + 8f; 
		}
	}

	private void renderSettingsHeader() {
		int sx = contentX + PAD;
		float hy = contentY - (float) scrollOffset;

		if (hy > scissorY - 80 && hy < scissorY + scissorH) {
			NVGRenderer.text(currentModSettings.name, sx, hy, Fonts.PRETENDARD_SEMIBOLD, UIColors.TEXT_PRIMARY, 26f);
			NVGRenderer.text(currentModSettings.description, sx, hy + 32, Fonts.PRETENDARD, UIColors.TEXT_SECONDARY, 14f);
		}

		int curY = contentY + 66;
		for (Map.Entry<String, List<Object>> e : groupConfigMembers().entrySet()) {
			float ry = curY + 14 - (float) scrollOffset;
			if (ry > scissorY - 30 && ry < scissorY + scissorH) {
				String catName = e.getKey().toUpperCase();
				float catW = NVGRenderer.textWidth(catName, Fonts.PRETENDARD_SEMIBOLD, 12f);
				float indent = 8f;
				
				NVGRenderer.text(catName, sx + indent, ry, Fonts.PRETENDARD_SEMIBOLD, UIColors.MUTED, 12f);
				
				float lineX = sx + indent + catW + 16f;
				float lineW = (contentW - PAD * 2) - (lineX - sx);
				NVGRenderer.rect(lineX, ry + 6f, lineW, 1.5f, UIColors.DIVIDER, 0.75f);
			}
			curY += 36;
			curY += e.getValue().size() * 84;
			curY += 16;
		}
	}

	private List<String> getCategories() {
		List<String> cats = new ArrayList<>();
		cats.add("All");
		for (Mod m : moduleManager.modules) if (!cats.contains(m.category)) cats.add(m.category);
		return cats;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
		float mx = UMouse.getScaledX(uiScale);
		float my = UMouse.getScaledY(uiScale);
		int btn  = event.button();

		if (currentSidebarPage.equals("Mods") && searchField != null && searchField.visible) {
			int bw = 200, bh = 32;
			int bx = winX + WINDOW_W - PAD - bw;
			int by = winY + (TOPBAR_H - bh) / 2;
			boolean hit = (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh);
			if (hit != searchFocused) {
				searchFocused = hit;
				searchField.setFocused(hit);
				if (hit) this.setFocused(searchField);
				else if (this.getFocused() == searchField) this.setFocused(null);
			}
		}

		for (UIWidget w : overlayWidgets) {
			if (!shouldSkipOverlay(w)) {
				if (w.mouseClicked(mx, my + scrollOffset, btn)) return true;
			}
		}

		if (mx >= contentX && mx <= contentX + contentW && my >= scissorY && my <= scissorY + scissorH) {
			for (UIWidget w : widgets) {
				if (w.mouseClicked(mx, my + scrollOffset, btn)) return true;
			}
			for (UIWidget w : overlayWidgets) {
				if (shouldSkipOverlay(w) && w.mouseClicked(mx, my + scrollOffset, btn)) return true;
			}
		}

		// 상단바 네비게이션 버튼 클릭
		float bx = contentX + PAD;
		float cy = winY + TOPBAR_H / 2f;
		
		// Back button ←
		if (mx >= bx - 4 && mx <= bx + 20 && my >= cy - 20 && my <= cy + 10) {
			if (!history.isEmpty()) {
				goBack();
				return true;
			}
		}
		// Forward button →
		if (mx >= bx + 28 && mx <= bx + 52 && my >= cy - 20 && my <= cy + 10) {
			if (!forwardHistory.isEmpty()) {
				goForward();
				return true;
			}
		}

		// 카테고리 탭 클릭
		if (currentSidebarPage.equals("Mods") && currentModSettings == null && btn == 0) {
			float catX = contentX + PAD;
			float catY = contentY;
			for (String cat : getCategories()) {
				float tw = NVGRenderer.textWidth(cat, Fonts.PRETENDARD_MEDIUM, 13f);
				int tabW = (int)(tw + 32);
				if (mx >= catX && mx <= catX + tabW && my >= catY && my <= catY + 28f) {
					currentCategory = cat;
					refreshUI();
					return true;
				}
				catX += tabW + 8f;
			}
		}

		// 사이드바 탭 전환
		if (btn == 0) {
			//int ix = winX + PAD;
			int sy = winY + 96; // Mods 시작 위치
			
			String[] pages  = {"Mods", "Profiles", "Themes", "Preferences"};
			int sy3 = sy + 36 + 36 + 32; // Themes 시작 위치 (Profiles 이후 68px 간격)
			int[] ys = {sy, sy + 36, sy3, sy3 + 36};
			
			for (int i = 0; i < pages.length; i++) {
				int ty = ys[i];
				if (mx >= winX + 16 && mx <= winX + SIDEBAR_W - 16 && my >= ty && my <= ty + 36) {
					String targetPage = pages[i];
					if (!currentSidebarPage.equals(targetPage) || (targetPage.equals("Mods") && currentModSettings != null)) {
						pushNav(targetPage, null, "All");
					}
					return true;
				}
			}

			float editHudY = winY + WINDOW_H - 100;
			if (mx >= winX + 16 && mx <= winX + SIDEBAR_W - 16 && my >= editHudY && my <= editHudY + 36) {
				this.onClose();
				minecraft.setScreen(new EditHudScreen(true));
				return true;
			}

			float closeY = winY + WINDOW_H - 100 + 38;
			if (mx >= winX + 16 && mx <= winX + SIDEBAR_W - 16 && my >= closeY && my <= closeY + 36) {
				this.onClose();
				return true;
			}
		}

		return super.mouseClicked(event, isDoubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
		float mx = UMouse.getScaledX(uiScale), my = UMouse.getScaledY(uiScale);;
		int btn  = event.button();

		for (UIWidget w : overlayWidgets) {
			if (!shouldSkipOverlay(w)) {
				if (w.mouseDragged(mx, my + scrollOffset, btn, event.x(), event.y())) return true;
			}
		}

		if (mx >= contentX && mx <= contentX + contentW && my >= scissorY && my <= scissorY + scissorH) {
			for (UIWidget w : widgets) if (w.mouseDragged(mx, my + scrollOffset, btn, event.x(), event.y())) return true;
			for (UIWidget w : overlayWidgets) if (shouldSkipOverlay(w) && w.mouseDragged(mx, my + scrollOffset, btn, event.x(), event.y())) return true;
		}
		return super.mouseDragged(event, mouseX, mouseY);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		float mx = UMouse.getScaledX(uiScale), my = UMouse.getScaledY(uiScale);
		int btn  = event.button();

		for (UIWidget w : overlayWidgets) {
			if (!shouldSkipOverlay(w)) {
				if (w.mouseReleased(mx, my + scrollOffset, btn)) return true;
			}
		}

		if (mx >= contentX && mx <= contentX + contentW && my >= scissorY && my <= scissorY + scissorH) {
			for (UIWidget w : widgets) if (w.mouseReleased(mx, my + scrollOffset, btn)) return true;
			for (UIWidget w : overlayWidgets) if (shouldSkipOverlay(w) && w.mouseReleased(mx, my + scrollOffset, btn)) return true;
		}
		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double hAmt, double vAmt) {
		float mx = UMouse.getScaledX(uiScale), my = UMouse.getScaledY(uiScale);

		for (UIWidget w : overlayWidgets) {
			if (!shouldSkipOverlay(w)) {
				if (w.mouseScrolled(mx, my + scrollOffset, hAmt, vAmt)) return true;
			}
		}

		if (mx >= contentX && mx <= contentX + contentW && my >= scissorY && my <= scissorY + scissorH) {
			for (UIWidget w : overlayWidgets) {
				if (shouldSkipOverlay(w)) {
					if (w.mouseScrolled(mx, my + scrollOffset, hAmt, vAmt)) return true;
				}
			}
			scrollOffset -= vAmt * 36;
			scrollOffset  = UAnimation.clamp(scrollOffset, 0, maxScroll);
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, hAmt, vAmt);
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		int key = input.key();

		// waiting 상태인 KeyBindButton에게 먼저 이벤트를 전달
		for (UIWidget w : overlayWidgets) {
			if (w instanceof KeyBindButton kbb && kbb.isWaiting()) {
				if (kbb.keyPressed(key, input.scancode(), input.modifiers())) return true;
			}
		}

		if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.onClose();
			return true;
		}

		if (key == GLFW.GLFW_KEY_TAB || key == GLFW.GLFW_KEY_LEFT) {
			if (!history.isEmpty()) goBack();
			return true;
		}

		if (key == GLFW.GLFW_KEY_RIGHT) {
			if (!forwardHistory.isEmpty()) goForward();
			return true;
		}

		for (UIWidget w : widgets) {
			if (w.keyPressed(key, input.scancode(), input.modifiers())) return true;
		}
		for (UIWidget w : overlayWidgets) {
			if (!shouldSkipOverlay(w) && w.keyPressed(key, input.scancode(), input.modifiers())) return true;
		}
		return super.keyPressed(input);
	}

	@Override
	public boolean keyReleased(KeyEvent input) {
		int key = input.key();

		for (UIWidget w : overlayWidgets) {
			if (w instanceof KeyBindButton kbb && kbb.isWaiting()) {
				if (kbb.keyReleased(key, input.scancode(), input.modifiers())) return true;
			}
		}

		for (UIWidget w : widgets) {
			if (w.keyReleased(key, input.scancode(), input.modifiers())) return true;
		}
		for (UIWidget w : overlayWidgets) {
			if (!shouldSkipOverlay(w) && w.keyReleased(key, input.scancode(), input.modifiers())) return true;
		}

		return super.keyReleased(input);
	}

	@Override
	public boolean charTyped(CharacterEvent event) {
		for (UIWidget w : widgets) {
			if (w.charTyped((char) event.codepoint(), event.modifiers())) return true;
		}
		for (UIWidget w : overlayWidgets) {
			if (!shouldSkipOverlay(w) && w.charTyped((char) event.codepoint(), event.modifiers())) return true;
		}
		return super.charTyped(event);
	}

	private boolean shouldSkipOverlay(UIWidget widget) {
		if (widget instanceof Selector selector && !selector.isOpen()) return true;
		if (widget instanceof ColorPickerButton colorPickerButton && !colorPickerButton.isPickerOpen()) return true;
		if (widget instanceof KeyBindButton keybindButton && !keybindButton.isWaiting()) return true;
		return false;
	}

	private class ModCardWidget extends UIWidget {
		private final Mod mod;
		private static final int BAR_H = 30;

		public ModCardWidget(int x, int y, int w, int h, Mod mod) {
			super(x, y, w, h);
			this.mod = mod;
		}

		@Override
		protected void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
			boolean hov = mx >= x && mx <= x + width && my >= y && my <= y + height;
			int topBg   = hov ? UIColors.CARD_HOVER : UIColors.CARD_BG;
			int barBg   = mod.isEnabled ? UIColors.ACCENT_BLUE : UIColors.TAB_BG; 
			if (hov && !mod.isEnabled) barBg = UIColors.withAlphaFloat(UIColors.TAB_BG, 0.6f);

			NVGRenderer.rect(x, y, width, height - BAR_H, topBg, 12, 12, 0, 0);
			NVGRenderer.rect(x, y + height - BAR_H, width, BAR_H, barBg, 0, 0, 12, 12);

			float midX = x + width / 2f;
			float topH = height - BAR_H;

			Image iconImg = null;
			if (mod.icon != null && !mod.icon.isEmpty()) iconImg = modIconsMap.get(mod.name);

			if (iconImg != null) NVGRenderer.image(iconImg, midX - 22f, y + (topH - 44f) / 2f, 44f, 44f);
			else {
				float initialW = NVGRenderer.textWidth(mod.name, Fonts.PRETENDARD_SEMIBOLD, 20f);
				NVGRenderer.text(mod.name, midX - initialW/2, y + (topH - 20) / 2, Fonts.PRETENDARD_SEMIBOLD, 0xFFFFFFFF, 20);
			}

			float barTop  = y + height - BAR_H;

			NVGRenderer.text(mod.name, x + 12f, barTop + 8f, Fonts.PRETENDARD_MEDIUM, UIColors.PURE_WHITE, 14f);

			float divX = x + width - 36f;
			NVGRenderer.rect(divX, barTop + 6f, 1, BAR_H - 12f, 0x55FFFFFF, 0f); // 약간 투명한 선
			
			if (iconSettings != null) NVGRenderer.image(iconSettings, divX + 10f, barTop + (BAR_H - 16f) / 2f, 16f, 16f);
		}

		@Override
		public boolean mouseClicked(double mx, double my, int btn) {
			if (btn == 0 && mx >= x && mx <= x + width && my >= y && my <= y + height) {
				if (mx >= x + width - 36 && my >= y + height - BAR_H) pushNav("Mods", mod, currentCategory);
				else mod.isEnabled = !mod.isEnabled;
				return true;
			}
			return false;
		}
	}

	private class SettingRowWidget extends UIWidget {
		private final String label, description;

		public SettingRowWidget(int x, int y, int w, int h, String label, String desc) {
			super(x, y, w, h);
			this.label       = label;
			this.description = desc;
		}

		@Override
		protected void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
			boolean hov = mx >= x && mx <= x + width && my >= y && my <= y + height;
			NVGRenderer.rect(x, y, width, height, hov ? UIColors.CARD_HOVER : UIColors.CARD_BG, 8f);
			NVGRenderer.outlineRect(x, y, width, height, 1, UIColors.ITEM_BORDER, 8f);
			NVGRenderer.text(label,       x + 16, y + 18, Fonts.PRETENDARD_MEDIUM, UIColors.TEXT_PRIMARY,   18f);
			NVGRenderer.text(description, x + 16, y + 43, Fonts.PRETENDARD_LIGHT, UIColors.TEXT_SECONDARY, 14f);
		}
	}
}