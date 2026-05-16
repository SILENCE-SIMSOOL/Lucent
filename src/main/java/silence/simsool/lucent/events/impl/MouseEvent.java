package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;

public final class MouseEvent {

	public static class ClickEvent {
		private final int button;
		private final int action;
		private boolean canceled = false;

		public ClickEvent(int button, int action) {
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

	public static final Event<Click> CLICK = createArrayBacked(
		Click.class, listeners -> event -> {
			for (Click listener : listeners) {
				listener.onMouseClick(event);
				if (event.isCanceled()) break;
			}
		}
	);

	@FunctionalInterface
	public interface Click {
		void onMouseClick(ClickEvent event);
	}

}