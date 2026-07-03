package silence.simsool.lucent.general.models.data.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import silence.simsool.lucent.general.utils.render.Render3D;
import silence.simsool.lucent.ui.utils.UColor;

public class CircleData {
	public Vec3 center;
	public float radius;
	public float thickness;
	public float r, g, b, a;
	public boolean depth;
	public boolean filled;

	public CircleData(Vec3 center, float radius, float thickness, int color, boolean depth, boolean filled) {
		set(center, radius, thickness, color, depth, filled);
	}

	public void set(Vec3 center, float radius, float thickness, int color, boolean depth, boolean filled) {
		this.center = center;
		this.radius = radius;
		this.thickness = thickness;
		this.r = UColor.getRedF(color);
		this.g = UColor.getGreenF(color);
		this.b = UColor.getBlueF(color);
		this.a = UColor.getAlphaF(color);
		this.depth = depth;
		this.filled = filled;
	}

	public RenderType renderType() {
		boolean isFullyOpaque = a >= 0.999f;
		if (filled) return Render3D.resolveFillRenderType(depth, isFullyOpaque);
		else return Render3D.resolveLineRenderType(depth, isFullyOpaque);
	}
}