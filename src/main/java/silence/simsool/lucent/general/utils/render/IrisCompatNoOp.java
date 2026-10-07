package silence.simsool.lucent.general.utils.render;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;

public class IrisCompatNoOp implements IrisCompatibility {
	@Override
	public void registerPipeline(RenderPipeline pipeline, IrisShaderType shaderType) {
		// No-op
	}
}