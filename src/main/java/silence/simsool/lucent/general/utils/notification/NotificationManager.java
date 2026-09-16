package silence.simsool.lucent.general.utils.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import silence.simsool.lucent.general.utils.useful.UChat;

public final class NotificationManager {

	private static final long DEFAULT_DURATION = 4500L;
	private static final List<Notification> notifications = new CopyOnWriteArrayList<>();

	private NotificationManager() {}

	public static Notification show(String title, String message, Notification.Type type, long durationMs) {
		Notification notification = new Notification(title, message, type, durationMs);
		notifications.add(notification);

		String prefix = switch (notification.getType()) {
			case SUCCESS -> "§a[SUCCESS]";
			case WARNING -> "§6[WARNING]";
			case ERROR -> "§c[ERROR]";
			default -> "§b[INFO]";
		};

		if (message != null && !message.isEmpty()) UChat.chat(prefix + " §f" + title + " §7- " + message);
		else UChat.chat(prefix + " §f" + title);

		return notification;
	}

	public static Notification show(String title, String message, Notification.Type type) {
		return show(title, message, type, DEFAULT_DURATION);
	}

	public static Notification info(String title, String message) {
		return show(title, message, Notification.Type.INFO);
	}

	public static Notification success(String title, String message) {
		return show(title, message, Notification.Type.SUCCESS);
	}

	public static Notification warning(String title, String message) {
		return show(title, message, Notification.Type.WARNING);
	}

	public static Notification error(String title, String message) {
		return show(title, message, Notification.Type.ERROR);
	}

	public static List<Notification> getNotifications() {
		return notifications;
	}

	public static void clearAll() {
		notifications.clear();
	}

	public static void remove(Notification notification) {
		notifications.remove(notification);
	}

	public static void update(float deltaTime) {
		if (notifications.isEmpty()) return;

		float deltaMs = deltaTime * 1000f;
		float progressDelta = deltaMs * 0.003f;

		List<Notification> toRemove = new ArrayList<>();
		for (Notification n : notifications) {
			float progress = n.getAnimProgress();
			if (n.isDismissing()) {
				progress -= progressDelta;
				if (progress <= 0f) {
					toRemove.add(n);
					continue;
				}
			}
			else {
				if (progress < 1f) progress = Math.min(1f, progress + progressDelta);
				if (progress >= 1f && n.isExpired()) n.setDismissing(true);
			}
			n.setAnimProgress(progress);
		}

		if (!toRemove.isEmpty()) {
			notifications.removeAll(toRemove);
		}
	}

}