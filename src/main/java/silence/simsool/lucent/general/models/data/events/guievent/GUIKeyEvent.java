package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUIKeyEvent {

	public final String keyName;
	public final int key;
	public final char character;
	public final int scanCode;
	public final Screen screen;
	public final String title;
	private boolean canceled = false;

	public GUIKeyEvent(String keyName, int key, char character, int scanCode, Screen screen) {
		this.keyName = keyName;
		this.key = key;
		this.character = character;
		this.scanCode = scanCode;
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