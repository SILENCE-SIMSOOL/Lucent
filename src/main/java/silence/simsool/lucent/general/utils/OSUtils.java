package silence.simsool.lucent.general.utils;

import java.io.File;

import silence.simsool.lucent.general.enums.OSType;

public class OSUtils {

	public static OSType getOS() {
		return OSType.getPlatform();
	}

	public static File getAppDir(String appName) {
		String home = System.getProperty("user.home");
		File baseDir;

		switch (getOS()) {
			case WINDOWS:
				String appData = System.getenv("APPDATA");
				if (appData != null) baseDir = new File(appData, appName);
				else baseDir = new File(home, "AppData/Roaming/" + appName);
				break;
			case MAC:
				baseDir = new File(home, "Library/Application Support/" + appName);
				break;
			default:
				baseDir = new File(home, "." + appName.toLowerCase());
				break;
		}

		if (!baseDir.exists()) baseDir.mkdirs();
		return baseDir;
	}

	public static File getLucentDir() {
		return getAppDir("Lucent");
	}

}