package silence.simsool.lucent.general.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.Lucent;

public class LucentRenderPipelines {

	public static final RenderPipeline LINES_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation(Lucent.ID + "/lines_esp")
			.build()
	);

	public static final RenderPipeline LINES_TRANSLUCENT_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation(Lucent.ID + "/lines_translucent_esp")
			.withDepthWrite(false)
			.build()
	);

	public static final RenderPipeline QUADS_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withLocation(Lucent.ID + "/quads_esp")
			.build()
	);

	public static final RenderPipeline PIPELINE_ROUND_RECT = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath(Lucent.ID, "pipeline/round_rect"))
			.withFragmentShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/round_rect"))
			.withVertexShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/round_rect"))
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.withUniform("u", UniformType.UNIFORM_BUFFER)
			.withBlend(BlendFunction.TRANSLUCENT)
			.build()
	);

}