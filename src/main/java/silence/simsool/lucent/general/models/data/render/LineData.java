package silence.simsool.lucent.general.models.data.render;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.Vec3;
import silence.simsool.lucent.general.utils.render.Render3D;

public class LineData {
	public Vec3 from, to;
	public int color1, color2;
	public float thickness;
	public boolean depth;
	public boolean isTracer;

	public LineData(Vec3 from, Vec3 to, int color1, int color2, float thickness, boolean depth, boolean isTracer) {
		set(from, to, color1, color2, thickness, depth, isTracer);
	}

	public LineData(Vec3 from, Vec3 to, int color1, int color2, float thickness, boolean depth) {
		set(from, to, color1, color2, thickness, depth, false);
	}

	public void set(Vec3 from, Vec3 to, int color1, int color2, float thickness, boolean depth, boolean isTracer) {
		this.from = from;
		this.to = to;
		this.color1 = color1;
		this.color2 = color2;
		this.thickness = thickness;
		this.depth = depth;
		this.isTracer = isTracer;
	}

	public RenderType renderType() {
		return Render3D.resolveLineRenderType(depth, Render3D.isFullyOpaque(color1) && Render3D.isFullyOpaque(color2));
	}
}