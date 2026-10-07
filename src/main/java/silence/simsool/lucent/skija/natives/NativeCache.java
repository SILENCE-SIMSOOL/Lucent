package silence.simsool.lucent.skija.natives;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import silence.simsool.lucent.general.utils.OSUtils;

public class NativeCache {

	private static final Pattern VERSION_DIR = Pattern.compile("\\d[\\d.]*");

	public static Path getRoot() {
		return OSUtils.getLucentDir().toPath().resolve("resources").resolve("natives").resolve("skija");
	}

	public static Path getVersionDir(String version) {
		return getRoot().resolve(version);
	}

	public static Path getPlatformDir(String version, String classifier) {
		return getVersionDir(version);
	}

	public static void prune(String currentVersion) {
		try {
			Path root = getRoot();
			if (!Files.exists(root)) return;
			try (Stream<Path> stream = Files.list(root)) {
				stream.filter(path -> Files.isDirectory(path)
						&& !path.getFileName().toString().equals(currentVersion)
						&& VERSION_DIR.matcher(path.getFileName().toString()).matches()
				).forEach(path -> deleteRecursively(path.toFile()));
			}
		} catch (Exception ignored) {}
	}

	private static void deleteRecursively(File file) {
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				deleteRecursively(child);
			}
		}
		file.delete();
	}

}