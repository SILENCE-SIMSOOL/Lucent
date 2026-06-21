package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.events.impl.EntityEvent;

@FunctionalInterface
public interface INameChangeEvent {
	void onNameChange(EntityEvent.NameChangeEvent event);
}