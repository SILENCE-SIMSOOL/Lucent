package silence.simsool.lucent.events.impl;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public final class ScreenEvent {

	public static final Event<Render> RENDER = EventFactory.createArrayBacked(Render.class,
		listeners -> (screen, graphics, mouseX, mouseY) -> {
			for (Render listener : listeners) {
				if (listener.onScreenRender(screen, graphics, mouseX, mouseY)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface public interface Render {
		/** @return true면 스크린 렌더 취소 */
		boolean onScreenRender(Screen screen, GuiGraphics graphics, int mouseX, int mouseY);
	}

}