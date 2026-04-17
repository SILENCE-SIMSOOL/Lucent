package silence.simsool.lucent.general.utils.render;

import static silence.simsool.lucent.Lucent.mc;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;

public class DrawContextUtils {

	public static void text(GuiGraphics graphics, String text, int x, int y, int color, boolean shadow) {
		graphics.drawString(mc.font, text, x, y, color, shadow);
	}

	public static void text(GuiGraphics graphics, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
		graphics.drawString(mc.font, text, x, y, color, shadow);
	}

	public static int getStringWidth(String text) {
		return mc.font.width(text);
	}

	public static void hollowFill(GuiGraphics graphics, int x, int y, int width, int height, int thickness, int color) {
		graphics.fill(x, y, x + width, y + thickness, color);
		graphics.fill(x, y + height - thickness, x + width, y + height, color);
		graphics.fill(x, y + thickness, x + thickness, y + height - thickness, color);
		graphics.fill(x + width - thickness, y + thickness, x + width, y + height - thickness, color);
	}

	public static void drawLine(GuiGraphics graphics, float x1, float y1, float x2, float y2, int color, float lineWidth) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		int half = Math.max(1, (int) (lineWidth / 2f));

		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(x1, y1);
		pose.mul(new Matrix3x2f().rotate((float) Math.atan2(dy, dx)));
		graphics.fill(0, -half, (int) Math.ceil(Math.hypot(dx, dy)), half, color);
		pose.popMatrix();
	}

	public static void roundedFill(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color, int radius) {
		DrawContextRenderer.roundedFill(graphics, x0, y0, x1, y1, color, (float) radius, 0, 0f);
	}

	public static void roundedFill(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color, int radius, int outlineColor, float outlineWidth) {
		DrawContextRenderer.roundedFill(graphics, x0, y0, x1, y1, color, (float) radius, outlineColor, outlineWidth);
	}

	public static void roundedOutline(GuiGraphics graphics, int x0, int y0, int x1, int y1, int outlineColor, float outlineWidth, int radius) {
		DrawContextRenderer.roundedOutline(graphics, x0, y0, x1, y1, outlineColor, outlineWidth, (float) radius);
	}

}