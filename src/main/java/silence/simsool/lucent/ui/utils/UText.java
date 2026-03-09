package silence.simsool.lucent.ui.utils;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.resources.Identifier.parse;
import static silence.simsool.lucent.Lucent.mc;

import java.awt.Font;
import java.io.InputStream;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.ui.font.CFontRenderer;
import silence.simsool.lucent.ui.font.LucentFont;

public class UText {



}

//public class UText {
//
//    private static final Map<Fonts, CFontRenderer> renderers = new HashMap<>();
//    private static CFontRenderer defaultRenderer;
//
//    public enum Fonts {
//        DEFAULT("pretendard"),
//        PRETENDARD("pretendard"),
//        PRETENDARD_MEDIUM("pretendard_medium"),
//        PRETENDARD_SEMIBOLD("pretendard_semibold"),
//        PRETENDARD_LIGHT("pretendard_light"),
//        PRETENDARD_EXTRALIGHT("pretendard_extralight");
//
//		private final String name;
//
//		Fonts(String name) {
//			this.name = name;
//		}
//
//		public String getName() {
//			return name;
//		}
//    }
//
//    public static void init() {
//        if (!renderers.isEmpty()) return;
//        
//        for (Fonts f : Fonts.values()) {
//            if (f == Fonts.DEFAULT) continue;
//            try {
//                InputStream is = UText.class.getResourceAsStream("/assets/" + Lucent.ID + "/fonts/" + f.getName() + ".ttf");
//                if (is == null) {
//                    System.err.println("Failed to load font: " + f.getName() + " - fallback to default Font");
//                    Font awtFont = new Font("Arial", Font.PLAIN, 44);
//                    CFontRenderer renderer = new CFontRenderer(awtFont, true);
//                    renderers.put(f, renderer);
//                    if (f == Fonts.PRETENDARD) defaultRenderer = renderer;
//                    continue;
//                }
//                Font awtFont = Font.createFont(Font.TRUETYPE_FONT, is);
//                awtFont = awtFont.deriveFont(Font.PLAIN, 44f);
//                CFontRenderer renderer = new CFontRenderer(awtFont, true);
//                renderers.put(f, renderer);
//                
//                if (f == Fonts.PRETENDARD) defaultRenderer = renderer;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static CFontRenderer getRenderer(Fonts font) {
//        if (renderers.isEmpty()) init();
//        if (font == null || font == Fonts.DEFAULT) return defaultRenderer;
//        return renderers.getOrDefault(font, defaultRenderer);
//    }
//
//    public static void drawText(GuiGraphics ctx, String text, int x, int y, int color) {
//        drawText(ctx, text, x, y, color, Fonts.DEFAULT, 11.0f);
//    }
//
//    public static void drawText(GuiGraphics ctx, String text, int x, int y, int color, Fonts font) {
//        drawText(ctx, text, x, y, color, font, 11.0f);
//    }
//
//    public static void drawText(GuiGraphics ctx, String text, int x, int y, int color, Fonts font, float size) {
//        CFontRenderer r = getRenderer(font);
//        if (r != null) r.drawString(ctx, text, x, y, color, false, size);
//    }
//
//    public static void drawShadowText(GuiGraphics ctx, String text, int x, int y, int color) {
//        drawShadowText(ctx, text, x, y, color, Fonts.DEFAULT, 11.0f);
//    }
//
//    public static void drawShadowText(GuiGraphics ctx, String text, int x, int y, int color, Fonts font) {
//        drawShadowText(ctx, text, x, y, color, font, 11.0f);
//    }
//
//    public static void drawShadowText(GuiGraphics ctx, String text, int x, int y, int color, Fonts font, float size) {
//        CFontRenderer r = getRenderer(font);
//        if (r != null) r.drawString(ctx, text, x, y, color, true, size);
//    }
//
//    public static void drawCenteredText(GuiGraphics ctx, String text, int cx, int y, int color) {
//        drawCenteredText(ctx, text, cx, y, color, Fonts.DEFAULT, 11.0f);
//    }
//
//    public static void drawCenteredText(GuiGraphics ctx, String text, int cx, int y, int color, Fonts font) {
//        drawCenteredText(ctx, text, cx, y, color, font, 11.0f);
//    }
//
//    public static void drawCenteredText(GuiGraphics ctx, String text, int cx, int y, int color, Fonts font, float size) {
//        int tw = measureText(text, font, size);
//        drawText(ctx, text, cx - tw / 2, y, color, font, size);
//    }
//    
//    public static void drawCenteredShadowText(GuiGraphics ctx, String text, int cx, int y, int color, Fonts font) {
//        drawCenteredShadowText(ctx, text, cx, y, color, font, 11.0f);
//    }
//
//    public static void drawCenteredShadowText(GuiGraphics ctx, String text, int cx, int y, int color, Fonts font, float size) {
//        int tw = measureText(text, font, size);
//        drawShadowText(ctx, text, cx - tw / 2, y, color, font, size);
//    }
//    
//    public static void drawRightAlignedText(GuiGraphics ctx, String text, int rx, int y, int color, Fonts font) {
//        drawRightAlignedText(ctx, text, rx, y, color, font, 11.0f);
//    }
//
//    public static void drawRightAlignedText(GuiGraphics ctx, String text, int rx, int y, int color, Fonts font, float size) {
//        drawText(ctx, text, rx - measureText(text, font, size), y, color, font, size);
//    }
//
//    public static void drawCenteredInBox(GuiGraphics ctx, String text, int x, int y, int w, int h, int color) {
//        drawCenteredInBox(ctx, text, x, y, w, h, color, Fonts.DEFAULT, 11.0f);
//    }
//
//    public static void drawCenteredInBox(GuiGraphics ctx, String text, int x, int y, int w, int h, int color, Fonts font) {
//        drawCenteredInBox(ctx, text, x, y, w, h, color, font, 11.0f);
//    }
//
//    public static void drawCenteredInBox(GuiGraphics ctx, String text, int x, int y, int w, int h, int color, Fonts font, float size) {
//        int tx = x + (w - measureText(text, font, size)) / 2;
//        int ty = y + (int) ((h - getLineHeight(font, size)) / 2);
//        drawText(ctx, text, tx, ty, color, font, size);
//    }
//
//    public static void drawTextWithBackground(GuiGraphics ctx, String text, int x, int y, int textColor, int bgColor, int padding) {
//        int tw = measureText(text);
//        URender.fillRect(ctx, x - padding, y - padding, tw + padding * 2, (int)getLineHeight(Fonts.DEFAULT, 11f) + padding * 2, bgColor);
//        drawText(ctx, text, x, y, textColor);
//    }
//    
//    public static void drawTextOutlined(GuiGraphics ctx, String text, int x, int y, int textColor, int outlineColor, Fonts font) {
//        drawTextOutlined(ctx, text, x, y, textColor, outlineColor, font, 11.0f);
//    }
//
//    public static void drawTextOutlined(GuiGraphics ctx, String text, int x, int y, int textColor, int outlineColor, Fonts font, float size) {
//        drawText(ctx, text, x - 1, y,     outlineColor, font, size);
//        drawText(ctx, text, x + 1, y,     outlineColor, font, size);
//        drawText(ctx, text, x,     y - 1, outlineColor, font, size);
//        drawText(ctx, text, x,     y + 1, outlineColor, font, size);
//        drawText(ctx, text, x - 1, y - 1, outlineColor, font, size);
//        drawText(ctx, text, x + 1, y - 1, outlineColor, font, size);
//        drawText(ctx, text, x - 1, y + 1, outlineColor, font, size);
//        drawText(ctx, text, x + 1, y + 1, outlineColor, font, size);
//        drawText(ctx, text, x,     y,     textColor,    font, size);
//    }
//
//    public static void drawWrappedText(GuiGraphics ctx, String text, int x, int y, int maxWidth, int color, Fonts font) {
//        drawWrappedText(ctx, text, x, y, maxWidth, color, font, 11.0f);
//    }
//
//    public static void drawWrappedText(GuiGraphics ctx, String text, int x, int y, int maxWidth, int color, Fonts font, float size) {
//        List<String> lines = wrapText(text, maxWidth, font, size);
//        int lh = (int) (getLineHeight(font, size) + 2);
//        for (int i = 0; i < lines.size(); i++) drawText(ctx, lines.get(i), x, y + i * lh, color, font, size);
//    }
//
//    public static int measureText(String text) {
//        return measureText(text, Fonts.DEFAULT, 11.0f);
//    }
//
//    public static int measureText(String text, Fonts font) {
//        return measureText(text, font, 11.0f);
//    }
//
//    public static int measureText(String text, Fonts font, float size) {
//        if (text == null || text.isEmpty()) return 0;
//        CFontRenderer r = getRenderer(font);
//        if (r == null) return 0;
//        return r.getStringWidth(text, size);
//    }
//
//    public static int measureTextHeight(String text, int maxWidth, Fonts font) {
//        return measureTextHeight(text, maxWidth, font, 11.0f);
//    }
//    
//    public static int measureTextHeight(String text, int maxWidth, Fonts font, float size) {
//        return wrapText(text, maxWidth, font, size).size() * (int)(getLineHeight(font, size) + 2);
//    }
//
//    public static int getLineHeight() {
//        return (int) getLineHeight(Fonts.DEFAULT, 11.0f);
//    }
//    
//    public static float getLineHeight(Fonts font, float size) {
//        CFontRenderer r = getRenderer(font);
//        if (r == null) return size;
//        return r.getFontHeight(size);
//    }
//
//    public static List<String> wrapText(String text, int maxWidth) {
//        return wrapText(text, maxWidth, Fonts.DEFAULT, 11.0f);
//    }
//
//    public static List<String> wrapText(String text, int maxWidth, Fonts font) {
//        return wrapText(text, maxWidth, font, 11.0f);
//    }
//    
//    public static List<String> wrapText(String text, int maxWidth, Fonts font, float size) {
//        List<String> lines = new ArrayList<>();
//        if (text == null) return lines;
//        String[] words = text.split(" ");
//        StringBuilder currentLine = new StringBuilder();
//        
//        for (String word : words) {
//            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
//            if (measureText(testLine, font, size) > maxWidth) {
//                if (currentLine.length() > 0) {
//                    lines.add(currentLine.toString());
//                    currentLine = new StringBuilder(word);
//                } else {
//                    lines.add(word); 
//                }
//            } else {
//                currentLine.append(currentLine.length() == 0 ? word : " " + word);
//            }
//        }
//        if (currentLine.length() > 0) lines.add(currentLine.toString());
//        return lines;
//    }
//    
//    public static String ellipsis(String text, int maxWidth) {
//        return ellipsis(text, maxWidth, Fonts.DEFAULT, 11.0f);
//    }
//
//    public static String ellipsis(String text, int maxWidth, Fonts font) {
//        return ellipsis(text, maxWidth, font, 11.0f);
//    }
//    
//    public static String ellipsis(String text, int maxWidth, Fonts font, float size) {
//        if (measureText(text, font, size) <= maxWidth) return text;
//        String e = "...";
//        int ew = measureText(e, font, size);
//        while (!text.isEmpty() && measureText(text, font, size) + ew > maxWidth)
//            text = text.substring(0, text.length() - 1);
//        return text + e;
//    }
//
//    public static String clipToWidth(String text, int offset, int maxWidth, Fonts font) {
//        return clipToWidth(text, offset, maxWidth, font, 11.0f);
//    }
//    
//    public static String clipToWidth(String text, int offset, int maxWidth, Fonts font, float size) {
//        if (offset < 0) offset = 0;
//        if (offset > text.length()) offset = text.length();
//        String sub = text.substring(offset);
//        while (!sub.isEmpty() && measureText(sub, font, size) > maxWidth)
//            sub = sub.substring(0, sub.length() - 1);
//        return sub;
//    }
//}