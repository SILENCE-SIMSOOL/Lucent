package silence.simsool.lucent.general.models.data.render;

import net.minecraft.world.phys.Vec3;

public class BeaconBeamData {
	public final Vec3 pos;
	public final int color;
	public final float partialTicks;
	public final long gameTime;
	public final boolean isScoping;

	public BeaconBeamData(Vec3 pos, int color, float partialTicks, long gameTime, boolean isScoping) {
		this.pos = pos;
		this.color = color;
		this.partialTicks = partialTicks;
		this.gameTime = gameTime;
		this.isScoping = isScoping;
	}
}