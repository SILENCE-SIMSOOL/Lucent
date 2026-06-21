package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import silence.simsool.lucent.general.models.interfaces.events.guievent.*;
import silence.simsool.lucent.general.utils.useful.UChat;

public final class GUIEvent {

	public static final class RenderHUD {
		public static final Event<IRenderHUD> EVENT = createArrayBacked(
			IRenderHUD.class, listeners -> event -> {
				for (IRenderHUD l : listeners) l.onRenderHUD(event);
			}
		);

		public final GuiGraphicsExtractor graphics;

		public RenderHUD(GuiGraphicsExtractor graphics) {
			this.graphics = graphics;
		}
	}

	public static final class OPEN {
		public static final Event<IGUIOpenEvent> EVENT = createArrayBacked(
			IGUIOpenEvent.class, listeners -> event -> {
				for (IGUIOpenEvent l : listeners) l.onOpen(event);
			}
		);
	}

	public static final class CLOSE {
		public static final Event<IGUICloseEvent> EVENT = createArrayBacked(
			IGUICloseEvent.class, listeners -> event -> {
				for (IGUICloseEvent l : listeners) {
					l.onClose(event);
					if (event.isCanceled()) break;
				}
			}
		);
	}

	public static final class CLICK {
		public static final Event<IGUIClickEvent> EVENT = createArrayBacked(
			IGUIClickEvent.class, listeners -> event -> {
				for (IGUIClickEvent l : listeners) {
					l.onClick(event);
					if (event.isCanceled()) break;
				}
			}
		);
	}

	public static final class KEY {
		public static final Event<IGUIKeyEvent> EVENT = createArrayBacked(
			IGUIKeyEvent.class, listeners -> event -> {
				for (IGUIKeyEvent l : listeners) {
					l.onKey(event);
					if (event.isCanceled()) break;
				}
			}
		);
	}

	public static final class SLOT {

		public static final class Click {
			public static final Event<ISlotClickEvent> EVENT = createArrayBacked(
				ISlotClickEvent.class, listeners -> event -> {
					for (ISlotClickEvent l : listeners) {
						l.onSlotClick(event);
						if (event.isCanceled()) break;
					}
				}
			);
		}

		public static final class RenderPre {
			public static final Event<IRenderSlotPreEvent> EVENT = createArrayBacked(
				IRenderSlotPreEvent.class, listeners -> event -> {
					for (IRenderSlotPreEvent l : listeners) {
						l.onSlotRenderPre(event);
						if (event.isCanceled()) break;
					}
				}
			);
		}

		public static final class RenderPost {
			public static final Event<IRenderSlotPostEvent> EVENT = createArrayBacked(
				IRenderSlotPostEvent.class, listeners -> event -> {
					for (IRenderSlotPostEvent l : listeners) {
						l.onSlotRenderPost(event);
					}
				}
			);
		}

		public static final class RenderHotbarPre {
			public static final Event<IRenderHotbarPreEvent> EVENT = createArrayBacked(
				IRenderHotbarPreEvent.class, listeners -> event -> {
					for (IRenderHotbarPreEvent l : listeners) {
						l.onHotbarRenderPre(event);
						if (event.isCanceled()) break;
					}
				}
			);
		}

		public static final class RenderHotbarPost {
			public static final Event<IRenderHotbarPostEvent> EVENT = createArrayBacked(
				IRenderHotbarPostEvent.class, listeners -> event -> {
					for (IRenderHotbarPostEvent l : listeners) {
						l.onHotbarRenderPost(event);
					}
				}
			);
		}

		public static final class Update {
			public static final Event<ISlotUpdateEvent> EVENT = createArrayBacked(
				ISlotUpdateEvent.class, listeners -> event -> {
					for (ISlotUpdateEvent l : listeners) {
						l.onSlotUpdate(event);
					}
				}
			);
		}

	}

	public static final class CONTAINER {

		public static final class All {
			public static final Event<IRenderContainer> EVENT = createArrayBacked(
				IRenderContainer.class, listeners -> event -> {
					for (IRenderContainer l : listeners) l.onContainer(event);
				}
			);
		}

		public static final class Inventory {
			public static final Event<IRenderInventory> EVENT = createArrayBacked(
				IRenderInventory.class, listeners -> event -> {
					for (IRenderInventory l : listeners) l.onInventory(event);
				}
			);
		}

		public static final class Chest {
			public static final Event<IRenderChest> EVENT = createArrayBacked(
				IRenderChest.class, listeners -> event -> {
					for (IRenderChest l : listeners) l.onChest(event);
				}
			);
		}

	}

	public static final class Tooltip {
		public static final Event<ITooltipEvent> EVENT = createArrayBacked(
			ITooltipEvent.class, listeners -> event -> {
				for (ITooltipEvent l : listeners) {
					l.onRenderTooltip(event);
					if (event.isCanceled()) break;
				}
			}
		);
	}

	public static class GUIClickEvent {
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

	public static class GUICloseEvent {
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

	public static class RenderContainer {
		public final GuiGraphicsExtractor graphics;
		public final Screen screen;
		public final String title;
		public final int mouseX;
		public final int mouseY;
		public final int x;
		public final int y;
		public final int width;
		public final int height;

		public RenderContainer(GuiGraphicsExtractor graphics, Screen screen, int mouseX, int mouseY, int x, int y, int width, int height) {
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

	public static class RenderChest {
		public final GuiGraphicsExtractor graphics;
		public final Screen screen;
		public final String title;
		public final int mouseX;
		public final int mouseY;
		public final int x;
		public final int y;
		public final int width;
		public final int height;

		public RenderChest(GuiGraphicsExtractor graphics, Screen screen, int mouseX, int mouseY, int x, int y, int width, int height) {
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

	public static class RenderInventory {
		public final GuiGraphicsExtractor graphics;
		public final Screen screen;
		public final String title;
		public final int mouseX;
		public final int mouseY;
		public final int x;
		public final int y;
		public final int width;
		public final int height;

		public RenderInventory(GuiGraphicsExtractor graphics, Screen screen, int mouseX, int mouseY, int x, int y, int width, int height) {
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

	public static class GUIKeyEvent {
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

	public static class GUIOpenEvent {
		public final Screen screen;
		public final String title;

		public GUIOpenEvent(Screen screen) {
			this.screen = screen;
			this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
		}
	}

	public static class SlotClickEvent {
		public final Slot slot;
		public final int slotId;
		public final int button;
		public final ContainerInput actionType;
		public final AbstractContainerMenu handler;
		public final AbstractContainerScreen<?> screen;
		public final String title;
		private boolean canceled = false;

		public SlotClickEvent(Slot slot, int slotId, int button, ContainerInput actionType, AbstractContainerMenu handler, AbstractContainerScreen<?> screen) {
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

	public static class RenderSlotPreEvent {
		public final Slot slot;
		public final GuiGraphicsExtractor graphics;
		public final AbstractContainerScreen<?> screen;
		public final String title;
		private boolean canceled = false;

		public RenderSlotPreEvent(Slot slot, GuiGraphicsExtractor graphics, AbstractContainerScreen<?> screen) {
			this.slot = slot;
			this.graphics = graphics;
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

	public static class RenderSlotPostEvent {
		public final Slot slot;
		public final GuiGraphicsExtractor graphics;
		public final AbstractContainerScreen<?> screen;
		public final String title;

		public RenderSlotPostEvent(Slot slot, GuiGraphicsExtractor graphics, AbstractContainerScreen<?> screen) {
			this.slot = slot;
			this.graphics = graphics;
			this.screen = screen;
			this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
		}
	}

	public static class RenderHotbarPreEvent {
		public final ItemStack itemStack;
		public final int x;
		public final int y;
		public final GuiGraphicsExtractor graphics;
		private boolean canceled = false;

		public RenderHotbarPreEvent(ItemStack itemStack, int x, int y, GuiGraphicsExtractor graphics) {
			this.itemStack = itemStack;
			this.x = x;
			this.y = y;
			this.graphics = graphics;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class RenderHotbarPostEvent {
		public final ItemStack itemStack;
		public final int x;
		public final int y;
		public final GuiGraphicsExtractor graphics;

		public RenderHotbarPostEvent(ItemStack itemStack, int x, int y, GuiGraphicsExtractor graphics) {
			this.itemStack = itemStack;
			this.x = x;
			this.y = y;
			this.graphics = graphics;
		}
	}

	public static class SlotUpdateEvent {
		public final Screen screen;
		public final ClientboundContainerSetSlotPacket packet;
		public final AbstractContainerMenu menu;
		public final String title;

		public SlotUpdateEvent(Screen screen, ClientboundContainerSetSlotPacket packet, AbstractContainerMenu menu) {
			this.screen = screen;
			this.packet = packet;
			this.menu = menu;
			this.title = (screen != null && screen.getTitle() != null) ? UChat.getString(screen.getTitle()) : "";
		}
	}

	public static class TooltipEvent {
		public final ItemStack itemStack;
		public final List<Component> toolTip;
		public final TooltipContext context;
		public final TooltipFlag flags;
		public final Player player;
		private boolean canceled = false;

		public TooltipEvent(ItemStack itemStack, List<Component> toolTip, TooltipContext context, TooltipFlag flags, @Nullable Player player) {
			this.itemStack = itemStack;
			this.toolTip = toolTip;
			this.context = context;
			this.flags = flags;
			this.player = player;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

}