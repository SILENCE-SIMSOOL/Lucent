package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.input.KeyEvent;
import silence.simsool.lucent.general.models.interfaces.events.inputevent.IMouseInputEvent;
import silence.simsool.lucent.general.models.interfaces.events.inputevent.IKeyInputEvent;

public final class InputEvent {

	public static final Event<IMouseInputEvent> MOUSE = createArrayBacked(
		IMouseInputEvent.class, listeners -> event -> {
			for (IMouseInputEvent listener : listeners) {
				listener.onMouseInput(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IKeyInputEvent> KEY = createArrayBacked(
		IKeyInputEvent.class, listeners -> event -> {
			for (IKeyInputEvent listener : listeners) {
				listener.onKeyInput(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static class MouseInputEvent {
		private final int button;
		private final int action;
		private boolean canceled = false;

		public MouseInputEvent(int button, int action) {
			this.button = button;
			this.action = action;
		}

		public int getButton() {
			return button;
		}

		public int getAction() {
			return action;
		}

		public boolean isPressed() {
			return action == 1;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class KeyInputEvent {
		public final KeyEvent keyEvent;
		public final boolean state;
		private boolean canceled = false;

		public KeyInputEvent(KeyEvent keyEvent, boolean state) {
			this.keyEvent = keyEvent;
			this.state = state;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

}