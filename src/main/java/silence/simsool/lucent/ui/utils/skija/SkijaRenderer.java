package silence.simsool.lucent.ui.utils.skija;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.FilterBlurMode;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.FontEdging;
import io.github.humbleui.skija.FontHinting;
import io.github.humbleui.skija.FontMetrics;
import io.github.humbleui.skija.FontMgr;
import io.github.humbleui.skija.FontStyle;
import io.github.humbleui.skija.MaskFilter;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.PaintMode;
import io.github.humbleui.skija.Path;
import io.github.humbleui.skija.PathBuilder;
import io.github.humbleui.skija.SamplingMode;
import io.github.humbleui.skija.Shader;
import io.github.humbleui.skija.Typeface;
import io.github.humbleui.skija.svg.SVGDOM;
import io.github.humbleui.types.Point;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import silence.simsool.lucent.general.enums.Direction;
import silence.simsool.lucent.general.enums.GradientType;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.skija.compositor.SkijaCompositor;
import silence.simsool.lucent.ui.font.LucentFont;
import silence.simsool.lucent.ui.utils.UIColors;

public class SkijaRenderer {

	private static final Map<String, Image> imageCache = new ConcurrentHashMap<>();
	private static final Map<Long, Font> fontCache = new ConcurrentHashMap<>();
	private static final Map<Float, MaskFilter> blurMaskCache = new ConcurrentHashMap<>();

	private static final ThreadLocal<Paint> RENDER_PAINT = ThreadLocal.withInitial(Paint::new);
	private static final ThreadLocal<Paint> SHADOW_PAINT = ThreadLocal.withInitial(Paint::new);

	private static float currentAlpha = 1f;
	private static final Deque<Float> alphaStack = new ArrayDeque<>();

	public static void draw(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Runnable renderContent) {
		if (renderContent == null) return;
		renderContent.run();
		if (graphics != null && graphics.guiRenderState != null) {
			SkijaCompositor.INSTANCE.markScreenLayer(graphics.guiRenderState);
		}
	}

	private static void enqueue(Consumer<Canvas> op) {
		// float alpha = currentAlpha;
		SkijaCompositor.INSTANCE.enqueue(canvas -> {
			op.accept(canvas);
		});
	}

	public static void push() {
		alphaStack.addLast(currentAlpha);
		enqueue(Canvas::save);
	}

	public static void pop() {
		if (!alphaStack.isEmpty()) {
			currentAlpha = alphaStack.removeLast();
		} else {
			currentAlpha = 1f;
		}
		enqueue(Canvas::restore);
	}

	public static void scale(float x, float y) {
		enqueue(canvas -> canvas.scale(x, y));
	}

	public static void translate(float x, float y) {
		enqueue(canvas -> canvas.translate(x, y));
	}

	public static void rotate(float amount) {
		float degrees = (float) Math.toDegrees(amount);
		enqueue(canvas -> canvas.rotate(degrees));
	}

	public static void globalAlpha(float amount) {
		currentAlpha = Math.max(0f, Math.min(1f, amount));
	}

	public static void pushScissor(float x, float y, float w, float h) {
		enqueue(canvas -> {
			canvas.save();
			canvas.clipRect(Rect.makeXYWH(x, y, w, h));
		});
	}

	public static void popScissor() {
		enqueue(Canvas::restore);
	}

	public static void line(float x1, float y1, float x2, float y2, float thickness, int color) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setMode(PaintMode.STROKE);
			paint.setStrokeWidth(thickness);
			paint.setColor(argb);
			paint.setAntiAlias(true);
			canvas.drawLine(x1, y1, x2, y2, paint);
		});
	}

	public static void rect(float x, float y, float w, float h, int color) {
		rect(x, y, w, h, color, 0f);
	}

	public static void rect(float x, float y, float w, float h, int color, float radius) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setColor(argb);
			paint.setAntiAlias(true);
			if (radius <= 0f) canvas.drawRect(Rect.makeXYWH(x, y, w, h), paint);
			else canvas.drawRRect(RRect.makeXYWH(x, y, w, h, radius), paint);
		});
	}

	public static void rect(float x, float y, float w, float h, int color, float r1, float r2, float r3, float r4) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setColor(argb);
			paint.setAntiAlias(true);
			float[] radii = new float[]{r1, r2, r3, r4};
			canvas.drawRRect(RRect.makeComplexXYWH(x, y, w, h, radii), paint);
		});
	}

	public static void outlineRect(float x, float y, float w, float h, float thickness, int color) {
		outlineRect(x, y, w, h, thickness, color, 0f);
	}

	public static void outlineRect(float x, float y, float w, float h, float thickness, int color, float radius) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setMode(PaintMode.STROKE);
			paint.setStrokeWidth(thickness);
			paint.setColor(argb);
			paint.setAntiAlias(true);
			if (radius <= 0f) canvas.drawRect(Rect.makeXYWH(x, y, w, h), paint);
			else canvas.drawRRect(RRect.makeXYWH(x, y, w, h, radius), paint);
		});
	}

	public static void outlineRect(float x, float y, float w, float h, float thickness, int color, float r1, float r2, float r3, float r4) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setMode(PaintMode.STROKE);
			paint.setStrokeWidth(thickness);
			paint.setColor(argb);
			paint.setAntiAlias(true);
			float[] radii = new float[]{r1, r2, r3, r4};
			canvas.drawRRect(RRect.makeComplexXYWH(x, y, w, h, radii), paint);
		});
	}

	public static void gradientRect(float x, float y, float w, float h, int color1, int color2, GradientType gradient) {
		gradientRect(x, y, w, h, color1, color2, gradient, 0f);
	}

	public static void gradientRect(float x, float y, float w, float h, int color1, int color2, GradientType gradient, float radius) {
		gradientRect(x, y, w, h, color1, color2, gradient, radius, radius, radius, radius);
	}

	public static void gradientRect(float x, float y, float w, float h, int color1, int color2, GradientType gradient, float r1, float r2, float r3, float r4) {
		int c1 = applyAlpha(color1, currentAlpha);
		int c2 = applyAlpha(color2, currentAlpha);
		enqueue(canvas -> {
			float x0 = x, y0 = y;
			float x1 = (gradient == GradientType.LEFT_TO_RIGHT) ? x + w : x;
			float y1 = (gradient == GradientType.TOP_TO_BOTTOM) ? y + h : y;

			try (Shader shader = Shader.makeLinearGradient(x0, y0, x1, y1, new int[]{c1, c2})) {
				Paint paint = RENDER_PAINT.get();
				paint.reset();
				paint.setShader(shader);
				paint.setAntiAlias(true);
				if (r1 <= 0 && r2 <= 0 && r3 <= 0 && r4 <= 0) canvas.drawRect(Rect.makeXYWH(x, y, w, h), paint);
				else canvas.drawRRect(RRect.makeComplexXYWH(x, y, w, h, new float[]{r1, r2, r3, r4}), paint);
			}
		});
	}

	public static void dropShadow(float x, float y, float width, float height, float blur, float spread) {
		dropShadow(x, y, width, height, blur, spread, 0f);
	}

	public static void dropShadow(float x, float y, float width, float height, float blur, float spread, float radius) {
		dropShadow(x, y, width, height, blur, spread, radius, radius, radius, radius);
	}

	public static void dropShadow(float x, float y, float width, float height, float blur, float spread, float r1, float r2, float r3, float r4) {
		int shadowColor = applyAlpha(new Color(0, 0, 0, 120).getRGB(), currentAlpha);
		float sigma = Math.max(0.1f, blur / 2f);
		float sx = x - spread;
		float sy = y - spread;
		float sw = width + spread * 2f;
		float sh = height + spread * 2f;

		enqueue(canvas -> {
			MaskFilter mask = blurMaskCache.computeIfAbsent(sigma, s -> MaskFilter.makeBlur(FilterBlurMode.NORMAL, s));
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setColor(shadowColor);
			paint.setMaskFilter(mask);
			paint.setAntiAlias(true);
			if (r1 <= 0 && r2 <= 0 && r3 <= 0 && r4 <= 0) canvas.drawRect(Rect.makeXYWH(sx, sy, sw, sh), paint);
			else canvas.drawRRect(RRect.makeComplexXYWH(sx, sy, sw, sh, new float[]{r1, r2, r3, r4}), paint);
		});
	}

	public static void circle(float x, float y, float radius, int color) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setColor(argb);
			paint.setAntiAlias(true);
			canvas.drawCircle(x, y, radius, paint);
		});
	}

	public static void outlineCircle(float x, float y, float radius, float thickness, int color) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setMode(PaintMode.STROKE);
			paint.setStrokeWidth(thickness);
			paint.setColor(argb);
			paint.setAntiAlias(true);
			canvas.drawCircle(x, y, radius, paint);
		});
	}

	public static void triangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			try (PathBuilder pb = new PathBuilder()) {
				pb.moveTo(x1, y1);
				pb.lineTo(x2, y2);
				pb.lineTo(x3, y3);
				pb.closePath();
				try (Path path = pb.build()) {
					Paint paint = RENDER_PAINT.get();
					paint.reset();
					paint.setColor(argb);
					paint.setAntiAlias(true);
					canvas.drawPath(path, paint);
				}
			}
		});
	}

	public static void outlineTriangle(float x1, float y1, float x2, float y2, float x3, float y3, float thickness, int color) {
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			try (PathBuilder pb = new PathBuilder()) {
				pb.moveTo(x1, y1);
				pb.lineTo(x2, y2);
				pb.lineTo(x3, y3);
				pb.closePath();
				try (Path path = pb.build()) {
					Paint paint = RENDER_PAINT.get();
					paint.reset();
					paint.setMode(PaintMode.STROKE);
					paint.setStrokeWidth(thickness);
					paint.setColor(argb);
					paint.setAntiAlias(true);
					canvas.drawPath(path, paint);
				}
			}
		});
	}

	public static void arrowTriangle(float cx, float cy, float w, float h, Direction direction, int color) {
		float halfW = w / 2f;
		float halfH = h / 2f;
		switch (direction) {
			case UP -> triangle(cx, cy - halfH, cx - halfW, cy + halfH, cx + halfW, cy + halfH, color);
			case DOWN -> triangle(cx, cy + halfH, cx - halfW, cy - halfH, cx + halfW, cy - halfH, color);
			case LEFT -> triangle(cx - halfW, cy, cx + halfW, cy - halfH, cx + halfW, cy + halfH, color);
			case RIGHT -> triangle(cx + halfW, cy, cx - halfW, cy - halfH, cx - halfW, cy + halfH, color);
		}
	}

	public static void drawCheckerboard(float x, float y, float w, float h, float radius) {
		drawCheckerboard(x, y, w, h, 6f, 0xFFCCCCCC, 0xFFFFFFFF, radius);
	}

	public static void drawCheckerboard(float x, float y, float w, float h, float gridSize, int color1, int color2, float radius) {
		int argb1 = applyAlpha(color1, currentAlpha);
		int argb2 = applyAlpha(color2, currentAlpha);
		enqueue(canvas -> {
			int save = canvas.save();
			if (radius > 0) canvas.clipRRect(RRect.makeXYWH(x, y, w, h, radius), true);
			else canvas.clipRect(Rect.makeXYWH(x, y, w, h));
			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setAntiAlias(true);
			paint.setColor(argb2);
			if (radius > 0) canvas.drawRRect(RRect.makeXYWH(x, y, w, h, radius), paint);
			else canvas.drawRect(Rect.makeXYWH(x, y, w, h), paint);
			paint.setColor(argb1);
			int cols = (int) Math.ceil(w / gridSize);
			int rows = (int) Math.ceil(h / gridSize);
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					if ((row + col) % 2 == 0) {
						canvas.drawRect(Rect.makeXYWH(x + col * gridSize, y + row * gridSize, gridSize, gridSize), paint);
					}
				}
			}
			canvas.restoreToCount(save);
		});
	}

	public static void text(String text, float x, float y, int color, float size) {
		text(text, x, y, Fonts.PRETENDARD, color, size, false);
	}

	public static void text(String text, float x, float y, LucentFont font, float size) {
		text(text, x, y, font, UIColors.PURE_WHITE, size, false);
	}

	public static void text(String text, float x, float y, LucentFont font, int color, float size) {
		text(text, x, y, font, color, size, false);
	}

	public static void text(String text, float x, float y, int color, float size, boolean shadow) {
		text(text, x, y, Fonts.PRETENDARD, color, size, shadow);
	}

	public static void text(String text, float x, float y, LucentFont font, float size, boolean shadow) {
		text(text, x, y, font, UIColors.PURE_WHITE, size, shadow);
	}

	public static void text(String text, float x, float y, LucentFont font, int color, float size, boolean shadow) {
		if (text == null || text.isEmpty()) return;
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Font skijaFont = getOrCreateFont(font, size);
			FontMetrics metrics = skijaFont.getMetrics();
			float asc = -metrics.getAscent();
			float desc = metrics.getDescent();
			float total = asc + desc;
			float baselineY = (total > 0) ? (y + size * (asc / total)) : (y + asc);

			if (shadow) {
				int shadowColor = applyAlpha(0x80000000, currentAlpha);
				Paint shadowPaint = SHADOW_PAINT.get();
				shadowPaint.reset();
				shadowPaint.setColor(shadowColor);
				shadowPaint.setAntiAlias(true);
				canvas.drawString(text, x + 0.75f, baselineY + 0.75f, skijaFont, shadowPaint);
			}

			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setColor(argb);
			paint.setAntiAlias(true);
			canvas.drawString(text, x, baselineY, skijaFont, paint);
		});
	}

	public static void centerText(String text, float x, float y, LucentFont font, int color, float size) {
		centerText(text, x, y, font, color, size, false);
	}

	public static void centerText(String text, float x, float y, LucentFont font, int color, float size, boolean shadow) {
		float width = textWidth(text, font, size);
		text(text, x - width / 2f, y, font, color, size, shadow);
	}

	public static void textShadow(String text, float x, float y, int color, float size) {
		text(text, x, y, Fonts.PRETENDARD, color, size, true);
	}

	public static void textShadow(String text, float x, float y, LucentFont font, float size) {
		text(text, x, y, font, UIColors.PURE_WHITE, size, true);
	}

	public static void textShadow(String text, float x, float y, LucentFont font, int color, float size) {
		text(text, x, y, font, color, size, true);
	}

	public static void centerTextShadow(String text, float x, float y, LucentFont font, int color, float size) {
		centerText(text, x, y, font, color, size, true);
	}

	public static float textWidth(String text, LucentFont font, float size) {
		if (text == null || text.isEmpty()) return 0f;
		Font skijaFont = getOrCreateFont(font, size);
		return skijaFont.measureTextWidth(text);
	}

	private static List<String> wrapText(String text, Font font, float maxWidth) {
		List<String> lines = new ArrayList<>();
		if (text == null || text.isEmpty()) return lines;

		String[] rawLines = text.split("\n", -1);
		for (String rawLine : rawLines) {
			if (rawLine.isEmpty()) {
				lines.add("");
				continue;
			}
			if (font.measureTextWidth(rawLine) <= maxWidth) {
				lines.add(rawLine);
				continue;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < rawLine.length(); i++) {
				char c = rawLine.charAt(i);
				sb.append(c);
				if (font.measureTextWidth(sb.toString()) > maxWidth) {
					if (sb.length() > 1) {
						sb.deleteCharAt(sb.length() - 1);
						lines.add(sb.toString());
						sb.setLength(0);
						sb.append(c);
					}
					else {
						lines.add(sb.toString());
						sb.setLength(0);
					}
				}
			}
			if (sb.length() > 0) {
				lines.add(sb.toString());
			}
		}
		return lines;
	}

	public static void drawWrappedString(String text, float x, float y, float w, LucentFont font, float size, int color, float lineHeight) {
		if (text == null || text.isEmpty()) return;
		int argb = applyAlpha(color, currentAlpha);
		enqueue(canvas -> {
			Font skijaFont = getOrCreateFont(font, size);
			FontMetrics metrics = skijaFont.getMetrics();
			float asc = -metrics.getAscent();
			float desc = metrics.getDescent();
			float total = asc + desc;
			float baselineY = (total > 0) ? (y + size * (asc / total)) : (y + asc);
			float lineGap = size * lineHeight;
			List<String> lines = wrapText(text, skijaFont, w);

			Paint paint = RENDER_PAINT.get();
			paint.reset();
			paint.setColor(argb);
			paint.setAntiAlias(true);
			float curY = baselineY;
			for (String line : lines) {
				canvas.drawString(line, x, curY, skijaFont, paint);
				curY += lineGap;
			}
		});
	}

	public static void drawWrappedString(String text, float x, float y, float w, LucentFont font, float size, int color) {
		drawWrappedString(text, x, y, w, font, size, color, 1.2f);
	}

	public static float[] wrappedTextBounds(String text, float w, LucentFont font, float size, float lineHeight) {
		if (text == null || text.isEmpty()) return new float[]{0f, 0f, 0f, 0f};
		Font skijaFont = getOrCreateFont(font, size);
		List<String> lines = wrapText(text, skijaFont, w);
		float maxLineWidth = 0f;
		for (String line : lines) {
			float lw = skijaFont.measureTextWidth(line);
			if (lw > maxLineWidth) maxLineWidth = lw;
		}
		float totalHeight = lines.size() * size * lineHeight;
		return new float[]{0f, 0f, maxLineWidth, totalHeight};
	}

	public static float[] wrappedTextBounds(String text, float w, LucentFont font, float size) {
		return wrappedTextBounds(text, w, font, size, 1.2f);
	}

	public static Image createImage(String resourcePath) throws Exception {
		return imageCache.computeIfAbsent(resourcePath, path -> {
			try {
				return new Image(path);
			} catch (Exception e) {
				throw new RuntimeException("Failed to load image: " + path, e);
			}
		});
	}

	public static void deleteImage(Image image) {
		if (image != null) {
			imageCache.remove(image.identifier);
			image.close();
		}
	}

	public static void image(Image image, float x, float y, float size) {
		image(image, x, y, size, size, 0f);
	}

	public static void image(Image image, float x, float y, float w, float h) {
		image(image, x, y, w, h, 0f);
	}

	public static void image(Image image, float x, float y, float w, float h, float radius) {
		image(image, x, y, w, h, radius, 1f);
	}

	public static void image(Image image, float x, float y, float w, float h, float radius, float alpha) {
		if (image == null) return;
		float combinedAlpha = currentAlpha * alpha;
		enqueue(canvas -> {
			if (image.isSVG) {
				SVGDOM dom = image.getSvgDom();
				if (dom != null) {
					canvas.save();
					if (radius > 0f) {
						canvas.clipRRect(RRect.makeXYWH(x, y, w, h, radius), true);
					}
					canvas.translate(x, y);
					dom.setContainerSize(new Point(w, h));
					dom.render(canvas);
					canvas.restore();
				}
			}
			else {
				io.github.humbleui.skija.Image skImage = image.getSkijaImage();
				if (skImage != null) {
					int imgAlpha = (int) (combinedAlpha * 255);
					Paint paint = RENDER_PAINT.get();
					paint.reset();
					paint.setAlpha(Math.max(0, Math.min(255, imgAlpha)));
					paint.setAntiAlias(true);
					canvas.save();
					if (radius > 0f) {
						canvas.clipRRect(RRect.makeXYWH(x, y, w, h, radius), true);
					}
					canvas.drawImageRect(skImage, Rect.makeXYWH(0, 0, skImage.getWidth(), skImage.getHeight()), Rect.makeXYWH(x, y, w, h), SamplingMode.LINEAR, paint, true);
					canvas.restore();
				}
			}
		});
	}

	public static void image(Image image, int textureWidth, int textureHeight, int subX, int subY, int subW, int subH, float x, float y, float w, float h, float radius) {
		if (image == null) return;
		enqueue(canvas -> {
			io.github.humbleui.skija.Image skImage = image.getSkijaImage();
			if (skImage != null) {
				Paint paint = RENDER_PAINT.get();
				paint.reset();
				paint.setAlpha(Math.max(0, Math.min(255, (int) (currentAlpha * 255))));
				paint.setAntiAlias(true);
				canvas.save();
				if (radius > 0f) {
					canvas.clipRRect(RRect.makeXYWH(x, y, w, h, radius), true);
				}
				canvas.drawImageRect(skImage, Rect.makeXYWH(subX, subY, subW, subH), Rect.makeXYWH(x, y, w, h), SamplingMode.LINEAR, paint, true);
				canvas.restore();
			}
		});
	}

	private static Font getOrCreateFont(LucentFont lucentFont, float size) {
		Typeface typeface = Fonts.getTypeface(lucentFont);
		if (typeface == null) {
			typeface = FontMgr.getDefault().matchFamilyStyle(null, FontStyle.NORMAL);
		}
		long key = (((long) typeface.hashCode()) << 32) | (Float.floatToIntBits(size) & 0xFFFFFFFFL);
		Typeface finalTf = typeface;

		return fontCache.computeIfAbsent(key, k -> {
			Font font = new Font(finalTf, size);
			font.setHinting(FontHinting.NONE);
			font.setSubpixel(true);
			font.setBaselineSnapped(false);
			font.setEdging(FontEdging.ANTI_ALIAS);
			return font;
		});
	}

	private static int applyAlpha(int color, float alpha) {
		if (alpha >= 1f) return color;
		int a = (color >> 24) & 0xFF;
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;
		a = Math.max(0, Math.min(255, (int) (a * alpha)));
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	public static float devicePixelRatio() {
		try {
			int fbw = UDisplay.getWidth();
			int ww = UDisplay.getScreenWidth();
			return ww == 0 ? 1f : (float) fbw / ww;
		} catch (Throwable t) {
			return 1f;
		}
	}

	public static float getStandardGuiScale() {
		float verticalScale = (UDisplay.getHeight() / 1080f) / devicePixelRatio();
		float horizontalScale = (UDisplay.getWidth() / 1920f) / devicePixelRatio();

		float scale = Math.max(verticalScale, horizontalScale);
		scale = Math.max(1f, Math.min(scale, 3f));

		return Math.round(scale * 10f) / 10f;
	}

	public static void cleanup() {
		blurMaskCache.values().forEach(MaskFilter::close);
		blurMaskCache.clear();
		fontCache.values().forEach(Font::close);
		fontCache.clear();
		imageCache.values().forEach(Image::close);
		imageCache.clear();
	}

}