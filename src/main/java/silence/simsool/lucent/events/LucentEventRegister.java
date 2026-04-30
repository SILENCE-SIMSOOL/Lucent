package silence.simsool.lucent.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import static silence.simsool.lucent.Lucent.mc;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.ScreenEvent;
import silence.simsool.lucent.general.utils.useful.UChat;

public class LucentEventRegister {

	private static int tickCounter = 0;

	public static void initialize() {

		ClientReceiveMessageEvents.ALLOW_GAME.register((component, isActionBar) -> {
			String message = component.getString(); 
			String chat = UChat.cleanColor(message);

			LucentEvent.MessageEvent event = new LucentEvent.MessageEvent(message, chat);

			if (isActionBar) LucentEvent.ACTIONBAR_EVENT.invoker().onActionBar(event);
			else LucentEvent.CHAT_EVENT.invoker().onChat(event);

			return !event.isCanceled();
		});

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
			LucentEvent.SERVER_JOIN_EVENT.invoker().onServerJoin()
		);

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
			LucentEvent.SERVER_DISCONNECT_EVENT.invoker().onServerDisconnect()
		);

		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
			LucentEvent.WORLD_LOAD_EVENT.invoker().onWorldLoad();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.level != null) {
				LucentEvent.TICK_EVENT.invoker().onTick();

				tickCounter++;

				if (tickCounter % 5 == 0) LucentEvent.TICK_EVENT.MEDIUM.invoker().onTick();

				if (tickCounter % 10 == 0) LucentEvent.TICK_EVENT.HIGH.invoker().onTick();

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