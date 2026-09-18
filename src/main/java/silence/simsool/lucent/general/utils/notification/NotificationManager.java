package silence.simsool.lucent.general.utils.notification;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.utils.useful.UDisplay;

public class NotificationManager {

	private static final long DEFAULT_DURATION = 4000L;
	private static final List<Notification> notifications = new CopyOnWriteArrayList<>();

	public static Notification show(String title, String message, Notification.Type type, long durationMs) {
		if (UDisplay.getWindow() == null) system(title, message, type);
		Notification notification = new Notification(title, message, type, durationMs);
		notifications.add(notification);
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

	public static void system(String title, String message, Notification.Type type) {
		SystemNotification.send(title, message, type != null ? type : Notification.Type.INFO);
	}

	public static void system(String title, String message) {
		system(title, message, Notification.Type.INFO);
	}

	public static Notification showWithSystem(String title, String message, Notification.Type type, long durationMs) {
		system(title, message, type);
		return show(title, message, type, durationMs);
	}

	public static void render(GuiGraphics graphics) {
		NotificationRenderer.render(graphics);
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

}