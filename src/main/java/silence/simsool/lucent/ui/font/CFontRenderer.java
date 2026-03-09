package silence.simsool.lucent.ui.font;

import java.awt.Font;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

public class CFontRenderer {
	private final Font font;
	private final boolean antiAliasing;
	private final GlyphPage[] pages = new GlyphPage[256];
	private final int[] colorCodes = new int[32];

	public CFontRenderer(Font font, boolean antiAliasing) {
		this.font = font;
		this.antiAliasing = antiAliasing;
		setupColorCodes();
	}

	private void setupColorCodes() {
		for (int i = 0; i < 32; i++) {
			int no = (i >> 3 & 1) * 85;
			int r  = (i >> 2 & 1) * 170 + no;
			int g  = (i >> 1 & 1) * 170 + no;
			int b  = (i      & 1) * 170 + no;
			if (i == 6) r += 85;
			if (i >= 16) { r /= 4; g /= 4; b /= 4; }
			colorCodes[i] = (r & 255) << 16 | (g & 255) << 8 | (b & 255);
		}
	}

	private GlyphPage getPage(char ch) {
		int index = ch >> 8;
		if (pages[index] == null) pages[index] = new GlyphPage(font, antiAliasing, index);
		return pages[index];
	}

	/**
	 * 문자열을 그린다.
	 * @param ctx      GuiGraphics 컨텍스트
	 * @param text     출력할 문자열 (§ 색상 코드 지원)
	 * @param x        왼쪽 기준 X
	 * @param y        위쪽 기준 Y
	 * @param color    기본 ARGB 색상
	 * @param shadow   그림자 여부
	 * @param size     폰트 크기 (px)
	 * @return 실제 렌더된 문자열 폭 (px)
	 */
	public int drawString(GuiGraphics ctx, String text, float x, float y, int color, boolean shadow, float size) {
		if (text == null) return 0;

		int baseHeight = getPage('A').getMaxFontHeight();
		if (baseHeight == 0) baseHeight = 11;
		float scale = size / (float) baseHeight;

		ctx.pose().pushMatrix();
		ctx.pose().translate(x, y);
		ctx.pose().scale(scale, scale);

		int r = 0;
		if (shadow) {
			int shadowColor = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
			renderString(ctx, text, 1f, 1f, shadowColor);
		}
		r = renderString(ctx, text, 0f, 0f, color);

		ctx.pose().popMatrix();
		return (int) (r * scale);
	}

	private int renderString(GuiGraphics ctx, String text, float startX, float startY, int color) {
		float posX = startX;

		int r = (color >> 16) & 0xFF;
		int g = (color >>  8) & 0xFF;
		int b =  color        & 0xFF;
		int a = (color >> 24) & 0xFF;
		if (a <= 5) a = 255;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			// § 색상 코드 파싱
			if (c == '§' && i + 1 < text.length()) {
				int colorIndex = "0123456789abcdefklmnor"
						.indexOf(String.valueOf(text.charAt(i + 1)).toLowerCase());
				if (colorIndex >= 0 && colorIndex < 16) {
					int codeColor = colorCodes[colorIndex];
					r = (codeColor >> 16) & 0xFF;
					g = (codeColor >>  8) & 0xFF;
					b =  codeColor        & 0xFF;
				} else if (colorIndex == 21) {
					r = (color >> 16) & 0xFF;
					g = (color >>  8) & 0xFF;
					b =  color        & 0xFF;
				}
				i++;
				continue;
			}

			GlyphPage page = getPage(c);
			float charW = page.getWidth(c);
			float charH = page.getHeight(c);
			float srcX  = page.getX(c);
			float srcY  = page.getY(c);
			float imgSz = page.getImgSize();

			if (charW <= 0 || charH <= 0) continue;

			int packedColor = (a << 24) | (r << 16) | (g << 8) | b;

			ctx.blit(
				RenderPipelines.GUI_TEXTURED,
				page.getTextureId(),
				(int) posX, (int) startY,
				srcX, srcY,
				(int) Math.ceil(charW), (int) Math.ceil(charH),
				(int) imgSz, (int) imgSz,
				packedColor
			);

			posX += charW - 1f;
		}

		return (int) posX;
	}

	public int getStringWidth(String text, float size) {
		if (text == null) return 0;
		float width = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == '§' && i + 1 < text.length()) { i++; continue; }
			width += getPage(c).getWidth(c) - 1f;
		}
		int baseHeight = getPage('A').getMaxFontHeight();
		if (baseHeight == 0) baseHeight = 11;
		float scale = size / (float) baseHeight;
		return (int) (width * scale);
	}

	public float getFontHeight(float size) {
		int baseHeight = getPage('A').getMaxFontHeight();
		if (baseHeight == 0) baseHeight = 11;
		return size;
	}
}