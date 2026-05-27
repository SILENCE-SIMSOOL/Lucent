package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.BlockInteractEvent;

@FunctionalInterface
public interface IBlockInteractEvent {
	void onBlockInteract(BlockInteractEvent event);
}