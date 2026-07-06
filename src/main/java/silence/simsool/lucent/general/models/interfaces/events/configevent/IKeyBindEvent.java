package silence.simsool.lucent.general.models.interfaces.events.configevent;

import silence.simsool.lucent.events.impl.ConfigEvent;

@FunctionalInterface
public interface IKeyBindEvent {
	void onChange(ConfigEvent.KeyBindEvent event);
}