package silence.simsool.lucent.general.models.data.events.lucentevent;

public class MessageSentEvent {

	public String message;
	private boolean canceled = false;

	public MessageSentEvent(String message) {
		this.message = message;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}