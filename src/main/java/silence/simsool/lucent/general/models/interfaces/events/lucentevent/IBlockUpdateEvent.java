package silence.simsool.lucent.general.models.interfaces.events.lucentevent;

import silence.simsool.lucent.events.impl.LucentEvent;

@FunctionalInterface
public interface IBlockUpdateEvent {
	void onBlockUpdate(LucentEvent.BlockUpdateEvent event);
}