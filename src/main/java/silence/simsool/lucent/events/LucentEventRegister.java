package silence.simsool.lucent.events;

import static silence.simsool.lucent.Lucent.mc;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.ScreenEvent;

public class LucentEventRegister {

	private static int tickCounter = 0;

	public static void initialize() {

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
			LucentEvent.WORLD_LOAD_EVENT.invoker().onWorldLoad()
		);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
			LucentEvent.WORLD_UNLOAD_EVENT.invoker().onWorldUnload()
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.level != null) {
				LucentEvent.TICK_EVENT.invoker().onTick();

				tickCounter++;
				if (tickCounter >= 20) {
					LucentEvent.EVERY_SECOND_EVENT.invoker().onEverySecond();
					tickCounter = 0;
				}
			}
		});

		WorldRenderEvents.END_EXTRACTION.register(context -> {
			if (mc.level == null || mc.player == null) return;
			float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			LucentEvent.RENDER_EXTRACT_EVENT.invoker().onExtract(partialTick);
		});

		WorldRenderEvents.END_MAIN.register(context -> {
			if (mc.level == null || mc.player == null) return;
			float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			LucentEvent.RENDER_LAST_EVENT.invoker().onRenderLast(partialTick);
		});

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			ScreenEvent.OPEN.invoker().onScreenOpen(screen);

			ScreenEvents.remove(screen).register(s -> 
				ScreenEvent.CLOSE.invoker().onScreenClose(s)
			);

			ScreenMouseEvents.allowMouseClick(screen).register((s, event) -> 
				!ScreenEvent.MOUSE_CLICK.invoker().onMouseClick(s, event.x(), event.y(), event.button())
			);

			ScreenMouseEvents.allowMouseRelease(screen).register((s, event) -> 
				!ScreenEvent.MOUSE_RELEASE.invoker().onMouseRelease(s, event.x(), event.y(), event.button())
			);

			ScreenKeyboardEvents.allowKeyPress(screen).register((s, event) -> 
				!ScreenEvent.KEY_PRESS.invoker().onKeyPress(s, event.key(), event.scancode(), event.modifiers())
			);
		});
	}

}