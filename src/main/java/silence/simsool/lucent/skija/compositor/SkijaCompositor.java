package silence.simsool.lucent.skija.compositor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.joml.Matrix3x2f;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Matrix33;
import io.github.humbleui.skija.Surface;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.skija.backend.SkijaBackend;
import silence.simsool.lucent.skija.backend.SkijaBackends;
import silence.simsool.lucent.skija.natives.SkijaNatives;

public class SkijaCompositor {

	public static final SkijaCompositor INSTANCE = new SkijaCompositor();

	private static final int UI_TEXTURE_USAGE = 0xf;

	private static class Target {
		final String name;
		GpuTexture texture;
		GpuTextureView view;
		Surface surface;
		int width = 0;
		int height = 0;

		Target(String name) {
			this.name = name;
		}

		boolean matches(int w, int h) {
			return surface != null && width == w && height == h;
		}

		void ensure(SkijaBackend backend, int w, int h) {
			if (matches(w, h)) return;
			close();
			texture = RenderSystem.getDevice().createTexture(name, UI_TEXTURE_USAGE, GpuFormat.RGBA8_UNORM, w, h, 1, 1);
			view = RenderSystem.getDevice().createTextureView(texture);
			surface = backend.wrapTarget(view);
			surface.getCanvas().clear(0);
			width = w;
			height = h;
		}

		void close() {
			if (surface != null) {
				surface.close();
				surface = null;
			}
			if (view != null) {
				view.close();
				view = null;
			}
			if (texture != null) {
				texture.close();
				texture = null;
			}
			width = 0;
			height = 0;
		}
	}

	private final Target hudTarget = new Target("lucent hud");
	private final Target overlayTarget = new Target("lucent ui");
	private int hudSplit = -1;
	private boolean warmed = false;
	private boolean inHud = false;
	private boolean screenBlitted = false;
	private boolean hudCacheValid = false;
	private boolean hudReuseRequested = false;
	private Runnable hudReuseFallback;
	private static final boolean PROFILE = Boolean.getBoolean("lucent.skija.profile");
	private static final boolean SKIP_BLIT = Boolean.getBoolean("lucent.skija.skipBlit");
	private int profileFrames = 0;
	private int profileReusedHudFrames = 0;
	private long profileTotalNs = 0;
	private long profileClearNs = 0;
	private long profileDrawNs = 0;
	private long profileSubmitNs = 0;
	private long profileBarrierNs = 0;
	private long profileBlitQueueNs = 0;

	private final List<Consumer<Canvas>> batch = new ArrayList<>();

	public boolean hasContent() {
		return !batch.isEmpty();
	}

	public int size() {
		return batch.size();
	}

	public void enqueue(Consumer<Canvas> op) {
		batch.add(op);
	}

	public void enqueue(Runnable task) {
		if (task == null) return;
		batch.add(canvas -> task.run());
	}

	public void discard() {
		batch.clear();
	}

	public void beginHud() {
		this.inHud = true;
		this.hudReuseRequested = false;
		this.hudReuseFallback = null;
	}

	public boolean canReuseHud() {
		Window window = UDisplay.getWindow();
		return hudCacheValid && batch.isEmpty() && !window.isIconified()
				&& hudTarget.matches(window.getWidth(), window.getHeight());
	}

	public void reuseHud(Runnable redraw) {
		hudReuseRequested = true;
		hudReuseFallback = redraw;
	}

	public void invalidateHudCache() {
		hudCacheValid = false;
	}

	public void markHudLayer(GuiRenderState guiRenderState) {
		this.inHud = false;
		if (!SkijaNatives.isReady()) return;
		SkijaBackend backend = SkijaBackends.getActive(); if (backend == null) return;

		int queued = batch.size(); if (queued == 0 && !hudReuseRequested) return;

		Window window = UDisplay.getWindow();
		int w = window.getWidth();
		int h = window.getHeight();
		if (window.isIconified() || w <= 0 || h <= 0) return;

		if (queued == 0) {
			hudReuseRequested = false;
			hudReuseFallback = null;
			if (hudCacheValid && hudTarget.matches(w, h)) {
				addBlit(guiRenderState, backend, hudTarget.view, window.getGuiScaledWidth(), window.getGuiScaledHeight());
				if (PROFILE && ++profileReusedHudFrames == 3000) {
					Lucent.LOG.info("Skija HUD cache reused 3000 frames");
					profileReusedHudFrames = 0;
				}
			}
			return;
		}
		if (hudReuseRequested && hudReuseFallback != null) {
			hudReuseFallback.run();
			queued = batch.size();
			hudCacheValid = false;
		}
		hudReuseRequested = false;
		hudReuseFallback = null;
		hudTarget.ensure(backend, w, h);
		GpuTextureView blitView = hudTarget.view; if (blitView == null) return;

		hudSplit = queued;
		addBlit(guiRenderState, backend, blitView, window.getGuiScaledWidth(), window.getGuiScaledHeight());
	}

	public void markScreenLayer(GuiRenderState guiRenderState) {
		if (inHud) return;
		if (screenBlitted) return;
		if (!SkijaNatives.isReady()) return;
		SkijaBackend backend = SkijaBackends.getActive(); if (backend == null) return;

		int queued = batch.size(); if (queued == 0) return;

		Window window = UDisplay.getWindow();
		int w = window.getWidth();
		int h = window.getHeight();
		if (window.isIconified() || w <= 0 || h <= 0) return;

		overlayTarget.ensure(backend, w, h);
		GpuTextureView blitView = overlayTarget.view; if (blitView == null) return;

		screenBlitted = true;
		addBlit(guiRenderState, backend, blitView, window.getGuiScaledWidth(), window.getGuiScaledHeight());
		guiRenderState.up();
	}

	public void composite(GuiRenderState guiRenderState) {
		if (!SkijaNatives.isReady()) {
			discard();
			return;
		}

		SkijaBackend backend = SkijaBackends.getActive();
		if (backend == null) {
			discard();
			return;
		}

		if (!warmed) {
			warmed = true;
			warmContext(backend);
		}

		if (batch.isEmpty()) {
			return;
		}

		long profileStart = PROFILE ? System.nanoTime() : 0;
		backend.saveState();
		try {
			backend.onBeginFrame();

			Window window = UDisplay.getWindow();
			int w = window.getWidth();
			int h = window.getHeight();
			if (window.isIconified() || w <= 0 || h <= 0) return;

			Matrix3x2f pose = new Matrix3x2f();

			if (hudSplit > 0) {
				hudTarget.ensure(backend, w, h);
				render(backend, hudTarget, pose, 0, hudSplit);
			}

			int overlayFrom = Math.max(0, hudSplit);
			if (batch.size() > overlayFrom) {
				overlayTarget.ensure(backend, w, h);
				render(backend, overlayTarget, pose, overlayFrom, Integer.MAX_VALUE);
				if (!screenBlitted && overlayTarget.view != null) {
					addTopmostBlit(guiRenderState, backend, overlayTarget.view, window.getGuiScaledWidth(), window.getGuiScaledHeight());
				}
			}
		} catch (Throwable t) {
			Lucent.LOG.error("Skija compositing failed: " + t.getMessage());
		} finally {
			if (PROFILE) profileTotalNs += System.nanoTime() - profileStart;
			if (PROFILE && ++profileFrames == 300) {
				Lucent.LOG.info("Skija CPU avg (us/frame): total={}, clear={}, draw={}, submit={}, barrier={}, blitQueue={}",
						profileTotalNs / 300_000, profileClearNs / 300_000, profileDrawNs / 300_000,
						profileSubmitNs / 300_000, profileBarrierNs / 300_000,
						profileBlitQueueNs / 300_000);
				profileFrames = 0;
				profileTotalNs = profileClearNs = profileDrawNs = profileSubmitNs = profileBarrierNs = profileBlitQueueNs = 0;
			}
			discard();
			hudSplit = -1;
			screenBlitted = false;
			inHud = false;
			backend.restoreState();
		}
	}

	private void render(SkijaBackend backend, Target target, Matrix3x2f pose, int from, int to) {
		Surface surface = target.surface; if (surface == null) return;
		GpuTextureView view = target.view; if (view == null) return;

		backend.onBeginFrame();
		Canvas canvas = surface.getCanvas();
		long start = PROFILE ? System.nanoTime() : 0;
		canvas.clear(0);
		long cleared = PROFILE ? System.nanoTime() : 0;

		int baseSave = canvas.save();
		canvas.concat(toMatrix33(pose));

		try {
			int end = Math.min(to, batch.size());
			for (int i = from; i < end; i++) {
				batch.get(i).accept(canvas);
			}
			long drawn = PROFILE ? System.nanoTime() : 0;
			backend.getContext().flushAndSubmit(surface, false);
			if (PROFILE) {
				profileClearNs += cleared - start;
				profileDrawNs += drawn - cleared;
				profileSubmitNs += System.nanoTime() - drawn;
			}
		} finally {
			canvas.restoreToCount(baseSave);
		}

		long beforeBarrier = PROFILE ? System.nanoTime() : 0;
		backend.orderWriteBeforeRead(view);
		if (PROFILE) profileBarrierNs += System.nanoTime() - beforeBarrier;
		if (target == hudTarget) hudCacheValid = true;
	}

	private static Matrix33 toMatrix33(Matrix3x2f m) {
		return new Matrix33(
			m.m00, m.m10, m.m20,
			m.m01, m.m11, m.m21,
			0f,    0f,    1f
		);
	}

	private void addTopmostBlit(GuiRenderState guiRenderState, SkijaBackend backend, GpuTextureView blitView, int guiScaledWidth, int guiScaledHeight) {
		guiRenderState.nextStratum();
		addBlit(guiRenderState, backend, blitView, guiScaledWidth, guiScaledHeight);
	}

	private void addBlit(GuiRenderState guiRenderState, SkijaBackend backend, GpuTextureView blitView, int guiScaledWidth, int guiScaledHeight) {
		if (SKIP_BLIT) return;
		long start = PROFILE ? System.nanoTime() : 0;
		GpuSampler sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST);
		float u0 = backend.isFlipBlitU() ? 1f : 0f;
		float u1 = backend.isFlipBlitU() ? 0f : 1f;
		float v0 = backend.isFlipBlitV() ? 1f : 0f;
		float v1 = backend.isFlipBlitV() ? 0f : 1f;

		guiRenderState.addBlitToCurrentLayer(
			new BlitRenderState(
					RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
					TextureSetup.singleTexture(blitView, sampler),
					new Matrix3x2f(),
					0, 0, guiScaledWidth, guiScaledHeight,
					u0, u1, v0, v1,
					-1,
					null
			)
		);
		if (PROFILE) profileBlitQueueNs += System.nanoTime() - start;
	}

	private void warmContext(SkijaBackend backend) {
		backend.saveState();
		try {
			DirectContext ctx = backend.getContext();
			Lucent.LOG.info("Skija GPU context pre-warmed: " + ctx);
		} catch (Throwable t) {
			Lucent.LOG.warn("Skija context warmup failed: " + t.getMessage());
		} finally {
			backend.restoreState();
		}
	}

	public void shutdown() {
		try {
			hudCacheValid = false;
			hudTarget.close();
			overlayTarget.close();
			SkijaBackends.reset();
		} catch (Throwable t) {
			Lucent.LOG.warn("Skija shutdown failed: " + t.getMessage());
		}
	}

}