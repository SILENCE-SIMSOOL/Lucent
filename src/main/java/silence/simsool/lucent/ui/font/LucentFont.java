package silence.simsool.lucent.ui.font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class LucentFont {
	private final String name;
	private final byte[] cachedBytes;
	private ByteBuffer directBuffer;

	public LucentFont(String name, InputStream inputStream) throws IOException {
		this.name = name;
		try (inputStream) {
			this.cachedBytes = inputStream.readAllBytes();
		}
	}

	public String getName() {
		return name;
	}

	public synchronized ByteBuffer buffer() {
		if (cachedBytes == null) {
			throw new IllegalStateException("Font bytes not cached for font: " + name);
		}

		if (directBuffer == null) {
			directBuffer = ByteBuffer.allocateDirect(cachedBytes.length)
				.order(ByteOrder.nativeOrder())
				.put(cachedBytes);
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