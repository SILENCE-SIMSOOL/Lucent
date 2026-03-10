package silence.simsool.lucent.ui.utils.nvg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
				// 앞의 '/' 제거: ClassLoader.getResourceAsStream()은 절대 경로가 아닌
				// 클래스패스 상대 경로를 사용하므로 leading slash가 있으면 null을 반환함
				String resourcePath = trimmedPath.startsWith("/") ? trimmedPath.substring(1) : trimmedPath;

				// Thread context classloader 사용: Lucent가 라이브러리로 사용될 때
				// 다른 모드(SilenceUtils 등)의 리소스도 찾을 수 있도록 함
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				InputStream stream = (cl != null) ? cl.getResourceAsStream(resourcePath) : null;

				// fallback: Lucent 자신의 클래스로더로 재시도
				if (stream == null) {
					stream = Image.class.getClassLoader().getResourceAsStream(resourcePath);
				}

				if (stream == null) throw new FileNotFoundException(path);
				return stream;
			}
		}
	}
}