package silence.simsool.lucent.ui.theme;

import java.util.ArrayList;
import java.util.List;

import silence.simsool.lucent.general.abstracts.LucentTheme;
import silence.simsool.lucent.ui.utils.UIColors;

public class ThemeManager {

	public static final List<LucentTheme> AVAILABLE_THEMES = new ArrayList<>();
	public static LucentTheme currentTheme;

	static {

		AVAILABLE_THEMES.add(new LucentTheme("Default",
				0xE6121215, 0xE618181C, 0xFF3B82F6, 0xFFFFFFFF, 0xFF8A8A8E, 0xFF6B7280,
				0x4D2B2B30, 0x6635353A, 0x33FFFFFF, 0xFF3B82F6, 0x4D35353A, 0x1AFFFFFF, 0x332B2B30, 0x4D000000)
		);

		AVAILABLE_THEMES.add(new LucentTheme("Glass Morphic",
				0xCC050507, 0xCC0A0A0F, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFDDDDDD, 0xFF999999,
				0x33000000, 0x4D000000, 0x1AFFFFFF, 0xFFFFFFFF, 0x1AFFFFFF, 0x1AFFFFFF, 0x26FFFFFF, 0x4D000000)
		);

		AVAILABLE_THEMES.add(new LucentTheme("Midnight Cyan",
				0xE60A0C13, 0xE606080E, 0xFF00D2FF, 0xFFA5B4FC, 0xFFFFFFFF, 0xFF6B7280,
				0x4D1A1C23, 0x66242730, 0x33FFFFFF, 0xFF00D2FF, 0x4D1A1C23, 0x1AFFFFFF, 0x331A1C23, 0x4D000000)
		);

		AVAILABLE_THEMES.add(new LucentTheme("Crimson Aurora",
				0xE61A0C0C, 0xE6120808, 0xFFF43F5E, 0xFFFDA4AF, 0xFFFFFFFF, 0xFF716B6B,
				0x4D2B1A1A, 0x663B2424, 0x33FFFFFF, 0xFFF43F5E, 0x4D2B1A1A, 0x1AFFFFFF, 0x332B1A1A, 0x4D000000)
		);

		AVAILABLE_THEMES.add(new LucentTheme("Emerald Mist",
				0xE60B120E, 0xE6070B08, 0xFF10B981, 0xFF6EE7B7, 0xFFFFFFFF, 0xFF6B716D,
				0x4D15261A, 0x661D3624, 0x33FFFFFF, 0xFF10B981, 0x4D15261A, 0x1AFFFFFF, 0x3315261A, 0x4D000000)
		);

		AVAILABLE_THEMES.add(new LucentTheme("Amethyst Eclipse",
				0xE60F0A1A, 0xE6140D24, 0xFFA855F7, 0xFFC084FC, 0xFFFFFFFF, 0xFF6D6B71,
				0x4D241A33, 0x6633244D, 0x33FFFFFF, 0xFFA855F7, 0x4D241A33, 0x1AFFFFFF, 0x33241A33, 0x4D000000)
		);
		
		applyTheme(AVAILABLE_THEMES.get(0)); // Default to first theme
	}

	public static void applyTheme(LucentTheme t) {
		if (t == null) return;
		currentTheme = t;
		UIColors.WIN_BG         = t.winBg;
		UIColors.SIDEBAR_BG     = t.sidebarBg;
		UIColors.ACCENT_BLUE    = t.accent;
		UIColors.TEXT_PRIMARY   = t.textPrimary;
		UIColors.TEXT_SECONDARY = t.textSecondary;
		UIColors.MUTED          = t.textLabel;
		UIColors.CARD_BG        = t.itemBg;
		UIColors.CARD_HOVER     = t.itemHover;
		UIColors.TAB_BG         = t.barOff;
		UIColors.DIVIDER        = t.divider;
		UIColors.SIDEBAR_SEL    = t.sidebarSel;
		UIColors.SEARCHBAR_BG   = t.searchbarBg;
		UIColors.ITEM_BORDER    = t.itemBorder;
	}

	public static LucentTheme findTheme(String name) {
		for (LucentTheme t : AVAILABLE_THEMES) {
			if (t.name.equalsIgnoreCase(name)) return t;
		}
		return AVAILABLE_THEMES.get(0);
	}

}