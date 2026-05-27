package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import silence.simsool.lucent.general.models.interfaces.events.mouseevent.IClickEvent;

public final class MouseEvent {

	public static final Event<IClickEvent> CLICK = createArrayBacked(
		IClickEvent.class, listeners -> event -> {
			for (IClickEvent listener : listeners) {
				listener.onMouseClick(event);
				if (event.isCanceled()) break;
			}
		}
	);

}