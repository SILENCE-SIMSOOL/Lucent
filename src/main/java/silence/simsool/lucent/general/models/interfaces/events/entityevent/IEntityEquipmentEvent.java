package silence.simsool.lucent.general.models.interfaces.events.entityevent;

import silence.simsool.lucent.events.impl.EntityEvent;

@FunctionalInterface
public interface IEntityEquipmentEvent {
	void onEntityEquipment(EntityEvent.EntityEquipmentEvent event);
}
