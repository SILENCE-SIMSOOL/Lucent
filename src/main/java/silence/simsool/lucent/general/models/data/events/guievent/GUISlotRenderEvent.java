package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUISlotRenderEvent {

	public final GuiGraphics graphics;
	public final Slot slot;
	public final AbstractContainerScreen<AbstractContainerMenu> screen;
	public final String title;

	public GUISlotRenderEvent(GuiGraphics graphics, Slot slot, AbstractContainerScreen<AbstractContainerMenu> screen) {
		this.graphics = graphics;
		this.slot = slot;
		this.screen = screen;
		this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
	}

}