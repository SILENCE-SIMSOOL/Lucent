package silence.simsool.lucent.general.models.data.cosmetics;

import net.minecraft.network.chat.Component;

public class CachedDisplayName {
	public final Component original;
	public final Component formatted;

	public CachedDisplayName(Component original, Component formatted) {
		this.original = original;
		this.formatted = formatted;
	}
}