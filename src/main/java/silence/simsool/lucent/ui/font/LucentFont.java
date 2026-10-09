package silence.simsool.lucent.ui.font;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class LucentFont {
	private final String name;
	private final byte[] cachedBytes;
	private final Path path;
	private ByteBuffer directBuffer;

	public LucentFont(String name, InputStream inputStream) throws IOException {
		this.name = name;
		this.path = null;
		try (inputStream) {
			this.cachedBytes = inputStream.readAllBytes();
		}
	}

	public LucentFont(String name, Path path) {
		this.name = name;
		this.path = path;
		this.cachedBytes = null;
	}

	public String getName() {
		return name;
	}

	public byte[] getBytes() {
		if (path == null) return cachedBytes;
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public synchronized ByteBuffer buffer() {
		if (directBuffer == null) {
			byte[] bytes = getBytes();
			directBuffer = ByteBuffer.allocateDirect(bytes.length)
				.order(ByteOrder.nativeOrder())
				.put(bytes);
			directBuffer.flip();
		}

		return directBuffer.duplicate();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof LucentFont)) return false;
		LucentFont font = (LucentFont) other;
		return Objects.equals(name, font.name);
	}
}