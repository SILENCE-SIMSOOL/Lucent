package silence.simsool.lucent.general.utils.notification;

public class Notification {

	public enum Type {
		INFO(0x2196F3),
		SUCCESS(0x4CAF50),
		WARNING(0xFF9800),
		ERROR(0xF44336);

		private final int color;

		Type(int color) {
			this.color = color;
		}

		public int getColor() {
			return color;
		}
	}

	private String title;
	private String message;
	private Type type;
	private final long duration;
	private final long createdAt;
	private float animProgress = 0f;
	private boolean dismissing = false;

	public Notification(String title, String message, Type type, long duration) {
		this.title = title;
		this.message = message;
		this.type = type != null ? type : Type.INFO;
		this.duration = duration;
		this.createdAt = System.currentTimeMillis();
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public boolean hasMessage() {
		return message != null && !message.isEmpty();
	}

	public Type getType() {
		return type;
	}

	public float getAnimProgress() {
		return animProgress;
	}

	public void setAnimProgress(float progress) {
		this.animProgress = progress;
	}

	public boolean isDismissing() {
		return dismissing;
	}

	public void setDismissing(boolean dismissing) {
		this.dismissing = dismissing;
	}

	public void dismiss() {
		this.dismissing = true;
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - createdAt >= duration;
	}

	public void update(String title, String message, Type type) {
		this.title = title;
		this.message = message;
		if (type != null) this.type = type;
	}

}