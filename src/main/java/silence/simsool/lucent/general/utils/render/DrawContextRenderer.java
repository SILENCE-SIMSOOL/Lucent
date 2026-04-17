package silence.simsool.lucent.general.utils.render;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.ui.utils.UColor;

public class DrawContextRenderer {

	public static class CornerRadii {
		public final float topLeft, topRight, bottomRight, bottomLeft;

		public CornerRadii(float topLeft, float topRight, float bottomRight, float bottomLeft) {
			this.topLeft = topLeft;
			this.topRight = topRight;
			this.bottomRight = bottomRight;
			this.bottomLeft = bottomLeft;
		}

		public static final CornerRadii ZERO = new CornerRadii(0, 0, 0, 0);

		public static CornerRadii uniform(float radius) {
			float clamped = Math.max(0, radius);
			return new CornerRadii(clamped, clamped, clamped, clamped);
		}
	}

	public static class OutlineStyle {
		public final int color;
		public final float width;

		public OutlineStyle(int color, float width) {
			this.color = color;
			this.width = width;
		}
	}

	public enum GradientDirection {
		LEFT_TO_RIGHT,
		TOP_TO_BOTTOM,
		TOP_LEFT_TO_BOTTOM_RIGHT,
		BOTTOM_LEFT_TO_TOP_RIGHT,
	}

	public static class RoundedOptions {
		public final CornerRadii radii;
		public final OutlineStyle outline;

		public RoundedOptions(CornerRadii radii, OutlineStyle outline) {
			this.radii = radii != null ? radii : CornerRadii.ZERO;
			this.outline = outline;
		}

		public RoundedOptions() {
			this(CornerRadii.ZERO, null);
		}
	}

	public static void roundedFill(GuiGraphics context, int x0, int y0, int x1, int y1, int color, RoundedOptions options) {
		submitRoundedRect(context, x0, y0, x1, y1, color, color, color, color, options);
	}

	public static void roundedFill(GuiGraphics context, int x0, int y0, int x1, int y1, int color, float radius, int outlineColor, float outlineWidth) {
		roundedFill(context, x0, y0, x1, y1, color, new RoundedOptions(CornerRadii.uniform(radius), outlineOrNull(outlineColor, outlineWidth)));
	}

	public static void roundedFill(GuiGraphics context, int x0, int y0, int x1, int y1, int color, CornerRadii radii, int outlineColor, float outlineWidth) {
		roundedFill(context, x0, y0, x1, y1, color, new RoundedOptions(radii, outlineOrNull(outlineColor, outlineWidth)));
	}

	public static void roundedOutline(GuiGraphics context, int x0, int y0, int x1, int y1, int outlineColor, float outlineWidth, CornerRadii radii) {
		int transparent = UColor.withAlpha(outlineColor, 0);
		submitRoundedRect(context, x0, y0, x1, y1, transparent, transparent, transparent, transparent, new RoundedOptions(radii, outlineOrNull(outlineColor, outlineWidth)));
	}

	public static void roundedOutline(GuiGraphics context, int x0, int y0, int x1, int y1, int outlineColor, float outlineWidth, float radius) {
		roundedOutline(context, x0, y0, x1, y1, outlineColor, outlineWidth, CornerRadii.uniform(radius));
	}

	public static void roundedFillGradient(GuiGraphics context, int x0, int y0, int x1, int y1, int startColor, int endColor, GradientDirection direction, RoundedOptions options) {
		int[] corners = gradientCorners(startColor, endColor, direction);
		submitRoundedRect(context, x0, y0, x1, y1, corners[0], corners[1], corners[2], corners[3], options);
	}

	private static OutlineStyle outlineOrNull(int color, float width) {
		float clampedWidth = Math.max(0, width);
		return clampedWidth > 0.0f ? new OutlineStyle(color, clampedWidth) : null;
	}

	private static void submitRoundedRect(
			GuiGraphics guiGraphics,
			int x0, int y0, int x1, int y1,
			int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor,
			RoundedOptions options
	) {
		float topLeftRadius = options.radii != null ? options.radii.topLeft : 0.0f;
		float topRightRadius = options.radii != null ? options.radii.topRight : 0.0f;
		float bottomRightRadius = options.radii != null ? options.radii.bottomRight : 0.0f;
		float bottomLeftRadius = options.radii != null ? options.radii.bottomLeft : 0.0f;

		int outlineColor = options.outline != null ? options.outline.color : 0;
		float outlineWidth = options.outline != null ? options.outline.width : 0.0f;

		RoundRectPIPRenderer.submit(
				guiGraphics, x0, y0, x1, y1,
				topLeftColor, topRightColor, bottomRightColor, bottomLeftColor,
				topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius,
				outlineColor, outlineWidth
		);
	}

	private static int[] gradientCorners(int startColor, int endColor, GradientDirection direction) {
		int mid = UColor.lerpColor(startColor, endColor, 0.5f);
		switch (direction) {
			case LEFT_TO_RIGHT: return new int[]{startColor, endColor, endColor, startColor};
			case TOP_TO_BOTTOM: return new int[]{startColor, startColor, endColor, endColor};
			case TOP_LEFT_TO_BOTTOM_RIGHT: return new int[]{startColor, mid, endColor, mid};
			case BOTTOM_LEFT_TO_TOP_RIGHT: return new int[]{mid, endColor, mid, startColor};
			default: return new int[]{startColor, endColor, endColor, startColor};
		}
	}

}