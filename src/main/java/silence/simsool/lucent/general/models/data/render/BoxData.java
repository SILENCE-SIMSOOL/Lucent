package silence.simsool.lucent.general.models.data.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.AABB;
import silence.simsool.lucent.general.utils.render.Render3D;
import silence.simsool.lucent.ui.utils.UColor;

public class BoxData {
	public AABB aabb;
	public float r, g, b, a;
	public float thickness;
	public boolean depth;

	public BoxData(AABB aabb, int color, float thickness, boolean depth) {
		set(aabb, color, thickness, depth);
	}

	public void set(AABB aabb, int color, float thickness, boolean depth) {
		this.aabb = aabb;
		this.r = UColor.getRedF(color);
		this.g = UColor.getGreenF(color);
		this.b = UColor.getBlueF(color);
		this.a = UColor.getAlphaF(color);
		this.thickness = thickness;
		this.depth = depth;
	}

	public RenderType lineRenderType() {
		boolean isFullyOpaque = a >= 0.999f;
		return Render3D.resolveLineRenderType(depth, isFullyOpaque);
	}

	public RenderType filledRenderType() {
		boolean isFullyOpaque = a >= 0.999f;
		return Render3D.resolveFillRenderType(depth, isFullyOpaque);
	}
}