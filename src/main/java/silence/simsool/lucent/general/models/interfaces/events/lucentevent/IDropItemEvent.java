package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.DropItemEvent;

@FunctionalInterface
public interface IDropItemEvent {
	void onDropItem(DropItemEvent event);
}