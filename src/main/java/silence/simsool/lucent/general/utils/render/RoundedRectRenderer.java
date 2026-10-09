package silence.simsool.lucent.general.utils.render;

import static silence.simsool.lucent.Lucent.mc;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.resources.Identifier;

public class RoundedRectRenderer {

	private static final float AA_PADDING = 2.0f;
	private static final float HALF_EXTENT_SCALE = 8.0f;
	private static final float RADIUS_SCALE = 4.0f;
	private static final float MAX_RADIUS = 255.0f / RADIUS_SCALE;
	private static final int ALPHA_MASK = 0xFF << 24;
	private static final TextureSetup NO_TEXTURE = TextureSetup.noTexture();

	public static final VertexFormat FORMAT = VertexFormat.builder(0)
			.addAttribute(DefaultVertexFormat.POSITION_SEMANTIC_NAME, GpuFormat.RGB32_FLOAT)
			.addAttribute(DefaultVertexFormat.COLOR_SEMANTIC_NAME, GpuFormat.RGBA8_UNORM)
			.addAttribute(DefaultVertexFormat.UV0_SEMANTIC_NAME, GpuFormat.RG32_FLOAT)
			.addAttribute(DefaultVertexFormat.UV1_SEMANTIC_NAME, GpuFormat.RG16_SINT)
			.addAttribute(DefaultVertexFormat.UV2_SEMANTIC_NAME, GpuFormat.RG16_SINT)
			.addAttribute(DefaultVertexFormat.LINE_WIDTH_SEMANTIC_NAME, GpuFormat.R32_FLOAT)
			.build();

	private static Matrix3x2f cachedPose = null;

	public static void submit(
			GuiGraphicsExtractor context,
			float x0, float y0, float x1, float y1,
			int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor,
			float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius,
			float outlineWidth
	) {
		submit(context, x0, y0, x1, y1, topLeftColor, topRightColor, bottomRightColor, bottomLeftColor, topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius, outlineWidth, null, null);
	}

	public static void submit(
			GuiGraphicsExtractor context,
			float x0, float y0, float x1, float y1,
			int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor,
			float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius,
			float outlineWidth,
			Identifier texture,
			ScreenRectangle clip
	) {
		RenderPipeline pipeline = (texture != null)
				? LucentRenderPipelines.PIPELINE_ROUND_RECT_TEXTURED
				: LucentRenderPipelines.PIPELINE_ROUND_RECT;
		emit(
				context, x0, y0, x1, y1,
				topLeftColor, topRightColor, bottomRightColor, bottomLeftColor,
				topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius,
				outlineWidth, AA_PADDING,
				pipeline, textureSetup(texture), clip
		);
	}

	public static void submitShadow(
			GuiGraphicsExtractor context,
			float x0, float y0, float x1, float y1,
			int color,
			float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius,
			float blur
	) {
		emit(
				context, x0, y0, x1, y1,
				color, color, color, color,
				topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius,
				blur, AA_PADDING + blur,
				LucentRenderPipelines.PIPELINE_ROUND_RECT_SHADOW, NO_TEXTURE, null
		);
	}

	private static void emit(
			GuiGraphicsExtractor context,
			float x0, float y0, float x1, float y1,
			int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor,
			float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius,
			float edgeWidth, float padding,
			RenderPipeline pipeline, TextureSetup textureSetup,
			ScreenRectangle clip
	) {
		if (((topLeftColor | topRightColor | bottomRightColor | bottomLeftColor) & ALPHA_MASK) == 0) return;

		float left = Math.min(x0, x1);
		float top = Math.min(y0, y1);
		float right = Math.max(x0, x1);
		float bottom = Math.max(y0, y1);

		float halfWidth = (right - left) * 0.5f;
		float halfHeight = (bottom - top) * 0.5f;
		if (halfWidth <= 0.0f || halfHeight <= 0.0f) return;

		Matrix3x2f pose = snapshotPose(context.pose());
		ScreenRectangle scissor = context.scissorStack.peek();

		int clipLeft = clip != null ? clip.left() : Integer.MIN_VALUE;
		int clipTop = clip != null ? clip.top() : Integer.MIN_VALUE;
		int clipRight = clip != null ? clip.right() : Integer.MAX_VALUE;
		int clipBottom = clip != null ? clip.bottom() : Integer.MAX_VALUE;

		int quadX0 = Math.max((int) Math.floor(left - padding), clipLeft);
		int quadY0 = Math.max((int) Math.floor(top - padding), clipTop);
		int quadX1 = Math.min((int) Math.ceil(right + padding), clipRight);
		int quadY1 = Math.min((int) Math.ceil(bottom + padding), clipBottom);
		if (quadX0 >= quadX1 || quadY0 >= quadY1) return;

		ScreenRectangle quad = new ScreenRectangle(quadX0, quadY0, quadX1 - quadX0, quadY1 - quadY0).transformMaxBounds(pose);
		ScreenRectangle bounds = (scissor != null) ? scissor.intersection(quad) : quad;

		context.guiRenderState.addGuiElement(new Element(
				pose,
				(float) quadX0, (float) quadY0, (float) quadX1, (float) quadY1,
				(left + right) * 0.5f, (top + bottom) * 0.5f,
				topLeftColor, topRightColor, bottomRightColor, bottomLeftColor,
				fixedPoint(halfWidth), fixedPoint(halfHeight),
				packRadius(topLeftRadius) | (packRadius(topRightRadius) << 8),
				packRadius(bottomRightRadius) | (packRadius(bottomLeftRadius) << 8),
				edgeWidth, pipeline, textureSetup,
				scissor, bounds
		));
	}

	private static TextureSetup textureSetup(Identifier texture) {
		if (texture == null) return NO_TEXTURE;
		var view = mc.getTextureManager().getTexture(texture).getTextureView();
		return TextureSetup.singleTexture(view, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
	}

	private static Matrix3x2f snapshotPose(Matrix3x2fc current) {
		if (cachedPose != null && cachedPose.equals(current, 0.0f)) return cachedPose;
		cachedPose = new Matrix3x2f(current);
		return cachedPose;
	}

	private static int fixedPoint(float value) {
		int rounded = Math.round(value * HALF_EXTENT_SCALE);
		return Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, rounded));
	}

	private static int packRadius(float radius) {
		float clamped = Math.max(0.0f, Math.min(MAX_RADIUS, radius));
		int rounded = Math.round(clamped * RADIUS_SCALE);
		return Math.max(0, Math.min(255, rounded));
	}

	private static class Element implements GuiElementRenderState {
		private final Matrix3x2fc pose;
		private final float x0, y0, x1, y1;
		private final float centerX, centerY;
		private final int topLeftColor, topRightColor, bottomRightColor, bottomLeftColor;
		private final int packedHalfWidth, packedHalfHeight;
		private final int packedRadiiTop, packedRadiiBottom;
		private final float edgeWidth;
		private final RenderPipeline pipeline;
		private final TextureSetup textureSetup;
		private final ScreenRectangle scissorArea;
		private final ScreenRectangle bounds;

		Element(
				Matrix3x2fc pose,
				float x0, float y0, float x1, float y1,
				float centerX, float centerY,
				int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor,
				int packedHalfWidth, int packedHalfHeight,
				int packedRadiiTop, int packedRadiiBottom,
				float edgeWidth,
				RenderPipeline pipeline,
				TextureSetup textureSetup,
				ScreenRectangle scissorArea,
				ScreenRectangle bounds
		) {
			this.pose = pose;
			this.x0 = x0;
			this.y0 = y0;
			this.x1 = x1;
			this.y1 = y1;
			this.centerX = centerX;
			this.centerY = centerY;
			this.topLeftColor = topLeftColor;
			this.topRightColor = topRightColor;
			this.bottomRightColor = bottomRightColor;
			this.bottomLeftColor = bottomLeftColor;
			this.packedHalfWidth = packedHalfWidth;
			this.packedHalfHeight = packedHalfHeight;
			this.packedRadiiTop = packedRadiiTop;
			this.packedRadiiBottom = packedRadiiBottom;
			this.edgeWidth = edgeWidth;
			this.pipeline = pipeline;
			this.textureSetup = textureSetup;
			this.scissorArea = scissorArea;
			this.bounds = bounds;
		}

		@Override
		public RenderPipeline pipeline() {
			return pipeline;
		}

		@Override
		public TextureSetup textureSetup() {
			return textureSetup;
		}

		@Override
		public ScreenRectangle scissorArea() {
			return scissorArea;
		}

		@Override
		public ScreenRectangle bounds() {
			return bounds;
		}

		@Override
		public void buildVertices(VertexConsumer consumer) {
			vertex(consumer, x0, y0, topLeftColor);
			vertex(consumer, x0, y1, bottomLeftColor);
			vertex(consumer, x1, y1, bottomRightColor);
			vertex(consumer, x1, y0, topRightColor);
		}

		private void vertex(VertexConsumer consumer, float x, float y, int color) {
			consumer.addVertexWith2DPose(pose, x, y)
					.setColor(color)
					.setUv(x - centerX, y - centerY)
					.setUv1(packedHalfWidth, packedHalfHeight)
					.setUv2(packedRadiiTop, packedRadiiBottom)
					.setLineWidth(edgeWidth);
		}
	}
}