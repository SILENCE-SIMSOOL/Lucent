package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

public class UFile {

	public static Resource getResource(String path) {
		return mc.getResourceManager().getResource(Identifier.parse(path)).get();
	}

	public static InputStream getResourceInputStream(String path) throws IOException {
		return getResource(path).open();
	}

	public static String getResourceAsString(String path) throws IOException {
		try (InputStream in = getResourceInputStream(path)) {
			return new String(readAllBytes(in), StandardCharsets.UTF_8);
		}
	}

	public static byte[] readAllBytes(InputStream in) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] temp = new byte[8192];
		int read;
		while ((read = in.read(temp)) != -1) {
			buffer.write(temp, 0, read);
		}
		return buffer.toByteArray();
	}

	public static String readString(File file) throws IOException {
		if (!file.exists()) return null;
		return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	}

	public static void writeString(File file, String content) throws IOException {
		if (file.getParentFile() != null) file.getParentFile().mkdirs();
		Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	public static byte[] fetchUrl(String urlString) throws IOException {
		URL url = URI.create(urlString).toURL();
		try (InputStream in = url.openStream()) {
			return readAllBytes(in);
		}
	}

	public static String fetchUrlAsString(String urlString) throws IOException {
		return new String(fetchUrl(urlString), StandardCharsets.UTF_8);
	}

	public static boolean deleteRecursive(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteRecursive(child);
				}
			}
		}
		return dir.delete();
	}

	public static String getExtension(File file) {
		String name = file.getName();
		int lastIndex = name.lastIndexOf('.');
		if (lastIndex == -1) return "";
		return name.substring(lastIndex + 1);
	}

}