package silence.simsool.lucent.ui.utils;

public class UIColors {

	// ---------- White → Black ----------
	public static final int PURE_WHITE = 0xFFFFFFFF;
	public static final int WHITE = 0xFFECECEC;
	public static final int DARK_WHITE = 0xFFF2F2F2;

	public static final int LIGHT_GRAY = 0xFFE1E1E1;
	public static final int SOFT_GRAY = 0xFFCFCFCF;
	public static final int GRAY = 0xFFADADAD;
	public static final int DIM_GRAY = 0xFF909090;
	public static final int MUTED = 0xFF757575;
	public static final int DARK_GRAY = 0xFF5A5A5A;

	public static final int DARK = 0xFF424242;
	public static final int VERY_DARK = 0xFF333333;
	public static final int DEEP_DARK = 0xFF2A2A2A;
	public static final int EXTRA_DARK = 0xFF1F1F1F;

	public static final int LIGHT_BLACK = 0xFF181818;
	public static final int BLACK = 0xFF0E0E0E;
	public static final int PURE_BLACK = 0xFF000000;

	// ---------- Background / Surface ----------
	public static final int BACKGROUND_LIGHT = 0xFFF8F9FA;
	public static final int BACKGROUND = 0xFFF1F3F5;
	public static final int SURFACE = 0xFFFFFFFF;

	// ---------- Border ----------
	public static final int BORDER_LIGHT = 0xFFE5E7EB;
	public static final int BORDER = 0xFFD1D5DB;
	public static final int BORDER_STRONG = 0xFF9CA3AF;

	// ---------- Text ----------
	public static final int TEXT_PRIMARY = 0xFF111827;
	public static final int TEXT_SECONDARY = 0xFF6B7280;
	public static final int TEXT_MUTED = 0xFF9CA3AF;
	public static final int TEXT_DISABLED = 0xFFD1D5DB;

	// ---------- Primary ----------
	public static final int PRIMARY_LIGHT = 0xFF60A5FA;
	public static final int PRIMARY = 0xFF3B82F6;
	public static final int PRIMARY_DARK = 0xFF1D4ED8;

	// ---------- Secondary ----------
	public static final int SECONDARY_LIGHT = 0xFFA78BFA;
	public static final int SECONDARY = 0xFF8B5CF6;
	public static final int SECONDARY_DARK = 0xFF6D28D9;

	// ---------- Success ----------
	public static final int SUCCESS_LIGHT = 0xFF4ADE80;
	public static final int SUCCESS = 0xFF22C55E;
	public static final int SUCCESS_DARK = 0xFF15803D;

	// ---------- Warning ----------
	public static final int WARNING_LIGHT = 0xFFFBBF24;
	public static final int WARNING = 0xFFF59E0B;
	public static final int WARNING_DARK = 0xFFD97706;

	// ---------- Error ----------
	public static final int ERROR_LIGHT = 0xFFF87171;
	public static final int ERROR = 0xFFEF4444;
	public static final int ERROR_DARK = 0xFFB91C1C;

	// ---------- Info ----------
	public static final int INFO_LIGHT = 0xFF22D3EE;
	public static final int INFO = 0xFF06B6D4;
	public static final int INFO_DARK = 0xFF0E7490;

	// ---------- Accent Colors ----------
	public static final int ACCENT_BLUE = 0xFF3B82F6;
	public static final int ACCENT_PURPLE = 0xFF8B5CF6;
	public static final int ACCENT_PINK = 0xFFEC4899;
	public static final int ACCENT_ORANGE = 0xFFF97316;
	public static final int ACCENT_TEAL = 0xFF14B8A6;
	public static final int ACCENT_LIME = 0xFF84CC16;

	// ---------- Theme : Stormy Morning ----------
	public static final int STORMY_MORNING_EXTRALIGHT = 0xFFBDDDFC;
	public static final int STORMY_MORNING_LIGHT = 0xFF88BDF2;
	public static final int STORMY_MORNING = 0xFF6A89A7;
	public static final int STORMY_MORNING_DARK = 0xFF384959;

	// ---------- Theme : Blue Eclipse ----------
	public static final int BLUE_ECLIPSE_EXTRALIGHT = 0xFF8686AC;
	public static final int BLUE_ECLIPSE_LIGHT = 0xFF505081;
	public static final int BLUE_ECLIPSE = 0xFF272757;
	public static final int BLUE_ECLIPSE_DARK = 0xFF0F0E47;

	/**
	 * Text color levels from pure white to pure black, with 14 distinct shades in between.
	 * 
	 * @param level 0(Pure White) ~ 13(Pure Black)
	 */
	public static int get(int level) {
		return switch (level) {
			case 0 -> PURE_WHITE;
			case 1 -> DARK_WHITE;
			case 2 -> LIGHT_GRAY;
			case 3 -> SOFT_GRAY;
			case 4 -> GRAY;
			case 5 -> DIM_GRAY;
			case 6 -> MUTED;
			case 7 -> DARK_GRAY;
			case 8 -> DARK;
			case 9 -> VERY_DARK;
			case 10 -> DEEP_DARK;
			case 11 -> EXTRA_DARK;
			case 12 -> BLACK;
			case 13 -> PURE_BLACK;
			default -> PURE_WHITE;
		};
	}

}