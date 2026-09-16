package silence.simsool.lucent.ui.utils.skija;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;

import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.svg.SVGDOM;

public class Image {
	public final String identifier;
	public final boolean isSVG;
	private final byte[] data;
	private io.github.humbleui.skija.Image skijaImage;
	private SVGDOM svgDom;

	public Image(String identifier) throws Exception {
		this(identifier, getBytesFromPath(identifier));
	}

	public Image(String identifier, byte[] data) {
		this.identifier = identifier;
		this.isSVG = identifier.toLowerCase().endsWith(".svg");
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public synchronized io.github.humbleui.skija.Image getSkijaImage() {
		if (skijaImage == null && !isSVG && data != null) {
			skijaImage = io.github.humbleui.skija.Image.makeDeferredFromEncodedBytes(data);
		}
		return skijaImage;
	}

	public synchronized SVGDOM getSvgDom() {
		if (svgDom == null && isSVG && data != null) {
			svgDom = new SVGDOM(Data.makeFromBytes(data));
		}
		return svgDom;
	}

	public synchronized void close() {
		if (skijaImage != null) {
			skijaImage.close();
			skijaImage = null;
		}
		if (svgDom != null) {
			svgDom.close();
			svgDom = null;
		}
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

	private static byte[] getBytesFromPath(String path) throws Exception {
		String trimmedPath = path.trim();
		if (trimmedPath.startsWith("http")) {
			throw new UnsupportedOperationException("HTTP loading not implemented");
		}
		File file = new File(trimmedPath);
		if (file.exists() && file.isFile()) {
			return Files.readAllBytes(file.toPath());
		}

		String resourcePath = trimmedPath.startsWith("/") ? trimmedPath.substring(1) : trimmedPath;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream stream = (cl != null) ? cl.getResourceAsStream(resourcePath) : null;
		if (stream == null) {
			stream = Image.class.getClassLoader().getResourceAsStream(resourcePath);
		}
		if (stream == null) {
			throw new FileNotFoundException(path);
		}
		try (InputStream in = stream) {
			return in.readAllBytes();
		}
	}
}