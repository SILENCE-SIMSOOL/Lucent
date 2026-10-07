package silence.simsool.lucent.mixin.accessors;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PictureInPictureRenderer.class)
public interface PictureInPictureRendererAccessor {

	@Accessor("textureView")
	GpuTextureView getTextureView();

	@Accessor("depthTextureView")
	GpuTextureView getDepthTextureView();
}
