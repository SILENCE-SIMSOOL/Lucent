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

	private static DepthStencilState NO_DEPTH = new DepthStencilState(CompareOp.ALWAYS_PASS, false);
	private static ColorTargetState TRANSLUCENT = new ColorTargetState(BlendFunction.TRANSLUCENT);

	public static final RenderPipeline LINES = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Lucent.ID + "/lines_opaque")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline LINES_TRANSLUCENT = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Lucent.ID + "/lines_translucent")
			.withCull(false)
			.withColorTargetState(TRANSLUCENT)
			.build()
	);

	public static final RenderPipeline LINES_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Lucent.ID + "/lines_esp")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline LINES_TRANSLUCENT_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Lucent.ID + "/lines_translucent_esp")
			.withColorTargetState(TRANSLUCENT)
			.withDepthStencilState(NO_DEPTH)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Lucent.ID + "/quads_opaque")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_TRANSLUCENT = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Lucent.ID + "/quads_translucent")
			.withColorTargetState(TRANSLUCENT)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Lucent.ID + "/quads_esp")
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_TRANSLUCENT_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Lucent.ID + "/quads_translucent_esp")
			.withColorTargetState(TRANSLUCENT)
			.withDepthStencilState(NO_DEPTH)
			.withCull(false)
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
			.withColorTargetState(TRANSLUCENT)
			.build()
	);

}