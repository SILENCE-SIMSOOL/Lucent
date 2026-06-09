package silence.simsool.lucent.general.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public class IrisCompatNoOp implements IrisCompatibility {
	@Override
	public void registerPipeline(RenderPipeline pipeline, IrisShaderType shaderType) {
		// No-op
	}
}
