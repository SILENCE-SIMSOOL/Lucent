package silence.simsool.lucent.skija.natives;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import silence.simsool.lucent.Lucent;

public class SkijaNatives {

	private static volatile boolean ready = false;
	private static final AtomicBoolean started = new AtomicBoolean(false);
	private static String version = null;

	public static boolean isReady() {
		return ready;
	}

	public static String getVersion() {
		if (version == null) {
			try (InputStream in = SkijaNatives.class.getResourceAsStream("/lucent/skija.version")) {
				if (in != null) {
					version = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
				}
			} catch (Exception ignored) {}
			if (version == null || version.isEmpty() || version.startsWith("$")) {
				version = "0.143.17";
			}
		}
		return version;
	}

	public static void ensure() {
		if (started.getAndSet(true)) return;

		Thread thread = new Thread(() -> {
			try {
				provision();
			} catch (Throwable t) {
				Lucent.LOG.error("Skija native provisioning failed: " + t.getMessage());
			}
		}, "Lucent-Skija-Natives");
		thread.setDaemon(true);
		thread.start();
	}

	private static void provision() {
		String classifier = NativePlatform.getCurrent();
		if (classifier == null) {
			Lucent.LOG.warn("Unsupported OS/arch for Skija (" + System.getProperty("os.name") + " / " + System.getProperty("os.arch") + "); Skija rendering disabled.");
			return;
		}

		String ver = getVersion();
		if (!ensurePlatform(classifier, ver)) {
			Lucent.LOG.warn("Skija native for " + classifier + " unavailable; Skija rendering disabled until an online launch.");
			return;
		}

		Path platformDir = NativeCache.getPlatformDir(ver, classifier);
		System.setProperty("skija.library.path", platformDir.toAbsolutePath().toString());
		ready = true;
		Lucent.LOG.info("Skija native ready: " + classifier + " (" + ver + ")");

		NativeCache.prune(ver);
	}

	private static boolean ensurePlatform(String classifier, String ver) {
		Path platformDir = NativeCache.getPlatformDir(ver, classifier);
		String os = NativePlatform.osOf(classifier);
		List<String> needed = NativePlatform.neededFiles(os);

		boolean present = true;
		for (String file : needed) {
			Path p = platformDir.resolve(file);
			try {
				if (!Files.isRegularFile(p) || Files.size(p) == 0) {
					present = false;
					break;
				}
			} catch (Exception e) {
				present = false;
				break;
			}
		}

		if (present) {
			return true;
		}

		try {
			Files.createDirectories(platformDir);
			NativeDownloader.fetch(classifier, ver, needed, platformDir);
			return true;
		} catch (Throwable t) {
			Lucent.LOG.warn("Could not download Skija native for " + classifier + ": " + t.getMessage());
			return false;
		}
	}
}
