package silence.simsool.lucent.general.enums;

public enum OSType {

	WINDOWS,
	MAC,
	LINUX,
	UNKNOWN;

	public static OSType getPlatform() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) return WINDOWS;
		if (os.contains("mac")) return MAC;
		if (os.contains("linux") || os.contains("unix")) return LINUX;
		return UNKNOWN;
	}

}