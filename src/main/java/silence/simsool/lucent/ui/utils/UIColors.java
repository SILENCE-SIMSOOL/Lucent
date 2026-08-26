package silence.simsool.lucent.ui.utils;

public class UIColors {

	// ---------- Base Static Colors ----------
	public static final int PURE_WHITE       = 0xFFFFFFFF;
	public static final int LIGHT_GRAY       = 0xFFE1E1E1;
	public static final int GRAY             = 0xFFADADAD;
	public static final int DIM_GRAY         = 0xFF909090;
	public static final int DARK_GRAY        = 0xFF555555;
	public static final int VERY_DARK_GRAY   = 0xFF191919;
	public static final int CHARCOAL         = 0xFF0F0F0F;
	public static final int JET_BLACK        = 0xFF0A0A0A;
	public static final int PURE_BLACK       = 0xFF000000;
	public static final int RED              = 0xFFEF4444;

	// ---------- Dynamic Theme Colors ----------
	public static int WIN_BG                 = 0xE6121215; 
	public static int SIDEBAR_BG             = 0xE618181C; 
	public static int CARD_BG                = 0x4D2B2B30; 
	public static int CARD_HOVER             = 0x6635353A; 
	public static int TAB_BG                 = 0x4D35353A; 
	public static int DIVIDER                = 0x1AFFFFFF; 
	public static int SEARCHBAR_BG           = 0x4D000000; 
	public static int SIDEBAR_SEL            = 0xFF3B82F6; 
	public static int ITEM_BORDER            = 0x33FFFFFF; 

	// ---------- Text & Accent ----------
	public static int TEXT_PRIMARY           = 0xFFFFFFFF;
	public static int TEXT_SECONDARY         = 0xFF9CA3AF;
	public static int MUTED                  = 0xFF6B7280;
	public static int DARK                   = 0xFF2A2A2D;  
	public static int ACCENT                 = 0xFF3B82F6;
	public static int ACCENT_BLUE            = 0xFF3B82F6;
	public static int BAR_ON                 = 0xFF3B82F6;

	// ----------- Transparent -----------
	public static int TRANSPARENT            = 0x00000000;

	// ---------- Utilities ----------
	public static int withAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | (alpha << 24);
	}

	public static int withAlphaFloat(int color, float alphaRatio) {
		int alpha = Math.max(0, Math.min(255, (int) (255 * alphaRatio)));
		return withAlpha(color, alpha);
	}

	public static boolean isLightColor(int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0;
		return luminance > 0.55;
	}

	public static int getContrastText(int bgColor) {
		return isLightColor(bgColor) ? 0xFF121215 : 0xFFFFFFFF;
	}

	public static int getContrastDivider(int bgColor) {
		return isLightColor(bgColor) ? 0x33000000 : 0x55FFFFFF;
	}

}