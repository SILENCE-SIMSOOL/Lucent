package silence.simsool.lucent.mixin.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {

	@Accessor("LINES_SNIPPET")
	static RenderPipeline.Snippet getLinesSnippet() {
		throw new AssertionError();
	}

	@Accessor("DEBUG_FILLED_SNIPPET")
	static RenderPipeline.Snippet getDebugFilledSnippet() {
		throw new AssertionError();
	}

	@Accessor("GUI_SNIPPET")
	static RenderPipeline.Snippet getGuiSnippet() {
		throw new AssertionError();
	}

	@Invoker("register")
	static RenderPipeline invokeRegister(RenderPipeline pipeline) {
		throw new AssertionError();
	}
}
