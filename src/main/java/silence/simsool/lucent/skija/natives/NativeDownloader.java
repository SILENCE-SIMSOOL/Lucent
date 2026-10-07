package silence.simsool.lucent.skija.natives;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import silence.simsool.lucent.Lucent;

public class NativeDownloader {

	private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2";

	public static void fetch(String classifier, String version, List<String> files, Path targetDir) throws Exception {
		String os = NativePlatform.osOf(classifier);
		String arch = NativePlatform.archOf(classifier);
		String url = MAVEN_CENTRAL + "/io/github/humbleui/skija-" + classifier + "/" + version + "/skija-" + classifier + "-" + version + ".jar";
		String resPrefix = "io/github/humbleui/skija/" + os + "/" + arch + "/";

		Path tmpJar = Files.createTempFile("skija-" + classifier + "-", ".jar");
		try {
			download(url, tmpJar);
			try (ZipFile zip = new ZipFile(tmpJar.toFile())) {
				for (String file : files) {
					ZipEntry entry = zip.getEntry(resPrefix + file);
					if (entry == null) {
						throw new IllegalStateException(file + " not found in " + url);
					}
					Path tmp = Files.createTempFile(targetDir, file, ".tmp");
					try (InputStream in = zip.getInputStream(entry)) {
						Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
					}
					Files.move(tmp, targetDir.resolve(file), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		} finally {
			Files.deleteIfExists(tmpJar);
		}
	}

	private static void download(String url, Path target) throws Exception {
		Lucent.LOG.info("Downloading Skija native from: " + url);
		HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(15))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.timeout(Duration.ofMinutes(5))
				.GET()
				.build();
		HttpResponse<Path> response = client.send(
				request,
				HttpResponse.BodyHandlers.ofFile(target, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)
		);
		if (response.statusCode() != 200) {
			throw new IllegalStateException("HTTP " + response.statusCode() + " when downloading " + url);
		}
	}

}