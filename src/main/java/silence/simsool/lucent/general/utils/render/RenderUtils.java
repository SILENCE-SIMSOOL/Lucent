package silence.simsool.lucent.general.utils.render;

import static silence.simsool.lucent.Lucent.mc;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import silence.simsool.lucent.ui.utils.UColor;

public class RenderUtils {
	private static final List<LineData> queuedLines = new ObjectArrayList<>();
	private static final List<BoxData> queuedFilledBoxes = new ObjectArrayList<>();
	private static final List<BoxData> queuedWireBoxes = new ObjectArrayList<>();
	private static final List<TextData> queuedTexts = new ObjectArrayList<>();

	private static class LineData {
		final Vec3 from, to;
		final int color1, color2;
		final float thickness;
		final boolean depth;

		LineData(Vec3 from, Vec3 to, int color1, int color2, float thickness, boolean depth) {
			this.from = from;
			this.to = to;
			this.color1 = color1;
			this.color2 = color2;
			this.thickness = thickness;
			this.depth = depth;
		}

		RenderType renderType() {
			boolean fullyOpaque = isFullyOpaque(color1) && isFullyOpaque(color2);
			return resolveLineRenderType(depth, fullyOpaque);
		}
	}

	private static class BoxData {
		final AABB aabb;
		final float r, g, b, a;
		final float thickness;
		final boolean depth;

		BoxData(AABB aabb, int color, float thickness, boolean depth) {
			this.aabb = aabb;
			this.r = UColor.getRedF(color);
			this.g = UColor.getGreenF(color);
			this.b = UColor.getBlueF(color);
			this.a = UColor.getAlphaF(color);
			this.thickness = thickness;
			this.depth = depth;
		}

		RenderType lineRenderType() {
			boolean fullyOpaque = a >= 0.999f;
			return resolveLineRenderType(depth, fullyOpaque);
		}

		RenderType filledRenderType() {
			return depth ? RenderTypes.debugFilledBox() : LucentRenderType.QUADS_ESP;
		}
	}

	private static class TextData {
		final String text;
		final Vec3 pos;
		final float scale;
		final boolean depth;
		final Quaternionf cameraRotation;
		final Font font;
		final float textWidth;

		TextData(String text, Vec3 pos, float scale, boolean depth, Quaternionf rotation, Font font, float width) {
			this.text = text;
			this.pos = pos;
			this.scale = scale;
			this.depth = depth;
			this.cameraRotation = rotation;
			this.font = font;
			this.textWidth = width;
		}
	}

	static {
		WorldRenderEvents.END_MAIN.register(context -> {
			PoseStack matrix = context.matrices();
			MultiBufferSource.BufferSource bufferSource = (MultiBufferSource.BufferSource) context.consumers();
			if (bufferSource == null) {
				clearAll();
				RoundRectPIPRenderer.clear();
				return;
			}

			Vec3 camera = mc.gameRenderer.getMainCamera().position();

			matrix.pushPose();
			matrix.translate(-camera.x, -camera.y, -camera.z);

			renderQueuedLinesAndWireBoxes(matrix, bufferSource);
			renderQueuedFilledBoxes(matrix, bufferSource);

			matrix.popPose();

			renderQueuedTexts(matrix, bufferSource, camera);

			clearAll();
			RoundRectPIPRenderer.clear();
		});
	}

	private static void clearAll() {
		queuedLines.clear();
		queuedFilledBoxes.clear();
		queuedWireBoxes.clear();
		queuedTexts.clear();
	}

	private static void renderQueuedLinesAndWireBoxes(PoseStack matrix, MultiBufferSource.BufferSource bufferSource) {
		if (queuedLines.isEmpty() && queuedWireBoxes.isEmpty()) return;
		PoseStack.Pose last = matrix.last();

		for (LineData line : queuedLines) {
			VertexConsumer buffer = bufferSource.getBuffer(line.renderType());
			Vector3f start = new Vector3f((float) line.from.x, (float) line.from.y, (float) line.from.z);
			Vec3 dir = line.to.subtract(line.from);
			PrimitiveRenderer.renderVector(last, buffer, start, dir, line.color1, line.color2, line.thickness);
		}

		for (BoxData box : queuedWireBoxes) {
			VertexConsumer buffer = bufferSource.getBuffer(box.lineRenderType());
			PrimitiveRenderer.renderLineBox(last, buffer, box.aabb, box.r, box.g, box.b, box.a, box.thickness);
		}
	}

	private static void renderQueuedFilledBoxes(PoseStack matrix, MultiBufferSource.BufferSource bufferSource) {
		if (queuedFilledBoxes.isEmpty()) return;
		PoseStack.Pose last = matrix.last();

		for (BoxData box : queuedFilledBoxes) {
			VertexConsumer buffer = bufferSource.getBuffer(box.filledRenderType());
			PrimitiveRenderer.addChainedFilledBoxVertices(last, buffer, (float) box.aabb.minX, (float) box.aabb.minY, (float) box.aabb.minZ, (float) box.aabb.maxX, (float) box.aabb.maxY, (float) box.aabb.maxZ, box.r, box.g, box.b, box.a);
		}
	}

	private static void renderQueuedTexts(PoseStack matrix, MultiBufferSource.BufferSource bufferSource, Vec3 camera) {
		for (TextData textData : queuedTexts) {
			matrix.pushPose();
			Matrix4f pose = matrix.last().pose();
			float scaleFactor = textData.scale * 0.025f;
			pose.translate((float) (textData.pos.x - camera.x), (float) (textData.pos.y - camera.y), (float) (textData.pos.z - camera.z)).rotate(textData.cameraRotation).scale(scaleFactor, -scaleFactor, scaleFactor);
			textData.font.drawInBatch(textData.text, -textData.textWidth / 2f, 0f, -1, true, pose, bufferSource, textData.depth ? Font.DisplayMode.POLYGON_OFFSET : Font.DisplayMode.SEE_THROUGH, 0, LightTexture.FULL_BRIGHT);
			matrix.popPose();
		}
	}

	private static boolean isFullyOpaque(int color) {
		return UColor.getAlpha(color) == 0xFF;
	}

	private static RenderType resolveLineRenderType(boolean depth, boolean fullyOpaque) {
		if (depth && fullyOpaque) return RenderTypes.lines();
		if (depth) return RenderTypes.linesTranslucent();
		if (fullyOpaque) return LucentRenderType.LINES_ESP;
		return LucentRenderType.LINES_TRANSLUCENT_ESP;
	}

	public static void drawLine(Vec3 from, Vec3 to, int color, boolean depth, float thickness) {
		queuedLines.add(new LineData(from, to, color, color, thickness, depth));
	}

	public static void drawLine(Vec3 from, Vec3 to, int color1, int color2, boolean depth, float thickness) {
		queuedLines.add(new LineData(from, to, color1, color2, thickness, depth));
	}

	public static void drawFilledBox(AABB aabb, int color, boolean depth) {
		queuedFilledBoxes.add(new BoxData(aabb, color, 3f, depth));
	}

	public static void drawWireFrameBox(AABB aabb, int color, float thickness, boolean depth) {
		queuedWireBoxes.add(new BoxData(aabb, color, thickness, depth));
	}

	/**
	 * Renders a wireframe box around a block's actual hitbox (VoxelShape).
	 */
	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness, float expandX, float expandY, float expandZ, boolean depth) {
		if (mc.level == null) return;
		BlockState state = mc.level.getBlockState(pos);
		VoxelShape shape = state.getShape(mc.level, pos);
		if (shape.isEmpty()) {
			drawWireFrameBox(new AABB(pos).inflate(expandX, expandY, expandZ), color, thickness, depth);
			return;
		}
		AABB aabb = shape.bounds().move(pos).inflate(expandX, expandY, expandZ);
		drawWireFrameBox(aabb, color, thickness, depth);
	}

	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness, boolean depth) {
		drawSelectedBoxLine(pos, color, thickness, 0, 0, 0, depth);
	}

	/**
	 * Renders a filled box around a block's actual hitbox (VoxelShape).
	 */
	public static void drawSelectedBoxFilled(BlockPos pos, int color, float expandX, float expandY, float expandZ, boolean depth) {
		if (mc.level == null) return;
		BlockState state = mc.level.getBlockState(pos);
		VoxelShape shape = state.getShape(mc.level, pos);
		if (shape.isEmpty()) {
			drawFilledBox(new AABB(pos).inflate(expandX, expandY, expandZ), color, depth);
			return;
		}
		AABB aabb = shape.bounds().move(pos).inflate(expandX, expandY, expandZ);
		drawFilledBox(aabb, color, depth);
	}

	public static void drawSelectedBoxFilled(BlockPos pos, int color, boolean depth) {
		drawSelectedBoxFilled(pos, color, 0, 0, 0, depth);
	}

	public static void drawText(String text, Vec3 pos, float scale, boolean depth) {
		Font font = mc.font;
		queuedTexts.add(new TextData(text, pos, scale, depth, mc.gameRenderer.getMainCamera().rotation(), font, font.width(text)));
	}

	public static void drawStyledBox(AABB aabb, int color, int style, boolean depth) {
		switch (style) {
			case 0 -> drawFilledBox(aabb, color, depth);
			case 1 -> drawWireFrameBox(aabb, color, 3f, depth);
			case 2 -> {
				drawFilledBox(aabb, UColor.withAlpha(color, UColor.getAlpha(color) / 2), depth);
				drawWireFrameBox(aabb, color, 3f, depth);
			}
		}
	}

	public static void drawCylinder(Vec3 center, float radius, float height, int color, int segments, float thickness, boolean depth) {
		double angleStep = 2.0 * Math.PI / segments;

		for (int i = 0; i < segments; i++) {
			double angle1 = i * angleStep;
			double angle2 = (i + 1) * angleStep;

			float x1 = (float) (radius * Math.cos(angle1));
			float z1 = (float) (radius * Math.sin(angle1));
			float x2 = (float) (radius * Math.cos(angle2));
			float z2 = (float) (radius * Math.sin(angle2));

			Vec3 p1Top = center.add(x1, height, z1);
			Vec3 p2Top = center.add(x2, height, z2);
			Vec3 p1Bottom = center.add(x1, 0, z1);
			Vec3 p2Bottom = center.add(x2, 0, z2);

			queuedLines.add(new LineData(p1Top, p2Top, color, color, thickness, depth));
			queuedLines.add(new LineData(p1Bottom, p2Bottom, color, color, thickness, depth));
			queuedLines.add(new LineData(p1Bottom, p1Top, color, color, thickness, depth));
		}
	}

	public static class PrimitiveRenderer {
		private static final int[] EDGES = { 0, 1, 1, 5, 5, 4, 4, 0, 3, 2, 2, 6, 6, 7, 7, 3, 0, 3, 1, 2, 5, 6, 4, 7 };

		public static void renderLineBox(PoseStack.Pose pose, VertexConsumer buffer, AABB aabb, float r, float g, float b, float a, float thickness) {
			float x0 = (float) aabb.minX;
			float y0 = (float) aabb.minY;
			float z0 = (float) aabb.minZ;
			float x1 = (float) aabb.maxX;
			float y1 = (float) aabb.maxY;
			float z1 = (float) aabb.maxZ;

			float[] corners = { x0, y0, z0, x1, y0, z0, x1, y1, z0, x0, y1, z0, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1 };

			for (int i = 0; i < EDGES.length; i += 2) {
				int i0 = EDGES[i] * 3;
				int i1 = EDGES[i + 1] * 3;
				float cx0 = corners[i0];
				float cy0 = corners[i0 + 1];
				float cz0 = corners[i0 + 2];
				float cx1 = corners[i1];
				float cy1 = corners[i1 + 1];
				float cz1 = corners[i1 + 2];
				float dx = cx1 - cx0;
				float dy = cy1 - cy0;
				float dz = cz1 - cz0;

				buffer.addVertex(pose, cx0, cy0, cz0).setColor(r, g, b, a).setNormal(pose, dx, dy, dz).setLineWidth(thickness);
				buffer.addVertex(pose, cx1, cy1, cz1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz).setLineWidth(thickness);
			}
		}

		public static void addChainedFilledBoxVertices(PoseStack.Pose pose, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
			Matrix4f matrix = pose.pose();
			vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
			vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);
			vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
			vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);

			vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
			vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);
			vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
			vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);

			vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
			vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);
			vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
			vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);

			vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
			vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);
			vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
			vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);

			vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
			vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);
			vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
			vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);

			vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
			vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);
			vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
			vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);
		}

		private static void vertex(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float r, float g, float b, float a) {
			buffer.addVertex(matrix, x, y, z).setColor(r, g, b, a);
		}

		public static void renderVector(PoseStack.Pose pose, VertexConsumer buffer, Vector3f start, Vec3 direction, int startColor, int endColor, float thickness) {
			float endX = start.x() + (float) direction.x;
			float endY = start.y() + (float) direction.y;
			float endZ = start.z() + (float) direction.z;
			float nx = (float) direction.x;
			float ny = (float) direction.y;
			float nz = (float) direction.z;

			buffer.addVertex(pose, start.x(), start.y(), start.z()).setColor(startColor).setNormal(pose, nx, ny, nz).setLineWidth(thickness);
			buffer.addVertex(pose, endX, endY, endZ).setColor(endColor).setNormal(pose, nx, ny, nz).setLineWidth(thickness);
		}
	}
}