package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IRenderEntityEvent;
import silence.simsool.lucent.general.models.interfaces.events.entityevent.IRenderLivingPreEvent;

public final class EntityEvent {

	public static final Event<IRenderLivingPreEvent> RENDER_LIVING_PRE_EVENT = createArrayBacked(
		IRenderLivingPreEvent.class, listeners -> event -> {
			for (IRenderLivingPreEvent listener : listeners) {
				listener.onRenderLivingPre(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IRenderEntityEvent> RENDER_ENTITY_EVENT = createArrayBacked(
		IRenderEntityEvent.class, listeners -> event -> {
			for (IRenderEntityEvent listener : listeners) {
				listener.onRenderEntity(event);
				if (event.isCanceled()) break;
			}
		}
	);

}