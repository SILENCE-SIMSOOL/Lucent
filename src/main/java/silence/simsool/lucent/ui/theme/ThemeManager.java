package silence.simsool.lucent.ui.theme;

import java.util.ArrayList;
import java.util.List;

import silence.simsool.lucent.general.models.data.LucentTheme;
import silence.simsool.lucent.ui.utils.UIColors;

public class ThemeManager {

	public static final List<LucentTheme> AVAILABLE_THEMES = new ArrayList<>();
	public static LucentTheme currentTheme;

	static {

		// 1. Default (기존 유지)
		AVAILABLE_THEMES.add(new LucentTheme("Default",
				0xE6121215, 0xE618181C, 0xFF3B82F6, 0xFFFFFFFF, 0xFF8A8A8E, 0xFF6B7280,
				0x4D2B2B30, 0x6635353A, 0x33FFFFFF, 0xFF3B82F6, 0x4D35353A, 0x1AFFFFFF, 0x332B2B30, 0x4D000000)
		);

		// 2. Silence Core (팔레트 기반: #201F1C, #282533, #B8A9E8 / #A78BFA)
		AVAILABLE_THEMES.add(new LucentTheme("Silence Core",
				0xE6201F1C, 0xE6282533, 0xFFA78BFA, 0xFFFFFFFF, 0xFFB8A9E8, 0xFF6B665E,
				0x4D282533, 0x80373347, 0x33A78BFA, 0xFFA78BFA, 0x4D282533, 0x1AA78BFA, 0x33A78BFA, 0x4D1A1920)
		);

		// 3. Glass Morphic
		AVAILABLE_THEMES.add(new LucentTheme("Glass Morphic",
				0xCC050507, 0xCC0A0A0F, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFDDDDDD, 0xFF999999,
				0x33000000, 0x4D000000, 0x1AFFFFFF, 0xFFFFFFFF, 0x1AFFFFFF, 0x1AFFFFFF, 0x26FFFFFF, 0x4D000000)
		);

		// 4. Midnight Cyan
		AVAILABLE_THEMES.add(new LucentTheme("Midnight Cyan",
				0xE60A0C13, 0xE606080E, 0xFF00D2FF, 0xFFA5B4FC, 0xFFFFFFFF, 0xFF6B7280,
				0x4D1A1C23, 0x66242730, 0x3300D2FF, 0xFF00D2FF, 0x4D1A1C23, 0x1A00D2FF, 0x3300D2FF, 0x4D000000)
		);

		// 5. Crimson Aurora
		AVAILABLE_THEMES.add(new LucentTheme("Crimson Aurora",
				0xE61A0C0C, 0xE6120808, 0xFFF43F5E, 0xFFFDA4AF, 0xFFFFFFFF, 0xFF716B6B,
				0x4D2B1A1A, 0x663B2424, 0x33F43F5E, 0xFFF43F5E, 0x4D2B1A1A, 0x1AF43F5E, 0x33F43F5E, 0x4D000000)
		);

		// 6. Emerald Mist
		AVAILABLE_THEMES.add(new LucentTheme("Emerald Mist",
				0xE60B120E, 0xE6070B08, 0xFF10B981, 0xFF6EE7B7, 0xFFFFFFFF, 0xFF6B716D,
				0x4D15261A, 0x661D3624, 0x3310B981, 0xFF10B981, 0x4D15261A, 0x1A10B981, 0x3310B981, 0x4D000000)
		);

		// 7. Amethyst Eclipse
		AVAILABLE_THEMES.add(new LucentTheme("Amethyst Eclipse",
				0xE60F0A1A, 0xE6140D24, 0xFFA855F7, 0xFFC084FC, 0xFFFFFFFF, 0xFF6D6B71,
				0x4D241A33, 0x6633244D, 0x33A855F7, 0xFFA855F7, 0x4D241A33, 0x1AA855F7, 0x33A855F7, 0x4D000000)
		);

		// 8. Sunset Amber
		AVAILABLE_THEMES.add(new LucentTheme("Sunset Amber",
				0xE61A130E, 0xE6140E0A, 0xFFF59E0B, 0xFFFDE68A, 0xFFFFFFFF, 0xFF786F66,
				0x4D2E2014, 0x66402D1D, 0x33F59E0B, 0xFFF59E0B, 0x4D2E2014, 0x1AF59E0B, 0x33F59E0B, 0x4D0A0806)
		);

		// 9. Rose Quartz
		AVAILABLE_THEMES.add(new LucentTheme("Rose Quartz",
				0xE6181014, 0xE6130B0F, 0xFFFB7185, 0xFFFECDD3, 0xFFFFFFFF, 0xFF756A70,
				0x4D2B1A24, 0x663B2432, 0x33FB7185, 0xFFFB7185, 0x4D2B1A24, 0x1AFB7185, 0x33FB7185, 0x4D0F080C)
		);

		// 10. Nordic Frost
		AVAILABLE_THEMES.add(new LucentTheme("Nordic Frost",
				0xE60D151A, 0xE6090E12, 0xFF38BDF8, 0xFFBAE6FD, 0xFFFFFFFF, 0xFF67737A,
				0x4D152530, 0x661D3342, 0x3338BDF8, 0xFF38BDF8, 0x4D152530, 0x1A38BDF8, 0x3338BDF8, 0x4D060A0D)
		);

		// 11. Cyberpunk Neon
		AVAILABLE_THEMES.add(new LucentTheme("Cyberpunk Neon",
				0xE60D0D0E, 0xE6121214, 0xFFEAB308, 0xFFFEF08A, 0xFFFFFFFF, 0xFF6E6E64,
				0x4D242416, 0x6633331F, 0x33EAB308, 0xFFEAB308, 0x4D242416, 0x1AEAB308, 0x33EAB308, 0x4D050505)
		);

		// 12. Obsidian Gold
		AVAILABLE_THEMES.add(new LucentTheme("Obsidian Gold",
				0xE614120C, 0xE60F0E09, 0xFFE5C07B, 0xFFF5E6C8, 0xFFFFFFFF, 0xFF736F64,
				0x4D292415, 0x663B341F, 0x33E5C07B, 0xFFE5C07B, 0x4D292415, 0x1AE5C07B, 0x33E5C07B, 0x4D080705)
		);

		// 13. Deep Sea Abyss
		AVAILABLE_THEMES.add(new LucentTheme("Deep Sea Abyss",
				0xE6081418, 0xE6050E12, 0xFF06B6D4, 0xFFA5F3FC, 0xFFFFFFFF, 0xFF65757A,
				0x4D102630, 0x66173645, 0x3306B6D4, 0xFF06B6D4, 0x4D102630, 0x1A06B6D4, 0x3306B6D4, 0x4D040A0D)
		);

		// 14. Sakura Blossom
		AVAILABLE_THEMES.add(new LucentTheme("Sakura Blossom",
				0xE6170E16, 0xE6120A11, 0xFFF472B6, 0xFFFBCFE8, 0xFFFFFFFF, 0xFF756873,
				0x4D2B1828, 0x663D2239, 0x33F472B6, 0xFFF472B6, 0x4D2B1828, 0x1AF472B6, 0x33F472B6, 0x4D0E070D)
		);

		// 15. Forest Sage
		AVAILABLE_THEMES.add(new LucentTheme("Forest Sage",
				0xE60E1510, 0xE60A100B, 0xFF84CC16, 0xFFD9F99D, 0xFFFFFFFF, 0xFF6B7569,
				0x4D1B2B18, 0x66273D23, 0x3384CC16, 0xFF84CC16, 0x4D1B2B18, 0x1A84CC16, 0x3384CC16, 0x4D070B08)
		);

		// 16. Solar Flare
		AVAILABLE_THEMES.add(new LucentTheme("Solar Flare",
				0xE6180E09, 0xE6120A06, 0xFFFF6B4A, 0xFFFFC2B2, 0xFFFFFFFF, 0xFF7A6A64,
				0x4D2B170E, 0x663D2114, 0x33FF6B4A, 0xFFFF6B4A, 0x4D2B170E, 0x1AFF6B4A, 0x33FF6B4A, 0x4D0D0704)
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