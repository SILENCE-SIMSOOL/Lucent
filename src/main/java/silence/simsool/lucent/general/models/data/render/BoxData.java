package silence.simsool.lucent.general.models.data.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.AABB;
import silence.simsool.lucent.general.utils.render.LucentRenderType;
import silence.simsool.lucent.general.utils.render.Render3D;
import silence.simsool.lucent.ui.utils.UColor;

public class BoxData {
	public final AABB aabb;
	public final float r, g, b, a;
	public final float thickness;
	public final boolean depth;

	public BoxData(AABB aabb, int color, float thickness, boolean depth) {
		this.aabb = aabb;
		this.r = UColor.getRedF(color);
		this.g = UColor.getGreenF(color);
		this.b = UColor.getBlueF(color);
		this.a = UColor.getAlphaF(color);
		this.thickness = thickness;
		this.depth = depth;
	}

	public RenderType lineRenderType() {
		boolean fullyOpaque = a >= 0.999f;
		return Render3D.resolveLineRenderType(depth, fullyOpaque);
	}

	public RenderType filledRenderType() {
		return depth ? RenderTypes.debugFilledBox() : LucentRenderType.QUADS_ESP;
	}
}