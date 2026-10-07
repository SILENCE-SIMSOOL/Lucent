package silence.simsool.lucent.skija.backend;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;

import com.mojang.blaze3d.opengl.GlTextureView;
import com.mojang.blaze3d.textures.GpuTextureView;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.BackendTexture;
import io.github.humbleui.skija.ColorAlphaType;
import io.github.humbleui.skija.ColorType;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.GLTextureInfo;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceOrigin;

public class GlSkijaBackend implements SkijaBackend {

	public static final GlSkijaBackend INSTANCE = new GlSkijaBackend();

	private static final int GL_RGBA8 = 0x8058;

	private DirectContext cachedContext = null;
	private final Deque<Integer> fbos = new ArrayDeque<>();

	@Override
	public String getDisplayName() {
		return "OpenGL";
	}

	@Override
	public DirectContext getContext() {
		if (cachedContext == null) {
			cachedContext = DirectContext.makeGL();
		}
		return cachedContext;
	}

	@Override
	public void onBeginFrame() {
		GlStateGuard.neutralizeUnpack();
		getContext().resetGLAll();
	}

	@Override
	public void saveState() {
		GlStateGuard.save();
	}

	@Override
	public void restoreState() {
		GlStateGuard.restore();
	}

	@Override
	public boolean isFlipBlitV() {
		return true;
	}

	@Override
	public Surface wrapTarget(GpuTextureView view) {
		int glId = ((GlTextureView) view).glId();
		int w = view.getWidth(0);
		int h = view.getHeight(0);

		int prevFbo = GL30C.glGetInteger(GL30C.GL_FRAMEBUFFER_BINDING);
		int fbo = GL30C.glGenFramebuffers();
		GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, fbo);
		GL30C.glFramebufferTexture2D(GL30C.GL_FRAMEBUFFER, GL30C.GL_COLOR_ATTACHMENT0, GL11C.GL_TEXTURE_2D, glId, 0);
		GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, prevFbo);

		fbos.addLast(fbo);
		while (fbos.size() > 2) {
			GL30C.glDeleteFramebuffers(fbos.removeFirst());
		}

		BackendRenderTarget rt = BackendRenderTarget.makeGL(w, h, 0, 0, fbo, GL_RGBA8);
		return Surface.wrapBackendRenderTarget(getContext(), rt, SurfaceOrigin.BOTTOM_LEFT, ColorType.RGBA_8888, null);
	}

	@Override
	public Image wrapTexture(GpuTextureView view, boolean premultiplied) {
		if (!(view instanceof GlTextureView glView)) return null;
		int glId = glView.glId();
		GLTextureInfo info = new GLTextureInfo(GL11C.GL_TEXTURE_2D, glId, GL_RGBA8);
		BackendTexture backend = BackendTexture.makeGL(view.getWidth(0), view.getHeight(0), false, info);
		ColorAlphaType alpha = premultiplied ? ColorAlphaType.PREMUL : ColorAlphaType.UNPREMUL;
		Image image = Image.borrowTextureFrom(getContext(), backend, SurfaceOrigin.TOP_LEFT, ColorType.RGBA_8888, alpha, null, null);
		backend.close();
		return image;
	}

	@Override
	public void dispose() {
		while (!fbos.isEmpty()) {
			GL30C.glDeleteFramebuffers(fbos.removeFirst());
		}
		if (cachedContext != null) {
			cachedContext.close();
			cachedContext = null;
		}
	}

}