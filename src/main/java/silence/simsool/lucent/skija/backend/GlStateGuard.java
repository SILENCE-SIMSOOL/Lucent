package silence.simsool.lucent.skija.backend;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL12C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL14C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL21C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryStack;

public class GlStateGuard {

	private static final int TEX_UNITS = 12;

	private static int sDrawFbo = 0;
	private static int sReadFbo = 0;
	private static int sProgram = 0;
	private static int sVao = 0;
	private static int sArrayBuffer = 0;
	private static int sElementBuffer = 0;
	private static int sActiveTexture = GL13C.GL_TEXTURE0;
	private static final int[] sTexBinding = new int[TEX_UNITS];
	private static final int[] sSamplerBinding = new int[TEX_UNITS];
	private static boolean sBlend = false;
	private static int sBlendSrcRgb = 0;
	private static int sBlendDstRgb = 0;
	private static int sBlendSrcAlpha = 0;
	private static int sBlendDstAlpha = 0;
	private static int sBlendEqRgb = 0;
	private static int sBlendEqAlpha = 0;
	private static boolean sDepthTest = false;
	private static int sDepthFunc = 0;
	private static boolean sDepthMask = false;
	private static boolean sCull = false;
	private static boolean sScissor = false;
	private static final int[] sScissorBox = new int[4];
	private static final boolean[] sColorMask = new boolean[4];
	private static boolean sFramebufferSrgb = false;
	private static int sUnpackBuffer = 0;
	private static int sUnpackRowLength = 0;
	private static int sUnpackImageHeight = 0;
	private static int sUnpackSkipPixels = 0;
	private static int sUnpackSkipRows = 0;
	private static int sUnpackSkipImages = 0;
	private static int sUnpackAlignment = 4;

	public static void neutralizeUnpack() {
		GL15C.glBindBuffer(GL21C.GL_PIXEL_UNPACK_BUFFER, 0);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_ROW_LENGTH, 0);
		GL11C.glPixelStorei(GL12C.GL_UNPACK_IMAGE_HEIGHT, 0);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_PIXELS, 0);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_ROWS, 0);
		GL11C.glPixelStorei(GL12C.GL_UNPACK_SKIP_IMAGES, 0);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, 4);
	}

	public static void save() {
		sDrawFbo = GL11C.glGetInteger(GL30C.GL_DRAW_FRAMEBUFFER_BINDING);
		sReadFbo = GL11C.glGetInteger(GL30C.GL_READ_FRAMEBUFFER_BINDING);
		sProgram = GL11C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
		sVao = GL11C.glGetInteger(GL30C.GL_VERTEX_ARRAY_BINDING);
		sArrayBuffer = GL11C.glGetInteger(GL15C.GL_ARRAY_BUFFER_BINDING);
		sElementBuffer = GL11C.glGetInteger(GL15C.GL_ELEMENT_ARRAY_BUFFER_BINDING);
		sActiveTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE);
		for (int i = 0; i < TEX_UNITS; i++) {
			GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + i);
			sTexBinding[i] = GL11C.glGetInteger(GL11C.GL_TEXTURE_BINDING_2D);
			sSamplerBinding[i] = GL11C.glGetInteger(GL33C.GL_SAMPLER_BINDING);
		}
		GL13C.glActiveTexture(sActiveTexture);

		sBlend = GL11C.glIsEnabled(GL11C.GL_BLEND);
		sBlendSrcRgb = GL11C.glGetInteger(GL14C.GL_BLEND_SRC_RGB);
		sBlendDstRgb = GL11C.glGetInteger(GL14C.GL_BLEND_DST_RGB);
		sBlendSrcAlpha = GL11C.glGetInteger(GL14C.GL_BLEND_SRC_ALPHA);
		sBlendDstAlpha = GL11C.glGetInteger(GL14C.GL_BLEND_DST_ALPHA);
		sBlendEqRgb = GL11C.glGetInteger(GL20C.GL_BLEND_EQUATION_RGB);
		sBlendEqAlpha = GL11C.glGetInteger(GL20C.GL_BLEND_EQUATION_ALPHA);

		sDepthTest = GL11C.glIsEnabled(GL11C.GL_DEPTH_TEST);
		sDepthFunc = GL11C.glGetInteger(GL11C.GL_DEPTH_FUNC);
		sDepthMask = GL11C.glGetInteger(GL11C.GL_DEPTH_WRITEMASK) != 0;
		sCull = GL11C.glIsEnabled(GL11C.GL_CULL_FACE);
		sScissor = GL11C.glIsEnabled(GL11C.GL_SCISSOR_TEST);
		GL11C.glGetIntegerv(GL11C.GL_SCISSOR_BOX, sScissorBox);
		sFramebufferSrgb = GL11C.glIsEnabled(GL30C.GL_FRAMEBUFFER_SRGB);

		sUnpackBuffer = GL11C.glGetInteger(GL21C.GL_PIXEL_UNPACK_BUFFER_BINDING);
		sUnpackRowLength = GL11C.glGetInteger(GL11C.GL_UNPACK_ROW_LENGTH);
		sUnpackImageHeight = GL11C.glGetInteger(GL12C.GL_UNPACK_IMAGE_HEIGHT);
		sUnpackSkipPixels = GL11C.glGetInteger(GL11C.GL_UNPACK_SKIP_PIXELS);
		sUnpackSkipRows = GL11C.glGetInteger(GL11C.GL_UNPACK_SKIP_ROWS);
		sUnpackSkipImages = GL11C.glGetInteger(GL12C.GL_UNPACK_SKIP_IMAGES);
		sUnpackAlignment = GL11C.glGetInteger(GL11C.GL_UNPACK_ALIGNMENT);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			ByteBuffer buf = stack.malloc(4);
			GL11C.glGetBooleanv(GL11C.GL_COLOR_WRITEMASK, buf);
			for (int i = 0; i < 4; i++) {
				sColorMask[i] = buf.get(i) != 0;
			}
		}
	}

	public static void restore() {
		GL30C.glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, sDrawFbo);
		GL30C.glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, sReadFbo);
		GL20C.glUseProgram(sProgram);
		GL30C.glBindVertexArray(sVao);
		GL15C.glBindBuffer(GL15C.GL_ELEMENT_ARRAY_BUFFER, sElementBuffer);
		GL15C.glBindBuffer(GL15C.GL_ARRAY_BUFFER, sArrayBuffer);
		for (int i = 0; i < TEX_UNITS; i++) {
			GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + i);
			GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, sTexBinding[i]);
			GL33C.glBindSampler(i, sSamplerBinding[i]);
		}
		GL13C.glActiveTexture(sActiveTexture);

		setEnabled(GL11C.GL_BLEND, sBlend);
		GL14C.glBlendFuncSeparate(sBlendSrcRgb, sBlendDstRgb, sBlendSrcAlpha, sBlendDstAlpha);
		GL20C.glBlendEquationSeparate(sBlendEqRgb, sBlendEqAlpha);

		setEnabled(GL11C.GL_DEPTH_TEST, sDepthTest);
		GL11C.glDepthFunc(sDepthFunc);
		GL11C.glDepthMask(sDepthMask);
		setEnabled(GL11C.GL_CULL_FACE, sCull);
		setEnabled(GL11C.GL_SCISSOR_TEST, sScissor);
		GL11C.glScissor(sScissorBox[0], sScissorBox[1], sScissorBox[2], sScissorBox[3]);
		setEnabled(GL30C.GL_FRAMEBUFFER_SRGB, sFramebufferSrgb);
		GL11C.glColorMask(sColorMask[0], sColorMask[1], sColorMask[2], sColorMask[3]);

		GL15C.glBindBuffer(GL21C.GL_PIXEL_UNPACK_BUFFER, sUnpackBuffer);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_ROW_LENGTH, sUnpackRowLength);
		GL11C.glPixelStorei(GL12C.GL_UNPACK_IMAGE_HEIGHT, sUnpackImageHeight);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_PIXELS, sUnpackSkipPixels);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_SKIP_ROWS, sUnpackSkipRows);
		GL11C.glPixelStorei(GL12C.GL_UNPACK_SKIP_IMAGES, sUnpackSkipImages);
		GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, sUnpackAlignment);
	}

	private static void setEnabled(int cap, boolean on) {
		if (on) GL11C.glEnable(cap);
		else GL11C.glDisable(cap);
	}

}