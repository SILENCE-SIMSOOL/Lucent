package silence.simsool.lucent.ui.utils.nvg;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_IMAGE_NODELETE;
import static silence.simsool.lucent.Lucent.mc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.nanovg.NSVGImage;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoSVG;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import silence.simsool.lucent.general.enums.Direction;
import silence.simsool.lucent.general.enums.GradientType;
import silence.simsool.lucent.general.models.data.nvg.NVGFont;
import silence.simsool.lucent.general.models.data.nvg.NVGImage;
import silence.simsool.lucent.ui.font.LucentFont;
import silence.simsool.lucent.ui.utils.UIColors;

public class NVGRenderer {

	private static final NVGPaint nvgPaint  = NVGPaint.malloc();
	private static final NVGColor nvgColor  = NVGColor.malloc();
	private static final NVGColor nvgColor2 = NVGColor.malloc();

	private static final Map<LucentFont, NVGFont> fontMap = new HashMap<>();
	private static final Map<Image, NVGImage> images = new HashMap<>();

	private static final float[] fontBounds = new float[4];
	static final Map<Long, Integer> checkerCache = new HashMap<>();
	private static long vg = -1L;

	private static Scissor scissor = null;
	private static boolean drawing = false;

	static {
		vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
		if (vg == -1L) throw new RuntimeException("Failed to initialize NanoVG");
	}

	private static class Scissor {
		final Scissor previous;
		final float x, y, maxX, maxY;

		Scissor(Scissor previous, float x, float y, float maxX, float maxY) {
			this.previous = previous;
			this.x = x;
			this.y = y;
			this.maxX = maxX;
			this.maxY = maxY;
		}

		/**
		 * Applies this scissor region to the NanoVG context.
		 * If a parent scissor exists, the applied region is the intersection
		 * of this scissor and the parent, preventing overdraw outside nested bounds.
		 */
		void applyScissor() {
			if (previous == null) nvgScissor(vg, x, y, maxX - x, maxY - y);
			else {
				float cx = Math.max(x, previous.x);
				float cy = Math.max(y, previous.y);
				float cw = Math.max(0f, Math.min(maxX, previous.maxX) - cx);
				float ch = Math.max(0f, Math.min(maxY, previous.maxY) - cy);
				nvgScissor(vg, cx, cy, cw, ch);
			}
		}
	}

	public static long getVG() {
		return vg;
	}

	/**
	 * Begins a new NanoVG frame for the given framebuffer dimensions.
	 * Automatically accounts for device pixel ratio (HiDPI/Retina support).
	 * Must be paired with {@link #endFrame()}.
	 *
	 * @param width  Framebuffer width in pixels
	 * @param height Framebuffer height in pixels
	 * @throws IllegalStateException if a frame is already in progress
	 */
	public static void beginFrame(float width, float height) {
		if (drawing) throw new IllegalStateException("[NVGRenderer] Already drawing, but called beginFrame");
		float dpr = devicePixelRatio();
		nvgBeginFrame(vg, width / dpr, height / dpr, dpr);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
		drawing = true;
	}

	/**
	 * Ends the current NanoVG frame and flushes all buffered draw calls to the GPU.
	 * Must be called after {@link #beginFrame(float, float)}.
	 *
	 * @throws IllegalStateException if no frame is currently in progress
	 */
	public static void endFrame() {
		if (!drawing) throw new IllegalStateException("[NVGRenderer] Not drawing, but called endFrame");
		nvgEndFrame(vg);
		drawing = false;
	}

	/**
	 * Saves the current NanoVG render state onto the state stack.
	 * Use this before applying temporary transforms or style changes.
	 * Must be paired with {@link #pop()}.
	 */
	public static void push() {
		nvgSave(vg);
	}

	/**
	 * Restores the most recently saved NanoVG render state from the stack.
	 * Reverts any transforms, styles, or scissor changes applied since the last {@link #push()}.
	 */
	public static void pop() {
		nvgRestore(vg);
	}

	/**
	 * Applies a scale transform to the current NanoVG render state.
	 * Subsequent draw calls will be scaled by the given factors.
	 *
	 * @param x Horizontal scale factor
	 * @param y Vertical scale factor
	 */
	public static void scale(float x, float y) {
		nvgScale(vg, x, y);
	}

	/**
	 * Applies a translation transform to the current NanoVG render state.
	 * Subsequent draw calls will be offset by (x, y).
	 *
	 * @param x Horizontal offset in pixels
	 * @param y Vertical offset in pixels
	 */
	public static void translate(float x, float y) {
		nvgTranslate(vg, x, y);
	}

	/**
	 * Applies a rotation transform to the current NanoVG render state.
	 * Subsequent draw calls will be rotated around the current origin.
	 *
	 * @param amount Rotation angle in radians
	 */
	public static void rotate(float amount) {
		nvgRotate(vg, amount);
	}

	/**
	 * Sets the global alpha (opacity) for all subsequent draw calls.
	 * The value is clamped to [0.0, 1.0].
	 *
	 * @param amount Alpha value where 0.0 is fully transparent and 1.0 is fully opaque
	 */
	public static void globalAlpha(float amount) {
		nvgGlobalAlpha(vg, Math.max(0f, Math.min(1f, amount)));
	}

	/**
	 * Pushes a new scissor (clip) region onto the scissor stack.
	 * If a scissor is already active, the new region is intersected with it.
	 * Must be paired with {@link #popScissor()}.
	 *
	 * @param x Left edge of the scissor region
	 * @param y Top edge of the scissor region
	 * @param w Width of the scissor region
	 * @param h Height of the scissor region
	 */
	public static void pushScissor(float x, float y, float w, float h) {
		scissor = new Scissor(scissor, x, y, w + x, h + y);
		scissor.applyScissor();
	}

	/**
	 * Pops the topmost scissor region from the stack and restores the previous one.
	 * If no parent scissor exists, the scissor is fully reset.
	 */
	public static void popScissor() {
		nvgResetScissor(vg);
		scissor = (scissor != null) ? scissor.previous : null;
		if (scissor != null)
			scissor.applyScissor();
	}

	/**
	 * Draws a straight line between two points with the given thickness and color.
	 *
	 * @param x1        Start X coordinate
	 * @param y1        Start Y coordinate
	 * @param x2        End X coordinate
	 * @param y2        End Y coordinate
	 * @param thickness Stroke width in pixels
	 * @param color     ARGB color packed as an int
	 */
	public static void line(float x1, float y1, float x2, float y2, float thickness, int color) {
		nvgBeginPath(vg);
		nvgMoveTo(vg, x1, y1);
		nvgLineTo(vg, x2, y2);
		nvgStrokeWidth(vg, thickness);
		color(color);
		nvgStrokeColor(vg, nvgColor);
		nvgStroke(vg);
	}

	/**
	 * Draws a filled rectangle with no corner rounding.
	 *
	 * @param x     Left edge
	 * @param y     Top edge
	 * @param w     Width
	 * @param h     Height
	 * @param color ARGB color packed as an int
	 */
	public static void rect(float x, float y, float w, float h, int color) {
		rect(x, y, w, h, color, 0f);
	}

	/**
	 * Draws a filled rectangle with uniform corner rounding.
	 *
	 * @param x      Left edge
	 * @param y      Top edge
	 * @param w      Width
	 * @param h      Height
	 * @param color  ARGB color packed as an int
	 * @param radius Corner radius in pixels
	 */
	public static void rect(float x, float y, float w, float h, int color, float radius) {
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, w, h + .5f, radius);
		color(color);
		nvgFillColor(vg, nvgColor);
		nvgFill(vg);
	}

	/**
	 * Draws a rectangle with each of the four corner radii set individually.
	 *
	 * @param x     Left edge
	 * @param y     Top edge
	 * @param w     Width
	 * @param h     Height
	 * @param color ARGB color packed as an int
	 * @param r1    Top-Left radius
	 * @param r2    Top-Right radius
	 * @param r3    Bottom-Left radius
	 * @param r4    Bottom-Right radius
	 */
	public static void rect(float x, float y, float w, float h, int color, float r1, float r2, float r3, float r4) {
		nvgBeginPath(vg);
		roundedRectPath(x, y, w, h, r1, r2, r3, r4);
		color(color);
		nvgFillColor(vg, nvgColor);
		nvgFill(vg);
	}

	/**
	 * Draws an outlined (stroked) rectangle with no corner rounding.
	 *
	 * @param x         Left edge
	 * @param y         Top edge
	 * @param w         Width
	 * @param h         Height
	 * @param thickness Stroke width in pixels
	 * @param color     ARGB color packed as an int
	 */
	public static void outlineRect(float x, float y, float w, float h, float thickness, int color) {
		outlineRect(x, y, w, h, thickness, color, 0f);
	}

	/**
	 * Draws an outlined (stroked) rectangle with uniform corner rounding.
	 *
	 * @param x         Left edge
	 * @param y         Top edge
	 * @param w         Width
	 * @param h         Height
	 * @param thickness Stroke width in pixels
	 * @param color     ARGB color packed as an int
	 * @param radius    Corner radius in pixels
	 */
	public static void outlineRect(float x, float y, float w, float h, float thickness, int color, float radius) {
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, w, h, radius);
		nvgStrokeWidth(vg, thickness);
		nvgPathWinding(vg, NVG_HOLE);
		color(color);
		nvgStrokeColor(vg, nvgColor);
		nvgStroke(vg);
	}

	/**
	 * Draws an outlined (stroked) rectangle with individually controlled corner radii.
	 *
	 * @param x         Left edge
	 * @param y         Top edge
	 * @param w         Width
	 * @param h         Height
	 * @param thickness Stroke width in pixels
	 * @param color     ARGB color packed as an int
	 * @param r1        Top-Left radius
	 * @param r2        Top-Right radius
	 * @param r3        Bottom-Left radius
	 * @param r4        Bottom-Right radius
	 */
	public static void outlineRect(float x, float y, float w, float h, float thickness, int color, float r1, float r2, float r3, float r4) {
	    nvgBeginPath(vg);
	    roundedRectPath(x, y, w, h, r1, r2, r3, r4);
	    nvgStrokeWidth(vg, thickness);
	    nvgPathWinding(vg, NVG_HOLE);
	    color(color);
	    nvgStrokeColor(vg, nvgColor);
	    nvgStroke(vg);
	}

	/**
	 * Draws a filled rectangle with a linear gradient fill and no corner rounding.
	 *
	 * @param x        Left edge
	 * @param y        Top edge
	 * @param w        Width
	 * @param h        Height
	 * @param color1   Start ARGB color packed as an int
	 * @param color2   End ARGB color packed as an int
	 * @param gradient Direction of the gradient (e.g. LEFT_TO_RIGHT, TOP_TO_BOTTOM)
	 */
	public static void gradientRect(float x, float y, float w, float h, int color1, int color2, GradientType gradient) {
		gradientRect(x, y, w, h, color1, color2, gradient, 0f);
	}

	/**
	 * Draws a filled rectangle with a linear gradient fill and uniform corner rounding.
	 *
	 * @param x        Left edge
	 * @param y        Top edge
	 * @param w        Width
	 * @param h        Height
	 * @param color1   Start ARGB color packed as an int
	 * @param color2   End ARGB color packed as an int
	 * @param gradient Direction of the gradient
	 * @param radius   Corner radius in pixels
	 */
	public static void gradientRect(float x, float y, float w, float h, int color1, int color2, GradientType gradient, float radius) {
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, w, h, radius);
		gradient(color1, color2, x, y, w, h, gradient);
		nvgFillPaint(vg, nvgPaint);
		nvgFill(vg);
	}

	/**
	 * Draws a filled rectangle with a linear gradient fill and individually controlled corner radii.
	 *
	 * @param x        Left edge
	 * @param y        Top edge
	 * @param w        Width
	 * @param h        Height
	 * @param color1   Start ARGB color packed as an int
	 * @param color2   End ARGB color packed as an int
	 * @param gradient Direction of the gradient
	 * @param r1       Top-Left radius
	 * @param r2       Top-Right radius
	 * @param r3       Bottom-Left radius
	 * @param r4       Bottom-Right radius
	 */
	public static void gradientRect(float x, float y, float w, float h, int color1, int color2, GradientType gradient, float r1, float r2, float r3, float r4) {
		nvgBeginPath(vg);
		roundedRectPath(x, y, w, h, r1, r2, r3, r4);
		gradient(color1, color2, x, y, w, h, gradient);
		nvgFillPaint(vg, nvgPaint);
		nvgFill(vg);
	}

	/**
	 * Draws a soft drop shadow beneath a rectangular region with no corner rounding.
	 * Uses a box gradient fading from semi-transparent black to fully transparent.
	 *
	 * @param x      Left edge of the casting element
	 * @param y      Top edge of the casting element
	 * @param width  Width of the casting element
	 * @param height Height of the casting element
	 * @param blur   Softness/feather radius of the shadow edge
	 * @param spread Expansion of the shadow beyond the element bounds
	 */
	public static void dropShadow(float x, float y, float width, float height, float blur, float spread) {
		dropShadow(x, y, width, height, blur, spread, 0f);
	}

	/**
	 * Draws a soft drop shadow beneath a rectangular region with uniform corner rounding.
	 * Uses a box gradient fading from semi-transparent black to fully transparent.
	 *
	 * @param x      Left edge of the casting element
	 * @param y      Top edge of the casting element
	 * @param width  Width of the casting element
	 * @param height Height of the casting element
	 * @param blur   Softness/feather radius of the shadow edge
	 * @param spread Expansion of the shadow beyond the element bounds
	 * @param radius Corner radius matching the casting element
	 */
	public static void dropShadow(float x, float y, float width, float height, float blur, float spread, float radius) {
		nvgRGBA((byte) 0, (byte) 0, (byte) 0, (byte) 125, nvgColor);
		nvgRGBA((byte) 0, (byte) 0, (byte) 0, (byte) 0, nvgColor2);
		nvgBoxGradient(vg, x - spread, y - spread, width + 2 * spread, height + 2 * spread, radius + spread, blur, nvgColor, nvgColor2, nvgPaint);
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x - spread - blur, y - spread - blur, width + 2 * spread + 2 * blur, height + 2 * spread + 2 * blur, radius + spread);
		nvgPathWinding(vg, NVG_HOLE);
		nvgFillPaint(vg, nvgPaint);
		nvgFill(vg);
	}

	/**
	 * Draws a soft drop shadow beneath a rectangular region with individually controlled corner radii.
	 * A hole path matching the element shape is cut out so the shadow only appears outside the element.
	 *
	 * @param x      Left edge of the casting element
	 * @param y      Top edge of the casting element
	 * @param width  Width of the casting element
	 * @param height Height of the casting element
	 * @param blur   Softness/feather radius of the shadow edge
	 * @param spread Expansion of the shadow beyond the element bounds
	 * @param radius Uniform fallback radius used for the box gradient feathering
	 * @param r1     Top-Left radius
	 * @param r2     Top-Right radius
	 * @param r3     Bottom-Left radius
	 * @param r4     Bottom-Right radius
	 */
	public static void dropShadow(float x, float y, float width, float height, float blur, float spread, float radius, float r1, float r2, float r3, float r4) {
		nvgRGBA((byte) 0, (byte) 0, (byte) 0, (byte) 125, nvgColor);
		nvgRGBA((byte) 0, (byte) 0, (byte) 0, (byte) 0, nvgColor2);
		float maxR = Math.max(r1, Math.max(r2, Math.max(r3, r4)));
		nvgBoxGradient(vg, x - spread, y - spread, width + 2 * spread, height + 2 * spread, maxR + spread, blur, nvgColor, nvgColor2, nvgPaint);
		nvgBeginPath(vg);
		roundedRectPath(x - spread - blur, y - spread - blur, width + 2 * spread + 2 * blur, height + 2 * spread + 2 * blur, r1 + spread + blur, r2 + spread + blur, r3 + spread + blur, r4 + spread + blur);
		roundedRectPath(x, y, width, height, r1, r2, r3, r4);
		nvgPathWinding(vg, NVG_HOLE);
		nvgFillPaint(vg, nvgPaint);
		nvgFill(vg);
	}

	/**
	 * Draws a filled circle.
	 *
	 * @param x      Center X coordinate
	 * @param y      Center Y coordinate
	 * @param radius Radius in pixels
	 * @param color  ARGB color packed as an int
	 */
	public static void circle(float x, float y, float radius, int color) {
		nvgBeginPath(vg);
		nvgCircle(vg, x, y, radius);
		color(color);
		nvgFillColor(vg, nvgColor);
		nvgFill(vg);
	}

	/**
	 * Draws an outlined (stroked) circle.
	 *
	 * @param x         Center X coordinate
	 * @param y         Center Y coordinate
	 * @param radius    Radius in pixels
	 * @param thickness Stroke width in pixels
	 * @param color     ARGB color packed as an int
	 */
	public static void outlineCircle(float x, float y, float radius, float thickness, int color) {
		nvgBeginPath(vg);
		nvgCircle(vg, x, y, radius);
		nvgStrokeWidth(vg, thickness);
		color(color);
		nvgStrokeColor(vg, nvgColor);
		nvgStroke(vg);
	}

	/**
	 * Draws a filled triangle defined by three vertices.
	 *
	 * @param x1    First vertex X
	 * @param y1    First vertex Y
	 * @param x2    Second vertex X
	 * @param y2    Second vertex Y
	 * @param x3    Third vertex X
	 * @param y3    Third vertex Y
	 * @param color ARGB color packed as an int
	 */
	public static void triangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
		nvgBeginPath(vg);
		nvgMoveTo(vg, x1, y1);
		nvgLineTo(vg, x2, y2);
		nvgLineTo(vg, x3, y3);
		nvgClosePath(vg);
		color(color);
		nvgFillColor(vg, nvgColor);
		nvgFill(vg);
	}

	/**
	 * Draws an outlined (stroked) triangle defined by three vertices.
	 *
	 * @param x1        First vertex X
	 * @param y1        First vertex Y
	 * @param x2        Second vertex X
	 * @param y2        Second vertex Y
	 * @param x3        Third vertex X
	 * @param y3        Third vertex Y
	 * @param thickness Stroke width in pixels
	 * @param color     ARGB color packed as an int
	 */
	public static void outlineTriangle(float x1, float y1, float x2, float y2, float x3, float y3, float thickness, int color) {
		nvgBeginPath(vg);
		nvgMoveTo(vg, x1, y1);
		nvgLineTo(vg, x2, y2);
		nvgLineTo(vg, x3, y3);
		nvgClosePath(vg);
		nvgStrokeWidth(vg, thickness);
		color(color);
		nvgStrokeColor(vg, nvgColor);
		nvgStroke(vg);
	}

	/**
	 * Draws a directional arrow triangle (convenience helper).
	 * The triangle points in the given {@link TriangleDirection}.
	 *
	 * @param cx        Center X of the bounding box
	 * @param cy        Center Y of the bounding box
	 * @param w         Width of the bounding box
	 * @param h         Height of the bounding box
	 * @param direction Direction the arrow tip points toward
	 * @param color     ARGB color packed as an int
	 */
	public static void arrowTriangle(float cx, float cy, float w, float h, Direction direction, int color) {
		float hw = w / 2f, hh = h / 2f;
		float x1, y1, x2, y2, x3, y3;
		switch (direction) {
			case UP -> { x1 = cx; y1 = cy - hh; x2 = cx - hw; y2 = cy + hh; x3 = cx + hw; y3 = cy + hh; }
			case DOWN -> { x1 = cx; y1 = cy + hh; x2 = cx - hw; y2 = cy - hh; x3 = cx + hw; y3 = cy - hh; }
			case LEFT -> { x1 = cx - hw; y1 = cy; x2 = cx + hw; y2 = cy - hh; x3 = cx + hw; y3 = cy + hh; }
			default -> { x1 = cx + hw; y1 = cy; x2 = cx - hw; y2 = cy - hh; x3 = cx - hw; y3 = cy + hh; }
		}
		triangle(x1, y1, x2, y2, x3, y3, color);
	}

	public static void drawCheckerboard(float x, float y, float w, float h, float radius) {
		drawCheckerboard(x, y, w, h, 6, UIColors.PURE_WHITE, UIColors.GRAY, radius);
	}

	/**
	 * Draws a checkerboard pattern inside the given rectangle with custom
	 * colors and no corner rounding.
	 *
	 * @param x         Left edge
	 * @param y         Top edge
	 * @param w         Width
	 * @param h         Height
	 * @param gridSize  Size of each checker cell in pixels
	 * @param color1    ARGB color for the "light" cells (packed int)
	 * @param color2    ARGB color for the "dark" cells (packed int)
	 * @param radius    Corner radius for the clip region in pixels (0 = sharp)
	 */
	public static void drawCheckerboard(float x, float y, float w, float h, float gridSize, int color1, int color2, float radius) {
		long key = ((long)color1 << 32) | (color2 & 0xFFFFFFFFL);
		
		int image = checkerCache.computeIfAbsent(key, k -> {
			ByteBuffer buffer = MemoryUtil.memAlloc(16);
			// 색상 순서: RGBA -> NanoVG는 기본적으로 리틀 엔디안을 고려하거나 byte 순서를 따름
			buffer.put(new byte[]{(byte)(color1 >> 16), (byte)(color1 >> 8), (byte)color1, (byte)(color1 >> 24)});
			buffer.put(new byte[]{(byte)(color2 >> 16), (byte)(color2 >> 8), (byte)color2, (byte)(color2 >> 24)});
			buffer.put(new byte[]{(byte)(color2 >> 16), (byte)(color2 >> 8), (byte)color2, (byte)(color2 >> 24)});
			buffer.put(new byte[]{(byte)(color1 >> 16), (byte)(color1 >> 8), (byte)color1, (byte)(color1 >> 24)});
			buffer.flip();
			int img = NanoVG.nvgCreateImageRGBA(vg, 2, 2, NanoVG.NVG_IMAGE_REPEATX | NanoVG.NVG_IMAGE_REPEATY | NanoVG.NVG_IMAGE_NEAREST, buffer);
			MemoryUtil.memFree(buffer);
			return img;
		});

		try (MemoryStack stack = MemoryStack.stackPush()) {
			NVGPaint paint = NVGPaint.malloc(stack);
			NanoVG.nvgImagePattern(vg, x, y, gridSize * 2, gridSize * 2, 0f, image, 1f, paint);
			
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRoundedRect(vg, x, y, w, h, radius);
			NanoVG.nvgFillPaint(vg, paint);
			NanoVG.nvgFill(vg);
		}
	}

	/**
	 * Draws text using the default font (PRETENDARD).
	 *
	 * @param text  String to render
	 * @param x     Left edge of the text baseline
	 * @param y     Top edge of the text
	 * @param color ARGB color packed as an int
	 * @param size  Font size in pixels
	 */
	public static void text(String text, float x, float y, int color, float size) {
		text(text, x, y, Fonts.PRETENDARD, color, size);
	}

	/**
	 * Draws text using the given font with the default white color.
	 *
	 * @param text String to render
	 * @param x    Left edge of the text
	 * @param y    Top edge of the text
	 * @param font Font to use for rendering
	 * @param size Font size in pixels
	 */
	public static void text(String text, float x, float y, LucentFont font, float size) {
		text(text, x, y, font, UIColors.PURE_WHITE, size);
	}

	/**
	 * Draws horizontally centered text using the given font and color.
	 * The provided X coordinate is used as the center point.
	 *
	 * @param text  String to render
	 * @param x     Horizontal center position
	 * @param y     Top edge of the text
	 * @param font  Font to use for rendering
	 * @param color ARGB color packed as an int
	 * @param size  Font size in pixels
	 */
	public static void centerText(String text, float x, float y, LucentFont font, int color, float size) {
		float textWidth = nvgTextBounds(vg, 0f, 0f, text, fontBounds);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
		text(text, x - textWidth / 2f, y, font, color, size);
	}

	/**
	 * Draws text with a drop shadow using the default font (PRETENDARD).
	 * The {@code shadow} parameter is accepted but shadow is always applied.
	 *
	 * @param text   String to render
	 * @param x      Left edge of the text
	 * @param y      Top edge of the text
	 * @param color  ARGB color packed as an int
	 * @param size   Font size in pixels
	 * @param shadow Unused — shadow is always drawn
	 */
	public static void text(String text, float x, float y, int color, float size, boolean shadow) {
		textShadow(text, x, y, Fonts.PRETENDARD, color, size);
	}

	/**
	 * Draws text with a drop shadow using the given font and default white color.
	 * The {@code shadow} parameter is accepted but shadow is always applied.
	 *
	 * @param text   String to render
	 * @param x      Left edge of the text
	 * @param y      Top edge of the text
	 * @param font   Font to use for rendering
	 * @param size   Font size in pixels
	 * @param shadow Unused — shadow is always drawn
	 */
	public static void text(String text, float x, float y, LucentFont font, float size, boolean shadow) {
		textShadow(text, x, y, font, UIColors.PURE_WHITE, size);
	}

	/**
	 * Draws horizontally centered text with a drop shadow.
	 * The {@code shadow} parameter is accepted but shadow is always applied.
	 *
	 * @param text   String to render
	 * @param x      Horizontal center position
	 * @param y      Top edge of the text
	 * @param font   Font to use for rendering
	 * @param color  ARGB color packed as an int
	 * @param size   Font size in pixels
	 * @param shadow Unused — shadow is always drawn
	 */
	public static void centerText(String text, float x, float y, LucentFont font, int color, float size, boolean shadow) {
		float textWidth = nvgTextBounds(vg, 0f, 0f, text, fontBounds);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
		textShadow(text, x - textWidth / 2f, y, font, color, size);
	}

	/**
	 * Draws text with the given font, color, and size.
	 * This is the core text rendering method; all other text variants delegate here.
	 *
	 * @param text  String to render
	 * @param x     Left edge of the text
	 * @param y     Top edge of the text
	 * @param font  Font to use for rendering
	 * @param color ARGB color packed as an int
	 * @param size  Font size in pixels
	 */
	public static void text(String text, float x, float y, LucentFont font, int color, float size) {
		nvgFontSize(vg, size);
		nvgFontFaceId(vg, getFontID(font));
		color(color);
		nvgFillColor(vg, nvgColor);
		nvgText(vg, x, y + .5f, text);
	}

	/**
	 * Draws text with a drop shadow using the default font (PRETENDARD).
	 * A black offset copy is drawn first, then the colored text on top.
	 *
	 * @param text  String to render
	 * @param x     Left edge of the text
	 * @param y     Top edge of the text
	 * @param color ARGB color packed as an int
	 * @param size  Font size in pixels
	 */
	public static void textShadow(String text, float x, float y, int color, float size) {
		textShadow(text, x, y, Fonts.PRETENDARD, color, size);
	}

	/**
	 * Draws text with a drop shadow using the given font and default white color.
	 *
	 * @param text String to render
	 * @param x    Left edge of the text
	 * @param y    Top edge of the text
	 * @param font Font to use for rendering
	 * @param size Font size in pixels
	 */
	public static void textShadow(String text, float x, float y, LucentFont font, float size) {
		textShadow(text, x, y, font, UIColors.PURE_WHITE, size);
	}

	/**
	 * Draws horizontally centered text with a drop shadow.
	 * The provided X coordinate is used as the center point.
	 *
	 * @param text  String to render
	 * @param x     Horizontal center position
	 * @param y     Top edge of the text
	 * @param font  Font to use for rendering
	 * @param color ARGB color packed as an int
	 * @param size  Font size in pixels
	 */
	public static void centerTextShadow(String text, float x, float y, LucentFont font, int color, float size) {
		float textWidth = nvgTextBounds(vg, 0f, 0f, text, fontBounds);
		nvgTextAlign(vg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
		textShadow(text, x - textWidth / 2f, y, font, color, size);
	}

	/**
	 * Draws text with a 2px black drop shadow using the given font and color.
	 * The shadow is rendered at (x+2, y+2) and the colored text is drawn on top at (x, y).
	 * Coordinates are pixel-snapped via {@link Math#round(float)} to avoid sub-pixel blur.
	 *
	 * @param text  String to render
	 * @param x     Left edge of the text
	 * @param y     Top edge of the text
	 * @param font  Font to use for rendering
	 * @param color ARGB color packed as an int
	 * @param size  Font size in pixels
	 */
	public static void textShadow(String text, float x, float y, LucentFont font, int color, float size) {
		nvgFontFaceId(vg, getFontID(font));
		nvgFontSize(vg, size);
		color(-16777216);
		nvgFillColor(vg, nvgColor);
		nvgText(vg, Math.round(x + 2f), Math.round(y + 2f), text);
		color(color);
		nvgFillColor(vg, nvgColor);
		nvgText(vg, Math.round(x), Math.round(y), text);
	}

	/**
	 * Returns the rendered width of the given string in pixels.
	 *
	 * @param text String to measure
	 * @param font Font used for measurement
	 * @param size Font size in pixels
	 * @return Width of the string in pixels
	 */
	public static float textWidth(String text, LucentFont font, float size) {
		nvgFontSize(vg, size);
		nvgFontFaceId(vg, getFontID(font));
		return nvgTextBounds(vg, 0f, 0f, text, fontBounds);
	}

	/**
	 * Draws word-wrapped text within a bounding box with a custom line height multiplier.
	 * Text automatically wraps to the next line when it exceeds the given width.
	 *
	 * @param text       String to render
	 * @param x          Left edge of the text box
	 * @param y          Top edge of the text box
	 * @param w          Maximum width before wrapping
	 * @param font       Font to use for rendering
	 * @param size       Font size in pixels
	 * @param color      ARGB color packed as an int
	 * @param lineHeight Line height multiplier (1.0 = default spacing)
	 */
	public static void drawWrappedString(String text, float x, float y, float w, LucentFont font, float size, int color, float lineHeight) {
		nvgFontSize(vg, size);
		nvgFontFaceId(vg, getFontID(font));
		nvgTextLineHeight(vg, lineHeight);
		color(color);
		nvgFillColor(vg, nvgColor);
		nvgTextBox(vg, x, y, w, text);
	}

	/**
	 * Draws word-wrapped text within a bounding box using the default line height (1.0).
	 *
	 * @param text  String to render
	 * @param x     Left edge of the text box
	 * @param y     Top edge of the text box
	 * @param w     Maximum width before wrapping
	 * @param font  Font to use for rendering
	 * @param size  Font size in pixels
	 * @param color ARGB color packed as an int
	 */
	public static void drawWrappedString(String text, float x, float y, float w, LucentFont font, float size, int color) {
		drawWrappedString(text, x, y, w, font, size, color, 1f);
	}

	/**
	 * Measures the bounding box of a word-wrapped string without rendering it.
	 * Useful for calculating the height a wrapped text block will occupy.
	 *
	 * @param text       String to measure
	 * @param w          Maximum width before wrapping
	 * @param size       Font size in pixels
	 * @param font       Font used for measurement
	 * @param lineHeight Line height multiplier
	 * @return A float[4] array containing [minX, minY, maxX, maxY]
	 */
	public static float[] wrappedTextBounds(String text, float w, float size, LucentFont font, float lineHeight) {
		float[] bounds = new float[4];
		nvgFontSize(vg, size);
		nvgFontFaceId(vg, getFontID(font));
		nvgTextLineHeight(vg, lineHeight);
		nvgTextBoxBounds(vg, 0f, 0f, w, text, bounds);
		return bounds;
	}

	/**
	 * Measures the bounding box of a word-wrapped string using the default line height (1.0).
	 *
	 * @param text String to measure
	 * @param w    Maximum width before wrapping
	 * @param font Font used for measurement
	 * @param size Font size in pixels
	 * @return A float[4] array containing [minX, minY, maxX, maxY]
	 */
	public static float[] wrappedTextBounds(String text, float w, LucentFont font, float size) {
		return wrappedTextBounds(text, w, size, font, 1f);
	}

	/**
	 * Registers an existing OpenGL texture as a NanoVG image handle.
	 * The returned handle can be used with NanoVG image paint calls.
	 * Note: NVG_IMAGE_NODELETE prevents NanoVG from deleting the texture on cleanup.
	 *
	 * @param textureId     OpenGL texture ID
	 * @param textureWidth  Width of the texture in pixels
	 * @param textureHeight Height of the texture in pixels
	 * @return NanoVG image handle
	 */
	public static int createNVGImage(int textureId, int textureWidth, int textureHeight) {
		return NanoVGGL3.nvglCreateImageFromHandle(vg, textureId, textureWidth, textureHeight, NVG_IMAGE_NODELETE);
	}

	/**
	 * Draws a sub-region of a NanoVG image handle onto the screen with optional corner rounding.
	 * The sub-region is defined in texel coordinates relative to the full texture dimensions.
	 *
	 * @param image         NanoVG image handle (from {@link #createNVGImage})
	 * @param textureWidth  Full texture width in pixels
	 * @param textureHeight Full texture height in pixels
	 * @param subX          Sub-region left offset in texels
	 * @param subY          Sub-region top offset in texels
	 * @param subW          Sub-region width in texels
	 * @param subH          Sub-region height in texels
	 * @param x             Screen X position
	 * @param y             Screen Y position
	 * @param w             Render width on screen
	 * @param h             Render height on screen
	 * @param radius        Corner radius in pixels
	 */
	public static void image(int image, int textureWidth, int textureHeight, int subX, int subY, int subW, int subH, float x, float y, float w, float h, float radius) {
		if (image == -1) return;
		float sx = (float) subX / textureWidth;
		float sy = (float) subY / textureHeight;
		float sw = (float) subW / textureWidth;
		float sh = (float) subH / textureHeight;
		float iw = w / sw, ih = h / sh;
		float ix = x - iw * sx, iy = y - ih * sy;
		nvgImagePattern(vg, ix, iy, iw, ih, 0f, image, 1f, nvgPaint);
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, w, h + .5f, radius);
		nvgFillPaint(vg, nvgPaint);
		nvgFill(vg);
	}

	/**
	 * Draws a loaded {@link Image} at the given position and size with no corner rounding.
	 *
	 * @param image The image to draw (must be loaded via {@link #createImage})
	 * @param x     Left edge
	 * @param y     Top edge
	 * @param size  Render pixel size
	 */
	public static void image(Image image, float x, float y, float size) {
		image(image, x, y, size, size, 0f);
	}

	/**
	 * Draws a loaded {@link Image} at the given position and size with no corner rounding.
	 *
	 * @param image The image to draw (must be loaded via {@link #createImage})
	 * @param x     Left edge
	 * @param y     Top edge
	 * @param w     Render width
	 * @param h     Render height
	 */
	public static void image(Image image, float x, float y, float w, float h) {
		image(image, x, y, w, h, 0f);
	}

	/**
	 * Draws a loaded {@link Image} at the given position and size with optional corner rounding.
	 *
	 * @param image  The image to draw (must be loaded via {@link #createImage})
	 * @param x      Left edge
	 * @param y      Top edge
	 * @param w      Render width
	 * @param h      Render height
	 * @param radius Corner radius in pixels
	 */
	public static void image(Image image, float x, float y, float w, float h, float radius) {
		image(image, x, y, w, h, radius, 1f);
	}

	public static void image(Image image, float x, float y, float w, float h, float radius, float alpha) {
		if (image == null) return;
		nvgImagePattern(vg, x, y, w, h, 0f, getImage(image), alpha, nvgPaint);
		nvgBeginPath(vg);
		nvgRoundedRect(vg, x, y, w, h + .5f, radius);
		nvgFillPaint(vg, nvgPaint);
		nvgFill(vg);
	}

	/**
	 * Loads an image from a resource path and registers it for rendering.
	 * If the image is already loaded, the existing instance is reused and its reference count is incremented.
	 * Supports both raster images (via STB) and SVG files (via NanoSVG).
	 * Call {@link #deleteImage(Image)} when the image is no longer needed.
	 *
	 * @param resourcePath Classpath or file path of the image resource
	 * @return An {@link Image} handle usable with the {@code image()} draw methods
	 * @throws Exception If the image cannot be loaded from the given path
	 */
	public static Image createImage(String resourcePath) throws Exception {
		Image image = images.keySet().stream().filter(i -> i.identifier.equals(resourcePath)).findFirst().orElse(new Image(resourcePath));
		NVGImage nvgImg = images.computeIfAbsent(image, img -> {
			int id = 0;
			try {
				id = img.isSVG ? loadSVG(img) : loadImage(img);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return new NVGImage(0, id);
		});
		nvgImg.incrementCount();
		return image;
	}

	/**
	 * Decrements the reference count of the given image and releases its GPU resources if it reaches zero.
	 * Should always be called when an image created with {@link #createImage} is no longer needed.
	 *
	 * @param image The image handle to release
	 */
	public static void deleteImage(Image image) {
		NVGImage nvgImg = images.get(image); if (nvgImg == null) return;
		nvgImg.decrementCount();
		if (nvgImg.getCount() == 0) {
			nvgDeleteImage(vg, nvgImg.getNvg());
			images.remove(image);
		}
	}

	/**
	 * Returns the device pixel ratio of the current Minecraft window.
	 * Used to correctly scale NanoVG frames on HiDPI/Retina displays.
	 * Falls back to 1.0 if the window is unavailable or an error occurs.
	 *
	 * @return Device pixel ratio (e.g. 2.0 on Retina displays)
	 */
	public static float devicePixelRatio() {
		try {
			var window = mc.getWindow();
			int fbw = window.getWidth();
			int ww = window.getScreenWidth();
			return ww == 0 ? 1f : (float) fbw / ww;
		} catch (Throwable t) {
			return 1f;
		}
	}

	public static float getStandardGuiScale() {
		float verticalScale = (mc.getWindow().getScreenHeight() / 1080f) / devicePixelRatio();
		float horizontalScale = (mc.getWindow().getScreenWidth() / 1920f) / devicePixelRatio();

		float scale = Math.max(verticalScale, horizontalScale);
		scale = Math.max(1f, Math.min(scale, 3f));

		return Math.round(scale * 10f) / 10f;
	}

	/**
	 * Returns the NanoVG font ID for the given {@link LucentFont}, loading it if not yet cached.
	 * Font data is read from the font's buffer and registered with NanoVG on first use.
	 *
	 * @param font The font to look up or load
	 * @return NanoVG font handle, or -1 if font is null
	 */
	private static int getFontID(LucentFont font) {
		if (font == null) return -1;
		return fontMap.computeIfAbsent(font, f -> {
			ByteBuffer buffer = f.buffer();
			int id = nvgCreateFontMem(vg, font.getName(), buffer, false);
			return new NVGFont(id, buffer);
		}).getId();
	}

	/**
	 * Retrieves the NanoVG image handle for the given {@link Image}.
	 *
	 * @param image The image to look up
	 * @return NanoVG image handle
	 * @throws IllegalStateException if the image has not been loaded via {@link #createImage}
	 */
	private static int getImage(Image image) {
		NVGImage nvgImg = images.get(image);
		if (nvgImg == null) throw new IllegalStateException("Image (" + image.identifier + ") doesn't exist");
		return nvgImg.getNvg();
	}

	/**
	 * Loads a raster image (PNG, JPEG, etc.) via STB and uploads it to NanoVG.
	 * The image data is decoded into raw RGBA bytes before being registered.
	 *
	 * @param image The {@link Image} descriptor containing the data buffer
	 * @return NanoVG image handle
	 * @throws IOException          If the buffer cannot be read
	 * @throws NullPointerException If STB fails to decode the image
	 */
	private static int loadImage(Image image) throws IOException {
		int[] w = new int[1], h = new int[1], channels = new int[1];
		ByteBuffer buffer = STBImage.stbi_load_from_memory(image.buffer(), w, h, channels, 4);
		if (buffer == null) throw new NullPointerException("Failed to load image: " + image.identifier);
		return nvgCreateImageRGBA(vg, w[0], h[0], 0, buffer);
	}

	/**
	 * Loads an SVG file via NanoSVG, rasterizes it to RGBA pixels, and uploads it to NanoVG.
	 * The resulting image is at the SVG's intrinsic size with a 96 DPI scale factor.
	 * Native memory is explicitly freed after upload.
	 *
	 * @param image The {@link Image} descriptor containing the SVG input stream
	 * @return NanoVG image handle
	 * @throws RuntimeException      If the SVG stream cannot be read
	 * @throws IllegalStateException If NanoSVG fails to parse the SVG source
	 */
	private static int loadSVG(Image image) {
		String vec;
		try (InputStream s = image.stream) {
			vec = new String(s.readAllBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		NSVGImage svg = NanoSVG.nsvgParse(vec, "px", 96f); if (svg == null) throw new IllegalStateException("Failed to parse " + image.identifier);
		int width = (int) svg.width();
		int height = (int) svg.height();
		ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
		try {
			long rasterizer = NanoSVG.nsvgCreateRasterizer();
			NanoSVG.nsvgRasterize(rasterizer, svg, 0f, 0f, 1f, buffer, width, height, width * 4);
			int nvgImage = nvgCreateImageRGBA(vg, width, height, 0, buffer);
			NanoSVG.nsvgDeleteRasterizer(rasterizer);
			return nvgImage;
		} finally {
			NanoSVG.nsvgDelete(svg);
			MemoryUtil.memFree(buffer);
		}
	}

	/**
	 * Unpacks an ARGB int color and writes it into the shared {@code nvgColor} struct.
	 *
	 * @param color ARGB color packed as an int
	 */
	private static void color(int color) {
		nvgRGBA((byte) ((color >> 16) & 0xFF), (byte) ((color >> 8) & 0xFF), (byte) (color & 0xFF), (byte) ((color >> 24) & 0xFF), nvgColor);
	}

	/**
	 * Unpacks two ARGB int colors and writes them into {@code nvgColor} and {@code nvgColor2} respectively.
	 * Used internally to prepare gradient start/end colors.
	 *
	 * @param color1 First ARGB color (written to nvgColor)
	 * @param color2 Second ARGB color (written to nvgColor2)
	 */
	private static void color(int color1, int color2) {
		nvgRGBA((byte) ((color1 >> 16) & 0xFF), (byte) ((color1 >> 8) & 0xFF), (byte) (color1 & 0xFF), (byte) ((color1 >> 24) & 0xFF), nvgColor);
		nvgRGBA((byte) ((color2 >> 16) & 0xFF), (byte) ((color2 >> 8) & 0xFF), (byte) (color2 & 0xFF), (byte) ((color2 >> 24) & 0xFF), nvgColor2);
	}

	/**
	 * Builds a linear gradient paint into {@code nvgPaint} based on the given direction.
	 * Supports LEFT_TO_RIGHT and TOP_TO_BOTTOM gradient orientations.
	 *
	 * @param color1    Start ARGB color packed as an int
	 * @param color2    End ARGB color packed as an int
	 * @param x         Left edge of the gradient region
	 * @param y         Top edge of the gradient region
	 * @param w         Width of the gradient region
	 * @param h         Height of the gradient region
	 * @param direction Gradient direction enum
	 */
	private static void gradient(int color1, int color2, float x, float y, float w, float h, GradientType direction) {
		color(color1, color2);
		switch (direction) {
			case LEFT_TO_RIGHT -> nvgLinearGradient(vg, x, y, x + w, y, nvgColor, nvgColor2, nvgPaint);
			case TOP_TO_BOTTOM -> nvgLinearGradient(vg, x, y, x, y + h, nvgColor, nvgColor2, nvgPaint);
		}
	}

	/**
	 * Appends a rounded rectangle path to the current NanoVG path using arc segments for each corner.
	 * Corner parameters follow the order: top-left, top-right, bottom-right, bottom-left.
	 * This method does not call {@code nvgBeginPath} — the caller is responsible for path lifecycle.
	 *
	 * @param x  Left edge
	 * @param y  Top edge
	 * @param w  Width
	 * @param h  Height
	 * @param tl Top-Left radius
	 * @param tr Top-Right radius
	 * @param br Bottom-Right radius
	 * @param bl Bottom-Left radius
	 */
	private static void roundedRectPath(float x, float y, float w, float h, float tl, float tr, float br, float bl) {
		float limit = Math.min(w, h) / 2f;
		tl = Math.min(tl, limit);
		tr = Math.min(tr, limit);
		br = Math.min(br, limit);
		bl = Math.min(bl, limit);

		nvgMoveTo(vg, x + tl, y);

		nvgLineTo(vg, x + w - tr, y);
		if (tr > 0) nvgArcTo(vg, x + w, y, x + w, y + tr, tr);
		else nvgLineTo(vg, x + w, y);

		nvgLineTo(vg, x + w, y + h - br);
		if (br > 0) nvgArcTo(vg, x + w, y + h, x + w - br, y + h, br);
		else nvgLineTo(vg, x + w, y + h);

		nvgLineTo(vg, x + bl, y + h);
		if (bl > 0) nvgArcTo(vg, x, y + h, x, y + h - bl, bl);
		else nvgLineTo(vg, x, y + h);

		nvgLineTo(vg, x, y + tl);
		if (tl > 0) nvgArcTo(vg, x, y, x + tl, y, tl);
		else nvgLineTo(vg, x, y);

	}
}