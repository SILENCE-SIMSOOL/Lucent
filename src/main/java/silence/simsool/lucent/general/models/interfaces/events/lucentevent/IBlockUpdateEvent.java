package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.general.models.data.events.lucentevent.BlockUpdateEvent;

@FunctionalInterface
public interface IBlockUpdateEvent {
	void onBlockUpdate(BlockUpdateEvent event);
}