package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUIClickEvent {

	public final double mouseX;
	public final double mouseY;
	public final int button;
	public final boolean state;
	public final Screen screen;
	public final String title;
	private boolean canceled = false;

	public GUIClickEvent(double mouseX, double mouseY, int button, boolean state, Screen screen) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.button = button;
		this.state = state;
		this.screen = screen;
		this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}