package silence.simsool.lucent.client.dev.screens;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import silence.simsool.lucent.general.abstracts.Module;
import silence.simsool.lucent.general.interfaces.ModConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import silence.simsool.lucent.client.ModuleManager;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
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

public class LucentConfigScreen extends Screen {

    // ─── 색상 팔레트 ─────────────────────────────────────────────────────────────
    private static final int C_WIN_BG         = 0xFF1C1C1F;
    private static final int C_SIDEBAR_BG     = 0xFF111113;
    private static final int C_ACCENT         = 0xFF3574F0;
    private static final int C_TEXT_PRIMARY   = 0xFFEAEAEB;
    private static final int C_TEXT_SECONDARY = 0xFF8A8A93;
    private static final int C_TEXT_LABEL     = 0xFF4A4A52;
    private static final int C_ITEM_BG        = 0xFF252527;
    private static final int C_ITEM_HOVER     = 0xFF2D2D31;
    private static final int C_ITEM_BORDER    = 0xFF363639;
    private static final int C_BAR_ON         = 0xFF3574F0;
    private static final int C_BAR_OFF        = 0xFF252527;
    private static final int C_DIVIDER        = 0xFF2A2A2D;
    private static final int C_SIDEBAR_SEL    = 0xFF232326;
    private static final int C_SEARCHBAR_BG   = 0xFF18181A;

    // ─── 레이아웃 상수 ────────────────────────────────────────────────────────────
    private static final int WINDOW_W  = 1100;
    private static final int WINDOW_H  = 680;
    private static final int SIDEBAR_W = 220;
    private static final int TOPBAR_H  = 64;
    private static final int PAD       = 22;

    // ─── 아이콘 Image (lazy init — NVG 컨텍스트 준비 후 로드) ──────────────────────
    // NVGRenderer.createImage(String path) 를 사용합니다.
    // path 는 classpath 기준 절대 경로 또는 mod jar 내 경로에 따라 프로젝트에 맞게 조정하세요.
    private Image iconMods, iconProfiles, iconThemes, iconPreferences;
    private Image iconEditHud, iconClose, iconSearch, iconSettings, iconBack;
    /** 아이콘이 이미 로드됐는지 여부 */
    private boolean iconsLoaded = false;

    /** NVG 컨텍스트가 준비된 시점에 한 번만 호출 */
    private void loadIconsIfNeeded() {
        if (iconsLoaded) return;
        iconsLoaded = true;
        // NVGRenderer.createImage(path) — path 는 assets 폴더 기준 조정 필요
        try {
			iconMods        = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/mods.png");
	        iconProfiles    = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/profiles.png");
	        iconThemes      = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/themes.png");
	        iconPreferences = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/preferences.png");
	        iconEditHud     = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/edithud.png");
	        iconClose       = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/close.png");
	        iconSearch      = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/search.png");
	        iconSettings    = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/settings.png");
	        iconBack        = NVGRenderer.createImage("/assets/lucent/textures/gui/icons/back.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

    /** null-safe 아이콘 렌더 헬퍼 */
    private static void drawIcon(Image icon, float x, float y, float size) {
        if (icon == null) return;
        NVGRenderer.image(icon, x, y, size, size);
    }

    // ─── 상태 ────────────────────────────────────────────────────────────────────
    private final ModuleManager moduleManager;
    private Module currentModSettings = null;
    private String currentCategory    = "All";
    private String lastSearchQuery    = "";

    /** NVG 좌표계(guiScale 나눈 값) 기준 윈도우 위치 */
    private int winX, winY;
    /** 컨텐츠 영역 (사이드바 오른쪽, 탑바 아래) */
    private int contentX, contentY, contentW, contentH;
    /** 스크롤 클립 영역 */
    private int scissorY, scissorH;

    private final List<UIWidget> widgets        = new ArrayList<>();
    private final List<UIWidget> overlayWidgets = new ArrayList<>();

    /**
     * 검색 EditBox
     * addRenderableWidget() 으로 등록 → vanilla 이벤트 자동 연결
     * 좌표는 vanilla(guiScale 기준) → NVG 좌표 × guiScale 로 변환
     */
    private EditBox searchField;
    private boolean searchFocused = false;

    private double scrollOffset = 0;
    private double maxScroll    = 0;

    // ─── 생성자 ──────────────────────────────────────────────────────────────────
    public LucentConfigScreen(ModuleManager moduleManager) {
        super(Component.literal("Lucent Config"));
        this.moduleManager = moduleManager;
    }

    // ─── 좌표 헬퍼 ──────────────────────────────────────────────────────────────
    private float scaledMX() {
        return (float)(minecraft.mouseHandler.xpos() / NVGRenderer.getStandardGuiScale());
    }
    private float scaledMY() {
        return (float)(minecraft.mouseHandler.ypos() / NVGRenderer.getStandardGuiScale());
    }

    // ═══════════════════════════════ init ════════════════════════════════════════
    @Override
    protected void init() {
        super.init();

        float gs      = NVGRenderer.getStandardGuiScale();
        float screenW = minecraft.getWindow().getScreenWidth()  / gs;
        float screenH = minecraft.getWindow().getScreenHeight() / gs;

        // NVG 좌표계 기준 윈도우 위치
        winX = (int)((screenW - WINDOW_W) / 2f);
        winY = (int)((screenH - WINDOW_H) / 2f);

        contentX = winX + SIDEBAR_W;
        contentY = winY + TOPBAR_H;
        contentW = WINDOW_W - SIDEBAR_W;
        contentH = WINDOW_H - TOPBAR_H;

        // ── 검색 EditBox 배치 ─────────────────────────────────────────────────────
        // NVG 좌표 기준 서치바 배경 위치
        int searchW = 190;
        int searchH = 34;
        int nvgBX   = winX + WINDOW_W - PAD - searchW; // 배경 x (NVG)
        int nvgBY   = winY + (TOPBAR_H - searchH) / 2; // 배경 y (NVG)
        // 텍스트 입력 시작 위치 (돋보기 아이콘 28px 오른쪽)
        int nvgTX   = nvgBX + 28;
        int nvgTY   = nvgBY + (searchH - 14) / 2;
        // vanilla 좌표로 변환 (guiScale 곱)
        searchField = new EditBox(
            font,
            Math.round(nvgTX * gs),
            Math.round(nvgTY * gs),
            Math.round((searchW - 32) * gs),
            Math.round(14 * gs),
            Component.literal("Search")
        );
        searchField.setHint(Component.literal("Search mods..."));
        searchField.setBordered(false);
        searchField.setMaxLength(64);
        searchField.setTextColor(0xFFEAEAEB);
        searchField.setTextColorUneditable(0xFF8A8A93);
        addRenderableWidget(searchField); // vanilla 이벤트 자동 연결
        searchField.setFocused(false);

        refreshUI();
    }

    // ═══════════════════════════════ UI 빌드 ══════════════════════════════════════

    private void refreshUI() {
        widgets.clear();
        overlayWidgets.clear();
        scrollOffset = 0;
        if (currentModSettings == null) buildMainWidgets();
        else                            buildSettingsWidgets();
    }

    // ── 메인 화면: 모드 카드 그리드 ─────────────────────────────────────────────
    private void buildMainWidgets() {
        // 카테고리 탭 높이(~40px) 아래부터 스크롤 영역 시작
        scissorY = contentY + 42;
        scissorH = contentH - 42;

        List<Module> mods = getFilteredMods();

        int cols  = 4;
        int gap   = 14;
        int cardW = (contentW - PAD * 2 - gap * (cols - 1)) / cols;
        int cardH = 138;
        int sx    = contentX + PAD;
        int sy    = scissorY + 10;
        int maxRow = 0;

        for (int i = 0; i < mods.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            maxRow  = Math.max(maxRow, row);
            widgets.add(new ModCardWidget(
                sx + col * (cardW + gap),
                sy + row * (cardH + gap) + 8,
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

    // ── 설정 화면 ────────────────────────────────────────────────────────────────
    private void buildSettingsWidgets() {
        scissorY = contentY;
        scissorH = contentH;

        int sx    = contentX + PAD;
        int curY  = scissorY + PAD + 95; // 헤더 여백
        int itemW = contentW - PAD * 2;

        for (Map.Entry<String, List<Field>> entry : groupSettingFields().entrySet()) {
            curY += 44; // 카테고리 제목 공간

            for (Field field : entry.getValue()) {
                ModConfig cfg = field.getAnnotation(ModConfig.class);
                widgets.add(new SettingRowWidget(sx, curY, itemW, 74, cfg.name(), cfg.description()));

                try {
                    field.setAccessible(true);
                    Object val = field.get(currentModSettings);
                    int ux = sx + itemW - PAD; // 위젯 오른쪽 끝 기준 x

                    switch (cfg.type()) {
                        case SWITCH -> {
                            ToggleButton t = new ToggleButton(ux - 48, curY + 25, 48, 24, (boolean) val);
                            t.setOnChange(v -> { try { field.set(currentModSettings, v); } catch (Exception e) {} });
                            widgets.add(t);
                        }
                        case SLIDER -> {
                            // ← Slider 너비 290으로 설정
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
                    }
                } catch (IllegalAccessException e) { e.printStackTrace(); }

                curY += 88;
            }
        }
        maxScroll = Math.max(0, curY - scissorY + PAD - scissorH);
    }

    private Map<String, List<Field>> groupSettingFields() {
        Map<String, List<Field>> map = new HashMap<>();
        for (Field f : currentModSettings.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(ModConfig.class)) {
                ModConfig cfg = f.getAnnotation(ModConfig.class);
                map.computeIfAbsent(cfg.category(), k -> new ArrayList<>()).add(f);
            }
        }
        return map;
    }

    // ═══════════════════════════════ 렌더링 ══════════════════════════════════════

    @Override
    public void renderBackground(GuiGraphics ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, 0xA8000000);
    }

    @Override
    public void render(GuiGraphics ctx, int mx, int my, float delta) {
        // 검색어 변경 감지 → 카드 리빌드
        if (searchField != null && !searchField.getValue().equals(lastSearchQuery)) {
            lastSearchQuery = searchField.getValue();
            if (currentModSettings == null) refreshUI();
        }

        NVGPIPRenderer.draw(ctx, 0, 0, width, height, () -> {
            float gs  = NVGRenderer.getStandardGuiScale();
            float smx = scaledMX();
            float smy = scaledMY();

            NVGRenderer.push();
            NVGRenderer.scale(gs, gs);

            // NVG 컨텍스트가 준비된 첫 렌더 시점에 아이콘 로드
            loadIconsIfNeeded();

            drawFrame();
            renderSidebar();
            renderTopBar();

            if (currentModSettings == null) {
                renderSearchBarBg();
                renderCategoryTabs();
            } else {
                renderSettingsHeader();
            }

            // ── 스크롤 클립 영역 ──
            NVGRenderer.pushScissor(contentX, scissorY, contentW, scissorH);
            NVGRenderer.push();
            NVGRenderer.translate(0, (float) -scrollOffset);

            for (UIWidget w : widgets)
                w.render(ctx, (int) smx, (int) (smy + scrollOffset), delta);
            for (UIWidget w : overlayWidgets)
                w.render(ctx, (int) smx, (int) (smy + scrollOffset), delta);

            NVGRenderer.pop();
            NVGRenderer.popScissor();

            // ── 드롭다운 오버레이 ──
            for (UIWidget w : overlayWidgets) {
                if (shouldSkipOverlay(w)) continue;
                int oy = w.getY();
                w.setPosition(w.getX(), (int) (oy - scrollOffset));
                w.renderOverlay(ctx, (int) smx, (int) smy, delta);
                w.setPosition(w.getX(), oy);
            }

            NVGRenderer.pop();
        });

        // vanilla 컴포넌트(EditBox) 렌더 — super.render() 가 처리
        super.render(ctx, mx, my, delta);
    }

    // ── 윈도우 프레임 ──────────────────────────────────────────────────────────
    private void drawFrame() {
        // 메인 배경
        NVGRenderer.rect(winX, winY, WINDOW_W, WINDOW_H, C_WIN_BG, 12f);
        // 사이드바 (왼쪽 모서리만 둥글게)
        NVGRenderer.rect(winX, winY, SIDEBAR_W, WINDOW_H, C_SIDEBAR_BG, 12f);
        NVGRenderer.rect(winX + SIDEBAR_W - 10, winY, 10, WINDOW_H, C_SIDEBAR_BG, 0f);
        // 경계선
        NVGRenderer.rect(winX + SIDEBAR_W, winY, 1, WINDOW_H, C_DIVIDER, 0f);
        NVGRenderer.rect(contentX, winY + TOPBAR_H, contentW, 1, C_DIVIDER, 0f);
    }

    // ── 사이드바 ──────────────────────────────────────────────────────────────
    private void renderSidebar() {
        int ix = winX + 16; // 아이콘 left x
        int tx = winX + 38; // 텍스트 left x

        // 로고 텍스트
        NVGRenderer.text("LUCENT", ix, winY + 28,
            Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 17f);

        // ── MOD CONFIG 섹션 ──
        int sy = winY + 52;
        NVGRenderer.text("MOD CONFIG", ix, sy,
            Fonts.PRETENDARD_SEMIBOLD, C_TEXT_LABEL, 10f);
        sy += 16;
        sy = sidebarItem(ix, tx, sy, iconMods,     "Mods",     currentModSettings == null);
        sy = sidebarItem(ix, tx, sy, iconProfiles, "Profiles", false);

        // ── PERSONALIZATION 섹션 ──
        sy += 8;
        NVGRenderer.text("PERSONALIZATION", ix, sy,
            Fonts.PRETENDARD_SEMIBOLD, C_TEXT_LABEL, 10f);
        sy += 16;
        sy = sidebarItem(ix, tx, sy, iconThemes,      "Themes",      false);
        sy = sidebarItem(ix, tx, sy, iconPreferences, "Preferences", false);

        // ── 하단 고정 (Edit HUD / Close) ──
        int bY = winY + WINDOW_H - 74;
        NVGRenderer.rect(winX + 12, bY - 4, SIDEBAR_W - 24, 1, C_DIVIDER, 0f);
        bY += 6;
        sidebarItem(ix, tx, bY,      iconEditHud, "Edit HUD", false);
        sidebarItem(ix, tx, bY + 34, iconClose,   "Close",    false);
    }

    /**
     * 사이드바 단일 항목 렌더링
     * 아이콘 16×16 + 텍스트, 활성 시 배경 + 왼쪽 액센트 바
     * @return 다음 항목 y 좌표
     */
    private int sidebarItem(int ix, int tx, int y, Image icon, String label, boolean active) {
		final int itemH = 30;
		
		if (active) {
			NVGRenderer.rect(winX + 8, y, SIDEBAR_W - 16, itemH, C_SIDEBAR_SEL, 7f);
			NVGRenderer.rect(winX + 9, y + 2, 3, itemH - 4, C_ACCENT, 2f);
		}

		int fg = active ? C_TEXT_PRIMARY : C_TEXT_SECONDARY;
		drawIcon(icon, ix + 3, y + (itemH - 16) / 2f, 16);
		NVGRenderer.text(label, tx + 8, y + (itemH - 14) / 2f + 1, Fonts.PRETENDARD_MEDIUM, fg, 14f);
		
		return y + 34;
		}

    // ── 탑바 ──────────────────────────────────────────────────────────────────
    private void renderTopBar() {
        float cy = winY + TOPBAR_H / 2f + 7f; // 텍스트 베이스라인 y
        int   cx = contentX + PAD;

        if (currentModSettings == null) {
            NVGRenderer.text("Mods", cx, cy,  Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 20f);
        } else {
            // 뒤로가기 아이콘 (수직 중앙)
            drawIcon(iconBack, cx, cy - 14, 16);
            // Breadcrumb: Mods  >  ModName
            float modsW = NVGRenderer.textWidth("Mods", Fonts.PRETENDARD_MEDIUM, 17f);
			NVGRenderer.text("Mods", cx + 22, cy - 14, Fonts.PRETENDARD_MEDIUM, C_TEXT_SECONDARY, 17f);
			NVGRenderer.text(">", cx + 24 + modsW, cy - 14, Fonts.PRETENDARD, C_TEXT_SECONDARY, 15f);
			NVGRenderer.text(currentModSettings.name, cx + 40 + modsW, cy - 14, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 17f);
        }
    }

    // ── 검색바 배경 ───────────────────────────────────────────────────────────
    private void renderSearchBarBg() {
        int bw = 190, bh = 34;
        int bx = winX + WINDOW_W - PAD - bw;
        int by = winY + (TOPBAR_H - bh) / 2;

        NVGRenderer.rect(bx, by, bw, bh, C_SEARCHBAR_BG, 8f);
        // 포커스 여부에 따라 테두리 색 변경
        NVGRenderer.outlineRect(bx, by, bw, bh, 1,
            searchFocused ? C_ACCENT : C_ITEM_BORDER, 8f);
        // 돋보기 아이콘 (수직 중앙)
        drawIcon(iconSearch, bx + 8, by + (bh - 16) / 2f, 16);
    }

    // ── 카테고리 탭 (밑줄 방식) ──────────────────────────────────────────────
    private void renderCategoryTabs() {
        List<String> cats = getCategories();
        float cx = contentX + PAD;
        float cy = contentY + 10f;

        for (String cat : cats) {
            float tw      = NVGRenderer.textWidth(cat, Fonts.PRETENDARD_MEDIUM, 16f);
            boolean active = cat.equals(currentCategory);
            int fg         = active ? C_TEXT_PRIMARY : C_TEXT_SECONDARY;

            NVGRenderer.text(cat, cx, cy + 14, Fonts.PRETENDARD_MEDIUM, fg, 16f);

            if (active) {
                // 활성: 텍스트 바로 아래 2px 액센트 밑줄
                NVGRenderer.rect(cx - 1, cy + 32, tw + 2, 2f, C_ACCENT, 2);
            }
            cx += tw + 18f;
        }
    }

    // ── 설정 화면 헤더 ────────────────────────────────────────────────────────
    private void renderSettingsHeader() {
        int sx = contentX + PAD;
        float hy = contentY + PAD - (float) scrollOffset;

        if (hy > scissorY - 80 && hy < scissorY + scissorH) {
            NVGRenderer.text(currentModSettings.name, sx, hy + 4, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_PRIMARY, 26f);
            NVGRenderer.text(currentModSettings.description, sx, hy + 32, Fonts.PRETENDARD, C_TEXT_SECONDARY, 13f);
        }

        // 설정 카테고리 제목들
        int curY = contentY + PAD + 60;
        for (Map.Entry<String, List<Field>> e : groupSettingFields().entrySet()) {
            float ry = curY + 14 - (float) scrollOffset;
            if (ry > scissorY - 30 && ry < scissorY + scissorH) {
                NVGRenderer.text(e.getKey().toUpperCase(), sx, ry, Fonts.PRETENDARD_SEMIBOLD, C_TEXT_LABEL, 11f);
            }
            curY += 44 + e.getValue().size() * 88;
        }
    }

    // ── 카테고리 목록 헬퍼 ──────────────────────────────────────────────────
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

        // ── 검색바 포커스 판정 ──
        if (currentModSettings == null && searchField != null) {
            int bw = 190, bh = 34;
            int bx = winX + WINDOW_W - PAD - bw;
            int by = winY + (TOPBAR_H - bh) / 2;
            boolean hit = (mx >= bx && mx <= bx + bw && my >= by && my <= by + bh);
            if (hit != searchFocused) {
                searchFocused = hit;
                searchField.setFocused(hit);
            }
        }

        // ── 오버레이 위젯 (드롭다운 등) ──
        for (UIWidget w : overlayWidgets) {
            if (shouldSkipOverlay(w)) continue;
            if (w.mouseClicked(mx, my + scrollOffset, btn)) return true;
        }

        // ── 컨텐츠 위젯 ──
        if (mx >= contentX && mx <= contentX + contentW
                && my >= scissorY && my <= scissorY + scissorH) {
            for (UIWidget w : widgets)
                if (w.mouseClicked(mx, my + scrollOffset, btn)) return true;
        }

        // ── ← 뒤로가기 영역 ──
        if (currentModSettings != null && btn == 0) {
            float bx = contentX + PAD;
            float by = winY + TOPBAR_H / 2f - 16f;
            if (mx >= bx - 4 && mx <= bx + 20 && my >= by && my <= by + 22) {
                currentModSettings = null;
                refreshUI();
                return true;
            }
        }

        // ── 카테고리 탭 클릭 ──
        if (currentModSettings == null && btn == 0) {
            float cx = contentX + PAD;
            float cy = contentY + 10f;
            for (String cat : getCategories()) {
                float tw = NVGRenderer.textWidth(cat, Fonts.PRETENDARD_MEDIUM, 14f);
                if (mx >= cx && mx <= cx + tw && my >= cy && my <= cy + 22f) {
                    currentCategory = cat;
                    refreshUI();
                    return true;
                }
                cx += tw + 24f;
            }
        }

        // ── 사이드바 Close 버튼 ──
        if (btn == 0) {
            float closeY = winY + WINDOW_H - 74f + 6f + 34f;
            if (mx >= winX + 8 && mx <= winX + SIDEBAR_W - 8
                    && my >= closeY - 2 && my <= closeY + 30) {
                this.onClose();
                return true;
            }
        }

        // searchField는 addRenderableWidget 등록으로 super 에서 처리됨
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
        // EditBox 는 addRenderableWidget 등록으로 super 에서 charTyped 처리됨
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

    /**
     * 모드 카드 위젯
     *
     * ┌──────────────────────┐
     * │                      │  ← 클릭: on/off 토글
     * │      모드 이름         │
     * │                      │
     * ├──────────────┬───────┤
     * │   모드이름     │  [⚙] │  ← 설정 아이콘 클릭: 설정 화면
     * └──────────────┴───────┘
     */
    private class ModCardWidget extends UIWidget {
        private final Module mod;
        private static final int BAR_H = 36;

        public ModCardWidget(int x, int y, int w, int h, Module mod) {
            super(x, y, w, h);
            this.mod = mod;
        }

        @Override
        protected void renderWidget(GuiGraphics ctx, int mx, int my, float delta) {
            boolean hov = mx >= x && mx <= x + width && my >= y && my <= y + height;
            int topBg   = hov ? C_ITEM_HOVER : C_ITEM_BG;
            int barBg   = mod.isEnabled ? UIColors.ACCENT_BLUE : UIColors.GRAY;

            // 전체 배경 (하단 바 색으로 먼저 칠함)
            //NVGRenderer.rect(x, y, width, height, barBg, 10f);
            // 상단 바디 (하단 직각 처리)
            NVGRenderer.rect(x, y, width, height, UIColors.DARK_GRAY, 12);
            NVGRenderer.rect(x, y + height - BAR_H, width, BAR_H, barBg, 0, 0, 12, 12);
            
//            NVGRenderer.pushScissor(x, y, width, height - BAR_H);
//            NVGRenderer.rect(x, y, width, height - BAR_H + 10, UIColors.DARK_GRAY, 10f);
//            NVGRenderer.popScissor();
//            // 테두리
//            NVGRenderer.outlineRect(x, y, width, height, 1,
//                hov ? UColor.withAlpha(C_ACCENT, 60) : C_ITEM_BORDER, 10f);

            // ── 상단 중앙: 모드 이름 ──
            int topH  = height - BAR_H;
            float nw  = NVGRenderer.textWidth(mod.name, Fonts.PRETENDARD_SEMIBOLD, 17f);
            NVGRenderer.text(mod.name,
                x + (width - nw) / 2f, y + topH / 2f,
                Fonts.PRETENDARD_SEMIBOLD,
                UIColors.PURE_WHITE, 17f);

            // ── 하단 바: [ 모드이름 | 수직선 | ⚙아이콘 ] ──
            float barTop  = y + height - BAR_H;
            float barMidY = barTop + (BAR_H / 2);
            float divX    = x + width - 34f;

            // 왼쪽: 모드 이름 (오버플로우 클립)
            //NVGRenderer.pushScissor(x, (int) barTop, (int) (divX - x - 4), BAR_H);
            NVGRenderer.text(mod.name, x + 12f, barMidY - 4, Fonts.PRETENDARD_MEDIUM, UIColors.PURE_WHITE, 14f);
            //NVGRenderer.popScissor();

            // 수직 구분선
            NVGRenderer.rect(divX, barTop + 7, 1, BAR_H - 14,
                mod.isEnabled ? 0x55FFFFFF : 0x35FFFFFF, 0f);

            // 설정 아이콘 PNG (우측, 수직 중앙)
            boolean gHov = isGearArea(mx, my);
            // 호버 시 흰색 강조, 기본은 barFg 색조 (Image는 tint 지원 안 하면 그냥 렌더)
            // 아이콘이 흰색이면 자연스럽게 barBg 위에서 가시적
            if (iconSettings != null) {
                NVGRenderer.image(iconSettings, divX + 9f, barTop + (BAR_H - 16) / 2f, 16, 16);
            }
        }

        private boolean isGearArea(int mx, int my) {
            return mx >= x + width - 34 && mx <= x + width
                && my >= y + height - BAR_H && my <= y + height;
        }

        @Override
        public boolean mouseClicked(double mx, double my, int btn) {
            if (btn == 0 && mx >= x && mx <= x + width && my >= y && my <= y + height) {
                if (isGearArea((int) mx, (int) my)) {
                    currentModSettings = mod;
                    refreshUI();
                } else {
                    mod.isEnabled = !mod.isEnabled;
                }
                return true;
            }
            return false;
        }
    }

    /** 설정 항목 행 위젯 */
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
            NVGRenderer.rect(x, y, width, height, hov ? C_ITEM_HOVER : C_ITEM_BG, 8f);
            NVGRenderer.outlineRect(x, y, width, height, 1, C_ITEM_BORDER, 8f);
            NVGRenderer.text(label,       x + 16, y + 24, Fonts.PRETENDARD_MEDIUM, C_TEXT_PRIMARY,   15f);
            NVGRenderer.text(description, x + 16, y + 46, Fonts.PRETENDARD,        C_TEXT_SECONDARY, 12f);
        }
    }
}


