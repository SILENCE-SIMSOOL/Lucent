package silence.simsool.lucent.general.abstracts;

public class NavState {
	public final String page;
	public final Mod mod;
	public final String category;

	public NavState(String page, Mod mod, String category) {
		this.page = page;
		this.mod = mod;
		this.category = category;
	}
}