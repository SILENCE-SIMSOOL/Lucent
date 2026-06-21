package silence.simsool.lucent.general.models.data;

public class LucentTheme {
	public final String name;
	public final int winBg, sidebarBg, accent, textPrimary, textSecondary, textLabel;
	public final int itemBg, itemHover, itemBorder, barOn, barOff, divider, sidebarSel, searchbarBg;

	public LucentTheme(String name, int winBg, int sidebarBg, int accent, int textPrimary, int textSecondary, int textLabel, int itemBg, int itemHover, int itemBorder, int barOn, int barOff, int divider, int sidebarSel, int searchbarBg) {
		this.name = name;
		this.winBg = winBg; this.sidebarBg = sidebarBg; this.accent = accent;
		this.textPrimary = textPrimary; this.textSecondary = textSecondary; this.textLabel = textLabel;
		this.itemBg = itemBg; this.itemHover = itemHover; this.itemBorder = itemBorder;
		this.barOn = barOn; this.barOff = barOff; this.divider = divider;
		this.sidebarSel = sidebarSel; this.searchbarBg = searchbarBg;
	}
}