package silence.simsool.lucent.general.utils.notification;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

public class SystemNotification {

	private static TrayIcon trayIcon = null;
	private static boolean initialized = false;

	private static synchronized boolean initTray() {
		if (initialized) return trayIcon != null;
		initialized = true;

		if (!SystemTray.isSupported()) return false;

		try {
			SystemTray tray = SystemTray.getSystemTray();
			BufferedImage image = createDefaultIcon();
			trayIcon = new TrayIcon(image, "Lucent");
			trayIcon.setImageAutoSize(true);
			tray.add(trayIcon);
			return true;
		} catch (AWTException | SecurityException e) {
			trayIcon = null;
			return false;
		}
	}

	private static BufferedImage createDefaultIcon() {
		int size = 16;
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Dark background circle
		g2d.setColor(new Color(0x11, 0x13, 0x18));
		g2d.fillOval(0, 0, size, size);

		// Cyan/Skyblue accent dot
		g2d.setColor(new Color(0x38, 0xBD, 0xF8));
		g2d.fillOval(4, 4, size - 8, size - 8);

		g2d.dispose();
		return image;
	}

	public static void send(String title, String message, Notification.Type type) {
		if (!initTray() || trayIcon == null) return;

		TrayIcon.MessageType msgType = switch (type) {
			case WARNING -> TrayIcon.MessageType.WARNING;
			case ERROR -> TrayIcon.MessageType.ERROR;
			default -> TrayIcon.MessageType.INFO;
		};

		String safeTitle = (title != null && !title.isEmpty()) ? title : "Lucent";
		String safeMsg = (message != null) ? message : "";

		trayIcon.displayMessage(safeTitle, safeMsg, msgType);
	}

}