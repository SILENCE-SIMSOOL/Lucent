package silence.simsool.lucent.general.utils.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.Lucent;

public class LucentRenderPipelines {

	public static final RenderPipeline LINES_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
			.withLocation(Lucent.ID + "/lines_esp")
			.build()
	);

	public static final RenderPipeline LINES_TRANSLUCENT_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
			.withLocation(Lucent.ID + "/lines_translucent_esp")
			.build()
	);

	public static final RenderPipeline QUADS_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
			.withLocation(Lucent.ID + "/quads_esp")
			.build()
	);

	public static final RenderPipeline PIPELINE_ROUND_RECT = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(Identifier.fromNamespaceAndPath(Lucent.ID, "pipeline/round_rect"))
			.withFragmentShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/round_rect"))
			.withVertexShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/round_rect"))
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withBindGroupLayout(BindGroupLayout.builder().withUniform("u", UniformType.UNIFORM_BUFFER).build())
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.build()
	);

}