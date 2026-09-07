package silence.simsool.lucent.general.enums;

public enum FontList {

	DEFAULT("pretendard", "pretendard/pretendard.ttf"),
	PRETENDARD("pretendard", "pretendard/pretendard.ttf"),
	PRETENDARD_MEDIUM("pretendard_medium", "pretendard/pretendard_medium.ttf"),
	PRETENDARD_SEMIBOLD("pretendard_semibold", "pretendard/pretendard_semibold.ttf"),
	PRETENDARD_BOLD("pretendard_bold", "pretendard/pretendard_bold.ttf"),
	PRETENDARD_EXTRABOLD("pretendard_extrabold", "pretendard/pretendard_extrabold.ttf"),
	PRETENDARD_LIGHT("pretendard_light", "pretendard/pretendard_light.ttf"),
	PRETENDARD_EXTRALIGHT("pretendard_extralight", "pretendard/pretendard_extralight.ttf"),
	MATERIAL_ICONS("material_icons", "icon/MaterialIcons-Regular.ttf"),
	MATERIAL_ICONS_ROUND("material_icons_round", "icon/MaterialIconsRound-Regular.otf");

	private final String name;
	private final String relativePath;

	FontList(String name, String relativePath) {
		this.name = name;
		this.relativePath = relativePath;
	}

	public String getName() {
		return name;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public String getFileName() {
		int lastSlash = relativePath.lastIndexOf('/');
		return lastSlash >= 0 ? relativePath.substring(lastSlash + 1) : relativePath;
	}

}