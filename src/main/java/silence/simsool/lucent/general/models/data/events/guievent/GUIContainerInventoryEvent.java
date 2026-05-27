package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUIContainerInventoryEvent {

	public final GuiGraphics graphics;
	public final Screen screen;
	public final String title;
	public final int mouseX;
	public final int mouseY;
	public final int x;
	public final int y;
	public final int width;
	public final int height;

	public GUIContainerInventoryEvent(GuiGraphics graphics, Screen screen, int mouseX, int mouseY, int x, int y, int width, int height) {
		this.graphics = graphics;
		this.screen = screen;
		this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

}