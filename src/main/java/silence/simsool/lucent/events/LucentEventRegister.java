package silence.simsool.lucent.events;

import static silence.simsool.lucent.Lucent.mc;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.utils.useful.UChat;

public class LucentEventRegister {

	private static int tickCounter = 0;

	public static void initialize() {

		// ─────────────────────────── Chat / Network ───────────────────────────

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

		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) ->
			LucentEvent.WORLD_LOAD_EVENT.invoker().onWorldLoad()
		);

		// ──────────────────────────── Tick ────────────────────────────────────

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level != null && client.player != null) {
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

		// ──────────────────────────── World Render ────────────────────────────

		WorldRenderEvents.END_EXTRACTION.register(context -> {
			if (mc.level == null || mc.player == null) return;
			float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			LucentEvent.RENDER_EXTRACT_EVENT.invoker().onExtract(new LucentEvent.RenderExtractEventData(partialTick));
		});

		WorldRenderEvents.END_MAIN.register(context -> {
			if (mc.level == null || mc.player == null) return;
			float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
			LucentEvent.RENDER_LAST_EVENT.invoker().onRenderLast(new LucentEvent.RenderLastEventData(partialTick));
		});

		// ───────────────────────────── GUI Screen ─────────────────────────────

		HudElementRegistry.attachElementBefore(VanillaHudElements.SLEEP, Identifier.fromNamespaceAndPath(Lucent.ID, "hud_element"), (graphics, tickDelta) -> {
			if (mc.options.hideGui || mc.level == null || mc.player == null) return;
			GUIEvent.RenderHUD.EVENT.invoker().onRenderHUD(new GUIEvent.RenderHUD(graphics));
		});

		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (mc.level == null || mc.player == null) return;

			if (screen != null) GUIEvent.Open.EVENT.invoker().onOpen(new GUIEvent.Open(screen));

			ScreenMouseEvents.allowMouseClick(screen).register((s, click) -> {
				GUIEvent.Click event = new GUIEvent.Click(click.x(), click.y(), click.button(), true, s);
				GUIEvent.Click.EVENT.invoker().onClick(event);
				return !event.isCanceled();
			});

			ScreenMouseEvents.allowMouseRelease(screen).register((s, click) -> {
				GUIEvent.Click event = new GUIEvent.Click(click.x(), click.y(), click.button(), false, s);
				GUIEvent.Click.EVENT.invoker().onClick(event);
				return !event.isCanceled();
			});

			ScreenKeyboardEvents.allowKeyPress(screen).register((s, keyInput) -> {
				String keyName = GLFW.glfwGetKeyName(keyInput.key(), keyInput.scancode());
				char charTyped = (keyName != null && !keyName.isEmpty()) ? keyName.charAt(0) : '\u0000';
				GUIEvent.Key event = new GUIEvent.Key(keyName, keyInput.key(), charTyped, keyInput.scancode(), s);
				GUIEvent.Key.EVENT.invoker().onKey(event);
				return !event.isCanceled();
			});
		});

	}

}