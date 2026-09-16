package silence.simsool.lucent.skija.natives;

import java.util.ArrayList;
import java.util.List;

import silence.simsool.lucent.general.enums.OSType;
import silence.simsool.lucent.general.utils.OSUtils;

public class NativePlatform {

	private static String cachedPlatform = null;

	public static String getCurrent() {
		if (cachedPlatform == null) {
			cachedPlatform = detect();
		}
		return cachedPlatform;
	}

	public static String osOf(String classifier) {
		int idx = classifier.indexOf('-');
		return idx != -1 ? classifier.substring(0, idx) : classifier;
	}

	public static String archOf(String classifier) {
		int idx = classifier.indexOf('-');
		return idx != -1 ? classifier.substring(idx + 1) : "";
	}

	public static List<String> neededFiles(String os) {
		List<String> list = new ArrayList<>();
		list.add(libFileName(os));
		if ("windows".equals(os)) {
			list.add("icudtl.dat");
		}
		return list;
	}

	private static String libFileName(String os) {
		return switch (os) {
			case "windows" -> "skija.dll";
			case "linux" -> "libskija.so";
			case "macos" -> "libskija.dylib";
			default -> throw new IllegalStateException("Unknown OS: " + os);
		};
	}

	private static String detect() {
		OSType type = OSUtils.getOS();
		String os;
		if      (type == OSType.WINDOWS) os = "windows";
		else if (type == OSType.MAC)     os = "macos";
		else if (type == OSType.LINUX)   os = "linux";
		else return null;

		String osArch = System.getProperty("os.arch", "").toLowerCase();
		String arch;
		if      (osArch.contains("aarch64") || osArch.contains("arm64"))                        arch = "arm64";
		else if (osArch.contains("amd64") || osArch.contains("x86_64") || osArch.equals("x64")) arch = "x64";
		else return null;

		return os + "-" + arch;
	}

}