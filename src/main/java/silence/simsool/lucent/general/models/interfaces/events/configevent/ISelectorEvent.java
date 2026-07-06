package silence.simsool.lucent.general.models.interfaces.events.configevent;

import silence.simsool.lucent.events.impl.ConfigEvent;

@FunctionalInterface
public interface ISelectorEvent {
	void onChange(ConfigEvent.SelectorEvent event);
}