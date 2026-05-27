package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import silence.simsool.lucent.general.models.interfaces.events.guievent.*;

public final class GUIEvent {

	public static final class RenderHUD {
		public static final Event<IRenderHUD> EVENT = createArrayBacked(
			IRenderHUD.class, listeners -> event -> {
				for (IRenderHUD l : listeners) l.onRenderHUD(event);
			}
		);
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
			public static final Event<IGUISlotClickEvent> EVENT = createArrayBacked(
				IGUISlotClickEvent.class, listeners -> event -> {
					for (IGUISlotClickEvent l : listeners) {
						l.onSlotClick(event);
						if (event.isCanceled()) break;
					}
				}
			);
		}

		public static final class Render {
			public static final Event<IGUISlotRenderEvent> EVENT = createArrayBacked(
				IGUISlotRenderEvent.class, listeners -> event -> {
					for (IGUISlotRenderEvent l : listeners) l.onSlotRender(event);
				}
			);
		}

	}

	public static final class CONTAINER {

		public static final class All {
			public static final Event<IGUIContainerAllEvent> EVENT = createArrayBacked(
				IGUIContainerAllEvent.class, listeners -> event -> {
					for (IGUIContainerAllEvent l : listeners) l.onContainer(event);
				}
			);
		}

		public static final class Inventory {
			public static final Event<IGUIContainerInventoryEvent> EVENT = createArrayBacked(
				IGUIContainerInventoryEvent.class, listeners -> event -> {
					for (IGUIContainerInventoryEvent l : listeners) l.onInventory(event);
				}
			);
		}

		public static final class Chest {
			public static final Event<IGUIContainerChestEvent> EVENT = createArrayBacked(
				IGUIContainerChestEvent.class, listeners -> event -> {
					for (IGUIContainerChestEvent l : listeners) l.onChest(event);
				}
			);
		}

	}

}