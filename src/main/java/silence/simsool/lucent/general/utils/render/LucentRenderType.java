package silence.simsool.lucent.general.utils.render;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class LucentRenderType {

	public static final RenderType LINES_ESP = RenderType.create(
		"lines-esp",
		RenderSetup.builder(LucentRenderPipelines.LINES_ESP)
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
			.createRenderSetup()
	);

	public static final RenderType LINES_TRANSLUCENT_ESP = RenderType.create(
		"lines-translucent-esp",
		RenderSetup.builder(LucentRenderPipelines.LINES_TRANSLUCENT_ESP)
			.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
			.createRenderSetup()
	);

	public static final RenderType QUADS_ESP = RenderType.create(
		"quads-esp",
		RenderSetup.builder(LucentRenderPipelines.QUADS_ESP)
			.sortOnUpload()
			.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
			.createRenderSetup()
	);

}