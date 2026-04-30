package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public final class ScreenEvent {

	/**
	 * Event fired when a screen is being rendered.
	 */
	public static final Event<Render> RENDER = createArrayBacked(
		Render.class, listeners -> (screen, graphics, mouseX, mouseY) -> {
			for (Render listener : listeners) {
				if (listener.onScreenRender(screen, graphics, mouseX, mouseY)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a screen is opened.
	 */
	public static final Event<Open> OPEN = createArrayBacked(
		Open.class, listeners -> screen -> {
			for (Open listener : listeners) listener.onScreenOpen(screen);
		}
	);

	/**
	 * Event fired when a screen is closed.
	 */
	public static final Event<Close> CLOSE = createArrayBacked(
		Close.class, listeners -> screen -> {
			for (Close listener : listeners) listener.onScreenClose(screen);
		}
	);

	/**
	 * Event fired when a mouse button is clicked within a screen.
	 */
	public static final Event<MouseClick> MOUSE_CLICK = createArrayBacked(
		MouseClick.class, listeners -> (screen, mouseX, mouseY, button) -> {
			for (MouseClick listener : listeners) {
				if (listener.onMouseClick(screen, mouseX, mouseY, button)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a mouse button is released within a screen.
	 */
	public static final Event<MouseRelease> MOUSE_RELEASE = createArrayBacked(
		MouseRelease.class, listeners -> (screen, mouseX, mouseY, button) -> {
			for (MouseRelease listener : listeners) {
				if (listener.onMouseRelease(screen, mouseX, mouseY, button)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a key is pressed while a screen is active.
	 */
	public static final Event<KeyPress> KEY_PRESS = createArrayBacked(
		KeyPress.class, listeners -> (screen, keyCode, scanCode, modifiers) -> {
			for (KeyPress listener : listeners) {
				if (listener.onKeyPress(screen, keyCode, scanCode, modifiers)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface
	public interface Render {
		/**
		 * Called during screen rendering.
		 *
		 * @param screen   The screen being rendered
		 * @param graphics The graphics context
		 * @param mouseX   Current X position of the mouse
		 * @param mouseY   Current Y position of the mouse
		 * @return true to cancel screen rendering; false otherwise
		 */
		boolean onScreenRender(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);
	}

	@FunctionalInterface
	public interface Open {
		/**
		 * Called when a screen is opened.
		 *
		 * @param screen The screen being opened
		 */
		void onScreenOpen(Screen screen);
	}

	@FunctionalInterface
	public interface Close {
		/**
		 * Called when a screen is closed.
		 *
		 * @param screen The screen being closed
		 */
		void onScreenClose(Screen screen);
	}

	@FunctionalInterface
	public interface MouseClick {
		/**
		 * Called when a mouse button is pressed.
		 *
		 * @param screen The active screen
		 * @param mouseX X position of the click
		 * @param mouseY Y position of the click
		 * @param button The mouse button index
		 * @return true to consume the click; false otherwise
		 */
		boolean onMouseClick(Screen screen, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface MouseRelease {
		/**
		 * Called when a mouse button is released.
		 *
		 * @param screen The active screen
		 * @param mouseX X position of the release
		 * @param mouseY Y position of the release
		 * @param button The mouse button index
		 * @return true to consume the release; false otherwise
		 */
		boolean onMouseRelease(Screen screen, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface KeyPress {
		/**
		 * Called when a key is pressed.
		 *
		 * @param screen    The active screen
		 * @param keyCode   The code of the key pressed
		 * @param scanCode  The scan code of the key
		 * @param modifiers Bit field for modifier keys (Shift, Ctrl, etc.)
		 * @return true to consume the input; false otherwise
		 */
		boolean onKeyPress(Screen screen, int keyCode, int scanCode, int modifiers);
	}

}