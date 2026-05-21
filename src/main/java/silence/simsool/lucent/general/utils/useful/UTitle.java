package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.network.chat.Component;

public class UTitle {

	public static void createTitle(String title, String lore, int fadeIn, int time, int fadeOut) {
		if (mc.gui == null) return;

		mc.gui.setTimes(fadeIn, time, fadeOut);

		// Subtitle
		if (lore != null && !lore.isEmpty()) mc.gui.setSubtitle(Component.literal(lore));
		else mc.gui.setSubtitle(null);

		// Title
		if (title != null && !title.isEmpty()) mc.gui.setTitle(Component.literal(title));
		else mc.gui.setTitle(Component.empty());
	}

}