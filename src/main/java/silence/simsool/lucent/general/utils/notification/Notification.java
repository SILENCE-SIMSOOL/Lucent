package silence.simsool.lucent.general.utils.notification;

public class Notification {

	public enum Type {
		INFO(0x1A2A6C, "\uE88F"),
		SUCCESS(0x1A4D3E, "\uE86C"),
		WARNING(0x6C521A, "\uE002"),
		ERROR(0x6C1A24, "\uE000");

		private final int color;
		private final String icon;

		Type(int color, String icon) {
			this.color = color;
			this.icon = icon;
		}

		public int getColor() {
			return color;
		}

		public String getIcon() {
			return icon;
		}
	}

	private String title;
	private String message;
	private Type type;
	private String icon;
	private final long duration;
	private final long createdAt;
	private float animProgress = 0f;
	private boolean dismissing = false;
	private float currentY = -1f;

	public Notification(String title, String message, Type type, long duration) {
		this(title, message, type, type != null ? type.getIcon() : "\uE88F", duration);
	}

	public Notification(String title, String message, Type type, String icon, long duration) {
		this.title = title;
		this.message = message;
		this.type = type != null ? type : Type.INFO;
		this.icon = icon != null ? icon : (this.type != null ? this.type.getIcon() : "\uE88F");
		this.duration = duration;
		this.createdAt = System.currentTimeMillis();
	}

	public String getIcon() {
		return icon != null ? icon : (type != null ? type.getIcon() : "\uE88F");
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	public long getDuration() {
		return duration;
	}

	public long getCreatedAt() {
		return createdAt;
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
		if (duration <= 0) return false;
		return System.currentTimeMillis() - createdAt >= duration;
	}

	public float getProgressRatio() {
		if (duration <= 0) return 1.0f;
		long elapsed = System.currentTimeMillis() - createdAt;
		return Math.max(0.0f, Math.min(1.0f, 1.0f - ((float) elapsed / (float) duration)));
	}

	public float getCurrentY() {
		return currentY;
	}

	public void setCurrentY(float y) {
		this.currentY = y;
	}

	public void update(String title, String message, Type type) {
		this.title = title;
		this.message = message;
		if (type != null) this.type = type;
	}

}