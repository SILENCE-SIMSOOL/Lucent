package silence.simsool.lucent.general.models.data.events.guievent;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import silence.simsool.lucent.general.utils.useful.UChat;

public class GUISlotUpdateEvent {

	public final Screen screen;
	public final ClientboundContainerSetSlotPacket packet;
	public final AbstractContainerMenu menu;
	public final String title;

	public GUISlotUpdateEvent(Screen screen, ClientboundContainerSetSlotPacket packet, AbstractContainerMenu menu) {
		this.screen = screen;
		this.packet = packet;
		this.menu = menu;
		this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
	}

}
