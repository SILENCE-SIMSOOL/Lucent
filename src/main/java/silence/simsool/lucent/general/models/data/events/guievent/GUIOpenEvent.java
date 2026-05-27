package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUIOpenEvent {

	public final Screen screen;
	public final String title;

	public GUIOpenEvent(Screen screen) {
		this.screen = screen;
		this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
	}

}