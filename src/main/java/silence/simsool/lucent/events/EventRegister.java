package silence.simsool.lucent.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import silence.simsool.lucent.events.impl.LucentEvent;

public class EventRegister {

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

	}

}