package silence.simsool.lucent.general.models.interfaces.events.entityevent;

import silence.simsool.lucent.events.impl.EntityEvent;

@FunctionalInterface
public interface IEntityLeaveEvent {
	void onEntityLeave(EntityEvent.EntityLeaveEvent event);
}