package silence.simsool.lucent.ui.utils.nvg;

import org.joml.Matrix3x2f;
import org.lwjgl.opengl.GL33C;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;

public class NVGPIPRenderer extends PictureInPictureRenderer<NVGPIPRenderer.NVGRenderState> {

	public NVGPIPRenderer() {
		super();
	}

	private static DirectStateAccess cachedDSA = null;
	private static boolean reflectionFailed = false;

	private static int getFbo(GlTexture colorTex, DirectStateAccess dsa, GlTexture depthTex) {
		try {
			var cacheField = GlTexture.class.getDeclaredField("frameBufferCache");
			cacheField.setAccessible(true);
			var cache = cacheField.get(colorTex);
			var getFboMethod = cache.getClass().getDeclaredMethod("getFbo", DirectStateAccess.class, java.util.List.class, com.mojang.blaze3d.opengl.FrameBufferAttachment.class);
			getFboMethod.setAccessible(true);
			return (int) getFboMethod.invoke(cache, dsa, java.util.List.of(colorTex), depthTex);
		} catch (Exception e) {
			return 0;
		}
	}

	private static DirectStateAccess getDSA() {
		if (cachedDSA != null) return cachedDSA;
		if (reflectionFailed) return null;
		try {
			var device = RenderSystem.getDevice();
			var backendField = device.getClass().getDeclaredField("backend");
			backendField.setAccessible(true);
			var backend = backendField.get(device);
			var dsaField = backend.getClass().getDeclaredField("directStateAccess");
			dsaField.setAccessible(true);
			cachedDSA = (DirectStateAccess) dsaField.get(backend);
			return cachedDSA;
		} catch (Exception e) {
			reflectionFailed = true;
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void renderToTexture(NVGRenderState state, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector submitNodeCollector) {
		GpuTextureView colorTex = RenderSystem.outputColorTextureOverride; if (colorTex == null) return;
		DirectStateAccess bufferManager = getDSA(); if (bufferManager == null) return;

		GpuTextureView depthTexObj = RenderSystem.outputDepthTextureOverride;
		if (!(depthTexObj != null && depthTexObj.texture() instanceof GlTexture glDepthTex)) return;

		int width = colorTex.getWidth(0);
		int height = colorTex.getHeight(0);

		if (colorTex.texture() instanceof GlTexture glColorTex) {
			int fbo = getFbo(glColorTex, bufferManager, glDepthTex);
			GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, fbo);
			GlStateManager._viewport(0, 0, width, height);
		}

		if (width <= 0 || height <= 0) return;

		GL33C.glBindSampler(0, 0);
		NVGRenderer.beginFrame(width, height);
		state.renderContent.run();
		NVGRenderer.endFrame();

		GlStateManager._disableDepthTest();
		GlStateManager._disableCull();
		GlStateManager._enableBlend(0);
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

	public static void draw(GuiGraphicsExtractor graphics, int x, int y, int width, int height, Runnable renderContent) {
		ScreenRectangle scissor = graphics.scissorStack.peek();
		Matrix3x2f pose = new Matrix3x2f(graphics.pose());
		ScreenRectangle bounds = createBounds(x, y, x + width, y + height, pose, scissor);
		NVGRenderState state = new NVGRenderState(x, y, width, height, scissor, bounds, renderContent);
		graphics.guiRenderState.addPicturesInPictureState(state);
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