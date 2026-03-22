package silence.simsool.lucent.ui.utils.nvg;

import org.joml.Matrix3x2f;
import org.lwjgl.opengl.GL33C;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;

public class NVGPIPRenderer extends PictureInPictureRenderer<NVGPIPRenderer.NVGRenderState> {

	public NVGPIPRenderer(MultiBufferSource.BufferSource vertexConsumers) {
		super(vertexConsumers);
	}

	@Override
	protected void renderToTexture(NVGRenderState state, PoseStack poseStack) {
		GpuTextureView colorTex = RenderSystem.outputColorTextureOverride; if (colorTex == null) return;
		GpuDevice device = RenderSystem.getDevice(); if (!(device instanceof GlDevice glDevice)) return;

		DirectStateAccess bufferManager = glDevice.directStateAccess();
		GpuTextureView depthTexObj = RenderSystem.outputDepthTextureOverride; if (!(depthTexObj != null && depthTexObj.texture() instanceof GlTexture glDepthTex)) return;

		int width = colorTex.getWidth(0);
		int height = colorTex.getHeight(0);

		if (colorTex.texture() instanceof GlTexture glColorTex) {
			int fbo = glColorTex.getFbo(bufferManager, glDepthTex);
			GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, fbo);
			GlStateManager._viewport(0, 0, width, height);
		}

		if (width <= 0 || height <= 0) return;

		GL33C.glBindSampler(0, 0);
		NVGRenderer.beginFrame(width, height);
		state.renderContent.run();
		NVGRenderer.endFrame();

		// I don't know if it's meaningful
		GlStateManager._disableDepthTest();
		GlStateManager._disableCull();
		GlStateManager._enableBlend();
		GlStateManager._blendFuncSeparate(770, 771, 1, 0);
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2f;
	}

	@Override
	public Class<NVGRenderState> getRenderStateClass() {
		return NVGRenderState.class;
	}

	@Override
	protected String getTextureLabel() {
		return "nvg_lucent_renderer";
	}

	public static void draw(GuiGraphics context, int x, int y, int width, int height, Runnable renderContent) {
		ScreenRectangle scissor = context.scissorStack.peek();
		Matrix3x2f pose = new Matrix3x2f(context.pose());
		ScreenRectangle bounds = createBounds(x, y, x + width, y + height, pose, scissor);
		NVGRenderState state = new NVGRenderState(x, y, width, height, scissor, bounds, renderContent);
		context.guiRenderState.submitPicturesInPictureState(state);
	}

	private static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, ScreenRectangle scissorArea) {
		ScreenRectangle  screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
		return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
	}

	public static class NVGRenderState implements PictureInPictureRenderState {
		private final int x, y, width, height;
		private final ScreenRectangle scissor;
		private final ScreenRectangle bounds;
		public final Runnable renderContent;

		public NVGRenderState(int x, int y, int width, int height, ScreenRectangle scissor, ScreenRectangle bounds, Runnable renderContent) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.scissor = scissor;
			this.bounds = bounds;
			this.renderContent = renderContent;
		}

		@Override
		public float scale() {
			return 1f;
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
		public ScreenRectangle scissorArea() {
			return scissor;
		}

		@Override
		public ScreenRectangle bounds() {
			return bounds;
		}
	}
}