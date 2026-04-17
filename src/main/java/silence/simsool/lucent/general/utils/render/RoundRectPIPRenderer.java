package silence.simsool.lucent.general.utils.render;

import static silence.simsool.lucent.Lucent.mc;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.MultiBufferSource;
import silence.simsool.lucent.general.utils.useful.UDisplay;

public class RoundRectPIPRenderer extends PictureInPictureRenderer<RoundRectPIPRenderer.State> {

	private static final DynamicUniformStorage<DynamicUniformStorage.DynamicUniform> uniformStorage = new DynamicUniformStorage<>(
			"Lucent Rounded Rectangle UBO",
			new Std140SizeCalculator()
					.putVec4() // u_Rect
					.putVec4() // u_Radii
					.putVec4() // u_OutlineColor
					.putVec4() // u_OutlineWidth (std140 padded)
					.get(),
			4
	);

	private State lastState;

	public RoundRectPIPRenderer(MultiBufferSource.BufferSource bufferSource) {
		super(bufferSource);
	}

	@Override
	public Class<State> getRenderStateClass() {
		return State.class;
	}

	@Override
	protected boolean textureIsReadyToBlit(State state) {
		return state.visuallyEquals(this.lastState);
	}

	@Override
	protected void renderToTexture(State state, PoseStack poseStack) {
		float w = state.width * state.scale;
		float h = state.height * state.scale;

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		builder.addVertex(0f, 0f, 0f).setColor(state.topLeftColor);
		builder.addVertex(0f, h, 0f).setColor(state.bottomLeftColor);
		builder.addVertex(w, h, 0f).setColor(state.bottomRightColor);
		builder.addVertex(w, 0f, 0f).setColor(state.topRightColor);

		try (MeshData mesh = builder.buildOrThrow()) {
			GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(
					RenderSystem.getModelViewMatrix(), new Vector4f(1f, 1f, 1f, 1f), new Vector3f(), new Matrix4f()
			);

			GpuBufferSlice uniforms = uniformStorage.writeUniform(buffer -> Std140Builder.intoBuffer(buffer)
					.putVec4(w * 0.5f, h * 0.5f, w, h)
					.putVec4(state.topLeftRadius * state.scale, state.topRightRadius * state.scale, state.bottomRightRadius * state.scale, state.bottomLeftRadius * state.scale)
					.putVec4(state.outlineRed, state.outlineGreen, state.outlineBlue, state.outlineAlpha)
					.putVec4(state.outlineWidth * state.scale, 0f, 0f, 0f));

			var pipelineVertexFormat = LucentRenderPipelines.PIPELINE_ROUND_RECT.getVertexFormat();
			var vertexBuffer = pipelineVertexFormat.uploadImmediateVertexBuffer(mesh.vertexBuffer());
			var indexStorage = RenderSystem.getSequentialBuffer(mesh.drawState().mode());
			var indexBuffer = indexStorage.getBuffer(mesh.drawState().indexCount());
			var renderTarget = mc.getMainRenderTarget();

			GpuTextureView colorTextureView = RenderSystem.outputColorTextureOverride;
			if (colorTextureView == null) colorTextureView = renderTarget.getColorTextureView();

			if (colorTextureView != null) {
				try (var pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Lucent Rounded Rectangle", colorTextureView, OptionalInt.empty(), renderTarget.useDepth ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : renderTarget.getDepthTextureView()) : null, OptionalDouble.empty())) {
					pass.setPipeline(LucentRenderPipelines.PIPELINE_ROUND_RECT);
					RenderSystem.bindDefaultUniforms(pass);
					pass.setUniform("DynamicTransforms", dynamicTransforms);
					pass.setUniform("u", uniforms);
					pass.setVertexBuffer(0, vertexBuffer);
					pass.setIndexBuffer(indexBuffer, indexStorage.type());
					pass.drawIndexed(0, 0, mesh.drawState().indexCount(), 1);
				}
			}
		}
		this.lastState = state;
	}

	@Override
	public String getTextureLabel() {
		return "Lucent Rounded Rectangle PIP";
	}

	public static void clear() {
		uniformStorage.endFrame();
	}

	public static void submit(GuiGraphics context, int x0, int y0, int x1, int y1, int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius, int outlineColor, float outlineWidth) {
		ScreenRectangle scissor = context.scissorStack.peek();
		Matrix3x2f pose = new Matrix3x2f(context.pose());

		Vector2f p0 = pose.transformPosition(new Vector2f((float) x0, (float) y0), new Vector2f());
		Vector2f p1 = pose.transformPosition(new Vector2f((float) x1, (float) y1), new Vector2f());

		int screenLeft = Math.round(Math.min(p0.x, p1.x));
		int screenTop = Math.round(Math.min(p0.y, p1.y));
		int screenW = Math.round(Math.max(p0.x, p1.x)) - screenLeft;
		int screenH = Math.round(Math.max(p0.y, p1.y)) - screenTop;

		float poseScale = pose.transformDirection(new Vector2f(1f, 0f), new Vector2f()).length();

		ScreenRectangle screenRect = new ScreenRectangle(screenLeft, screenTop, screenW, screenH);
		ScreenRectangle bounds = (scissor != null) ? scissor.intersection(screenRect) : screenRect;

		context.guiRenderState.submitPicturesInPictureState(new State(
				screenLeft, screenTop, screenW, screenH,
				topLeftColor, topRightColor, bottomRightColor, bottomLeftColor,
				topLeftRadius * poseScale, topRightRadius * poseScale, bottomRightRadius * poseScale, bottomLeftRadius * poseScale,
				outlineColor, outlineWidth * poseScale,
				scissor, bounds
		));
	}

	public static class State implements PictureInPictureRenderState {
		final int x, y, width, height;
		final int topLeftColor, topRightColor, bottomRightColor, bottomLeftColor;
		final float topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius;
		final int outlineColor;
		final float outlineWidth;
		private final ScreenRectangle scissorArea;
		private final ScreenRectangle bounds;

		final float scale = (float) UDisplay.getGuiScale();
		final float outlineRed, outlineGreen, outlineBlue, outlineAlpha;

		public State(int x, int y, int width, int height, int topLeftColor, int topRightColor, int bottomRightColor, int bottomLeftColor, float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius, int outlineColor, float outlineWidth, ScreenRectangle scissorArea, ScreenRectangle bounds) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.topLeftColor = topLeftColor;
			this.topRightColor = topRightColor;
			this.bottomRightColor = bottomRightColor;
			this.bottomLeftColor = bottomLeftColor;
			this.topLeftRadius = topLeftRadius;
			this.topRightRadius = topRightRadius;
			this.bottomRightRadius = bottomRightRadius;
			this.bottomLeftRadius = bottomLeftRadius;
			this.outlineColor = outlineColor;
			this.outlineWidth = outlineWidth;
			this.scissorArea = scissorArea;
			this.bounds = bounds;

			this.outlineRed = ((outlineColor >> 16) & 0xFF) / 255.0f;
			this.outlineGreen = ((outlineColor >> 8) & 0xFF) / 255.0f;
			this.outlineBlue = (outlineColor & 0xFF) / 255.0f;
			this.outlineAlpha = ((outlineColor >> 24) & 0xFF) / 255.0f;
		}

		@Override
		public int x0() {
			return x;
		}

		@Override
		public int y0() {
			return y;
		}

		@Override
		public int x1() {
			return x + width;
		}

		@Override
		public int y1() {
			return y + height;
		}

		@Override
		public float scale() {
			return 1.0f;
		}

		@Override
		public ScreenRectangle scissorArea() {
			return scissorArea;
		}

		@Override
		public ScreenRectangle bounds() {
			return bounds;
		}

		public boolean visuallyEquals(State other) {
			if (other == null) return false;
			return width == other.width &&
					height == other.height &&
					topLeftColor == other.topLeftColor &&
					topRightColor == other.topRightColor &&
					bottomRightColor == other.bottomRightColor &&
					bottomLeftColor == other.bottomLeftColor &&
					topLeftRadius == other.topLeftRadius &&
					topRightRadius == other.topRightRadius &&
					bottomRightRadius == other.bottomRightRadius &&
					bottomLeftRadius == other.bottomLeftRadius &&
					outlineColor == other.outlineColor &&
					outlineWidth == other.outlineWidth &&
					scale == other.scale;
		}
	}

}