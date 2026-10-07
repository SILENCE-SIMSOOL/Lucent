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

	Image wrapTexture(GpuTextureView view, boolean premultiplied);

	default void orderWriteBeforeRead(GpuTextureView view) {}

	default boolean isFlipBlitU() {
		return false;
	}

	default boolean isFlipBlitV() {
		return false;
	}

	void dispose();

}