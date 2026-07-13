package silence.simsool.lucent.general.models.data.cosmetics;

import java.util.Map;

public class UserCosmetic {
	public final Map<String, CosmeticSetting> cosmetics;

	public UserCosmetic(Map<String, CosmeticSetting> cosmetics) {
		this.cosmetics = cosmetics;
	}
}