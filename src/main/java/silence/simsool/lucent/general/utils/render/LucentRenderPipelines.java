package silence.simsool.lucent.general.utils.render;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

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

	public static final RenderPipeline PIPELINE_ROUND_RECT = roundRect("round_rect", RenderPipelines.GUI_SNIPPET);
	public static final RenderPipeline PIPELINE_ROUND_RECT_TEXTURED = roundRect("round_rect_textured", RenderPipelines.GUI_TEXTURED_SNIPPET);
	public static final RenderPipeline PIPELINE_ROUND_RECT_SHADOW = roundRect("round_rect_shadow", RenderPipelines.GUI_SNIPPET);

	private static RenderPipeline roundRect(String name, RenderPipeline.Snippet snippet) {
		return RenderPipelines.register(
			RenderPipeline.builder(snippet)
				.withLocation(Identifier.fromNamespaceAndPath(Lucent.ID, "pipeline/" + name))
				.withFragmentShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/" + name))
				.withVertexShader(Identifier.fromNamespaceAndPath(Lucent.ID, "core/round_rect"))
				.withVertexBinding(0, RoundedRectRenderer.FORMAT)
				.build()
		);
	}

}