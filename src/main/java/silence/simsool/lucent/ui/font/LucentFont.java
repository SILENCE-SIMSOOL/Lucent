package silence.simsool.lucent.ui.font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class LucentFont {
	private final String name;
	private final byte[] cachedBytes;

	public LucentFont(String name, InputStream inputStream) throws IOException {
		this.name = name;
		try (inputStream) {
			this.cachedBytes = inputStream.readAllBytes();
		}
	}

	public String getName() {
		return name;
	}

	public ByteBuffer buffer() {
		if (cachedBytes == null) {
			throw new IllegalStateException("Font bytes not cached for font: " + name);
		}

		ByteBuffer buffer = ByteBuffer.allocateDirect(cachedBytes.length)
			.order(ByteOrder.nativeOrder())
			.put(cachedBytes);
		
		return (ByteBuffer) buffer.flip();
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