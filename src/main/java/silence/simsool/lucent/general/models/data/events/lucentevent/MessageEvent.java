package silence.simsool.lucent.general.models.data.events.lucentevent;

public class MessageEvent {

	public String message;
	public String chat;
	private boolean canceled = false;

	public MessageEvent(String message, String chat) {
		this.message = message;
		this.chat = chat;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}