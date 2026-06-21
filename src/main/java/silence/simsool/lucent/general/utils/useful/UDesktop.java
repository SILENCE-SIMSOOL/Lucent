package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class UDesktop {
	private static boolean isLinux;
	private static boolean isXdg;
	private static boolean isKde;
	private static boolean isGnome;
	private static boolean isMac;
	private static boolean isWindows;

	static {
		String osName;
		try {
			osName = System.getProperty("os.name");
		} catch (SecurityException e) {
			osName = null;
		}

		isLinux = osName != null && (osName.startsWith("Linux") || osName.startsWith("LINUX"));
		isMac = osName != null && osName.startsWith("Mac");
		isWindows = osName != null && osName.startsWith("Windows");

		if (isLinux) {
			String xdgId = System.getenv("XDG_SESSION_ID");
			if (xdgId != null) isXdg = !xdgId.isEmpty();

			String gdmSession = System.getenv("GDMSESSION");
			if (gdmSession != null) {
				gdmSession = gdmSession.toLowerCase();
				isGnome = gdmSession.contains("gnome");
				isKde = gdmSession.contains("kde");
			}
		}

		else {
			isXdg = false;
			isKde = false;
			isGnome = false;
		}
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isXdg() {
		return isXdg;
	}

	public static boolean isKde() {
		return isKde;
	}

	public static boolean isGnome() {
		return isGnome;
	}

	public static boolean isMac() {
		return isMac;
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean browse(URI uri) {
		return browseDesktop(uri) || openSystemSpecific(uri.toString());
	}

	public static boolean open(File file) {
		return openDesktop(file) || openSystemSpecific(file.getPath());
	}

	public static boolean edit(File file) {
		return editDesktop(file) || openSystemSpecific(file.getPath());
	}

	private static boolean openSystemSpecific(String file) {
		if (isLinux) {
			List<String> commands = Arrays.asList("xdg-open", "kde-open", "gnome-open");
			for (String cmd : commands) {
				if (runCommand(new String[]{cmd, file}, true)) return true;
			}
			return false;
		}
		else if (isMac) return runCommand(new String[]{"open", file}, false);
		else if (isWindows) return runCommand(new String[]{"rundll32", "url.dll,FileProtocolHandler", file}, false);
		return false;
	}

	private static boolean browseDesktop(URI uri) {
		if (!Desktop.isDesktopSupported()) return false;
		try {
			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.BROWSE)) return false;
			desktop.browse(uri);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	private static boolean openDesktop(File file) {
		if (!Desktop.isDesktopSupported()) return false;
		try {
			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.OPEN)) return false;
			desktop.open(file);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	private static boolean editDesktop(File file) {
		if (!Desktop.isDesktopSupported()) return false;
		try {
			Desktop desktop = Desktop.getDesktop();
			if (!desktop.isSupported(Desktop.Action.EDIT)) return false;
			desktop.edit(file);
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	private static boolean runCommand(String[] command, boolean checkExitStatus) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			if (process == null) return false;
			if (checkExitStatus) {
				if (process.waitFor(3, TimeUnit.SECONDS)) return process.exitValue() == 0;
				else return true;
			} else return process.isAlive();
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	public static String getClipboardString() {
		return mc.keyboardHandler.getClipboard();
	}

	public static void setClipboardString(String str) {
		mc.keyboardHandler.setClipboard(str);
	}

	public static boolean copyToClipboard(String text) {
		try {
			StringSelection selection = new StringSelection(text);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void createSystemNotification(String title, String text, TrayIcon.MessageType type, long time) {
		SystemTray sTray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().createImage(new byte[0]);
		TrayIcon trayIcon = new TrayIcon(image, "System Tray Notification");
		trayIcon.setImageAutoSize(true);
		try {
			sTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		trayIcon.displayMessage(title, text, type);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				sTray.remove(trayIcon);
			}
		}, time);
	}

	public static String getUTC() {
		LocalDateTime utcTime = LocalDateTime.now(ZoneOffset.UTC);
		return utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
	}

}