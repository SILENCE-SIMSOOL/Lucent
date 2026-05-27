package silence.simsool.lucent.general.utils.render;

import static silence.simsool.lucent.Lucent.mc;

import java.awt.Color;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.models.data.render.BeaconBeamData;
import silence.simsool.lucent.general.models.data.render.BoxData;
import silence.simsool.lucent.general.models.data.render.LineData;
import silence.simsool.lucent.general.models.data.render.TextData;
import silence.simsool.lucent.general.utils.useful.URender;
import silence.simsool.lucent.mixin.accessors.BeaconBeamAccessor;
import silence.simsool.lucent.ui.utils.UColor;

public class RenderUtils {

	private static final List<LineData> queuedLines = new ObjectArrayList<>();
	private static final List<BoxData> queuedFilledBoxes = new ObjectArrayList<>();
	private static final List<BoxData> queuedWireBoxes = new ObjectArrayList<>();
	private static final List<TextData> queuedTexts = new ObjectArrayList<>();
	private static final List<BeaconBeamData> queuedBeaconBeams = new ObjectArrayList<>();

	private static final Identifier BEAM_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/beacon_beam.png");

	private static float lastTickDelta = 1.0f;

	public static void init() {
		LucentEvent.WORLD_RENDER_LAST.register(event -> {
			PoseStack matrix = event.context.matrices();
			MultiBufferSource.BufferSource bufferSource = (MultiBufferSource.BufferSource) event.context.consumers();
			if (bufferSource == null) {
				clearAll();
				RoundRectPIPRenderer.clear();
				return;
			}

			lastTickDelta = event.partialTick;

			Vec3 camera = mc.gameRenderer.getMainCamera().position();

			matrix.pushPose();
			matrix.translate(-camera.x, -camera.y, -camera.z);

			renderQueuedLinesAndWireBoxes(matrix, bufferSource);
			renderQueuedFilledBoxes(matrix, bufferSource);

			matrix.popPose();

			renderQueuedBeaconBeams(matrix, bufferSource, camera);
			renderQueuedTexts(matrix, bufferSource, camera);

			clearAll();
			RoundRectPIPRenderer.clear();
		});
	}

	// Draw Box
	public static void drawBox(Object target, Object color, boolean depth) {
		queuedFilledBoxes.add(new BoxData(toAABB(target), toColorInt(color), 3f, depth));
	}

	public static void drawBox(Object target, Object color) {
		drawBox(target, color, true);
	}

	// Draw Box Line
	public static void drawBoxLine(Object target, Object color, float thickness, boolean depth) {
		queuedWireBoxes.add(new BoxData(toAABB(target), toColorInt(color), thickness, depth));
	}

	public static void drawBoxLine(Object target, Object color, float thickness) {
		drawBoxLine(target, color, thickness, true);
	}

	// Draw Line
	public static void drawLine(Object from, Object to, Object color, boolean depth, float thickness) {
		drawLine(from, to, color, color, depth, thickness);
	}

	public static void drawLine(Object from, Object to, Object color, float thickness) {
		drawLine(from, to, color, color, true, thickness);
	}

	public static void drawLine(Object from, Object to, Object color1, Object color2, boolean depth, float thickness) {
		queuedLines.add(new LineData(toVec3(from), toVec3(to), toColorInt(color1), toColorInt(color2), thickness, depth));
	}

	public static void drawLine(Object from, Object to, Object color1, Object color2, float thickness) {
		drawLine(from, to, color1, color2, true, thickness);
	}

	// Draw Tracer
	public static void drawTracer(Object to, Object color, boolean depth, float thickness) {
		Vec3 from = URender.getRenderPos(mc.player).add(mc.player.getForward().add(0.0, mc.player.getEyeHeight(), 0.0));
		drawLine(from, to, color, depth, thickness);
	}

	public static void drawTracer(Object to, Object color, float thickness) {
		drawTracer(to, color, true, thickness);
	}

	// Draw Selected Box
	public static void drawSelectedBox(Object target, Object color, float expandX, float expandY, float expandZ, boolean depth) {
		int c = toColorInt(color);
		if (target instanceof AABB aabb) {
			drawBox(aabb.inflate(expandX, expandY, expandZ), c, depth);
		} else {
			BlockPos pos = toBlockPos(target);
			BlockState state = mc.level.getBlockState(pos);
			VoxelShape shape = state.getShape(mc.level, pos);
			if (shape.isEmpty()) {
				drawBox(new AABB(pos).inflate(expandX, expandY, expandZ), c, depth);
				return;
			}
			AABB aabb = shape.bounds().move(pos).inflate(expandX, expandY, expandZ);
			drawBox(aabb, c, depth);
		}
	}

	public static void drawSelectedBox(Object target, Object color, float expandX, float expandY, float expandZ) {
		drawSelectedBox(target, color, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBox(Object target, Object color, boolean depth) {
		drawSelectedBox(target, color, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBox(Object target, Object color) {
		drawSelectedBox(target, color, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBox(Object target, Object color, float expandXYZ, boolean depth) {
		drawSelectedBox(target, color, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBox(Object target, Object color, float expandXYZ) {
		drawSelectedBox(target, color, expandXYZ, expandXYZ, expandXYZ, true);
	}

	// Draw Selected Box Line
	public static void drawSelectedBoxLine(Object target, Object color, float thickness, float expandX, float expandY, float expandZ, boolean depth) {
		int c = toColorInt(color);
		if (target instanceof AABB aabb) {
			drawBoxLine(aabb.inflate(expandX, expandY, expandZ), c, thickness, depth);
		} else {
			BlockPos pos = toBlockPos(target);
			BlockState state = mc.level.getBlockState(pos);
			VoxelShape shape = state.getShape(mc.level, pos);
			if (shape.isEmpty()) {
				drawBoxLine(new AABB(pos).inflate(expandX, expandY, expandZ), c, thickness, depth);
				return;
			}
			AABB aabb = shape.bounds().move(pos).inflate(expandX, expandY, expandZ);
			drawBoxLine(aabb, c, thickness, depth);
		}
	}

	public static void drawSelectedBoxLine(Object target, Object color, float thickness, float expandX, float expandY, float expandZ) {
		drawSelectedBoxLine(target, color, thickness, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBoxLine(Object target, Object color, float thickness, boolean depth) {
		drawSelectedBoxLine(target, color, thickness, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBoxLine(Object target, Object color, float thickness) {
		drawSelectedBoxLine(target, color, thickness, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBoxLine(Object target, Object color, float thickness, float expandXYZ, boolean depth) {
		drawSelectedBoxLine(target, color, thickness, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBoxLine(Object target, Object color, float thickness, float expandXYZ) {
		drawSelectedBoxLine(target, color, thickness, expandXYZ, expandXYZ, expandXYZ, true);
	}

	// Draw Text
	public static void drawText(String text, Object pos, float scale, boolean depth) {
		Font font = mc.font;
		queuedTexts.add(new TextData(text, toVec3(pos), scale, depth, mc.gameRenderer.getMainCamera().rotation(), font, font.width(text)));
	}

	public static void drawText(String text, Object pos, float scale) {
		drawText(text, pos, scale, true);
	}

	// Draw Styled Box
	public static void drawStyledBox(Object target, Object color, int style, boolean depth) {
		int c = toColorInt(color);
		AABB aabb = toAABB(target);
		switch (style) {
			case 0 -> drawBox(aabb, c, depth);
			case 1 -> drawBoxLine(aabb, c, 3f, depth);
			case 2 -> {
				drawBox(aabb, UColor.withAlpha(c, UColor.getAlpha(c) / 2), depth);
				drawBoxLine(aabb, c, 3f, depth);
			}
		}
	}

	public static void drawStyledBox(Object target, Object color, int style) {
		drawStyledBox(target, color, style, true);
	}

	// Draw Cylinder
	public static void drawCylinder(Object center, float radius, float height, Object color, int segments, float thickness, boolean depth) {
		Vec3 cVec = toVec3(center);
		int c = toColorInt(color);
		double angleStep = 2.0 * Math.PI / segments;

		for (int i = 0; i < segments; i++) {
			double angle1 = i * angleStep;
			double angle2 = (i + 1) * angleStep;

			float x1 = (float) (radius * Math.cos(angle1));
			float z1 = (float) (radius * Math.sin(angle1));
			float x2 = (float) (radius * Math.cos(angle2));
			float z2 = (float) (radius * Math.sin(angle2));

			Vec3 p1Top = cVec.add(x1, height, z1);
			Vec3 p2Top = cVec.add(x2, height, z2);
			Vec3 p1Bottom = cVec.add(x1, 0, z1);
			Vec3 p2Bottom = cVec.add(x2, 0, z2);

			queuedLines.add(new LineData(p1Top, p2Top, c, c, thickness, depth));
			queuedLines.add(new LineData(p1Bottom, p2Bottom, c, c, thickness, depth));
			queuedLines.add(new LineData(p1Bottom, p1Top, c, c, thickness, depth));
		}
	}

	public static void drawCylinder(Object center, float radius, float height, Object color, int segments, float thickness) {
		drawCylinder(center, radius, height, color, segments, thickness, true);
	}

	// Draw Beacon Beam
	public static void drawBeaconBeam(Object pos, Object color, float partialTicks, long gameTime, boolean isScoping) {
		queuedBeaconBeams.add(new BeaconBeamData(toVec3(pos), toColorInt(color), partialTicks, gameTime, isScoping));
	}

	public static void drawBeaconBeam(Object pos, Object color, boolean isScoping) {
		long gameTime = mc.level != null ? mc.level.getGameTime() : 0L;
		drawBeaconBeam(pos, color, lastTickDelta, gameTime, isScoping);
	}

	public static void drawBeaconBeam(Object pos, Object color) {
		drawBeaconBeam(pos, color, false);
	}

	private static void clearAll() {
		queuedLines.clear();
		queuedFilledBoxes.clear();
		queuedWireBoxes.clear();
		queuedTexts.clear();
		queuedBeaconBeams.clear();
	}

	private static Vec3 toVec3(Object obj) {
		if (obj instanceof Vec3 vec) return vec;
		if (obj instanceof BlockPos pos) return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		if (obj instanceof AABB aabb) return aabb.getCenter();
		throw new IllegalArgumentException("Unsupported type for Vec3 conversion: " + (obj == null ? "null" : obj.getClass()));
	}

	private static BlockPos toBlockPos(Object obj) {
		if (obj instanceof BlockPos pos) return pos;
		if (obj instanceof Vec3 vec) return new BlockPos((int) Math.floor(vec.x), (int) Math.floor(vec.y), (int) Math.floor(vec.z));
		if (obj instanceof AABB aabb) return new BlockPos((int) Math.floor(aabb.minX), (int) Math.floor(aabb.minY), (int) Math.floor(aabb.minZ));
		throw new IllegalArgumentException("Unsupported type for BlockPos conversion: " + (obj == null ? "null" : obj.getClass()));
	}

	private static AABB toAABB(Object obj) {
		if (obj instanceof AABB aabb) return aabb;
		if (obj instanceof BlockPos pos) return new AABB(pos);
		if (obj instanceof Vec3 vec) return new AABB(vec.x, vec.y, vec.z, vec.x + 1.0, vec.y + 1.0, vec.z + 1.0);
		throw new IllegalArgumentException("Unsupported type for AABB conversion: " + (obj == null ? "null" : obj.getClass()));
	}

	private static int toColorInt(Object color) {
		if (color instanceof Integer c) return c;
		if (color instanceof Color c) return c.getRGB();
		throw new IllegalArgumentException("Unsupported type for Color conversion: " + (color == null ? "null" : color.getClass()));
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

	private static void renderQueuedBeaconBeams(PoseStack matrix, MultiBufferSource bufferSource, Vec3 camera) {
		if (queuedBeaconBeams.isEmpty()) return;
		for (BeaconBeamData beam : queuedBeaconBeams) {
			matrix.pushPose();
			matrix.translate(beam.pos.x - camera.x, beam.pos.y - camera.y, beam.pos.z - camera.z);

			double centerX = beam.pos.x + 0.5;
			double centerZ = beam.pos.z + 0.5;
			double dx = camera.x - centerX;
			double dz = camera.z - centerZ;
			float length = (float) Math.sqrt(dx * dx + dz * dz);

			float scale = beam.isScoping ? 1.0f : Math.max(1.0f, length * 0.010416667f);
			float animationTime = (float) (beam.gameTime % 40) + beam.partialTicks;

			BeaconBeamAccessor.invokeRenderBeam(
				matrix,
				mc.gameRenderer.getFeatureRenderDispatcher().getSubmitNodeStorage(),
				BEAM_TEXTURE,
				1.0f,
				animationTime,
				0,
				319,
				beam.color,
				0.2f * scale,
				0.25f * scale
			);
			matrix.popPose();
		}
	}

	public static boolean isFullyOpaque(int color) {
		return UColor.getAlpha(color) == 0xFF;
	}

	public static RenderType resolveLineRenderType(boolean depth, boolean fullyOpaque) {
		if (depth && fullyOpaque) return RenderTypes.lines();
		if (depth) return RenderTypes.linesTranslucent();
		if (fullyOpaque) return LucentRenderType.LINES_ESP;
		return LucentRenderType.LINES_TRANSLUCENT_ESP;
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