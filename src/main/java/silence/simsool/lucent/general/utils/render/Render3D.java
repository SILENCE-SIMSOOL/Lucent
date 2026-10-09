package silence.simsool.lucent.general.utils.render;

import static silence.simsool.lucent.Lucent.mc;

import java.awt.Color;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.enums.RenderStyle;
import silence.simsool.lucent.general.models.data.render.BeaconBeamData;
import silence.simsool.lucent.general.models.data.render.BoxData;
import silence.simsool.lucent.general.models.data.render.CircleData;
import silence.simsool.lucent.general.models.data.render.LineData;
import silence.simsool.lucent.general.models.data.render.TextData;
import silence.simsool.lucent.general.utils.useful.UWorld;
import silence.simsool.lucent.mixin.accessors.BeaconBeamAccessor;
import silence.simsool.lucent.ui.utils.UColor;

public class Render3D {

	private static final RenderType[] LINE_RENDER_TYPES = {
		LucentRenderType.LINES_TRANSLUCENT_ESP,
		LucentRenderType.LINES_ESP,
		LucentRenderType.LINES_TRANSLUCENT,
		LucentRenderType.LINES
	};

	private static final RenderType[] FILL_RENDER_TYPES = {
		LucentRenderType.FILLED_TRANSLUCENT_ESP,
		LucentRenderType.FILLED_ESP,
		LucentRenderType.FILLED_TRANSLUCENT,
		LucentRenderType.FILLED
	};

	@SuppressWarnings("unchecked")
	private static final List<LineData>[] lineBuckets = new List[4];
	@SuppressWarnings("unchecked")
	private static final List<BoxData>[] wireBoxBuckets = new List[4];
	@SuppressWarnings("unchecked")
	private static final List<BoxData>[] filledBoxBuckets = new List[4];
	@SuppressWarnings("unchecked")
	private static final List<CircleData>[] lineCircleBuckets = new List[4];
	@SuppressWarnings("unchecked")
	private static final List<CircleData>[] filledCircleBuckets = new List[4];

	private static final List<TextData> queuedTexts = new ObjectArrayList<>();
	private static final List<BeaconBeamData> queuedBeaconBeams = new ObjectArrayList<>();

	static {
		for (int i = 0; i < 4; i++) {
			lineBuckets[i] = new ObjectArrayList<>();
			wireBoxBuckets[i] = new ObjectArrayList<>();
			filledBoxBuckets[i] = new ObjectArrayList<>();
			lineCircleBuckets[i] = new ObjectArrayList<>();
			filledCircleBuckets[i] = new ObjectArrayList<>();
		}
	}

	// Pools
	private static final List<LineData> linePool = new ObjectArrayList<>();
	private static final List<BoxData> boxPool = new ObjectArrayList<>();
	private static final List<TextData> textPool = new ObjectArrayList<>();
	private static final List<BeaconBeamData> beaconBeamPool = new ObjectArrayList<>();
	private static final List<CircleData> circlePool = new ObjectArrayList<>();

	private static int linePoolIndex = 0;
	private static int boxPoolIndex = 0;
	private static int textPoolIndex = 0;
	private static int beaconBeamPoolIndex = 0;
	private static int circlePoolIndex = 0;

	// Lookup tables for Circle Rendering
	private static final float[] COS_TABLE = new float[181];
	private static final float[] SIN_TABLE = new float[181];
	static {
		for (int i = 0; i <= 180; i++) {
			double rad = Math.toRadians(i * 2);
			COS_TABLE[i] = (float) Math.cos(rad);
			SIN_TABLE[i] = (float) Math.sin(rad);
		}
	}

	private static final Identifier BEAM_TEXTURE = Identifier.withDefaultNamespace("textures/entity/beacon/beacon_beam.png");

	private static float lastTickDelta = 1.0f;

	public static void init() {
		LucentEvent.WORLD_RENDER_LAST.register(event -> {
			PoseStack matrix = event.context.poseStack();
			SubmitNodeCollector submitNodeCollector = event.context.submitNodeCollector();
			if (submitNodeCollector == null) {
				clearAll();
				return;
			}

			lastTickDelta = event.partialTick;

			Vec3 camera = UWorld.getCameraPos();

			matrix.pushPose();

			renderQueuedLinesAndWireBoxes(matrix, submitNodeCollector, camera);
			renderQueuedFilledBoxes(matrix, submitNodeCollector, camera);

			matrix.popPose();

			renderQueuedBeaconBeams(matrix, submitNodeCollector, camera);
			renderQueuedTexts(matrix, submitNodeCollector, camera);

			clearAll();
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
		BoxData data = getOrCreateBox(aabb, color, 3f, depth);
		filledBoxBuckets[data.filledRenderTypeIndex()].add(data);
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
		BoxData data = getOrCreateBox(aabb, color, thickness, depth);
		wireBoxBuckets[data.lineRenderTypeIndex()].add(data);
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
		LineData data = getOrCreateLine(from, to, color1, color2, thickness, depth, false);
		lineBuckets[data.renderTypeIndex()].add(data);
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
		LineData data = getOrCreateLine(getTracerSource(), to, color, color, thickness, depth, true);
		lineBuckets[data.renderTypeIndex()].add(data);
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
		LineData data = getOrCreateLine(getTracerSource(), getBlockCenter(to), color, color, thickness, depth, true);
		lineBuckets[data.renderTypeIndex()].add(data);
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
	public static void drawText(String text, Vec3 pos, float scale, boolean depth, boolean shadow) {
		drawText(text, pos, scale, -1, depth, shadow);
	}

	public static void drawText(String text, Vec3 pos, float scale, boolean depth) {
		drawText(text, pos, scale, depth, true);
	}

	public static void drawText(String text, Vec3 pos, float scale) {
		drawText(text, pos, scale, true, true);
	}

	public static void drawText(String text, BlockPos pos, float scale, boolean depth, boolean shadow) {
		drawText(text, getBlockCenter(pos), scale, depth, shadow);
	}

	public static void drawText(String text, BlockPos pos, float scale, boolean depth) {
		drawText(text, pos, scale, depth, true);
	}

	public static void drawText(String text, BlockPos pos, float scale) {
		drawText(text, pos, scale, true, true);
	}

	public static void drawText(String text, Vec3 pos, float scale, int color, boolean depth, boolean shadow) {
		Font font = mc.font;
		queuedTexts.add(getOrCreateText(text, pos, scale, depth, UWorld.getCamera().rotation(), font, font.width(text), color, 0, 0, shadow));
	}

	public static void drawText(String text, Vec3 pos, float scale, int color, boolean depth) {
		drawText(text, pos, scale, color, depth, true);
	}

	public static void drawText(String text, Vec3 pos, float scale, int color) {
		drawText(text, pos, scale, color, true, true);
	}

	public static void drawText(String text, BlockPos pos, float scale, int color, boolean depth, boolean shadow) {
		drawText(text, getBlockCenter(pos), scale, color, depth, shadow);
	}

	public static void drawText(String text, BlockPos pos, float scale, int color, boolean depth) {
		drawText(text, pos, scale, color, depth, true);
	}

	public static void drawText(String text, BlockPos pos, float scale, int color) {
		drawText(text, pos, scale, color, true, true);
	}

	public static void drawText(String text, Vec3 pos, float scale, Color color, boolean depth, boolean shadow) {
		drawText(text, pos, scale, color.getRGB(), depth, shadow);
	}

	public static void drawText(String text, Vec3 pos, float scale, Color color, boolean depth) {
		drawText(text, pos, scale, color.getRGB(), depth, true);
	}

	public static void drawText(String text, Vec3 pos, float scale, Color color) {
		drawText(text, pos, scale, color.getRGB(), true, true);
	}

	public static void drawText(String text, BlockPos pos, float scale, Color color, boolean depth, boolean shadow) {
		drawText(text, getBlockCenter(pos), scale, color.getRGB(), depth, shadow);
	}

	public static void drawText(String text, BlockPos pos, float scale, Color color, boolean depth) {
		drawText(text, pos, scale, color.getRGB(), depth, true);
	}

	public static void drawText(String text, BlockPos pos, float scale, Color color) {
		drawText(text, pos, scale, color.getRGB(), true, true);
	}

	// ==========================================
	// Draw Background Text
	// ==========================================
	public static void drawBackgroundText(String text, Vec3 pos, float scale, int color, int backgroundColor, boolean depth, boolean shadow) {
		Font font = mc.font;
		queuedTexts.add(getOrCreateText(text, pos, scale, depth, UWorld.getCamera().rotation(), font, font.width(text), color, backgroundColor, 0, shadow));
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, int color, int backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, color, backgroundColor, depth, true);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, int color, int backgroundColor) {
		drawBackgroundText(text, pos, scale, color, backgroundColor, true, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, int color, int backgroundColor, boolean depth, boolean shadow) {
		drawBackgroundText(text, getBlockCenter(pos), scale, color, backgroundColor, depth, shadow);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, int color, int backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, color, backgroundColor, depth, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, int color, int backgroundColor) {
		drawBackgroundText(text, pos, scale, color, backgroundColor, true, true);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, int backgroundColor, boolean depth, boolean shadow) {
		drawBackgroundText(text, pos, scale, -1, backgroundColor, depth, shadow);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, int backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, backgroundColor, depth, true);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, int backgroundColor) {
		drawBackgroundText(text, pos, scale, backgroundColor, true, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, int backgroundColor, boolean depth, boolean shadow) {
		drawBackgroundText(text, getBlockCenter(pos), scale, -1, backgroundColor, depth, shadow);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, int backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, backgroundColor, depth, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, int backgroundColor) {
		drawBackgroundText(text, pos, scale, backgroundColor, true, true);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, Color color, Color backgroundColor, boolean depth, boolean shadow) {
		drawBackgroundText(text, pos, scale, color.getRGB(), backgroundColor.getRGB(), depth, shadow);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, Color color, Color backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, color.getRGB(), backgroundColor.getRGB(), depth, true);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, Color color, Color backgroundColor) {
		drawBackgroundText(text, pos, scale, color.getRGB(), backgroundColor.getRGB(), true, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, Color color, Color backgroundColor, boolean depth, boolean shadow) {
		drawBackgroundText(text, getBlockCenter(pos), scale, color.getRGB(), backgroundColor.getRGB(), depth, shadow);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, Color color, Color backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, color.getRGB(), backgroundColor.getRGB(), depth, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, Color color, Color backgroundColor) {
		drawBackgroundText(text, pos, scale, color.getRGB(), backgroundColor.getRGB(), true, true);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, Color backgroundColor, boolean depth, boolean shadow) {
		drawBackgroundText(text, pos, scale, -1, backgroundColor.getRGB(), depth, shadow);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, Color backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, backgroundColor, depth, true);
	}

	public static void drawBackgroundText(String text, Vec3 pos, float scale, Color backgroundColor) {
		drawBackgroundText(text, pos, scale, backgroundColor, true, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, Color backgroundColor, boolean depth, boolean shadow) {
		drawBackgroundText(text, getBlockCenter(pos), scale, -1, backgroundColor.getRGB(), depth, shadow);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, Color backgroundColor, boolean depth) {
		drawBackgroundText(text, pos, scale, backgroundColor, depth, true);
	}

	public static void drawBackgroundText(String text, BlockPos pos, float scale, Color backgroundColor) {
		drawBackgroundText(text, getBlockCenter(pos), scale, -1, backgroundColor.getRGB(), true, true);
	}

	// ==========================================
	// Draw Outline Text
	// ==========================================
	public static void drawOutlineText(String text, Vec3 pos, float scale, int color, int outlineColor, boolean depth, boolean shadow) {
		Font font = mc.font;
		queuedTexts.add(getOrCreateText(text, pos, scale, depth, UWorld.getCamera().rotation(), font, font.width(text), color, 0, outlineColor, shadow));
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, int color, int outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, color, outlineColor, depth, true);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, int color, int outlineColor) {
		drawOutlineText(text, pos, scale, color, outlineColor, true, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, int color, int outlineColor, boolean depth, boolean shadow) {
		drawOutlineText(text, getBlockCenter(pos), scale, color, outlineColor, depth, shadow);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, int color, int outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, color, outlineColor, depth, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, int color, int outlineColor) {
		drawOutlineText(text, pos, scale, color, outlineColor, true, true);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, int outlineColor, boolean depth, boolean shadow) {
		drawOutlineText(text, pos, scale, -1, outlineColor, depth, shadow);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, int outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, outlineColor, depth, true);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, int outlineColor) {
		drawOutlineText(text, pos, scale, outlineColor, true, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, int outlineColor, boolean depth, boolean shadow) {
		drawOutlineText(text, getBlockCenter(pos), scale, -1, outlineColor, depth, shadow);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, int outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, outlineColor, depth, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, int outlineColor) {
		drawOutlineText(text, pos, scale, outlineColor, true, true);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, Color color, Color outlineColor, boolean depth, boolean shadow) {
		drawOutlineText(text, pos, scale, color.getRGB(), outlineColor.getRGB(), depth, shadow);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, Color color, Color outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, color.getRGB(), outlineColor.getRGB(), depth, true);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, Color color, Color outlineColor) {
		drawOutlineText(text, pos, scale, color.getRGB(), outlineColor.getRGB(), true, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, Color color, Color outlineColor, boolean depth, boolean shadow) {
		drawOutlineText(text, getBlockCenter(pos), scale, color.getRGB(), outlineColor.getRGB(), depth, shadow);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, Color color, Color outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, color.getRGB(), outlineColor.getRGB(), depth, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, Color color, Color outlineColor) {
		drawOutlineText(text, pos, scale, color.getRGB(), outlineColor.getRGB(), true, true);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, Color outlineColor, boolean depth, boolean shadow) {
		drawOutlineText(text, pos, scale, -1, outlineColor.getRGB(), depth, shadow);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, Color outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, outlineColor, depth, true);
	}

	public static void drawOutlineText(String text, Vec3 pos, float scale, Color outlineColor) {
		drawOutlineText(text, pos, scale, outlineColor, true, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, Color outlineColor, boolean depth, boolean shadow) {
		drawOutlineText(text, getBlockCenter(pos), scale, -1, outlineColor.getRGB(), depth, shadow);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, Color outlineColor, boolean depth) {
		drawOutlineText(text, pos, scale, outlineColor, depth, true);
	}

	public static void drawOutlineText(String text, BlockPos pos, float scale, Color outlineColor) {
		drawOutlineText(text, getBlockCenter(pos), scale, -1, outlineColor.getRGB(), true, true);
	}

	// ==========================================
	// Draw Circle
	// ==========================================
	public static void drawCircle(Vec3 pos, float radius, int color, boolean depth) {
		CircleData data = getOrCreateCircle(pos, radius, 1.0f, color, depth, true);
		filledCircleBuckets[data.renderTypeIndex()].add(data);
	}

	public static void drawCircle(Vec3 pos, float radius, int color) {
		drawCircle(pos, radius, color, true);
	}

	public static void drawCircle(Vec3 pos, float radius, Color color, boolean depth) {
		drawCircle(pos, radius, color.getRGB(), depth);
	}

	public static void drawCircle(Vec3 pos, float radius, Color color) {
		drawCircle(pos, radius, color.getRGB(), true);
	}

	public static void drawCircle(BlockPos pos, float radius, int color, boolean depth) {
		drawCircle(
			new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
			radius, color, depth
		);
	}

	public static void drawCircle(BlockPos pos, float radius, int color) {
		drawCircle(pos, radius, color, true);
	}

	public static void drawCircle(BlockPos pos, float radius, Color color, boolean depth) {
		drawCircle(pos, radius, color.getRGB(), depth);
	}

	public static void drawCircle(BlockPos pos, float radius, Color color) {
		drawCircle(pos, radius, color.getRGB(), true);
	}

	public static void drawCircleLine(Vec3 pos, float radius, float thickness, int color, boolean depth) {
		CircleData data = getOrCreateCircle(pos, radius, thickness, color, depth, false);
		lineCircleBuckets[data.renderTypeIndex()].add(data);
	}

	public static void drawCircleLine(Vec3 pos, float radius, float thickness, int color) {
		drawCircleLine(pos, radius, thickness, color, true);
	}

	public static void drawCircleLine(Vec3 pos, float radius, float thickness, Color color, boolean depth) {
		drawCircleLine(pos, radius, thickness, color.getRGB(), depth);
	}

	public static void drawCircleLine(Vec3 pos, float radius, float thickness, Color color) {
		drawCircleLine(pos, radius, thickness, color.getRGB(), true);
	}

	public static void drawCircleLine(BlockPos pos, float radius, float thickness, int color, boolean depth) {
		drawCircleLine(
			new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
			radius, thickness, color, depth
		);
	}

	public static void drawCircleLine(BlockPos pos, float radius, float thickness, int color) {
		drawCircleLine(pos, radius, thickness, color, true);
	}

	public static void drawCircleLine(BlockPos pos, float radius, float thickness, Color color, boolean depth) {
		drawCircleLine(pos, radius, thickness, color.getRGB(), depth);
	}

	public static void drawCircleLine(BlockPos pos, float radius, float thickness, Color color) {
		drawCircleLine(pos, radius, thickness, color.getRGB(), true);
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

			LineData l1 = getOrCreateLine(p1Top, p2Top, color, color, thickness, depth, false);
			lineBuckets[l1.renderTypeIndex()].add(l1);
			LineData l2 = getOrCreateLine(p1Bottom, p2Bottom, color, color, thickness, depth, false);
			lineBuckets[l2.renderTypeIndex()].add(l2);
			LineData l3 = getOrCreateLine(p1Bottom, p1Top, color, color, thickness, depth, false);
			lineBuckets[l3.renderTypeIndex()].add(l3);
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
		queuedBeaconBeams.add(getOrCreateBeaconBeam(pos, color, partialTicks, gameTime, isScoping));
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
		for (int i = 0; i < 4; i++) {
			lineBuckets[i].clear();
			wireBoxBuckets[i].clear();
			filledBoxBuckets[i].clear();
			lineCircleBuckets[i].clear();
			filledCircleBuckets[i].clear();
		}
		queuedTexts.clear();
		queuedBeaconBeams.clear();

		linePoolIndex = 0;
		boxPoolIndex = 0;
		textPoolIndex = 0;
		beaconBeamPoolIndex = 0;
		circlePoolIndex = 0;
	}

	private static Vec3 getBlockCenter(BlockPos pos) {
		return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	private static Vec3 getTracerSource() {
		CameraRenderState cam = mc.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
		Vec3 from = cam.pos.add(Vec3.directionFromRotation(cam.xRot, cam.yRot));
		return from;
	}

	private static void renderQueuedLinesAndWireBoxes(PoseStack matrix, SubmitNodeCollector submitNodeCollector, Vec3 camera) {
		Vec3 tracerSource = null;

		for (int i = 0; i < 4; i++) {
			List<LineData> lines = lineBuckets[i];
			List<BoxData> wireBoxes = wireBoxBuckets[i];
			List<CircleData> wireCircles = lineCircleBuckets[i];

			if (lines.isEmpty() && wireBoxes.isEmpty() && wireCircles.isEmpty()) continue;

			RenderType renderType = LINE_RENDER_TYPES[i];

			if (tracerSource == null) {
				tracerSource = getTracerSource();
			}
			final Vec3 finalTracerSource = tracerSource;

			submitNodeCollector.submitCustomGeometry(matrix, renderType, (pose, buffer) -> {
				double camX = camera.x;
				double camY = camera.y;
				double camZ = camera.z;

				// 1. Lines
				for (int j = 0; j < lines.size(); j++) {
					LineData line = lines.get(j);
					Vec3 fromPos = line.isTracer ? finalTracerSource : line.from;
					float startX = (float) (fromPos.x - camX);
					float startY = (float) (fromPos.y - camY);
					float startZ = (float) (fromPos.z - camZ);
					Vector3f start = new Vector3f(startX, startY, startZ);
					Vec3 dir = line.to.subtract(fromPos);
					PrimitiveRenderer.renderVector(pose, buffer, start, dir, line.color1, line.color2, line.thickness);
				}

				// 2. Wire Boxes
				for (int j = 0; j < wireBoxes.size(); j++) {
					BoxData box = wireBoxes.get(j);
					float x0 = (float) (box.aabb.minX - camX);
					float y0 = (float) (box.aabb.minY - camY);
					float z0 = (float) (box.aabb.minZ - camZ);
					float x1 = (float) (box.aabb.maxX - camX);
					float y1 = (float) (box.aabb.maxY - camY);
					float z1 = (float) (box.aabb.maxZ - camZ);
					PrimitiveRenderer.renderLineBox(pose, buffer, x0, y0, z0, x1, y1, z1, box.r, box.g, box.b, box.a, box.thickness);
				}

				// 3. Wire Circles
				for (int j = 0; j < wireCircles.size(); j++) {
					CircleData circle = wireCircles.get(j);
					float cx = (float) (circle.center.x - camX);
					float cy = (float) (circle.center.y - camY);
					float cz = (float) (circle.center.z - camZ);

					for (int deg = 0; deg < 360; deg += 2) {
						int idx1 = deg / 2;
						int idx2 = (deg + 2) / 2;
						float dx1 = COS_TABLE[idx1] * circle.radius;
						float dz1 = SIN_TABLE[idx1] * circle.radius;
						float dx2 = COS_TABLE[idx2] * circle.radius;
						float dz2 = SIN_TABLE[idx2] * circle.radius;

						float nx = dx2 - dx1;
						float nz = dz2 - dz1;

						buffer.addVertex(pose, cx + dx1, cy, cz + dz1).setColor(circle.r, circle.g, circle.b, circle.a).setNormal(pose, nx, 0, nz).setLineWidth(circle.thickness);
						buffer.addVertex(pose, cx + dx2, cy, cz + dz2).setColor(circle.r, circle.g, circle.b, circle.a).setNormal(pose, nx, 0, nz).setLineWidth(circle.thickness);
					}
				}
			});
		}
	}

	private static void renderQueuedFilledBoxes(PoseStack matrix, SubmitNodeCollector submitNodeCollector, Vec3 camera) {
		for (int i = 0; i < 4; i++) {
			List<BoxData> filledBoxes = filledBoxBuckets[i];
			List<CircleData> filledCircles = filledCircleBuckets[i];

			if (filledBoxes.isEmpty() && filledCircles.isEmpty()) continue;

			RenderType renderType = FILL_RENDER_TYPES[i];

			submitNodeCollector.submitCustomGeometry(matrix, renderType, (pose, buffer) -> {
				double camX = camera.x;
				double camY = camera.y;
				double camZ = camera.z;

				// 1. Filled Boxes
				for (int j = 0; j < filledBoxes.size(); j++) {
					BoxData box = filledBoxes.get(j);
					float minX = (float) (box.aabb.minX - camX);
					float minY = (float) (box.aabb.minY - camY);
					float minZ = (float) (box.aabb.minZ - camZ);
					float maxX = (float) (box.aabb.maxX - camX);
					float maxY = (float) (box.aabb.maxY - camY);
					float maxZ = (float) (box.aabb.maxZ - camZ);
					PrimitiveRenderer.addChainedFilledBoxVertices(pose, buffer, minX, minY, minZ, maxX, maxY, maxZ, box.r, box.g, box.b, box.a);
				}

				// 2. Filled Circles
				if (!filledCircles.isEmpty()) {
					Matrix4f matrix4f = pose.pose();
					for (int j = 0; j < filledCircles.size(); j++) {
						CircleData circle = filledCircles.get(j);
						float cx = (float) (circle.center.x - camX);
						float cy = (float) (circle.center.y - camY);
						float cz = (float) (circle.center.z - camZ);

						for (int deg = 0; deg < 360; deg += 2) {
							int idx1 = deg / 2;
							int idx2 = (deg + 2) / 2;
							float dx1 = COS_TABLE[idx1] * circle.radius;
							float dz1 = SIN_TABLE[idx1] * circle.radius;
							float dx2 = COS_TABLE[idx2] * circle.radius;
							float dz2 = SIN_TABLE[idx2] * circle.radius;

							buffer.addVertex(matrix4f, cx, cy, cz).setColor(circle.r, circle.g, circle.b, circle.a);
							buffer.addVertex(matrix4f, cx + dx1, cy, cz + dz1).setColor(circle.r, circle.g, circle.b, circle.a);
							buffer.addVertex(matrix4f, cx + dx2, cy, cz + dz2).setColor(circle.r, circle.g, circle.b, circle.a);
							buffer.addVertex(matrix4f, cx, cy, cz).setColor(circle.r, circle.g, circle.b, circle.a);
						}
					}
				}
			});
		}
	}

	private static void renderQueuedTexts(PoseStack matrix, SubmitNodeCollector submitNodeCollector, Vec3 camera) {
		for (TextData textData : queuedTexts) {
			matrix.pushPose();
			float scaleFactor = textData.scale * 0.025f;
			matrix.translate((float) (textData.pos.x - camera.x), (float) (textData.pos.y - camera.y), (float) (textData.pos.z - camera.z));
			matrix.mulPose(textData.cameraRotation);
			matrix.scale(scaleFactor, -scaleFactor, scaleFactor);
			submitNodeCollector.submitText(matrix, -textData.textWidth / 2f, 0f, Component.literal(textData.text).getVisualOrderText(), textData.shadow, textData.depth ? Font.DisplayMode.POLYGON_OFFSET : Font.DisplayMode.SEE_THROUGH, LightCoordsUtil.FULL_BRIGHT, textData.color, textData.backgroundColor, textData.outlineColor);
			matrix.popPose();
		}
	}

	private static void renderQueuedBeaconBeams(PoseStack matrix, SubmitNodeCollector submitNodeCollector, Vec3 camera) {
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
				submitNodeCollector,
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

	public static RenderType resolveLineRenderType(boolean depth, boolean isFullOpaque) {
		if (depth && isFullOpaque) return LucentRenderType.LINES;
		else if (depth) return LucentRenderType.LINES_TRANSLUCENT;
		else if (isFullOpaque) return LucentRenderType.LINES_ESP;
		return LucentRenderType.LINES_TRANSLUCENT_ESP;
	}

	public static RenderType resolveFillRenderType(boolean depth, boolean isFullOpaque) {
		if (depth && isFullOpaque) return LucentRenderType.FILLED;
		else if (depth) return LucentRenderType.FILLED_TRANSLUCENT;
		else if (isFullOpaque) return LucentRenderType.FILLED_ESP;
		return LucentRenderType.FILLED_TRANSLUCENT_ESP;
	}

	private static LineData getOrCreateLine(Vec3 from, Vec3 to, int color1, int color2, float thickness, boolean depth, boolean isTracer) {
		if (linePoolIndex < linePool.size()) {
			LineData data = linePool.get(linePoolIndex++);
			data.set(from, to, color1, color2, thickness, depth, isTracer);
			return data;
		} else {
			LineData data = new LineData(from, to, color1, color2, thickness, depth, isTracer);
			linePool.add(data);
			linePoolIndex++;
			return data;
		}
	}

	private static BoxData getOrCreateBox(AABB aabb, int color, float thickness, boolean depth) {
		if (boxPoolIndex < boxPool.size()) {
			BoxData data = boxPool.get(boxPoolIndex++);
			data.set(aabb, color, thickness, depth);
			return data;
		} else {
			BoxData data = new BoxData(aabb, color, thickness, depth);
			boxPool.add(data);
			boxPoolIndex++;
			return data;
		}
	}

	private static TextData getOrCreateText(String text, Vec3 pos, float scale, boolean depth, Quaternionf rotation, Font font, float width, int color, int backgroundColor, int outlineColor, boolean shadow) {
		if (textPoolIndex < textPool.size()) {
			TextData data = textPool.get(textPoolIndex++);
			data.set(text, pos, scale, depth, rotation, font, width, color, backgroundColor, outlineColor, shadow);
			return data;
		} else {
			TextData data = new TextData(text, pos, scale, depth, rotation, font, width, color, backgroundColor, outlineColor, shadow);
			textPool.add(data);
			textPoolIndex++;
			return data;
		}
	}

	private static BeaconBeamData getOrCreateBeaconBeam(Vec3 pos, int color, float partialTicks, long gameTime, boolean isScoping) {
		if (beaconBeamPoolIndex < beaconBeamPool.size()) {
			BeaconBeamData data = beaconBeamPool.get(beaconBeamPoolIndex++);
			data.set(pos, color, partialTicks, gameTime, isScoping);
			return data;
		} else {
			BeaconBeamData data = new BeaconBeamData(pos, color, partialTicks, gameTime, isScoping);
			beaconBeamPool.add(data);
			beaconBeamPoolIndex++;
			return data;
		}
	}

	private static CircleData getOrCreateCircle(Vec3 pos, float radius, float thickness, int color, boolean depth, boolean filled) {
		if (circlePoolIndex < circlePool.size()) {
			CircleData data = circlePool.get(circlePoolIndex++);
			data.set(pos, radius, thickness, color, depth, filled);
			return data;
		} else {
			CircleData data = new CircleData(pos, radius, thickness, color, depth, filled);
			circlePool.add(data);
			circlePoolIndex++;
			return data;
		}
	}

}