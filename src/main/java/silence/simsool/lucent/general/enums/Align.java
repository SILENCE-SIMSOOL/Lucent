package silence.simsool.lucent.general.enums;

public enum Align {

	LEFT, CENTER, RIGHT;

	public Align next() {
		Align[] values = values();
		return values[(ordinal() + 1) % values.length];
	}

	public String displayName() {
		return switch (this) {
			case LEFT -> "Left";
			case CENTER -> "Center";
			case RIGHT -> "Right";
		};
	}

}