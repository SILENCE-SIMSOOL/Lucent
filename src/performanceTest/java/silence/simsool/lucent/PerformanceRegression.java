package silence.simsool.lucent;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import silence.simsool.lucent.ui.font.LucentFont;

public class PerformanceRegression {
	public static void main(String[] args) throws Exception {
		testFileFont();
		System.out.println("PASS: file-backed font bytes, independent buffers, stream compatibility");
		if (System.getProperty("skija.library.path") != null) HudCacheRegression.run();
		else System.out.println("SKIP: native HUD pixels (set -PskijaNativePath to enable)");
	}

	private static void testFileFont() throws Exception {
		byte[] data = {1, 3, 5, 7, 9};
		Path path = Files.createTempFile("lucent-font-test-", ".bin");
		try {
			Files.write(path, data);
			LucentFont fileFont = new LucentFont("test", path);
			check(Arrays.equals(data, fileFont.getBytes()), "File bytes changed");
			ByteBuffer first = fileFont.buffer();
			first.get();
			ByteBuffer second = fileFont.buffer();
			check(second.position() == 0 && second.remaining() == data.length, "Buffer positions are shared");
			check(second.get() == data[0], "Direct buffer bytes changed");
			Files.delete(path);
			check(fileFont.buffer().remaining() == data.length, "Direct buffer was not reused");
			LucentFont streamFont = new LucentFont("test", new ByteArrayInputStream(data));
			check(Arrays.equals(streamFont.getBytes(), data), "Stream constructor regressed");
			check(fileFont.equals(streamFont), "Font identity changed");
		} finally {
			Files.deleteIfExists(path);
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
