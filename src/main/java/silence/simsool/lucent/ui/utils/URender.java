package silence.simsool.lucent.ui.utils;

import static silence.simsool.lucent.Lucent.mc;

import java.util.UUID;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class URender {

	public static void drawRect(GuiGraphics graphics, int x, int y, int w, int h, int color) {
		graphics.fill(x, y, x + w, y + h, color);
	}

	public static void drawBorder(GuiGraphics graphics, int x, int y, int width, int height, int borderWidth, int color) {
		drawRect(graphics, x - borderWidth, y - borderWidth, width + borderWidth * 2, borderWidth, color);
		drawRect(graphics, x - borderWidth, y + height, width + borderWidth * 2, borderWidth, color);
		drawRect(graphics, x - borderWidth, y, borderWidth, height, color);
		drawRect(graphics, x + width, y, borderWidth, height, color);
	}

	public static void drawImage(GuiGraphics graphics, Identifier img, int x, int y, int w, int h) {
		if (img == null) return;
		graphics.blit(RenderPipelines.GUI_TEXTURED, img, x, y, 0f, 0f, w, h, w, h);
	}

	public static void drawImage(GuiGraphics graphics, Identifier img, int x, int y, int w, int h, float u, float v) {
		if (img == null) return;
		graphics.blit(RenderPipelines.GUI_TEXTURED, img, x, y, u, v, w, h, w, h);
	}

	public static void drawPlayerHead(GuiGraphics graphics, int x, int y, int size, UUID uuid) {
		if (uuid == null || mc.getConnection() == null) return;
		PlayerInfo entry = mc.getConnection().getPlayerInfo(uuid);
		if (entry != null) PlayerFaceRenderer.draw(graphics, entry.getSkin(), x, y, size);
		else if (mc.player != null && mc.player.getUUID().equals(uuid)) PlayerFaceRenderer.draw(graphics, mc.player.getSkin(), x, y, size);
	}

}