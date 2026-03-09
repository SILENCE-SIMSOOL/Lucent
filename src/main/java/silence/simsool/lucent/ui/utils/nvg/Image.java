package silence.simsool.lucent.ui.utils.nvg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;

import org.lwjgl.system.MemoryUtil;

public class Image {
	public final String identifier;
	public boolean isSVG;
	public InputStream stream;
	private ByteBuffer buffer;

	public Image(String identifier) throws Exception {
		this(identifier, false, getStream(identifier), null);
	}

	public Image(String identifier, boolean isSVG, InputStream stream, ByteBuffer buffer) {
		this.identifier = identifier;
		this.isSVG = identifier.toLowerCase().endsWith(".svg");
		this.stream = stream;
		this.buffer = buffer;
	}

	public ByteBuffer buffer() throws IOException {
		if (buffer == null) {
			byte[] bytes = stream.readAllBytes();
			buffer = MemoryUtil.memAlloc(bytes.length);
			buffer.put(bytes);
			buffer.flip();
			stream.close();
		}
		if (buffer == null) {
			throw new IllegalStateException("Image has no data");
		}
		return buffer;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof Image)) return false;
		return identifier.equals(((Image) other).identifier);
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}

	private static InputStream getStream(String path) throws Exception {
		String trimmedPath = path.trim();

		if (trimmedPath.startsWith("http")) {
			throw new UnsupportedOperationException("HTTP loading not implemented");
		} else {
			File file = new File(trimmedPath);
			if (file.exists() && file.isFile()) {
				return Files.newInputStream(file.toPath());
			} else {
				InputStream stream = Image.class.getResourceAsStream(trimmedPath);
				if (stream == null) throw new FileNotFoundException(trimmedPath);
				return stream;
			}
		}
	}
}