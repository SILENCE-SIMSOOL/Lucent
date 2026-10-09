package silence.simsool.lucent.skija.backend;

import com.mojang.blaze3d.textures.GpuTextureView;

import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.Surface;

public interface SkijaBackend {

	String getDisplayName();

	DirectContext getContext();

	void onBeginFrame();

	default void saveState() {}

	default void restoreState() {}

	Surface wrapTarget(GpuTextureView view);

	default void releaseTarget(GpuTextureView view) {}

	Image wrapTexture(GpuTextureView view, boolean premultiplied);

	default void orderWriteBeforeRead(GpuTextureView view) {}

	default void orderWritesBeforeRead(GpuTextureView first, GpuTextureView second) {
		orderWriteBeforeRead(first);
		if (second != null) orderWriteBeforeRead(second);
	}

	default void waitForIdle() {
		getContext().flushAndSubmit(true);
	}

	default boolean isFlipBlitU() {
		return false;
	}

	default boolean isFlipBlitV() {
		return false;
	}

	void dispose();

}