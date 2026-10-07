package silence.simsool.lucent.general.utils.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.UniformType;

import net.minecraft.resources.Identifier;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.mixin.accessors.RenderPipelinesAccessor;

public class LucentRenderPipelines {

	private static DepthStencilState NO_DEPTH = new DepthStencilState(CompareOp.ALWAYS_PASS, false);
	private static ColorTargetState TRANSLUCENT = new ColorTargetState(BlendFunction.TRANSLUCENT);

	public static final RenderPipeline LINES = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getLinesSnippet())
			.withLocation(Lucent.ID + "/lines_opaque")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline LINES_TRANSLUCENT = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getLinesSnippet())
			.withLocation(Lucent.ID + "/lines_translucent")
			.withCull(false)
			.withColorTargetState(TRANSLUCENT)
			.build()
	);

	public static final RenderPipeline LINES_ESP = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getLinesSnippet())
			.withLocation(Lucent.ID + "/lines_esp")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline LINES_TRANSLUCENT_ESP = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getLinesSnippet())
			.withLocation(Lucent.ID + "/lines_translucent_esp")
			.withColorTargetState(TRANSLUCENT)
			.withDepthStencilState(NO_DEPTH)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getDebugFilledSnippet())
			.withLocation(Lucent.ID + "/quads_opaque")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_TRANSLUCENT = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getDebugFilledSnippet())
			.withLocation(Lucent.ID + "/quads_translucent")
			.withColorTargetState(TRANSLUCENT)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_ESP = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getDebugFilledSnippet())
			.withLocation(Lucent.ID + "/quads_esp")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_TRANSLUCENT_ESP = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getDebugFilledSnippet())
			.withLocation(Lucent.ID + "/quads_translucent_esp")
			.withColorTargetState(TRANSLUCENT)
			.withDepthStencilState(NO_DEPTH)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline PIPELINE_ROUND_RECT = RenderPipelinesAccessor.invokeRegister(
		RenderPipeline.builder(RenderPipelinesAccessor.getGuiSnippet())
			.withLocation(Identifier.fromNamespaceAndPath(Lucent.ID, "pipeline/round_rect"))
			.withFragmentShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/round_rect"))
			.withVertexShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/round_rect"))
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withBindGroupLayout(BindGroupLayout.builder().withUniform("u", UniformType.UNIFORM_BUFFER).build())
			.withColorTargetState(TRANSLUCENT)
			.build()
	);
}