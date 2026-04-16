package silence.simsool.lucent.events.impl;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.BossEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public final class GUIEvent {

	public static final Event<Render> RENDER = EventFactory.createArrayBacked(Render.class,
		listeners -> (screen, graphics, mouseX, mouseY) -> {
			for (Render listener : listeners) {
				if (listener.onGuiRender(screen, graphics, mouseX, mouseY)) return true;
			}
			return false;
		}
	);

	public static final Event<SlotClick> SLOT_CLICK = EventFactory.createArrayBacked(SlotClick.class,
		listeners -> (screen, slotId, button) -> {
			for (SlotClick listener : listeners) {
				if (listener.onSlotClick(screen, slotId, button)) return true;
			}
			return false;
		}
	);

	public static final Event<SlotUpdate> SLOT_UPDATE = EventFactory.createArrayBacked(SlotUpdate.class,
		listeners -> (screen, packet, menu) -> {
			for (SlotUpdate listener : listeners) listener.onSlotUpdate(screen, packet, menu);
		}
	);

	public static final Event<RenderSlot> RENDER_SLOT = EventFactory.createArrayBacked(RenderSlot.class,
		listeners -> (screen, graphics, slot) -> {
			for (RenderSlot listener : listeners) {
				if (listener.onRenderSlot(screen, graphics, slot)) return true;
			}
			return false;
		}
	);

	public static final Event<DrawTooltip> DRAW_TOOLTIP = EventFactory.createArrayBacked(DrawTooltip.class,
		listeners -> (screen, graphics, mouseX, mouseY) -> {
			for (DrawTooltip listener : listeners) {
				if (listener.onDrawTooltip(screen, graphics, mouseX, mouseY)) return true;
			}
			return false;
		}
	);

	public static final Event<BossBarRender> BOSS_BAR_RENDER = EventFactory.createArrayBacked(BossBarRender.class,
		listeners -> bossBar -> {
			for (BossBarRender listener : listeners) {
				if (listener.onBossBarRender(bossBar)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface public interface Render {
		/** @return true면 렌더 취소 */
		boolean onGuiRender(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);
	}

	@FunctionalInterface public interface SlotClick {
		/** @return true면 슬롯 클릭 취소 */
		boolean onSlotClick(Screen screen, int slotId, int button);
	}

	@FunctionalInterface public interface SlotUpdate {
		void onSlotUpdate(Screen screen, ClientboundContainerSetSlotPacket packet, AbstractContainerMenu menu);
	}

	@FunctionalInterface public interface RenderSlot {
		/** @return true면 슬롯 렌더 취소 */
		boolean onRenderSlot(Screen screen, GuiGraphics graphics, Slot slot);
	}

	@FunctionalInterface public interface DrawTooltip {
		/** @return true면 툴팁 렌더 취소 */
		boolean onDrawTooltip(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);
	}

	@FunctionalInterface public interface BossBarRender {
		/** @return true면 보스바 렌더 취소 */
		boolean onBossBarRender(BossEvent bossBar);
	}

}