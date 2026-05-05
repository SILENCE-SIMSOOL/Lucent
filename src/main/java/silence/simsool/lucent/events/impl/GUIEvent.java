package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;

public final class GUIEvent {

	public static class RenderHUD {
		public final GuiGraphics graphics;

		public RenderHUD(GuiGraphics graphics) {
			this.graphics = graphics;
		}

		public static final Event<Handler> EVENT = createArrayBacked(
			Handler.class, listeners -> event -> {
				for (Handler l : listeners) l.onRenderHUD(event);
			}
		);

		@FunctionalInterface
		public interface Handler {
			void onRenderHUD(RenderHUD event);
		}
	}

	public static class Open {
		public final Screen screen;

		public Open(Screen screen) {
			this.screen = screen;
		}

		public static final Event<Handler> EVENT = createArrayBacked(
			Handler.class, listeners -> event -> {
				for (Handler l : listeners) l.onOpen(event);
			}
		);

		@FunctionalInterface
		public interface Handler {
			void onOpen(Open event);
		}
	}

	public static class Close {
		public final Screen screen;
		public final AbstractContainerMenu handler;
		private boolean canceled = false;

		public Close(Screen screen, AbstractContainerMenu handler) {
			this.screen = screen;
			this.handler = handler;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public static final Event<Handler> EVENT = createArrayBacked(
			Handler.class, listeners -> event -> {
				for (Handler l : listeners) {
					l.onClose(event);
					if (event.isCanceled()) break;
				}
			}
		);

		@FunctionalInterface
		public interface Handler {
			void onClose(Close event);
		}
	}

	public static class Click {
		public final double mouseX;
		public final double mouseY;
		public final int button;
		public final boolean state;
		public final Screen screen;
		private boolean canceled = false;

		public Click(double mouseX, double mouseY, int button, boolean state, Screen screen) {
			this.mouseX = mouseX;
			this.mouseY = mouseY;
			this.button = button;
			this.state = state;
			this.screen = screen;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public static final Event<Handler> EVENT = createArrayBacked(
			Handler.class, listeners -> event -> {
				for (Handler l : listeners) {
					l.onClick(event);
					if (event.isCanceled()) break;
				}
			}
		);

		@FunctionalInterface
		public interface Handler {
			void onClick(Click  event);
		}
	}

	public static class Key {
		public final String keyName;
		public final int key;
		public final char character;
		public final int scanCode;
		public final Screen screen;
		private boolean canceled = false;

		public Key(String keyName, int key, char character, int scanCode, Screen screen) {
			this.keyName = keyName;
			this.key = key;
			this.character = character;
			this.scanCode = scanCode;
			this.screen = screen;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}

		public static final Event<Handler> EVENT = createArrayBacked(
			Handler.class, listeners -> event -> {
				for (Handler l : listeners) {
					l.onKey(event);
					if (event.isCanceled()) break;
				}
			}
		);

		@FunctionalInterface
		public interface Handler {
			void onKey(Key event);
		}
	}

	public static final class Slot {

		public static class Click {
			public final Slot slot;
			public final int slotId;
			public final int button;
			public final ClickType actionType;
			public final AbstractContainerMenu handler;
			public final AbstractContainerScreen<?> screen;
			private boolean canceled = false;

			public Click(Slot slot, int slotId, int button, ClickType actionType, AbstractContainerMenu handler, AbstractContainerScreen<?> screen) {
				this.slot = slot;
				this.slotId = slotId;
				this.button = button;
				this.actionType = actionType;
				this.handler = handler;
				this.screen = screen;
			}

			public void cancel() {
				this.canceled = true;
			}

			public boolean isCanceled() {
				return canceled;
			}

			public static final Event<Handler> EVENT = createArrayBacked(
				Handler.class, listeners -> event -> {
					for (Handler l : listeners) {
						l.onSlotClick(event);
						if (event.isCanceled()) break;
					}
				}
			);

			@FunctionalInterface
			public interface Handler {
				void onSlotClick(Click event);
			}
		}

		public static class Render {
			public final GuiGraphics graphics;
			public final Slot slot;
			public final AbstractContainerScreen<AbstractContainerMenu> screen;

			public Render(GuiGraphics graphics, Slot slot, AbstractContainerScreen<AbstractContainerMenu> screen) {
				this.graphics = graphics;
				this.slot = slot;
				this.screen = screen;
			}

			public static final Event<Handler> EVENT = createArrayBacked(
				Handler.class, listeners -> event -> {
					for (Handler l : listeners) l.onSlotRender(event);
				}
			);

			@FunctionalInterface
			public interface Handler {
				void onSlotRender(Render event);
			}
		}

	}

	public static final class Container {

		public static class All {
			public final GuiGraphics graphics;
			public final Screen screen;
			public final int mouseX;
			public final int mouseY;
			public final int x;
			public final int y;
			public final int width;
			public final int height;

			public All(GuiGraphics graphics, Screen screen, int mouseX, int mouseY, int x, int y, int width, int height) {
				this.graphics = graphics;
				this.screen = screen;
				this.mouseX = mouseX;
				this.mouseY = mouseY;
				this.x = x;
				this.y = y;
				this.width = width;
				this.height = height;
			}

			public static final Event<Handler> EVENT = createArrayBacked(
				Handler.class, listeners -> event -> {
					for (Handler l : listeners) l.onContainer(event);
				}
			);

			@FunctionalInterface
			public interface Handler {
				void onContainer(All event);
			}
		}

		public static class Inventory {
			public final GuiGraphics graphics;
			public final Screen screen;
			public final int mouseX;
			public final int mouseY;
			public final int x;
			public final int y;
			public final int width;
			public final int height;

			public Inventory(GuiGraphics graphics, Screen screen, int mouseX, int mouseY, int x, int y, int width, int height) {
				this.graphics = graphics;
				this.screen = screen;
				this.mouseX = mouseX;
				this.mouseY = mouseY;
				this.x = x;
				this.y = y;
				this.width = width;
				this.height = height;
			}

			public static final Event<Handler> EVENT = createArrayBacked(
				Handler.class, listeners -> event -> {
					for (Handler l : listeners) l.onInventory(event);
				}
			);

			@FunctionalInterface
			public interface Handler {
				void onInventory(Inventory event);
			}
		}

		public static class Chest {
			public final GuiGraphics graphics;
			public final Screen screen;
			public final int mouseX;
			public final int mouseY;
			public final int x;
			public final int y;
			public final int width;
			public final int height;

			public Chest(GuiGraphics graphics, Screen screen, int mouseX, int mouseY, int x, int y, int width, int height) {
				this.graphics = graphics;
				this.screen = screen;
				this.mouseX = mouseX;
				this.mouseY = mouseY;
				this.x = x;
				this.y = y;
				this.width = width;
				this.height = height;
			}

			public static final Event<Handler> EVENT = createArrayBacked(
				Handler.class, listeners -> event -> {
					for (Handler l : listeners) l.onChest(event);
				}
			);

			@FunctionalInterface
			public interface Handler {
				void onChest(Chest event);
			}
		}

	}

}