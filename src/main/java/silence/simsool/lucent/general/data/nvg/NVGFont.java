package silence.simsool.lucent.general.data.nvg;

import java.nio.ByteBuffer;

public class NVGFont {
	final int id;
	final ByteBuffer buffer;

	public NVGFont(int id, ByteBuffer buffer) {
		this.id = id;
		this.buffer = buffer;
	}

	public int getId() {
		return id;
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}
}