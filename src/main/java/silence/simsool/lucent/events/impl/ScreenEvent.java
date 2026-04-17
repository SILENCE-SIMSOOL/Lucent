package silence.simsool.lucent.events.impl;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public final class ScreenEvent {

	// Screen Render Event
	public static final Event<Render> RENDER = EventFactory.createArrayBacked(Render.class,
		listeners -> (screen, graphics, mouseX, mouseY) -> {
			for (Render listener : listeners) {
				if (listener.onScreenRender(screen, graphics, mouseX, mouseY)) return true;
			}
			return false;
		}
	);

	// Screen Open Event
	public static final Event<Open> OPEN = EventFactory.createArrayBacked(Open.class,
		listeners -> screen -> {
			for (Open listener : listeners) listener.onScreenOpen(screen);
		}
	);

	// Screen Close Event
	public static final Event<Close> CLOSE = EventFactory.createArrayBacked(Close.class,
		listeners -> screen -> {
			for (Close listener : listeners) listener.onScreenClose(screen);
		}
	);

	// Mouse Click Event
	public static final Event<MouseClick> MOUSE_CLICK = EventFactory.createArrayBacked(MouseClick.class,
		listeners -> (screen, mouseX, mouseY, button) -> {
			for (MouseClick listener : listeners) {
				if (listener.onMouseClick(screen, mouseX, mouseY, button)) return true;
			}
			return false;
		}
	);

	// Mouse Release Event
	public static final Event<MouseRelease> MOUSE_RELEASE = EventFactory.createArrayBacked(MouseRelease.class,
		listeners -> (screen, mouseX, mouseY, button) -> {
			for (MouseRelease listener : listeners) {
				if (listener.onMouseRelease(screen, mouseX, mouseY, button)) return true;
			}
			return false;
		}
	);

	// Key Press Event
	public static final Event<KeyPress> KEY_PRESS = EventFactory.createArrayBacked(KeyPress.class,
		listeners -> (screen, keyCode, scanCode, modifiers) -> {
			for (KeyPress listener : listeners) {
				if (listener.onKeyPress(screen, keyCode, scanCode, modifiers)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface
	public interface Render {
		/** @return true면 스크린 렌더링 취소 */
		boolean onScreenRender(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);
	}

	@FunctionalInterface
	public interface Open {
		void onScreenOpen(Screen screen);
	}

	@FunctionalInterface
	public interface Close {
		void onScreenClose(Screen screen);
	}

	@FunctionalInterface
	public interface MouseClick {
		boolean onMouseClick(Screen screen, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface MouseRelease {
		boolean onMouseRelease(Screen screen, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface KeyPress {
		boolean onKeyPress(Screen screen, int keyCode, int scanCode, int modifiers);
	}

}