package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import java.util.List;
import java.util.Random;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import silence.simsool.lucent.ui.utils.UIColors;

public class UText {

	// ==========================================
	// Standard Text Drawing
	// ==========================================

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, int color, boolean shadow) {
		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(x, y);
		pose.scale(scale, scale);
		graphics.text(mc.font, text, 0, 0, color, shadow);
		pose.popMatrix();
	}

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, boolean shadow) {
		drawText(graphics, text, x, y, scale, UIColors.PURE_WHITE, shadow);
	}

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, int color, boolean shadow) {
		drawText(graphics, text, x, y, 1.0f, color, shadow);
	}

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, boolean shadow) {
		drawText(graphics, text, x, y, 1.0f, UIColors.PURE_WHITE, shadow);
	}

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, int color) {
		drawText(graphics, text, x, y, scale, color, false);
	}

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, float scale) {
		drawText(graphics, text, x, y, scale, UIColors.PURE_WHITE, false);
	}

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, int color) {
		drawText(graphics, text, x, y, 1.0f, color, false);
	}

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y) {
		drawText(graphics, text, x, y, 1.0f, UIColors.PURE_WHITE, false);
	}

	public static void drawShadowText(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, int color) {
		drawText(graphics, text, x, y, scale, color, true);
	}

	public static void drawShadowText(GuiGraphicsExtractor graphics, String text, float x, float y, float scale) {
		drawText(graphics, text, x, y, scale, UIColors.PURE_WHITE, true);
	}

	public static void drawShadowText(GuiGraphicsExtractor graphics, String text, float x, float y, int color) {
		drawText(graphics, text, x, y, 1.0f, color, true);
	}

	public static void drawShadowText(GuiGraphicsExtractor graphics, String text, float x, float y) {
		drawText(graphics, text, x, y, 1.0f, UIColors.PURE_WHITE, true);
	}

	// ==========================================
	// Text Drawing with Scale Ratio
	// ==========================================

	public static void drawText(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, float scaleRatio, int color, boolean shadow) {
		Matrix3x2fStack pose = graphics.pose();
		pose.pushMatrix();
		pose.translate(x * scaleRatio, y * scaleRatio);
		pose.scale(scaleRatio * scale, scaleRatio * scale);
		graphics.text(mc.font, text, 0, 0, color, shadow);
		pose.popMatrix();
	}

	public static void drawTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, float scaleRatio, boolean shadow) {
		drawText(graphics, text, x, y, scale, scaleRatio, UIColors.PURE_WHITE, shadow);
	}

	public static void drawTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scaleRatio, int color, boolean shadow) {
		drawText(graphics, text, x, y, 1.0f, scaleRatio, color, shadow);
	}

	public static void drawTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scaleRatio, boolean shadow) {
		drawText(graphics, text, x, y, 1.0f, scaleRatio, UIColors.PURE_WHITE, shadow);
	}

	public static void drawTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, float scaleRatio, int color) {
		drawText(graphics, text, x, y, scale, scaleRatio, color, false);
	}

	public static void drawTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, float scaleRatio) {
		drawText(graphics, text, x, y, scale, scaleRatio, UIColors.PURE_WHITE, false);
	}

	public static void drawTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scaleRatio, int color) {
		drawText(graphics, text, x, y, 1.0f, scaleRatio, color, false);
	}

	public static void drawTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scaleRatio) {
		drawText(graphics, text, x, y, 1.0f, scaleRatio, UIColors.PURE_WHITE, false);
	}

	public static void drawShadowTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, float scaleRatio, int color) {
		drawText(graphics, text, x, y, scale, scaleRatio, color, true);
	}

	public static void drawShadowTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scale, float scaleRatio) {
		drawText(graphics, text, x, y, scale, scaleRatio, UIColors.PURE_WHITE, true);
	}

	public static void drawShadowTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scaleRatio, int color) {
		drawText(graphics, text, x, y, 1.0f, scaleRatio, color, true);
	}

	public static void drawShadowTextRatio(GuiGraphicsExtractor graphics, String text, float x, float y, float scaleRatio) {
		drawText(graphics, text, x, y, 1.0f, scaleRatio, UIColors.PURE_WHITE, true);
	}

	// ==========================================
	// Centered Text Drawing
	// ==========================================

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y, float scale, int color, boolean shadow) {
		float width = mc.font.width(text) * scale;
		drawText(graphics, text, centerX - width / 2f, y, scale, color, shadow);
	}

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y, float scale, boolean shadow) {
		drawCenteredText(graphics, text, centerX, y, scale, UIColors.PURE_WHITE, shadow);
	}

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y, int color, boolean shadow) {
		drawCenteredText(graphics, text, centerX, y, 1.0f, color, shadow);
	}

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y, boolean shadow) {
		drawCenteredText(graphics, text, centerX, y, 1.0f, UIColors.PURE_WHITE, shadow);
	}

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y, float scale, int color) {
		drawCenteredText(graphics, text, centerX, y, scale, color, false);
	}

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y, float scale) {
		drawCenteredText(graphics, text, centerX, y, scale, UIColors.PURE_WHITE, false);
	}

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y, int color) {
		drawCenteredText(graphics, text, centerX, y, 1.0f, color, false);
	}

	public static void drawCenteredText(GuiGraphicsExtractor graphics, String text, float centerX, float y) {
		drawCenteredText(graphics, text, centerX, y, 1.0f, UIColors.PURE_WHITE, false);
	}

	public static void drawCenteredShadowText(GuiGraphicsExtractor graphics, String text, float centerX, float y, float scale, int color) {
		drawCenteredText(graphics, text, centerX, y, scale, color, true);
	}

	public static void drawCenteredShadowText(GuiGraphicsExtractor graphics, String text, float centerX, float y, float scale) {
		drawCenteredText(graphics, text, centerX, y, scale, UIColors.PURE_WHITE, true);
	}

	public static void drawCenteredShadowText(GuiGraphicsExtractor graphics, String text, float centerX, float y, int color) {
		drawCenteredText(graphics, text, centerX, y, 1.0f, color, true);
	}

	public static void drawCenteredShadowText(GuiGraphicsExtractor graphics, String text, float centerX, float y) {
		drawCenteredText(graphics, text, centerX, y, 1.0f, UIColors.PURE_WHITE, true);
	}

	// ==========================================
	// Multi-line Text Drawing (Lists)
	// ==========================================

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float lineHeight, int color, boolean shadow) {
		float currentY = y;
		for (String line : lines) {
			drawText(graphics, line, x, currentY, scale, color, shadow);
			currentY += lineHeight;
		}
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, boolean shadow) {
		drawTexts(graphics, lines, x, y, scale, (mc.font.lineHeight + 2) * scale, UIColors.PURE_WHITE, shadow);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, int color, boolean shadow) {
		drawTexts(graphics, lines, x, y, 1.0f, (mc.font.lineHeight + 2), color, shadow);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, boolean shadow) {
		drawTexts(graphics, lines, x, y, 1.0f, (mc.font.lineHeight + 2), UIColors.PURE_WHITE, shadow);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float lineHeight, boolean shadow) {
		drawTexts(graphics, lines, x, y, scale, lineHeight, UIColors.PURE_WHITE, shadow);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, int color, boolean shadow) {
		drawTexts(graphics, lines, x, y, scale, (mc.font.lineHeight + 2) * scale, color, shadow);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, int color) {
		drawTexts(graphics, lines, x, y, scale, color, false);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale) {
		drawTexts(graphics, lines, x, y, scale, UIColors.PURE_WHITE, false);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, int color) {
		drawTexts(graphics, lines, x, y, 1.0f, color, false);
	}

	public static void drawTexts(GuiGraphicsExtractor graphics, List<String> lines, float x, float y) {
		drawTexts(graphics, lines, x, y, 1.0f, UIColors.PURE_WHITE, false);
	}

	// ==========================================
	// Multi-line Centered Text Drawing
	// ==========================================

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, float scale, float lineHeight, int color, boolean shadow) {
		float currentY = y;
		for (String line : lines) {
			drawCenteredText(graphics, line, centerX, currentY, scale, color, shadow);
			currentY += lineHeight;
		}
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, float scale, boolean shadow) {
		drawCenteredTexts(graphics, lines, centerX, y, scale, (mc.font.lineHeight + 2) * scale, UIColors.PURE_WHITE, shadow);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, int color, boolean shadow) {
		drawCenteredTexts(graphics, lines, centerX, y, 1.0f, (mc.font.lineHeight + 2), color, shadow);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, boolean shadow) {
		drawCenteredTexts(graphics, lines, centerX, y, 1.0f, (mc.font.lineHeight + 2), UIColors.PURE_WHITE, shadow);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, float scale, float lineHeight, boolean shadow) {
		drawCenteredTexts(graphics, lines, centerX, y, scale, lineHeight, UIColors.PURE_WHITE, shadow);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, float scale, int color, boolean shadow) {
		drawCenteredTexts(graphics, lines, centerX, y, scale, (mc.font.lineHeight + 2) * scale, color, shadow);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, float scale, int color) {
		drawCenteredTexts(graphics, lines, centerX, y, scale, color, false);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, float scale) {
		drawCenteredTexts(graphics, lines, centerX, y, scale, UIColors.PURE_WHITE, false);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y, int color) {
		drawCenteredTexts(graphics, lines, centerX, y, 1.0f, color, false);
	}

	public static void drawCenteredTexts(GuiGraphicsExtractor graphics, List<String> lines, float centerX, float y) {
		drawCenteredTexts(graphics, lines, centerX, y, 1.0f, UIColors.PURE_WHITE, false);
	}

	// ==========================================
	// Multi-line Text Drawing with Ratio
	// ==========================================

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float scaleRatio, float lineHeight, int color, boolean shadow) {
		float currentY = y;
		for (String line : lines) {
			drawText(graphics, line, x, currentY, scale, scaleRatio, color, shadow);
			currentY += lineHeight;
		}
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float scaleRatio, boolean shadow) {
		drawTextsRatio(graphics, lines, x, y, scale, scaleRatio, (mc.font.lineHeight + 2) * scale, UIColors.PURE_WHITE, shadow);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scaleRatio, int color, boolean shadow) {
		drawTextsRatio(graphics, lines, x, y, 1.0f, scaleRatio, (mc.font.lineHeight + 2), color, shadow);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scaleRatio, boolean shadow) {
		drawTextsRatio(graphics, lines, x, y, 1.0f, scaleRatio, (mc.font.lineHeight + 2), UIColors.PURE_WHITE, shadow);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float scaleRatio, float lineHeight, boolean shadow) {
		drawTextsRatio(graphics, lines, x, y, scale, scaleRatio, lineHeight, UIColors.PURE_WHITE, shadow);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float scaleRatio, int color, boolean shadow) {
		drawTextsRatio(graphics, lines, x, y, scale, scaleRatio, (mc.font.lineHeight + 2) * scale, color, shadow);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float scaleRatio, int color) {
		drawTextsRatio(graphics, lines, x, y, scale, scaleRatio, color, false);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scale, float scaleRatio) {
		drawTextsRatio(graphics, lines, x, y, scale, scaleRatio, UIColors.PURE_WHITE, false);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scaleRatio, int color) {
		drawTextsRatio(graphics, lines, x, y, 1.0f, scaleRatio, (mc.font.lineHeight + 2), color, false);
	}

	public static void drawTextsRatio(GuiGraphicsExtractor graphics, List<String> lines, float x, float y, float scaleRatio) {
		drawTextsRatio(graphics, lines, x, y, 1.0f, scaleRatio, (mc.font.lineHeight + 2), UIColors.PURE_WHITE, false);
	}

	// ==========================================
	// Miscellaneous Utilities
	// ==========================================

	public static String generateRandomString(int length) {
		Random random = new Random();
		StringBuilder result = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			char randomChar = (char) ('a' + random.nextInt(26));
			result.append(randomChar);
		}
		return result.toString();
	}

}