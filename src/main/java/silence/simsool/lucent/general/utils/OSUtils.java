package silence.simsool.lucent.general.utils;

import java.io.File;

public class OSUtils {

	public static File getLucentDir() {
		String os = System.getProperty("os.name").toLowerCase();
		String home = System.getProperty("user.home");
		File baseDir;

		if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			if (appData != null) baseDir = new File(appData, "Lucent");
			else baseDir = new File(home, "AppData/Roaming/Lucent");
		} else if (os.contains("mac")) {
			baseDir = new File(home, "Library/Application Support/Lucent");
		} else {
			baseDir = new File(home, ".lucent");
		}

		if (!baseDir.exists()) baseDir.mkdirs();
		return baseDir;
	}

}
