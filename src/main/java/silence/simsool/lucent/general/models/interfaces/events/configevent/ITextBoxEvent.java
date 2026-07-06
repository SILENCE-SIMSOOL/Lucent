package silence.simsool.lucent.general.models.interfaces.events.configevent;

import silence.simsool.lucent.events.impl.ConfigEvent;

@FunctionalInterface
public interface ITextBoxEvent {
	void onChange(ConfigEvent.TextBoxEvent event);
}