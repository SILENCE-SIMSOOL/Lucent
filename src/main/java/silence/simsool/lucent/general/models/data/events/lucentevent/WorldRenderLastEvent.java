package silence.simsool.lucent.general.models.data.events.lucentevent;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.renderer.LevelRenderer;

public class WorldRenderLastEvent {

	public final WorldRenderContext context;
	public final LevelRenderer handler;
	public final float partialTick;

	public WorldRenderLastEvent(WorldRenderContext context, LevelRenderer handler, float partialTick) {
		this.context = context;
		this.handler = handler;
		this.partialTick = partialTick;
	}

	public float getFloat() {
		return partialTick;
	}

}