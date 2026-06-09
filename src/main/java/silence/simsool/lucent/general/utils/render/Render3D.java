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
import silence.simsool.lucent.general.enums.RenderStyle;
import silence.simsool.lucent.general.models.data.render.BeaconBeamData;
import silence.simsool.lucent.general.models.data.render.BoxData;
import silence.simsool.lucent.general.models.data.render.LineData;
import silence.simsool.lucent.general.models.data.render.TextData;
import silence.simsool.lucent.general.utils.useful.URender;
import silence.simsool.lucent.mixin.accessors.BeaconBeamAccessor;
import silence.simsool.lucent.ui.utils.UColor;

public class Render3D {

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

	// ==========================================
	// Draw Styled Box
	// ==========================================
	public static void drawStyledBox(AABB aabb, int color, RenderStyle style, boolean depth) {
		switch (style) {
			case RenderStyle.BOX -> drawBox(aabb, color, depth);
			case RenderStyle.LINE -> drawBoxLine(aabb, color, 3f, depth);
			case RenderStyle.FULL -> {
				drawBox(aabb, UColor.withAlpha(color, UColor.getAlpha(color) / 5), depth);
				drawBoxLine(aabb, color, 3f, depth);
			}
		}
	}

	public static void drawStyledBox(AABB aabb, int color, RenderStyle style) {
		drawStyledBox(aabb, color, style, true);
	}

	public static void drawStyledBox(BlockPos pos, int color, RenderStyle style, boolean depth) {
		drawStyledBox(new AABB(pos), color, style, depth);
	}

	public static void drawStyledBox(BlockPos pos, int color, RenderStyle style) {
		drawStyledBox(new AABB(pos), color, style, true);
	}

	public static void drawStyledBox(AABB aabb, Color color, RenderStyle style, boolean depth) {
		drawStyledBox(aabb, color.getRGB(), style, depth);
	}

	public static void drawStyledBox(AABB aabb, Color color, RenderStyle style) {
		drawStyledBox(aabb, color.getRGB(), style, true);
	}

	public static void drawStyledBox(BlockPos pos, Color color, RenderStyle style, boolean depth) {
		drawStyledBox(new AABB(pos), color.getRGB(), style, depth);
	}

	public static void drawStyledBox(BlockPos pos, Color color, RenderStyle style) {
		drawStyledBox(new AABB(pos), color.getRGB(), style, true);
	}

	// ==========================================
	// Draw Box
	// ==========================================
	public static void drawBox(AABB aabb, int color, boolean depth) {
		queuedFilledBoxes.add(new BoxData(aabb, color, 3f, depth));
	}

	public static void drawBox(AABB aabb, int color) {
		drawBox(aabb, color, true);
	}

	public static void drawBox(BlockPos pos, int color, boolean depth) {
		drawBox(new AABB(pos), color, depth);
	}

	public static void drawBox(BlockPos pos, int color) {
		drawBox(pos, color, true);
	}

	public static void drawBox(AABB aabb, Color color, boolean depth) {
		drawBox(aabb, color.getRGB(), depth);
	}

	public static void drawBox(AABB aabb, Color color) {
		drawBox(aabb, color.getRGB(), true);
	}

	public static void drawBox(BlockPos pos, Color color, boolean depth) {
		drawBox(new AABB(pos), color.getRGB(), depth);
	}

	public static void drawBox(BlockPos pos, Color color) {
		drawBox(pos, color, true);
	}

	public static void drawBox(Vec3 pos, int color, boolean depth) {
		drawBox(new AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1), color, depth);
	}

	public static void drawBox(Vec3 pos, int color) {
		drawBox(pos, color, true);
	}

	public static void drawBox(Vec3 pos, Color color, boolean depth) {
		drawBox(new AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1), color.getRGB(), depth);
	}

	public static void drawBox(Vec3 pos, Color color) {
		drawBox(pos, color, true);
	}

	// ==========================================
	// Draw Box Line
	// ==========================================
	public static void drawBoxLine(AABB aabb, int color, float thickness, boolean depth) {
		queuedWireBoxes.add(new BoxData(aabb, color, thickness, depth));
	}

	public static void drawBoxLine(AABB aabb, int color, float thickness) {
		drawBoxLine(aabb, color, thickness, true);
	}

	public static void drawBoxLine(BlockPos pos, int color, float thickness, boolean depth) {
		drawBoxLine(new AABB(pos), color, thickness, depth);
	}

	public static void drawBoxLine(BlockPos pos, int color, float thickness) {
		drawBoxLine(pos, color, thickness, true);
	}

	public static void drawBoxLine(AABB aabb, Color color, float thickness, boolean depth) {
		drawBoxLine(aabb, color.getRGB(), thickness, depth);
	}

	public static void drawBoxLine(AABB aabb, Color color, float thickness) {
		drawBoxLine(aabb, color.getRGB(), thickness, true);
	}

	public static void drawBoxLine(BlockPos pos, Color color, float thickness, boolean depth) {
		drawBoxLine(new AABB(pos), color.getRGB(), thickness, depth);
	}

	public static void drawBoxLine(BlockPos pos, Color color, float thickness) {
		drawBoxLine(pos, color, thickness, true);
	}

	public static void drawBoxLine(Vec3 pos, int color, float thickness, boolean depth) {
		drawBoxLine(new AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1), color, thickness, depth);
	}

	public static void drawBoxLine(Vec3 pos, int color, float thickness) {
		drawBoxLine(pos, color, thickness, true);
	}

	public static void drawBoxLine(Vec3 pos, Color color, float thickness, boolean depth) {
		drawBoxLine(new AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1), color.getRGB(), thickness, depth);
	}

	public static void drawBoxLine(Vec3 pos, Color color, float thickness) {
		drawBoxLine(pos, color, thickness, true);
	}

	// ==========================================
	// Draw Selected Box
	// ==========================================
	public static void drawSelectedBox(AABB aabb, int color, float expandX, float expandY, float expandZ, boolean depth) {
		drawBox(aabb.inflate(expandX, expandY, expandZ), color, depth);
	}

	public static void drawSelectedBox(BlockPos pos, int color, float expandX, float expandY, float expandZ, boolean depth) {
		BlockState state = mc.level.getBlockState(pos);
		VoxelShape shape = state.getShape(mc.level, pos);
		AABB aabb = shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);
		drawBox(aabb.inflate(expandX, expandY, expandZ), color, depth);
	}

	public static void drawSelectedBox(Vec3 pos, int color, float expandX, float expandY, float expandZ, boolean depth) {
		drawBox(new AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1).inflate(expandX, expandY, expandZ), color, depth);
	}

	public static void drawSelectedBox(AABB aabb, int color, float expandX, float expandY, float expandZ) {
		drawSelectedBox(aabb, color, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBox(BlockPos pos, int color, float expandX, float expandY, float expandZ) {
		drawSelectedBox(pos, color, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBox(Vec3 pos, int color, float expandX, float expandY, float expandZ) {
		drawSelectedBox(pos, color, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBox(AABB aabb, int color, boolean depth) {
		drawSelectedBox(aabb, color, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBox(BlockPos pos, int color, boolean depth) {
		drawSelectedBox(pos, color, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBox(Vec3 pos, int color, boolean depth) {
		drawSelectedBox(pos, color, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBox(AABB aabb, int color) {
		drawSelectedBox(aabb, color, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBox(BlockPos pos, int color) {
		drawSelectedBox(pos, color, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBox(Vec3 pos, int color) {
		drawSelectedBox(pos, color, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBox(AABB aabb, int color, float expandXYZ, boolean depth) {
		drawSelectedBox(aabb, color, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBox(BlockPos pos, int color, float expandXYZ, boolean depth) {
		drawSelectedBox(pos, color, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBox(Vec3 pos, int color, float expandXYZ, boolean depth) {
		drawSelectedBox(pos, color, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBox(AABB aabb, int color, float expandXYZ) {
		drawSelectedBox(aabb, color, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBox(BlockPos pos, int color, float expandXYZ) {
		drawSelectedBox(pos, color, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBox(Vec3 pos, int color, float expandXYZ) {
		drawSelectedBox(pos, color, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBox(AABB aabb, Color color, float expandXYZ) {
		drawSelectedBox(aabb, color.getRGB(), expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBox(BlockPos pos, Color color, float expandXYZ) {
		drawSelectedBox(pos, color.getRGB(), expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBox(Vec3 pos, Color color, float expandXYZ) {
		drawSelectedBox(pos, color.getRGB(), expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBox(AABB aabb, Color color) {
		drawSelectedBox(aabb, color.getRGB(), 0f, 0f, 0f, true);
	}

	public static void drawSelectedBox(BlockPos pos, Color color) {
		drawSelectedBox(pos, color.getRGB(), 0f, 0f, 0f, true);
	}

	public static void drawSelectedBox(Vec3 pos, Color color) {
		drawSelectedBox(pos, color.getRGB(), 0f, 0f, 0f, true);
	}

	// ==========================================
	// Draw Selected Box Line
	// ==========================================
	public static void drawSelectedBoxLine(AABB aabb, int color, float thickness, float expandX, float expandY, float expandZ, boolean depth) {
		drawBoxLine(aabb.inflate(expandX, expandY, expandZ), color, thickness, depth);
	}

	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness, float expandX, float expandY, float expandZ, boolean depth) {
		BlockState state = mc.level.getBlockState(pos);
		VoxelShape shape = state.getShape(mc.level, pos);
		AABB aabb = shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);
		drawBoxLine(aabb.inflate(expandX, expandY, expandZ), color, thickness, depth);
	}

	public static void drawSelectedBoxLine(Vec3 pos, int color, float thickness, float expandX, float expandY, float expandZ, boolean depth) {
		drawBoxLine(new AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 1, pos.z + 1).inflate(expandX, expandY, expandZ), color, thickness, depth);
	}

	public static void drawSelectedBoxLine(AABB aabb, int color, float thickness, float expandX, float expandY, float expandZ) {
		drawSelectedBoxLine(aabb, color, thickness, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness, float expandX, float expandY, float expandZ) {
		drawSelectedBoxLine(pos, color, thickness, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBoxLine(Vec3 pos, int color, float thickness, float expandX, float expandY, float expandZ) {
		drawSelectedBoxLine(pos, color, thickness, expandX, expandY, expandZ, true);
	}

	public static void drawSelectedBoxLine(AABB aabb, int color, float thickness, boolean depth) {
		drawSelectedBoxLine(aabb, color, thickness, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness, boolean depth) {
		drawSelectedBoxLine(pos, color, thickness, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBoxLine(Vec3 pos, int color, float thickness, boolean depth) {
		drawSelectedBoxLine(pos, color, thickness, 0f, 0f, 0f, depth);
	}

	public static void drawSelectedBoxLine(AABB aabb, int color, float thickness) {
		drawSelectedBoxLine(aabb, color, thickness, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness) {
		drawSelectedBoxLine(pos, color, thickness, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBoxLine(Vec3 pos, int color, float thickness) {
		drawSelectedBoxLine(pos, color, thickness, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBoxLine(AABB aabb, int color, float thickness, float expandXYZ, boolean depth) {
		drawSelectedBoxLine(aabb, color, thickness, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness, float expandXYZ, boolean depth) {
		drawSelectedBoxLine(pos, color, thickness, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBoxLine(Vec3 pos, int color, float thickness, float expandXYZ, boolean depth) {
		drawSelectedBoxLine(pos, color, thickness, expandXYZ, expandXYZ, expandXYZ, depth);
	}

	public static void drawSelectedBoxLine(AABB aabb, int color, float thickness, float expandXYZ) {
		drawSelectedBoxLine(aabb, color, thickness, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBoxLine(BlockPos pos, int color, float thickness, float expandXYZ) {
		drawSelectedBoxLine(pos, color, thickness, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBoxLine(Vec3 pos, int color, float thickness, float expandXYZ) {
		drawSelectedBoxLine(pos, color, thickness, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBoxLine(AABB aabb, Color color, float thickness, float expandXYZ) {
		drawSelectedBoxLine(aabb, color.getRGB(), thickness, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBoxLine(BlockPos pos, Color color, float thickness, float expandXYZ) {
		drawSelectedBoxLine(pos, color.getRGB(), thickness, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBoxLine(Vec3 pos, Color color, float thickness, float expandXYZ) {
		drawSelectedBoxLine(pos, color.getRGB(), thickness, expandXYZ, expandXYZ, expandXYZ, true);
	}

	public static void drawSelectedBoxLine(AABB aabb, Color color, float thickness) {
		drawSelectedBoxLine(aabb, color.getRGB(), thickness, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBoxLine(BlockPos pos, Color color, float thickness) {
		drawSelectedBoxLine(pos, color.getRGB(), thickness, 0f, 0f, 0f, true);
	}

	public static void drawSelectedBoxLine(Vec3 pos, Color color, float thickness) {
		drawSelectedBoxLine(pos, color.getRGB(), thickness, 0f, 0f, 0f, true);
	}

	// ==========================================
	// Draw Line
	// ==========================================
	public static void drawLine(Vec3 from, Vec3 to, int color1, int color2, boolean depth, float thickness) {
		queuedLines.add(new LineData(from, to, color1, color2, thickness, depth));
	}

	public static void drawLine(Vec3 from, Vec3 to, int color, boolean depth, float thickness) {
		drawLine(from, to, color, color, depth, thickness);
	}

	public static void drawLine(Vec3 from, Vec3 to, int color, float thickness) {
		drawLine(from, to, color, color, true, thickness);
	}

	public static void drawLine(Vec3 from, Vec3 to, Color color1, Color color2, boolean depth, float thickness) {
		drawLine(from, to, color1.getRGB(), color2.getRGB(), depth, thickness);
	}

	public static void drawLine(Vec3 from, Vec3 to, Color color, boolean depth, float thickness) {
		drawLine(from, to, color.getRGB(), color.getRGB(), depth, thickness);
	}

	public static void drawLine(Vec3 from, Vec3 to, Color color, float thickness) {
		drawLine(from, to, color.getRGB(), color.getRGB(), true, thickness);
	}

	public static void drawLine(BlockPos from, BlockPos to, int color1, int color2, boolean depth, float thickness) {
		drawLine(getBlockCenter(from), getBlockCenter(to), color1, color2, depth, thickness);
	}

	public static void drawLine(BlockPos from, BlockPos to, int color, boolean depth, float thickness) {
		drawLine(getBlockCenter(from), getBlockCenter(to), color, color, depth, thickness);
	}

	public static void drawLine(BlockPos from, BlockPos to, int color, float thickness) {
		drawLine(getBlockCenter(from), getBlockCenter(to), color, color, true, thickness);
	}

	public static void drawLine(BlockPos from, BlockPos to, Color color1, Color color2, boolean depth, float thickness) {
		drawLine(getBlockCenter(from), getBlockCenter(to), color1.getRGB(), color2.getRGB(), depth, thickness);
	}

	public static void drawLine(BlockPos from, BlockPos to, Color color, boolean depth, float thickness) {
		drawLine(getBlockCenter(from), getBlockCenter(to), color.getRGB(), color.getRGB(), depth, thickness);
	}

	public static void drawLine(BlockPos from, BlockPos to, Color color, float thickness) {
		drawLine(getBlockCenter(from), getBlockCenter(to), color.getRGB(), color.getRGB(), true, thickness);
	}

	public static void drawLine(Vec3 from, BlockPos to, int color, float thickness) {
		drawLine(from, getBlockCenter(to), color, color, true, thickness);
	}

	public static void drawLine(BlockPos from, Vec3 to, int color, float thickness) {
		drawLine(getBlockCenter(from), to, color, color, true, thickness);
	}

	// ==========================================
	// Draw Tracer
	// ==========================================
	public static void drawTracer(Vec3 to, int color, boolean depth, float thickness) {
		drawLine(getTracerSource(), to, color, depth, thickness);
	}

	public static void drawTracer(Vec3 to, int color, float thickness) {
		drawTracer(to, color, true, thickness);
	}

	public static void drawTracer(Vec3 to, Color color, boolean depth, float thickness) {
		drawTracer(to, color.getRGB(), depth, thickness);
	}

	public static void drawTracer(Vec3 to, Color color, float thickness) {
		drawTracer(to, color.getRGB(), true, thickness);
	}

	public static void drawTracer(BlockPos to, int color, boolean depth, float thickness) {
		drawLine(getTracerSource(), getBlockCenter(to), color, depth, thickness);
	}

	public static void drawTracer(BlockPos to, int color, float thickness) {
		drawTracer(to, color, true, thickness);
	}

	public static void drawTracer(BlockPos to, Color color, boolean depth, float thickness) {
		drawTracer(to, color.getRGB(), depth, thickness);
	}

	public static void drawTracer(BlockPos to, Color color, float thickness) {
		drawTracer(to, color.getRGB(), true, thickness);
	}

	// ==========================================
	// Draw Text
	// ==========================================
	public static void drawText(String text, Vec3 pos, float scale, boolean depth) {
		Font font = mc.font;
		queuedTexts.add(new TextData(text, pos, scale, depth, mc.gameRenderer.getMainCamera().rotation(), font, font.width(text)));
	}

	public static void drawText(String text, Vec3 pos, float scale) {
		drawText(text, pos, scale, true);
	}

	public static void drawText(String text, BlockPos pos, float scale, boolean depth) {
		drawText(text, getBlockCenter(pos), scale, depth);
	}

	public static void drawText(String text, BlockPos pos, float scale) {
		drawText(text, getBlockCenter(pos), scale, true);
	}

	// ==========================================
	// Draw Cylinder
	// ==========================================
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

	public static void drawCylinder(Vec3 center, float radius, float height, int color, int segments, float thickness) {
		drawCylinder(center, radius, height, color, segments, thickness, true);
	}

	public static void drawCylinder(BlockPos center, float radius, float height, int color, int segments, float thickness, boolean depth) {
		drawCylinder(getBlockCenter(center), radius, height, color, segments, thickness, depth);
	}

	public static void drawCylinder(BlockPos center, float radius, float height, int color, int segments, float thickness) {
		drawCylinder(getBlockCenter(center), radius, height, color, segments, thickness, true);
	}

	public static void drawCylinder(Vec3 center, float radius, float height, Color color, int segments, float thickness, boolean depth) {
		drawCylinder(center, radius, height, color.getRGB(), segments, thickness, depth);
	}

	public static void drawCylinder(Vec3 center, float radius, float height, Color color, int segments, float thickness) {
		drawCylinder(center, radius, height, color.getRGB(), segments, thickness, true);
	}

	public static void drawCylinder(BlockPos center, float radius, float height, Color color, int segments, float thickness, boolean depth) {
		drawCylinder(getBlockCenter(center), radius, height, color.getRGB(), segments, thickness, depth);
	}

	public static void drawCylinder(BlockPos center, float radius, float height, Color color, int segments, float thickness) {
		drawCylinder(getBlockCenter(center), radius, height, color.getRGB(), segments, thickness, true);
	}

	// ==========================================
	// Draw Beacon Beam
	// ==========================================
	public static void drawBeaconBeam(Vec3 pos, int color, float partialTicks, long gameTime, boolean isScoping) {
		queuedBeaconBeams.add(new BeaconBeamData(pos, color, partialTicks, gameTime, isScoping));
	}

	public static void drawBeaconBeam(Vec3 pos, int color, boolean isScoping) {
		long gameTime = mc.level != null ? mc.level.getGameTime() : 0L;
		drawBeaconBeam(pos, color, lastTickDelta, gameTime, isScoping);
	}

	public static void drawBeaconBeam(Vec3 pos, int color) {
		drawBeaconBeam(pos, color, false);
	}

	public static void drawBeaconBeam(BlockPos pos, int color, boolean isScoping) {
		long gameTime = mc.level != null ? mc.level.getGameTime() : 0L;
		drawBeaconBeam(getBlockCenter(pos), color, lastTickDelta, gameTime, isScoping);
	}

	public static void drawBeaconBeam(BlockPos pos, int color) {
		drawBeaconBeam(getBlockCenter(pos), color, false);
	}

	public static void drawBeaconBeam(Vec3 pos, Color color, boolean isScoping) {
		long gameTime = mc.level != null ? mc.level.getGameTime() : 0L;
		drawBeaconBeam(pos, color.getRGB(), lastTickDelta, gameTime, isScoping);
	}

	public static void drawBeaconBeam(Vec3 pos, Color color) {
		drawBeaconBeam(pos, color.getRGB(), false);
	}

	public static void drawBeaconBeam(BlockPos pos, Color color, boolean isScoping) {
		long gameTime = mc.level != null ? mc.level.getGameTime() : 0L;
		drawBeaconBeam(getBlockCenter(pos), color.getRGB(), lastTickDelta, gameTime, isScoping);
	}

	public static void drawBeaconBeam(BlockPos pos, Color color) {
		drawBeaconBeam(getBlockCenter(pos), color.getRGB(), false);
	}

	// ==========================================
	// Internals
	// ==========================================
	private static void clearAll() {
		queuedLines.clear();
		queuedFilledBoxes.clear();
		queuedWireBoxes.clear();
		queuedTexts.clear();
		queuedBeaconBeams.clear();
	}

	private static Vec3 getBlockCenter(BlockPos pos) {
		return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	private static Vec3 getTracerSource() {
		return URender.getRenderPos(mc.player).add(mc.player.getForward().add(0.0, mc.player.getEyeHeight(), 0.0));
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