package silence.simsool.lucent.general.models.abstracts;

public abstract class Mod {
	public final String name;
	public final String description;
	public final String category;
	public final String searchTags;
	public final String icon;
	public boolean isEnabled = false; // 모드 자체의 활성화 여부

	public Mod(String name, String description, String category, String searchTags, String icon) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.searchTags = searchTags;
		this.icon = icon;
	}
}