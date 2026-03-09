package silence.simsool.lucent.client.dev.screens;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import silence.simsool.lucent.client.ModuleManager;
import silence.simsool.lucent.general.abstracts.Module;
import silence.simsool.lucent.general.interfaces.ModConfig;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.Image;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.ColorPickerButton;
import silence.simsool.lucent.ui.widget.Selector;
import silence.simsool.lucent.ui.widget.Slider;
import silence.simsool.lucent.ui.widget.ToggleButton;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class LucentConfigScreenSecond extends Screen {

    // ─── 색상 팔레트 (OneConfig 스타일로 조정) ─────────────────────────────────────────────
    private static final int C_WIN_BG         = 0xFF18181A;
    private static final int C_SIDEBAR_BG     = 0xFF1D1D21;
    private static final int C_ACCENT         = 0xFF245EEA;
    private static final int C_TEXT_PRIMARY   = 0xFFFFFFFF;
    private static final int C_TEXT_SECONDARY = 0xFF9A9A9E;
    private static final int C_TEXT_LABEL     = 0xFF6B6B70;
    private static final int C_CARD_BG        = 0xFF2B2B30;
    private static final int C_CARD_HOVER     = 0xFF35353A;
    private static final int C_TAB_BG         = 0xFF35353A;
    private static final int C_DIVIDER        = 0xFF2F2F32;
    private static final int C_SEARCHBAR_BG   = 0xFF141416; // 약간 밝게
    private static final int C_SEARCHBAR_BDR  = 0xFF2A2A2D;

    // ─── 레이아웃 상수 ────────────────────────────────────────────────────────────
    private static final int WINDOW_W  = 1100;
    private static final int WINDOW_H  = 680;
    private static final int SIDEBAR_W = 210;
    private static final int TOPBAR_H  = 72;
    private static final int PAD       = 24;

    // ─── 아이콘 Image ──────────────────────────────────────────────────────────────
    private Image iconMods, iconProfiles, iconThemes, iconPreferences;
    private Image iconEditHud, iconClose, iconSearch, iconSettings;
    private boolean iconsLoaded = false;
    private final Map<String, Image> modIconsMap = new HashMap<>();

    private void loadIconsIfNeeded() {
        if (iconsLoaded) return;
        iconsLoaded = true;
        try {
            iconMods        = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/mods.png");
            iconProfiles    = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/profiles.png");
            iconThemes      = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/themes.png");
            iconPreferences = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/preferences.png");
            iconEditHud     = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/edithud.png");
            iconClose       = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/close.png");
            iconSearch      = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/search.png");
            iconSettings    = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/settings.png");
            
            // 모드 아이콘 로드 로직
            if (moduleManager != null) {
                for (Module m : moduleManager.modules) {
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

    private static void drawIcon(Image icon, float x, float y, float size) {
        if (icon == null) return;
        NVGRenderer.image(icon, x, y, size, size);
    }

    // ─── 상태 ────────────────────────────────────────────────────────────────────
    private final ModuleManager moduleManager;
    private Module currentModSettings = null;
    private Module lastModSettings = null;
    private String currentCategory    = "All";
    private String currentSidebarPage = "Mods";
    private String lastSearchQuery    = "";

    private int winX, winY;
    private int contentX, contentY, contentW, contentH;
    private int scissorY, scissorH;

    private final List<UIWidget> widgets        = new ArrayList<>();
    private final List<UIWidget> overlayWidgets = new ArrayList<>();

    private EditBox searchField;
    private boolean searchFocused = false;

    private double scrollOffset = 0;
    private double maxScroll    = 0;

    // ─── 생성자 ──────────────────────────────────────────────────────────────────
    public LucentConfigScreenSecond(ModuleManager moduleManager) {
        super(Component.literal("Lucent Config"));
        this.moduleManager = moduleManager;
    }

    private float scaledMX() {
        return (float)(minecraft.mouseHandler.xpos() / NVGRenderer.getStandardGuiScale());
    }
    private float scaledMY() {
        return (float)(minecraft.mouseHandler.ypos() / NVGRenderer.getStandardGuiScale());
    }

    @Override
    protected void init() {
        super.init();

        float gs      = NVGRenderer.getStandardGuiScale();
        float screenW = minecraft.getWindow().getScreenWidth()  / gs;
        float screenH = minecraft.getWindow().getScreenHeight() / gs;

        winX = (int)((screenW - WINDOW_W) / 2f);
        winY = (int)((screenH - WINDOW_H) / 2f);

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
        widgets.clear();
        overlayWidgets.clear();
        scrollOffset = 0;
        if (currentSidebarPage.equals("Mods")) {
            if (currentModSettings == null) buildMainWidgets();
            else                            buildSettingsWidgets();
        } else {
            buildPlaceholderWidgets(currentSidebarPage);
        }
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

        List<Module> mods = getFilteredMods();

        int cols  = 4;
        int gap   = 16;
        int cardW = (contentW - PAD * 2 - gap * (cols - 1)) / cols;
        int cardH = 120; // 가로보다 세로가 살짝 짧은 비율
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

    private List<Module> getFilteredMods() {
        String q = (searchField == null) ? "" : searchField.getValue().trim().toLowerCase();
        List<Module> out = new ArrayList<>();
        for (Module m : moduleManager.modules) {
            boolean catOk = currentCategory.equals("All") || m.category.equals(currentCategory);
            boolean qOk   = q.isEmpty()
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

        for (Map.Entry<String, List<Field>> entry : groupSettingFields().entrySet()) {
            curY += 36; 

            for (Field field : entry.getValue()) {
                ModConfig cfg = field.getAnnotation(ModConfig.class);
                widgets.add(new SettingRowWidget(sx, curY, itemW, 74, cfg.name(), cfg.description()));

                try {
                    field.setAccessible(true);
                    Object val = field.get(currentModSettings);
                    int ux = sx + itemW - PAD; 

                    switch (cfg.type()) {
                        case SWITCH -> {
                            ToggleButton t = new ToggleButton(ux - 48, curY + 25, 48, 24, (boolean) val);
                            t.setOnChange(v -> { try { field.set(currentModSettings, v); } catch (Exception e) {} });
                            widgets.add(t);
                        }
                        case SLIDER -> {
                            Slider s = new Slider(ux - 290, curY + 25, 290, 24,
                                cfg.min(), cfg.max(), cfg.step(), (double) val);
                            s.setOnChange(v -> { try { field.set(currentModSettings, v); } catch (Exception e) {} });
                            widgets.add(s);
                        }
                        case SELECTOR -> {
                            Selector sel = new Selector(ux - 148, curY + 17, 148, 38, List.of(cfg.options()));
                            sel.setValue((String) val);
                            sel.setOnChange(v -> { try { field.set(currentModSettings, v); } catch (Exception e) {} });
                            overlayWidgets.add(sel);
                        }
                        case COLOR -> {
                            ColorPickerButton cp = new ColorPickerButton(ux - 54, curY + 17, 54, 38, (int) val);
                            cp.setOnChange(c -> { try { field.set(currentModSettings, c); } catch (Exception e) {} });
                            overlayWidgets.add(cp);
                        }
                        case BUTTON -> {
                        	// Do nothing for now
                        }
                    }
                } catch (IllegalAccessException e) { e.printStackTrace(); }

                curY += 84;
            }
            curY += 16;
        }
        maxScroll = Math.max(0, curY - scissorY + 10 - scissorH);
    }

    private Map<String, List<Field>> groupSettingFields() {
        Map<String, List<Field>> map = new java.util.LinkedHashMap<>();
        String q = (searchField == null) ? "" : searchField.getValue().trim().toLowerCase();
        for (Field f : currentModSettings.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(ModConfig.class)) {
                ModConfig cfg = f.getAnnotation(ModConfig.class);
                boolean match = q.isEmpty()
                             || cfg.name().toLowerCase().contains(q)
                             || cfg.description().toLowerCase().contains(q)
                             || cfg.category().toLowerCase().contains(q);
                if (match) {
                    map.computeIfAbsent(cfg.category(), k -> new ArrayList<>()).add(f);
                }
            }
        }
        return map;
    }

    // ═══════════════════════════════ 렌더링 ══════════════════════════════════════

    @Override
    public void renderBackground(GuiGraphics ctx, int mx, int my, float delta) {
        // 배경 블러 구현 (혹은 어두운 반투명 색상) - vanilla 스크린의 blur 등을 사용하거나 단순 어둡게 처리
        ctx.fill(0, 0, width, height, 0x50000000); 
    }

    @Override
    public void render(GuiGraphics ctx, int mx, int my, float delta) {
        if (searchField != null) {
            boolean showSearch = currentSidebarPage.equals("Mods");
            if (searchField.visible != showSearch) {
                searchField.visible = showSearch;
            }
            if (!searchField.getValue().equals(lastSearchQuery)) {
                lastSearchQuery = searchField.getValue();
                if (showSearch) refreshUI();
            }
        }

        NVGPIPRenderer.draw(ctx, 0, 0, width, height, () -> {
            float gs  = NVGRenderer.getStandardGuiScale();
            float smx = scaledMX();
            float smy = scaledMY();

            NVGRenderer.push();
            NVGRenderer.scale(gs, gs);

            loadIconsIfNeeded();

            drawFrame();
            renderSidebar();
            renderTopBar();

            if (currentSidebarPage.equals("Mods")) {
                renderSearchBarBg();
                if (currentModSettings == null) {
                    renderCategoryTabs();
                } else {
                    renderSettingsHeader();
                }
            } else {
                float sx = contentX + PAD;
                float hy = contentY + PAD;
                NVGRenderer.text(currentSidebarPage, sx, hy + 20, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 26f);
            }

            NVGRenderer.pushScissor(contentX, scissorY, contentW, scissorH);
            NVGRenderer.push();
            NVGRenderer.translate(0, (float) -scrollOffset);

            for (UIWidget w : widgets)
                w.render(ctx, (int) smx, (int) (smy + scrollOffset), delta);
            for (UIWidget w : overlayWidgets)
                w.render(ctx, (int) smx, (int) (smy + scrollOffset), delta);

            NVGRenderer.pop();
            NVGRenderer.popScissor();

            for (UIWidget w : overlayWidgets) {
                if (shouldSkipOverlay(w)) continue;
                int oy = w.getY();
                w.setPosition(w.getX(), (int) (oy - scrollOffset));
                w.renderOverlay(ctx, (int) smx, (int) smy, delta);
                w.setPosition(w.getX(), oy);
            }

            NVGRenderer.pop();
        });

        super.render(ctx, mx, my, delta);
    }

    private void drawFrame() {
    	int round = 14;
        NVGRenderer.rect(winX, winY, WINDOW_W, WINDOW_H, C_WIN_BG, round);
        NVGRenderer.rect(winX, winY, SIDEBAR_W, WINDOW_H, C_SIDEBAR_BG, round);
        NVGRenderer.rect(winX + SIDEBAR_W - 12, winY, 12, WINDOW_H, C_SIDEBAR_BG, 0f);
    }

    private void renderSidebar() {
        int ix = winX + PAD; // 좌측 패딩
        
        // 로고 텍스트 (아이콘 느낌)
        float lw = NVGRenderer.textWidth("L", Fonts.PRETENDARD_SEMIBOLD, 20f);
        NVGRenderer.text("LUCENT", ix, winY + 26f, Fonts.PRETENDARD_SEMIBOLD, C_ACCENT, 20f);
        //NVGRenderer.text("L", ix, winY + 26f, Fonts.PRETENDARD_SEMIBOLD, C_ACCENT, 20f);
        //NVGRenderer.text("UCENT", ix + lw, winY + 26f, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 20f);

        int sy = winY + 44;

        
        sy += 36;
        NVGRenderer.text("MOD CONFIG", ix, sy, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_LABEL, 10f); // 폰트 약간 작고 진하게
        sy += 16;
        sy = sidebarItem(ix, sy, iconMods, "Mods", currentSidebarPage.equals("Mods"));
        sy = sidebarItem(ix, sy, iconProfiles, "Profiles", currentSidebarPage.equals("Profiles"));

        sy += 16;
        NVGRenderer.text("PERSONALIZATION", ix, sy, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_LABEL, 10f);
        sy += 16;
        sy = sidebarItem(ix, sy, iconThemes, "Themes", currentSidebarPage.equals("Themes"));
        sy = sidebarItem(ix, sy, iconPreferences, "Preferences", currentSidebarPage.equals("Preferences"));

        int bY = winY + WINDOW_H - 100;
        sidebarItem(ix, bY, iconEditHud, "Edit HUD", false);
        sidebarItem(ix, bY + 38, iconClose, "Close", false);
    }

    private int sidebarItem(int x, int y, Image icon, String label, boolean active) {
        final int itemH = 34; // 기존 36에서 34로 약간 줄임 (둥근 사각형 크기)
        
        if (active) {
            NVGRenderer.rect(winX + 12, y, SIDEBAR_W - 24, itemH, 0xFF2B2B30, 8f);
            // 좌측 강조 라인 (왼쪽 끝에 붙어야 함)
            NVGRenderer.rect(winX + 12, y + 6, 3, itemH - 12, C_ACCENT, 1.5f);
        }

        int fg = active ? C_TEXT_PRIMARY : C_TEXT_SECONDARY;
        drawIcon(icon, x + 4, y + (itemH - 16) / 2f, 16);
        NVGRenderer.text(label, x + 30, y + 10f, Fonts.PRETENDARD_MEDIUM, fg, 14f);
        
        return y + itemH + 2;
    }

    private void renderTopBar() {
        int   cx = contentX + PAD;

        boolean isSub = currentModSettings != null;
        boolean canGoBack = isSub;
        boolean canGoFwd = !isSub && lastModSettings != null;

        int backColor = canGoBack ? C_TEXT_PRIMARY : C_TEXT_LABEL;
        int fwdColor  = canGoFwd  ? C_TEXT_PRIMARY : C_TEXT_LABEL;

        NVGRenderer.text("←", cx, winY + 26f,  Fonts.PRETENDARD_MEDIUM, backColor, 20f);
        NVGRenderer.text("→", cx + 32, winY + 26f,  Fonts.PRETENDARD_MEDIUM, fwdColor, 20f);
        NVGRenderer.text("Mods", cx + 70, winY + 25f,  Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 22f);
    }

    private void renderSearchBarBg() {
        int bw = 200, bh = 32;
        int bx = winX + WINDOW_W - PAD - bw;
        int by = winY + (TOPBAR_H - bh) / 2;

        NVGRenderer.rect(bx, by, bw, bh, C_SEARCHBAR_BG, 8f);
        NVGRenderer.outlineRect(bx, by, bw, bh, 1, searchFocused ? C_ACCENT : C_SEARCHBAR_BDR, 8f);
        drawIcon(iconSearch, bx + 10, by + (bh - 16) / 2f, 16);
        
        if (searchField != null) {
            String txt = searchField.getValue();
            float textY = by + (bh - 14f) / 2f;
            
            if (txt.isEmpty() && !searchFocused) {
                NVGRenderer.text("Search...", bx + 34, textY, Fonts.PRETENDARD_MEDIUM, C_TEXT_SECONDARY, 14f);
            } else {
                NVGRenderer.text(txt, bx + 34, textY, Fonts.PRETENDARD_MEDIUM, C_TEXT_PRIMARY, 14f);
                if (searchFocused && (System.currentTimeMillis() / 500) % 2 == 0) {
                    int cpos = searchField.getCursorPosition();
                    String beforeCursor = txt.substring(0, Math.min(cpos, txt.length()));
                    float tw = NVGRenderer.textWidth(beforeCursor, Fonts.PRETENDARD_MEDIUM, 14f);
                    NVGRenderer.rect(bx + 34 + tw + 1f, textY - 1f, 1.5f, 16f, C_TEXT_PRIMARY, 0f);
                }
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
            
            int bg = active ? C_ACCENT : C_TAB_BG;
            int fg = active ? C_TEXT_PRIMARY : 0xFFCCCCCC;
            
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
            NVGRenderer.text(currentModSettings.name, sx, hy, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 26f);
            NVGRenderer.text(currentModSettings.description, sx, hy + 32, Fonts.PRETENDARD, C_TEXT_SECONDARY, 14f);
        }

        int curY = contentY + 66;
        for (Map.Entry<String, List<Field>> e : groupSettingFields().entrySet()) {
            float ry = curY + 14 - (float) scrollOffset;
            if (ry > scissorY - 30 && ry < scissorY + scissorH) {
                String catName = e.getKey().toUpperCase();
                float catW = NVGRenderer.textWidth(catName, Fonts.PRETENDARD_SEMIBOLD, 12f);
                float indent = 8f;
                
                NVGRenderer.text(catName, sx + indent, ry, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_LABEL, 12f);
                
                float lineX = sx + indent + catW + 16f;
                float lineW = (contentW - PAD * 2) - (lineX - sx);
                NVGRenderer.rect(lineX, ry + 6f, lineW, 1.5f, C_DIVIDER, 0.75f);
            }
            curY += 36;
            curY += e.getValue().size() * 84;
            curY += 16;
        }
    }

    private List<String> getCategories() {
        List<String> cats = new ArrayList<>();
        cats.add("All");
        for (Module m : moduleManager.modules)
            if (!cats.contains(m.category)) cats.add(m.category);
        return cats;
    }

    // ═══════════════════════════════ 이벤트 ══════════════════════════════════

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        float mx = scaledMX();
        float my = scaledMY();
        int btn  = event.button();

        if (currentSidebarPage.equals("Mods") && searchField != null && searchField.visible) {
            int bw = 200, bh = 32;
            int bx = winX + WINDOW_W - PAD - bw;
            int by = winY + (TOPBAR_H - bh) / 2;
            boolean hit = (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh);
            if (hit != searchFocused) {
                searchFocused = hit;
                searchField.setFocused(hit);
                if (hit) {
                    this.setFocused(searchField);
                } else if (this.getFocused() == searchField) {
                    this.setFocused(null);
                }
            }
        }

        for (UIWidget w : overlayWidgets) {
            if (shouldSkipOverlay(w)) continue;
            if (w.mouseClicked(mx, my + scrollOffset, btn)) return true;
        }

        if (mx >= contentX && mx <= contentX + contentW
                && my >= scissorY && my <= scissorY + scissorH) {
            for (UIWidget w : widgets)
                if (w.mouseClicked(mx, my + scrollOffset, btn)) return true;
        }

        // 상단바 네비게이션 버튼 클릭
        if (currentSidebarPage.equals("Mods") && btn == 0) {
            float bx = contentX + PAD;
            float cy = winY + TOPBAR_H / 2f;
            
            boolean isSub = currentModSettings != null;
            boolean canGoBack = isSub;
            boolean canGoFwd = !isSub && lastModSettings != null;
            
            // Back button ←
            if (mx >= bx - 4 && mx <= bx + 20 && my >= cy - 20 && my <= cy + 10) {
                if (canGoBack) {
                    lastModSettings = currentModSettings;
                    currentModSettings = null;
                    refreshUI();
                    return true;
                }
            }
            // Forward button →
            if (mx >= bx + 28 && mx <= bx + 52 && my >= cy - 20 && my <= cy + 10) {
                if (canGoFwd) {
                    currentModSettings = lastModSettings;
                    refreshUI();
                    return true;
                }
            }
        }

        // 카테고리 탭 클릭
        if (currentSidebarPage.equals("Mods") && currentModSettings == null && btn == 0) {
            float cx = contentX + PAD;
            float cy = contentY;
            for (String cat : getCategories()) {
                float tw = NVGRenderer.textWidth(cat, Fonts.PRETENDARD_MEDIUM, 13f);
                int tabW = (int)(tw + 32);
                if (mx >= cx && mx <= cx + tabW && my >= cy && my <= cy + 28f) {
                    currentCategory = cat;
                    refreshUI();
                    return true;
                }
                cx += tabW + 8f;
            }
        }

        // 사이드바 탭 전환
        if (btn == 0) {
            int ix = winX + PAD;
            int sy = winY + 80 + 36 + 16; // 첫 Mods y 위치
            
            String[] pages  = {"Mods", "Profiles", "Themes", "Preferences"};
            int[] ys = {sy, sy + 38, sy + 38 + 16 + 16, sy + 38 + 16 + 16 + 38};
            
            for (int i = 0; i < pages.length; i++) {
                int ty = ys[i];
                if (mx >= winX + 16 && mx <= winX + SIDEBAR_W - 16 && my >= ty && my <= ty + 36) {
                    currentSidebarPage = pages[i];
                    if (currentSidebarPage.equals("Mods")) {
                        currentModSettings = null; // Always reset to mods list when clicking Mods tab?
                    } else if (searchField != null) {
                        searchField.setFocused(false);
                    }
                    refreshUI();
                    return true;
                }
            }

            float closeY = winY + WINDOW_H - 100 + 38;
            if (mx >= winX + 16 && mx <= winX + SIDEBAR_W - 16
                    && my >= closeY && my <= closeY + 36) {
                this.onClose();
                return true;
            }
        }

        return super.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        float mx = scaledMX(), my = scaledMY();
        int btn  = event.button();
        for (UIWidget w : overlayWidgets) {
            if (shouldSkipOverlay(w)) continue;
            if (w.mouseDragged(mx, my + scrollOffset, btn, event.x(), event.y())) return true;
        }
        if (mx >= contentX && mx <= contentX + contentW
                && my >= scissorY && my <= scissorY + scissorH) {
            for (UIWidget w : widgets)
                if (w.mouseDragged(mx, my + scrollOffset, btn, event.x(), event.y())) return true;
        }
        return super.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        float mx = scaledMX(), my = scaledMY();
        int btn  = event.button();
        for (UIWidget w : overlayWidgets) {
            if (shouldSkipOverlay(w)) continue;
            if (w.mouseReleased(mx, my + scrollOffset, btn)) return true;
        }
        if (mx >= contentX && mx <= contentX + contentW
                && my >= scissorY && my <= scissorY + scissorH) {
            for (UIWidget w : widgets)
                if (w.mouseReleased(mx, my + scrollOffset, btn)) return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double hAmt, double vAmt) {
        float mx = scaledMX(), my = scaledMY();
        if (mx >= contentX && mx <= contentX + contentW
                && my >= scissorY && my <= scissorY + scissorH) {
            for (UIWidget w : overlayWidgets) {
                if (shouldSkipOverlay(w)) continue;
                if (w.mouseScrolled(mx, my + scrollOffset, hAmt, vAmt)) return true;
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
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (currentModSettings != null) {
                currentModSettings = null;
                refreshUI();
            } else {
                this.onClose();
            }
            return true;
        }
        for (UIWidget w : widgets)
            if (w.keyPressed(key, input.scancode(), input.modifiers())) return true;
        for (UIWidget w : overlayWidgets)
            if (!shouldSkipOverlay(w) && w.keyPressed(key, input.scancode(), input.modifiers())) return true;
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        for (UIWidget w : widgets)
            if (w.charTyped((char) event.codepoint(), event.modifiers())) return true;
        for (UIWidget w : overlayWidgets)
            if (!shouldSkipOverlay(w) && w.charTyped((char) event.codepoint(), event.modifiers())) return true;
        return super.charTyped(event);
    }

    private boolean shouldSkipOverlay(UIWidget w) {
        if (w instanceof Selector s          && !s.isOpen())         return true;
        if (w instanceof ColorPickerButton c && !c.isPickerOpen())   return true;
        return false;
    }

    // ═══════════════════════════════ 내부 위젯 ════════════════════════════════

    private class ModCardWidget extends UIWidget {
        private final Module mod;
        private static final int BAR_H = 30;

        public ModCardWidget(int x, int y, int w, int h, Module mod) {
            super(x, y, w, h);
            this.mod = mod;
        }

        @Override
        protected void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
            boolean hov = mx >= x && mx <= x + width && my >= y && my <= y + height;
            int topBg   = hov ? C_CARD_HOVER : C_CARD_BG;
            int barBg   = mod.isEnabled ? C_ACCENT : C_TAB_BG; // 활성화 시 파란색, 비활성화 시 어두운 회색. (사진 상은 파란색)
            if (hov && !mod.isEnabled) barBg = 0xFF45454C;

            // 상단 몸통
            NVGRenderer.rect(x, y, width, height - BAR_H, topBg, 12, 12, 0, 0);
            // 하단 바
            NVGRenderer.rect(x, y + height - BAR_H, width, BAR_H, barBg, 0, 0, 12, 12);
            
            // 중앙 아이콘 또는 큰 글자
            float midX = x + width / 2f;
            float topH = height - BAR_H;

            Image iconImg = null;
            if (mod.icon != null && !mod.icon.isEmpty()) {
                iconImg = modIconsMap.get(mod.name);
            }
            
            if (iconImg != null) {
                NVGRenderer.image(iconImg, midX - 22f, y + (topH - 44f) / 2f, 44f, 44f);
            } else {
                float initialW = NVGRenderer.textWidth(mod.name, Fonts.PRETENDARD_SEMIBOLD, 20f);
                NVGRenderer.text(mod.name, midX - initialW/2, y + (topH - 20) / 2, Fonts.PRETENDARD_SEMIBOLD, 0xFFFFFFFF, 20);
            }

            // 하단 텍스트 및 설정 아이콘
            float barTop  = y + height - BAR_H;

            NVGRenderer.text(mod.name, x + 12f, barTop + 8f, Fonts.PRETENDARD_MEDIUM, UIColors.PURE_WHITE, 14f);
            
            // 구분선 및 설정 아이콘
            float divX = x + width - 36f;
            NVGRenderer.rect(divX, barTop + 6f, 1, BAR_H - 12f, 0x55FFFFFF, 0f); // 약간 투명한 선
            
            if (iconSettings != null) {
                NVGRenderer.image(iconSettings, divX + 10f, barTop + (BAR_H - 16f) / 2f, 16f, 16f);
            }
        }

        @Override
        public boolean mouseClicked(double mx, double my, int btn) {
            if (btn == 0 && mx >= x && mx <= x + width && my >= y && my <= y + height) {
                // 우측 끝 설정 아이콘 영역 클릭
                if (mx >= x + width - 36 && my >= y + height - BAR_H) {
                    currentModSettings = mod;
                    refreshUI();
                } else {
                    // 그 외 전 영역 클릭 (상단 몸통 및 하단 바 좌측) 클릭
                    mod.isEnabled = !mod.isEnabled;
                }
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
            NVGRenderer.rect(x, y, width, height, hov ? C_CARD_HOVER : C_CARD_BG, 8f);
            NVGRenderer.outlineRect(x, y, width, height, 1, C_SEARCHBAR_BDR, 8f);
            NVGRenderer.text(label,       x + 16, y + 24, Fonts.PRETENDARD_MEDIUM, C_TEXT_PRIMARY,   15f);
            NVGRenderer.text(description, x + 16, y + 46, Fonts.PRETENDARD,        C_TEXT_SECONDARY, 12f);
        }
    }

    @Override
    public void removed() {
        super.removed();
        if (moduleManager != null) {
            moduleManager.saveConfigs();
        }
    }
}
