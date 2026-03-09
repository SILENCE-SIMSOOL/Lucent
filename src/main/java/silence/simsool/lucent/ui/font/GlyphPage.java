package silence.simsool.lucent.ui.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.UUID;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import static silence.simsool.lucent.Lucent.mc;

public class GlyphPage {
	private final int imgSize = 512;
	private final int[] glyphX      = new int[256];
	private final int[] glyphY      = new int[256];
	private final int[] glyphWidth  = new int[256];
	private final int[] glyphHeight = new int[256];

	private Identifier textureId;
	private Font font;
	private boolean antiAliasing;
	private boolean generated = false;
	private int maxFontHeight = 0;

	public GlyphPage(Font font, boolean antiAliasing, int index) {
		this.font         = font;
		this.antiAliasing = antiAliasing;
		generatePage(index);
	}

	private void generatePage(int index) {
		BufferedImage img = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();

		g.setFont(font);
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, imgSize, imgSize);
		g.setColor(Color.WHITE);

		if (antiAliasing) {
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,  RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,          RenderingHints.VALUE_RENDER_QUALITY);
		}

		FontMetrics fm = g.getFontMetrics();
		int charHeight = fm.getHeight();
		if (charHeight <= 0) charHeight = font.getSize();
		this.maxFontHeight = charHeight;

		int posX = 0, posY = 0;

		for (int i = 0; i < 256; i++) {
			char c = (char) ((index << 8) + i);
			if (!font.canDisplay(c)) continue;

			Rectangle2D bounds = fm.getStringBounds(Character.toString(c), g);
			int width  = (int) Math.ceil(bounds.getWidth()) + 2;
			int height = charHeight;

			if (posX + width >= imgSize) {
				posX = 0;
				posY += charHeight + 2;
				if (posY + charHeight >= imgSize) break; // 공간 부족
			}

			g.drawString(Character.toString(c), posX, posY + fm.getAscent());

			glyphX[i]      = posX;
			glyphY[i]      = posY;
			glyphWidth[i]  = width;
			glyphHeight[i] = height;

			posX += width + 2;
		}
		g.dispose();

		NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, imgSize, imgSize, true);
		for (int y = 0; y < imgSize; y++) {
			for (int x = 0; x < imgSize; x++) {
				int argb = img.getRGB(x, y);
				int a = (argb >> 24) & 0xFF;
				int r = (argb >> 16) & 0xFF;
				int gc = (argb >> 8) & 0xFF;
				int b  =  argb       & 0xFF;
				// ABGR = (A<<24)|(B<<16)|(G<<8)|R
				int abgr = (a << 24) | (b << 16) | (gc << 8) | r;
				nativeImage.setPixelABGR(x, y, abgr);
			}
		}

		String name = "lucent_font_" + UUID.randomUUID().toString().replace("-", "");
		this.textureId = Identifier.fromNamespaceAndPath("lucent", name);

		DynamicTexture texture = new DynamicTexture(() -> this.textureId.toString(), nativeImage);
		mc.getTextureManager().register(this.textureId, texture);

		this.generated = true;
	}

	public Identifier getTextureId() {
		return textureId;
	}

	public boolean isGenerated() {
		return generated;
	}

	public float getWidth(char ch) {
		return glyphWidth[ch & 0xFF];
	}

	public float getHeight(char ch) {
		return glyphHeight[ch & 0xFF];
	}

	public float getX(char ch) {
		return glyphX[ch & 0xFF];
	}

	public float getY(char ch) {
		return glyphY[ch & 0xFF];
	}

	public int getMaxFontHeight() {
		return maxFontHeight;
	}

	public int getImgSize() {
		return imgSize;
	}
}