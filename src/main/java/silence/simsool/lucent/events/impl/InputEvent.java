package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import com.mojang.blaze3d.platform.InputConstants;
import silence.simsool.lucent.general.models.interfaces.events.inputevent.IMouseInputEvent;
import silence.simsool.lucent.general.models.interfaces.events.inputevent.IKeyInputEvent;
import silence.simsool.lucent.mixin.accessors.KeyMappingAccessor;

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

	public static boolean matchesMouse(KeyMapping keyMapping, int button) {
		InputConstants.Key key = ((KeyMappingAccessor) keyMapping).getKey();
		return key.getType() == InputConstants.Type.MOUSE && key.getValue() == button;
	}

	public static class MouseInputEvent {
		public final int button;
		public final int action;
		public final boolean state;
		private boolean canceled = false;

		public MouseInputEvent(int button, int action) {
			this.button = button;
			this.action = action;
			this.state = (action == 1);
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