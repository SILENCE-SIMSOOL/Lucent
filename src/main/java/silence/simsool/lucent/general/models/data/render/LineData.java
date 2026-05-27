package silence.simsool.lucent.general.models.data.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import silence.simsool.lucent.general.utils.render.RenderUtils;

public class LineData {
	public final Vec3 from, to;
	public final int color1, color2;
	public final float thickness;
	public final boolean depth;

	public LineData(Vec3 from, Vec3 to, int color1, int color2, float thickness, boolean depth) {
		this.from = from;
		this.to = to;
		this.color1 = color1;
		this.color2 = color2;
		this.thickness = thickness;
		this.depth = depth;
	}

	public RenderType renderType() {
		boolean fullyOpaque = RenderUtils.isFullyOpaque(color1) && RenderUtils.isFullyOpaque(color2);
		return RenderUtils.resolveLineRenderType(depth, fullyOpaque);
	}
}