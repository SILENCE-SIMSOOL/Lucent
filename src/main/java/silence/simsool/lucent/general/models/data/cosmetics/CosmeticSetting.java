package silence.simsool.lucent.general.models.data.cosmetics;

import net.minecraft.resources.Identifier;

public class CosmeticSetting {
	public final boolean enabled;
	public final String mode;
	public volatile Identifier resolvedTexture;

	public CosmeticSetting(boolean enabled, String mode) {
		this.enabled = enabled;
		this.mode = mode;
	}
}