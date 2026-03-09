package silence.simsool.lucent.general.abstracts;

public abstract class Module {
	
	public final String name;
	public final String description;
	public final String category;
	public final String searchTags;
	public final String icon;
	public boolean isEnabled = true; // 모드 자체의 활성화 여부

	public Module(String name, String description, String category, String searchTags, String icon) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.searchTags = searchTags;
		this.icon = icon;
	}
}