package silence.simsool.lucent.ui.utils;

import static silence.simsool.lucent.Lucent.mc;

import java.util.UUID;

import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import silence.simsool.lucent.mixin.accessors.InventoryScreenAccessor;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

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

	public void renderItem(GuiGraphics context, ItemStack item, float x, float y, float scale) {
		Matrix3x2fStack pose = context.pose();
		pose.pushMatrix();
		pose.translate(x, y);
		pose.scale(scale, scale);
		context.renderItem(item, 0, 0);
		context.renderItemDecorations(mc.font, item, 0, 0);
		pose.popMatrix();
	}

	/**
	 * 토글 버튼 그리기
	 * @param onProgress 0.0(off) ~ 1.0(on) 상태 진행도
	 * @param hoverProgress 0.0(기본) ~ 1.0(호버됨) 상태 진행도
	 */
	public static void drawToggleButton(float x, float y, float w, float h, float onProgress, float hoverProgress) {
		int bgOff = UIColors.DIM_GRAY;
		int bgOn = UIColors.ACCENT_BLUE;
		int bgColor = UAnimation.lerpColor(bgOff, bgOn, onProgress);

		float radius = h / 2f;
		NVGRenderer.rect(x, y, w, h, bgColor, radius);

		float padding = 2f;
		float baseCircleRadius = (h - padding * 2) / 2f;
		// 호버 시 원이 약간 커지는 애니메이션 (최대 1.5px 확장)
		float hoverExpansion = 1.5f * hoverProgress;
		float currentCircleRadius = baseCircleRadius + hoverExpansion;

		float minX = x + padding + baseCircleRadius;
		float maxX = x + w - padding - baseCircleRadius;
		float circleX = UAnimation.lerp(minX, maxX, onProgress);
		float circleY = y + h / 2f;

		// 원에 그림자를 줘서 입체감 추가
		NVGRenderer.dropShadow(circleX - currentCircleRadius, circleY - currentCircleRadius, currentCircleRadius * 2, currentCircleRadius * 2, 3f, 0f, currentCircleRadius);
		NVGRenderer.circle(circleX, circleY, currentCircleRadius, UIColors.PURE_WHITE);
	}

	public static Vec3 getRenderPos(Entity entity) {
		return new Vec3(entity.getX(), entity.getY(), entity.getZ());
	}

	public static void drawPlayerWithRotation(GuiGraphics graphics, int x1, int y1, int x2, int y2, int scale, float yaw, float pitch, LivingEntity entity) {
		float originalYRot = entity.getYRot();
		float originalYBodyRot = entity.yBodyRot;
		float originalYHeadRot = entity.yHeadRot;
		float originalYRotO = entity.yRotO;
		float originalYBodyRotO = entity.yBodyRotO;
		float originalYHeadRotO = entity.yHeadRotO;

		float actualYaw = 180.0f + yaw;

		entity.setYRot(actualYaw);
		entity.yBodyRot = actualYaw;
		entity.yHeadRot = actualYaw;
		entity.yRotO = actualYaw;
		entity.yBodyRotO = actualYaw;
		entity.yHeadRotO = actualYaw;

		EntityRenderState state = InventoryScreenAccessor.invokeExtractRenderState(entity);

		entity.setYRot(originalYRot);
		entity.yBodyRot = originalYBodyRot;
		entity.yHeadRot = originalYHeadRot;
		entity.yRotO = originalYRotO;
		entity.yBodyRotO = originalYBodyRotO;
		entity.yHeadRotO = originalYHeadRotO;

		if (state == null) return;

		if (state instanceof LivingEntityRenderState livingState) {
			livingState.bodyRot = 0.0f + yaw;
			livingState.yRot = 0.0f;
			livingState.xRot = -pitch;
			
			livingState.boundingBoxWidth = livingState.boundingBoxWidth / livingState.scale;
			livingState.boundingBoxHeight = livingState.boundingBoxHeight / livingState.scale;
			livingState.scale = 1.0f;
		}

		Quaternionf q1 = new Quaternionf().rotateZ((float) Math.toRadians(180f));
		Quaternionf q2 = new Quaternionf().rotateX((float) Math.toRadians(pitch));
		q1.mul(q2);

		Vector3f offset = new Vector3f(0.0f, state.boundingBoxHeight / 2.0f + 0.0625f, 0.0f);

		graphics.submitEntityRenderState(state, (float) scale, offset, q1, q2, x1, y1, x2, y2);
	}

}