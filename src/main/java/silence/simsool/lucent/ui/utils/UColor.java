package silence.simsool.lucent.ui.utils;

public class UColor {

	public static int argb(int a, int r, int g, int b) {
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}

	public static int rgb(int r, int g, int b) {
		return argb(255, r, g, b);
	}

	public static int withAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
	}

	public static int withAlphaF(int color, float alpha) {
		return withAlpha(color, (int)(alpha * 255));
	}

	public static int getAlpha(int color) {
		return (color >> 24) & 0xFF;
	}

	public static int getRed(int color) {
		return (color >> 16) & 0xFF;
	}

	public static int getGreen(int color) {
		return (color >> 8) & 0xFF;
	}

	public static int getBlue(int color) {
		return color & 0xFF;
	}

	public static float getAlphaF(int color) {
		return getAlpha(color) / 255f;
	}

	public static float getRedF(int color) {
		return getRed(color) / 255f;
	}

	public static float getGreenF(int color) {
		return getGreen(color) / 255f;
	}

	public static float getBlueF(int color) {
		return getBlue(color) / 255f;
	}

	public static int lerpColor(int c1, int c2, float t) {
		t = Math.max(0, Math.min(1, t));
		int a = (int)(getAlpha(c1) + (getAlpha(c2) - getAlpha(c1)) * t);
		int r = (int)(getRed(c1)   + (getRed(c2)   - getRed(c1))   * t);
		int g = (int)(getGreen(c1) + (getGreen(c2) - getGreen(c1)) * t);
		int b = (int)(getBlue(c1)  + (getBlue(c2)  - getBlue(c1))  * t);
		return argb(a, r, g, b);
	}

	public static int darken(int color, float amount) {
		int r = (int)(getRed(color)   * (1 - amount));
		int g = (int)(getGreen(color) * (1 - amount));
		int b = (int)(getBlue(color)  * (1 - amount));
		return argb(getAlpha(color), r, g, b);
	}

	public static int lighten(int color, float amount) {
		int r = getRed(color)   + (int)((255 - getRed(color))   * amount);
		int g = getGreen(color) + (int)((255 - getGreen(color)) * amount);
		int b = getBlue(color)  + (int)((255 - getBlue(color))  * amount);
		return argb(getAlpha(color), r, g, b);
	}
	
	public static int brighten(int color, float amount) {
		return lighten(color, amount);
	}

	public static float[] toHSV(int color) {
		float r = getRed(color)   / 255f;
		float g = getGreen(color) / 255f;
		float b = getBlue(color)  / 255f;

		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));
		float delta = max - min;

		float h = 0, s = 0, v = max;

		if (delta > 0.00001f) {
			s = delta / max;
			if (max == r) h = 60f * (((g - b) / delta) % 6);
			else if (max == g) h = 60f * (((b - r) / delta) + 2);
			else h = 60f * (((r - g) / delta) + 4);
			if (h < 0) h += 360f;
		}

		return new float[]{h, s, v};
	}

	public static int fromHSV(float h, float s, float v) {
		return fromHSVA(h, s, v, 1.0f);
	}

	public static int fromHSVA(float h, float s, float v, float a) {
		h = ((h % 360) + 360) % 360;
		int hi = (int)(h / 60) % 6;
		float f = h / 60 - (int)(h / 60);

		float p = v * (1 - s);
		float q = v * (1 - f * s);
		float t = v * (1 - (1 - f) * s);

		float r, g, b;
		switch (hi) {
			case 0 -> { r = v; g = t; b = p; }
			case 1 -> { r = q; g = v; b = p; }
			case 2 -> { r = p; g = v; b = t; }
			case 3 -> { r = p; g = q; b = v; }
			case 4 -> { r = t; g = p; b = v; }
			default -> { r = v; g = p; b = q; }
		}

		return argb((int)(a * 255), (int)(r * 255), (int)(g * 255), (int)(b * 255));
	}

	/** ARGB int → "#RRGGBBAA" */
	public static String toHex(int color) {
		return String.format("#%02X%02X%02X%02X",
			getRed(color), getGreen(color), getBlue(color), getAlpha(color));
	}

	/** "#RRGGBB", "#RRGGBBAA", "RRGGBB", "RRGGBBAA" 모두 처리 */
	public static int fromHex(String hex) {
		hex = hex.trim().replace("#", "");
		if (hex.length() == 6) hex = "FF" + hex; // 알파 없으면 불투명으로
		else if (hex.length() == 8) hex = hex.substring(6, 8) + hex.substring(0, 6); // RRGGBBAA → AARRGGBB
		else throw new IllegalArgumentException("Invalid hex color: " + hex);
		return (int) Long.parseLong(hex, 16);
	}

	/** Hex 파싱 실패시 fallback 반환 */
	public static int fromHexSafe(String hex, int fallback) {
		try {
			return fromHex(hex);
		} catch (Exception e) {
			return fallback;
		}
	}

}