package silence.simsool.lucent.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.SubmitNodeStorage;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
	@Accessor("renderBuffers")
	RenderBuffers getRenderBuffers();

	@Accessor
	SubmitNodeStorage getSubmitNodeStorage();
}