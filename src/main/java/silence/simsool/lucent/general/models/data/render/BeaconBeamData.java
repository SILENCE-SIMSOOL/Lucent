package silence.simsool.lucent.general.models.data.render;

import net.minecraft.world.phys.Vec3;

public class BeaconBeamData {
	public Vec3 pos;
	public int color;
	public float partialTicks;
	public long gameTime;
	public boolean isScoping;

	public BeaconBeamData(Vec3 pos, int color, float partialTicks, long gameTime, boolean isScoping) {
		set(pos, color, partialTicks, gameTime, isScoping);
	}

	public void set(Vec3 pos, int color, float partialTicks, long gameTime, boolean isScoping) {
		this.pos = pos;
		this.color = color;
		this.partialTicks = partialTicks;
		this.gameTime = gameTime;
		this.isScoping = isScoping;
	}
}