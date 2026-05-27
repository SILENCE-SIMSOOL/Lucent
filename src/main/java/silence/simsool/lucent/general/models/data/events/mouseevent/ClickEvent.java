package silence.simsool.lucent.general.models.data.events.mouseevent;

public class ClickEvent {

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