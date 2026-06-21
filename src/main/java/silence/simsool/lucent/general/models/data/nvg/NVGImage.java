package silence.simsool.lucent.general.models.data.nvg;

public class NVGImage {
	int count;
	final int nvg;

	public NVGImage(int count, int nvg) {
		this.count = count;
		this.nvg = nvg;
	}

	public int getCount() {
		return count;
	}

	public int getNvg() {
		return nvg;
	}

	public void incrementCount() {
		this.count++;
	}

	public void decrementCount() {
		this.count--;
	}
}