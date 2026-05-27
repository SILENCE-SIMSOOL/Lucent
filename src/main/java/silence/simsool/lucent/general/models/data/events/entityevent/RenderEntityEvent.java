package silence.simsool.lucent.general.models.data.events.entityevent;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;

public class RenderEntityEvent {

	public final Entity entity;
	public final Frustum frustum;
	public final double x;
	public final double y;
	public final double z;
	private boolean canceled = false;

	public RenderEntityEvent(Entity entity, Frustum frustum, double x, double y, double z) {
		this.entity = entity;
		this.frustum = frustum;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}