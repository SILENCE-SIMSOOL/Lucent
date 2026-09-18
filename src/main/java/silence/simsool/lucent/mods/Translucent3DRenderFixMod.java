package silence.simsool.lucent.mods;

import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.ConfigType;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.interfaces.annotations.ModConfig;
import silence.simsool.lucent.general.utils.LucentCategory;

public class Translucent3DRenderFixMod extends Mod {

	public Translucent3DRenderFixMod() {
		super(
			"lucent.config.lucent.translucent3drenderfixmod.general.name",
			"lucent.config.lucent.translucent3drenderfixmod.general.description",
			LucentCategory.GRAPHICS,
			"render, translucent, 3d, fix, geometry",
			"\uE3B7",
			true
		);
	}

	public static boolean isEnabled() {
		return Lucent.config.isModuleEnabled(Translucent3DRenderFixMod.class);
	}

	@ModConfig(
		type = ConfigType.SWITCH,
		name = "lucent.config.lucent.translucent3drenderfixmod.property.customgeometry.name",
		description = "lucent.config.lucent.translucent3drenderfixmod.property.customgeometry.description"
	)
	public static boolean CustomGeometry = true;

}