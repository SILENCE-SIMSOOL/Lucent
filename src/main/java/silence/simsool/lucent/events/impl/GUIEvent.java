package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.BossEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public final class GUIEvent {

	/**
	 * Event fired when a GUI screen is being rendered.
	 */
	public static final Event<Render> RENDER = createArrayBacked(
		Render.class, listeners -> (screen, graphics, mouseX, mouseY) -> {
			for (Render listener : listeners) {
				if (listener.onGuiRender(screen, graphics, mouseX, mouseY)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a slot is clicked within a screen.
	 */
	public static final Event<SlotClick> SLOT_CLICK = createArrayBacked(
		SlotClick.class, listeners -> (screen, slotId, button) -> {
			for (SlotClick listener : listeners) {
				if (listener.onSlotClick(screen, slotId, button)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a specific slot in a container is updated.
	 */
	public static final Event<SlotUpdate> SLOT_UPDATE = createArrayBacked(
		SlotUpdate.class, listeners -> (screen, packet, menu) -> {
			for (SlotUpdate listener : listeners) listener.onSlotUpdate(screen, packet, menu);
		}
	);

	/**
	 * Event fired when an individual slot is being rendered.
	 */
	public static final Event<RenderSlot> RENDER_SLOT = createArrayBacked(
		RenderSlot.class, listeners -> (screen, graphics, slot) -> {
			for (RenderSlot listener : listeners) {
				if (listener.onRenderSlot(screen, graphics, slot)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a tooltip is being drawn on a screen.
	 */
	public static final Event<DrawTooltip> DRAW_TOOLTIP = createArrayBacked(
		DrawTooltip.class, listeners -> (screen, graphics, mouseX, mouseY) -> {
			for (DrawTooltip listener : listeners) {
				if (listener.onDrawTooltip(screen, graphics, mouseX, mouseY)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a boss bar is being rendered.
	 */
	public static final Event<BossBarRender> BOSS_BAR_RENDER = createArrayBacked(
		BossBarRender.class, listeners -> bossBar -> {
			for (BossBarRender listener : listeners) {
				if (listener.onBossBarRender(bossBar)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface
	public interface Render {
		/**
		 * Called during GUI rendering.
		 *
		 * @param screen   The active screen
		 * @param graphics The graphics context
		 * @param mouseX   Current X position of the mouse
		 * @param mouseY   Current Y position of the mouse
		 * @return true to cancel rendering; false otherwise
		 */
		boolean onGuiRender(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);
	}

	@FunctionalInterface
	public interface SlotClick {
		/**
		 * Called when a container slot is clicked.
		 *
		 * @param screen  The active screen
		 * @param slotId  The ID of the clicked slot
		 * @param button  The mouse button used
		 * @return true to cancel the slot click; false otherwise
		 */
		boolean onSlotClick(Screen screen, int slotId, int button);
	}

	@FunctionalInterface
	public interface SlotUpdate {
		/**
		 * Called when a slot's content is updated via a packet.
		 *
		 * @param screen The active screen
		 * @param packet The packet containing slot update data
		 * @param menu   The current container menu
		 */
		void onSlotUpdate(Screen screen, ClientboundContainerSetSlotPacket packet, AbstractContainerMenu menu);
	}

	@FunctionalInterface
	public interface RenderSlot {
		/**
		 * Called when a slot is being rendered.
		 *
		 * @param screen   The active screen
		 * @param graphics The graphics context
		 * @param slot     The slot being rendered
		 * @return true to cancel slot rendering; false otherwise
		 */
		boolean onRenderSlot(Screen screen, GuiGraphics graphics, Slot slot);
	}

	@FunctionalInterface
	public interface DrawTooltip {
		/**
		 * Called when drawing a tooltip.
		 *
		 * @param screen   The active screen
		 * @param graphics The graphics context
		 * @param mouseX   Current X position of the mouse
		 * @param mouseY   Current Y position of the mouse
		 * @return true to cancel tooltip drawing; false otherwise
		 */
		boolean onDrawTooltip(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);
	}

	@FunctionalInterface
	public interface BossBarRender {
		/**
		 * Called when a boss bar is rendered.
		 *
		 * @param bossBar The boss event data
		 * @return true to cancel boss bar rendering; false otherwise
		 */
		boolean onBossBarRender(BossEvent bossBar);
	}

}