package silence.simsool.lucent.guide;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.general.utils.useful.UScreen;

/** Releases the mouse for the initial coach mark without pausing the world. */
public final class GuideScreen extends Screen {

	public GuideScreen() {
		super(Component.literal(GuideManager.text("guide.title")));
	}

	@Override
	public boolean isPauseScreen() { return false; }

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (Lucent.CONFIG_KEY.matches(input)) {
			UScreen.setScreen(LucentAPI.createEditHUDScreen(Lucent.config));
			return true;
		}
		return super.keyPressed(input);
	}

}