package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.network.chat.Component;

public class UTitle {

	public static void createTitle(String title, String lore, int fadeIn, int time, int fadeOut) {
		if (mc.gui == null) return;

		if (mc.gui.hud != null) {
			mc.gui.hud.setTimes(fadeIn, time, fadeOut);

			// Subtitle
			if (lore != null && !lore.isEmpty()) mc.gui.hud.setSubtitle(Component.literal(lore));
			else mc.gui.hud.setSubtitle(null);

			// Title
			if (title != null && !title.isEmpty()) mc.gui.hud.setTitle(Component.literal(title));
			else mc.gui.hud.setTitle(Component.empty());
		}
	}

}