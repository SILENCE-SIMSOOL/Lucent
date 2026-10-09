package silence.simsool.lucent.general.utils.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.Lucent;

public class LucentRenderPipelines {

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
			.withBlend(BlendFunction.TRANSLUCENT)
			.build()
	);

	public static final RenderPipeline LINES_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Lucent.ID + "/lines_esp")
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline LINES_TRANSLUCENT_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
			.withLocation(Lucent.ID + "/lines_translucent_esp")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withCull(false)
			.withDepthWrite(false)
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
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Lucent.ID + "/quads_esp")
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withCull(false)
			.build()
	);

	public static final RenderPipeline FILLED_TRANSLUCENT_ESP = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
			.withLocation(Lucent.ID + "/quads_translucent_esp")
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withBlend(BlendFunction.TRANSLUCENT)
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
				.withVertexFormat(RoundedRectRenderer.FORMAT, VertexFormat.Mode.QUADS)
				.withBlend(BlendFunction.TRANSLUCENT)
				.build()
		);
	}

}