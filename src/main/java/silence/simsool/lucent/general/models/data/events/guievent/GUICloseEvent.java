package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUICloseEvent {

	public final Screen screen;
	public final String title;
	public final AbstractContainerMenu handler;
	private boolean canceled = false;

	public GUICloseEvent(Screen screen, AbstractContainerMenu handler) {
		this.screen = screen;
		this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
		this.handler = handler;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}