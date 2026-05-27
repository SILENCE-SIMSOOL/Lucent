package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.*;

public class LucentEvent {

	public static final Event<IInitFinishedEvent> INIT_FINISHED_EVENT = createArrayBacked(
		IInitFinishedEvent.class, listeners -> () -> {
			for (IInitFinishedEvent listener : listeners) listener.onInitFinished();
		}
	);

	public static final Event<IResourcesReadyEvent> RESOURCES_READY_EVENT = createArrayBacked(
		IResourcesReadyEvent.class, listeners -> () -> {
			for (IResourcesReadyEvent listener : listeners) listener.onResourcesReady();
		}
	);

	public static class TickEvents {

		public final Event<ITickEvent> LOW = createArrayBacked(
			ITickEvent.class, listeners -> () -> {
				for (ITickEvent listener : listeners) listener.onTick();
			}
		);

		public final Event<ITickEvent> MEDIUM = createArrayBacked(
			ITickEvent.class, listeners -> () -> {
				for (ITickEvent listener : listeners) listener.onTick();
			}
		);

		public final Event<ITickEvent> HIGH = createArrayBacked(
			ITickEvent.class, listeners -> () -> {
				for (ITickEvent listener : listeners) listener.onTick();
			}
		);

		public void register(ITickEvent listener) {
			LOW.register(listener);
		}

		public ITickEvent invoker() {
			return LOW.invoker();
		}

	}

	public static final TickEvents TICK_EVENT = new TickEvents();

	public static final Event<IEverySecondEvent> EVERY_SECOND_EVENT = createArrayBacked(
		IEverySecondEvent.class, listeners -> () -> {
			for (IEverySecondEvent listener : listeners) listener.onEverySecond();
		}
	);

	public static final Event<ITickEvent> SERVER_TICK_EVENT = createArrayBacked(
		ITickEvent.class, listeners -> () -> {
			for (ITickEvent listener : listeners) listener.onTick();
		}
	);

	public static final Event<IChatEvent> CHAT_EVENT = createArrayBacked(
		IChatEvent.class, listeners -> event -> {
			for (IChatEvent listener : listeners) {
				listener.onChat(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IActionBarEvent> ACTIONBAR_EVENT = createArrayBacked(
		IActionBarEvent.class, listeners -> event -> {
			for (IActionBarEvent listener : listeners) {
				listener.onActionBar(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IServerJoinEvent> SERVER_JOIN_EVENT = createArrayBacked(
		IServerJoinEvent.class, listeners -> () -> {
			for (IServerJoinEvent listener : listeners) listener.onServerJoin();
		}
	);

	public static final Event<IServerDisconnectEvent> SERVER_DISCONNECT_EVENT = createArrayBacked(
		IServerDisconnectEvent.class, listeners -> () -> {
			for (IServerDisconnectEvent listener : listeners) listener.onServerDisconnect();
		}
	);

	public static final Event<IWorldLoadEvent> WORLD_LOAD_EVENT = createArrayBacked(
		IWorldLoadEvent.class, listeners -> () -> {
			for (IWorldLoadEvent listener : listeners) listener.onWorldLoad();
		}
	);

	public static final Event<IBlockUpdateEvent> BLOCK_UPDATE_EVENT = createArrayBacked(
		IBlockUpdateEvent.class, listeners -> event -> {
			for (IBlockUpdateEvent listener : listeners) listener.onBlockUpdate(event);
		}
	);

	public static final Event<IWorldRenderEvent> WORLD_RENDER = createArrayBacked(
		IWorldRenderEvent.class, listeners -> event -> {
			for (IWorldRenderEvent listener : listeners) listener.onWorldRender(event);
		}
	);

	public static final Event<IWorldRenderLastEvent> WORLD_RENDER_LAST = createArrayBacked(
		IWorldRenderLastEvent.class, listeners -> event -> {
			for (IWorldRenderLastEvent listener : listeners) listener.onWorldRenderLast(event);
		}
	);

	public static final Event<IBlockInteractEvent> BLOCK_INTERACT_EVENT = createArrayBacked(
		IBlockInteractEvent.class, listeners -> event -> {
			for (IBlockInteractEvent listener : listeners) {
				listener.onBlockInteract(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IKeyInputEvent> KEY_INPUT_EVENT = createArrayBacked(
		IKeyInputEvent.class, listeners -> event -> {
			for (IKeyInputEvent listener : listeners) {
				listener.onKeyInput(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IMessageSentEvent> MESSAGE_SENT_EVENT = createArrayBacked(
		IMessageSentEvent.class, listeners -> event -> {
			for (IMessageSentEvent listener : listeners) {
				listener.onMessageSent(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<ITabCompleteEvent> TAB_COMPLETION_EVENT = createArrayBacked(
		ITabCompleteEvent.class, listeners -> event -> {
			for (ITabCompleteEvent listener : listeners) {
				listener.onTabComplete(event);
			}
		}
	);

	public static final Event<IBossBarRenderEvent> BOSS_BAR_RENDER_EVENT = createArrayBacked(
		IBossBarRenderEvent.class, listeners -> event -> {
			for (IBossBarRenderEvent l : listeners) {
				l.onBossBarRender(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IKeybindEvent> KEYBIND_EVENT = createArrayBacked(
		IKeybindEvent.class, listeners -> event -> {
			for (IKeybindEvent listener : listeners) {
				listener.onKeybind(event);
			}
		}
	);

	public static final Event<IDropItemEvent> DROP_ITEM_EVENT = createArrayBacked(
		IDropItemEvent.class, listeners -> event -> {
			for (IDropItemEvent listener : listeners) {
				listener.onDropItem(event);
				if (event.isCanceled()) break;
			}
		}
	);

}