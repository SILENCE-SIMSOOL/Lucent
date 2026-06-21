package silence.simsool.lucent.ui.utils;

import static silence.simsool.lucent.Lucent.mc;

import java.util.UUID;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class URender {

	public static void drawRect(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {
		graphics.fill(x, y, x + w, y + h, color);
	}

	public static void drawBorder(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int borderWidth, int color) {
		drawRect(graphics, x - borderWidth, y - borderWidth, width + borderWidth * 2, borderWidth, color);
		drawRect(graphics, x - borderWidth, y + height, width + borderWidth * 2, borderWidth, color);
		drawRect(graphics, x - borderWidth, y, borderWidth, height, color);
		drawRect(graphics, x + width, y, borderWidth, height, color);
	}

	public static void drawImage(GuiGraphicsExtractor graphics, Identifier img, int x, int y, int w, int h) {
		if (img == null) return;
		graphics.blit(RenderPipelines.GUI_TEXTURED, img, x, y, 0f, 0f, w, h, w, h);
	}

	public static void drawImage(GuiGraphicsExtractor graphics, Identifier img, int x, int y, int w, int h, float u, float v) {
		if (img == null) return;
		graphics.blit(RenderPipelines.GUI_TEXTURED, img, x, y, u, v, w, h, w, h);
	}

	public static void drawPlayerHead(GuiGraphicsExtractor graphics, int x, int y, int size, UUID uuid) {
		if (uuid == null || mc.getConnection() == null) return;
		PlayerInfo entry = mc.getConnection().getPlayerInfo(uuid);
		if (entry != null) PlayerFaceExtractor.extractRenderState(graphics, entry.getSkin(), x, y, size);
		else if (mc.player != null && mc.player.getUUID().equals(uuid)) PlayerFaceExtractor.extractRenderState(graphics, mc.player.getSkin(), x, y, size);
	}

	public void renderItem(GuiGraphicsExtractor context, ItemStack item, float x, float y, float scale) {
		Matrix3x2fStack pose = context.pose();
		pose.pushMatrix();
		pose.translate(x, y);
		pose.scale(scale, scale);
		context.item(item, 0, 0);
		context.itemDecorations(mc.font, item, 0, 0);
		pose.popMatrix();
	}

}