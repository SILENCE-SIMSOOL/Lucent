package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUISlotClickEvent {

	public final Slot slot;
	public final int slotId;
	public final int button;
	public final ClickType actionType;
	public final AbstractContainerMenu handler;
	public final AbstractContainerScreen<?> screen;
	public final String title;
	private boolean canceled = false;

	public GUISlotClickEvent(Slot slot, int slotId, int button, ClickType actionType, AbstractContainerMenu handler, AbstractContainerScreen<?> screen) {
		this.slot = slot;
		this.slotId = slotId;
		this.button = button;
		this.actionType = actionType;
		this.handler = handler;
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