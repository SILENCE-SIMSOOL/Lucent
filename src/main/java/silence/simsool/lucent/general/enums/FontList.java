package silence.simsool.lucent.general.enums;

public enum FontList {
	DEFAULT("pretendard"),
	PRETENDARD("pretendard"),
	PRETENDARD_MEDIUM("pretendard_medium"),
	PRETENDARD_SEMIBOLD("pretendard_semibold"),
	PRETENDARD_LIGHT("pretendard_light"),
	PRETENDARD_EXTRALIGHT("pretendard_extralight");

	private final String name;

	FontList(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}