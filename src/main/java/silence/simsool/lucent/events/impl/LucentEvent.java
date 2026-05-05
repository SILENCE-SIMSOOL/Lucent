package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class LucentEvent {

	/**
	 * Event fired when initialization is finished.
	 */
	public static final Event<InitFinishedEvent> INIT_FINISHED_EVENT = createArrayBacked(
		InitFinishedEvent.class, listeners -> () -> {
			for (InitFinishedEvent listener : listeners) listener.onInitFinished();
		}
	);

	/**
	 * Event fired when resources are ready for use.
	 */
	public static final Event<ResourcesReadyEvent> RESOURCES_READY_EVENT = createArrayBacked(
		ResourcesReadyEvent.class, listeners -> () -> {
			for (ResourcesReadyEvent listener : listeners) listener.onResourcesReady();
		}
	);

	/**
	 * Container for tick events with different priority levels.
	 */
	public static class TickEvents {

		public final Event<TickEvent> LOW = createArrayBacked(
			TickEvent.class, listeners -> () -> {
				for (TickEvent listener : listeners) listener.onTick();
			}
		);

		public final Event<TickEvent> MEDIUM = createArrayBacked(
			TickEvent.class, listeners -> () -> {
				for (TickEvent listener : listeners) listener.onTick();
			}
		);

		public final Event<TickEvent> HIGH = createArrayBacked(
			TickEvent.class, listeners -> () -> {
				for (TickEvent listener : listeners) listener.onTick();
			}
		);

		/**
		 * Registers a listener to the LOW priority tick event.
		 * * @param listener The tick listener to register
		 */
		public void register(TickEvent listener) {
			LOW.register(listener);
		}

		/**
		 * Returns the invoker for the LOW priority tick event.
		 * * @return The invoker instance
		 */
		public TickEvent invoker() {
			return LOW.invoker();
		}

	}

	public static final TickEvents TICK_EVENT = new TickEvents();

	/**
	 * Event fired every second (typically every 20 ticks).
	 */
	public static final Event<EverySecondEvent> EVERY_SECOND_EVENT = createArrayBacked(
		EverySecondEvent.class, listeners -> () -> {
			for (EverySecondEvent listener : listeners) listener.onEverySecond();
		}
	);

	/**
	 * Event fired on each server-side tick.
	 */
	public static final Event<TickEvent> SERVER_TICK_EVENT = createArrayBacked(
		TickEvent.class, listeners -> () -> {
			for (TickEvent listener : listeners) listener.onTick();
		}
	);

	/**
	 * Data container for message-related events.
	 */
	public static class MessageEvent {
		public String message;
		public String chat;
		private boolean canceled = false;

		public MessageEvent(String message, String chat) {
			this.message = message;
			this.chat = chat;
		}

		/**
		 * Cancels the current event.
		 */
		public void cancel() {
			this.canceled = true;
		}

		/**
		 * Checks if the event has been canceled.
		 * * @return true if canceled; false otherwise
		 */
		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class TabCompletionEvent {
		private final String fullInput;
		private final String beforeCursor;
		private final ArrayList<String> existing;
		private String[] additional;

		public TabCompletionEvent(String fullInput, String beforeCursor, ArrayList<String> existing) {
			this.fullInput = fullInput;
			this.beforeCursor = beforeCursor;
			this.existing = existing;
		}

		public void post() {
			TAB_COMPLETION_EVENT.invoker().onTabComplete(this);
		}

		public String[] intoSuggestionArray() {
			return additional;
		}

		public void setAdditional(String[] additional) {
			this.additional = additional;
		}

		public String getFullInput() {
			return fullInput;
		}

		public String getBeforeCursor() {
			return beforeCursor;
		}

		public ArrayList<String> getExisting() {
			return existing;
		}
	}

	/**
	 * Event fired when a chat message is received.
	 */
	public static final Event<ChatEvent> CHAT_EVENT = createArrayBacked(
		ChatEvent.class, listeners -> event -> {
			for (ChatEvent listener : listeners) {
				listener.onChat(event);
				if (event.isCanceled()) break;
			}
		}
	);

	/**
	 * Event fired when an action bar message is received.
	 */
	public static final Event<ActionBarEvent> ACTIONBAR_EVENT = createArrayBacked(
		ActionBarEvent.class, listeners -> event -> {
			for (ActionBarEvent listener : listeners) {
				listener.onActionBar(event);
				if (event.isCanceled()) break;
			}
		}
	);

	/**
	 * Event fired when joining a server.
	 */
	public static final Event<ServerJoinEvent> SERVER_JOIN_EVENT = createArrayBacked(
		ServerJoinEvent.class, listeners -> () -> {
			for (ServerJoinEvent listener : listeners) listener.onServerJoin();
		}
	);

	/**
	 * Event fired when disconnecting from a server.
	 */
	public static final Event<ServerDisconnectEvent> SERVER_DISCONNECT_EVENT = createArrayBacked(
		ServerDisconnectEvent.class, listeners -> () -> {
			for (ServerDisconnectEvent listener : listeners) listener.onServerDisconnect();
		}
	);

	/**
	 * Event fired when a world is loaded.
	 */
	public static final Event<WorldLoadEvent> WORLD_LOAD_EVENT = createArrayBacked(
		WorldLoadEvent.class, listeners -> () -> {
			for (WorldLoadEvent listener : listeners) listener.onWorldLoad();
		}
	);

	public static class BlockUpdateEventData {
		public BlockPos pos;
		public BlockState oldState;
		public BlockState newState;
		public BlockUpdateEventData(BlockPos pos, BlockState oldState, BlockState newState) {
			this.pos = pos;
			this.oldState = oldState;
			this.newState = newState;
		}
	}

	public static class RenderExtractEventData {
		public float partialTick;
		public RenderExtractEventData(float partialTick) {
			this.partialTick = partialTick;
		}
	}

	public static class RenderLastEventData {
		public float partialTick;
		public RenderLastEventData(float partialTick) {
			this.partialTick = partialTick;
		}
	}

	public static class BlockInteractEventData {
		public BlockPos pos;
		private boolean canceled = false;
		public BlockInteractEventData(BlockPos pos) {
			this.pos = pos;
		}
		public void cancel() { this.canceled = true; }
		public boolean isCanceled() { return canceled; }
	}

	public static class KeyInputEventData {
		public InputConstants.Key key;
		private boolean canceled = false;
		public KeyInputEventData(InputConstants.Key key) {
			this.key = key;
		}
		public void cancel() { this.canceled = true; }
		public boolean isCanceled() { return canceled; }
	}

	public static class MessageSentEventData {
		public String message;
		private boolean canceled = false;
		public MessageSentEventData(String message) {
			this.message = message;
		}
		public void cancel() { this.canceled = true; }
		public boolean isCanceled() { return canceled; }
	}

	/**
	 * Event fired when a block state is updated.
	 */
	public static final Event<BlockUpdateEvent> BLOCK_UPDATE_EVENT = createArrayBacked(
		BlockUpdateEvent.class, listeners -> event -> {
			for (BlockUpdateEvent listener : listeners) listener.onBlockUpdate(event);
		}
	);

	/**
	 * Event fired during the render data extraction phase.
	 */
	public static final Event<RenderExtractEvent> RENDER_EXTRACT_EVENT = createArrayBacked(
		RenderExtractEvent.class, listeners -> event -> {
			for (RenderExtractEvent listener : listeners) listener.onExtract(event);
		}
	);

	/**
	 * Event fired after all rendering is completed.
	 */
	public static final Event<RenderLastEvent> RENDER_LAST_EVENT = createArrayBacked(
		RenderLastEvent.class, listeners -> event -> {
			for (RenderLastEvent listener : listeners) listener.onRenderLast(event);
		}
	);

	/**
	 * Event fired when interacting with a block.
	 */
	public static final Event<BlockInteractEvent> BLOCK_INTERACT_EVENT = createArrayBacked(
		BlockInteractEvent.class, listeners -> event -> {
			for (BlockInteractEvent listener : listeners) {
				listener.onBlockInteract(event);
				if (event.isCanceled()) break; }
		}
	);

	/**
	 * Event fired upon keyboard input.
	 */
	public static final Event<KeyInputEvent> KEY_INPUT_EVENT = createArrayBacked(
		KeyInputEvent.class, listeners -> event -> {
			for (KeyInputEvent listener : listeners) {
				listener.onKeyInput(event);
				if (event.isCanceled()) break; }
		}
	);

	/**
	 * Event fired when a message is being sent.
	 */
	public static final Event<MessageSentEvent> MESSAGE_SENT_EVENT = createArrayBacked(
		MessageSentEvent.class, listeners -> event -> {
			for (MessageSentEvent listener : listeners) {
				listener.onMessageSent(event);
				if (event.isCanceled()) break; }
		}
	);

	/**
	 * Event fired for tab completion suggestions.
	 */
	public static final Event<TabCompleteEvent> TAB_COMPLETION_EVENT = createArrayBacked(
		TabCompleteEvent.class, listeners -> event -> {
			for (TabCompleteEvent listener : listeners) {
				listener.onTabComplete(event);
			}
		}
	);

	@FunctionalInterface
	public interface InitFinishedEvent {
		/** Called when initialization is complete. */
		void onInitFinished();
	}

	@FunctionalInterface
	public interface ResourcesReadyEvent {
		/** Called when resources are fully loaded and ready. */
		void onResourcesReady();
	}

	@FunctionalInterface
	public interface TickEvent {
		/** Called on every tick. */
		void onTick();
	}

	@FunctionalInterface
	public interface EverySecondEvent {
		/** Called every second (every 20 ticks). */
		void onEverySecond();
	}

	@FunctionalInterface
	public interface ChatEvent {
		/** * Called when a chat message is received.
		 * * @param event Object containing message data and cancellation control
		 */
		void onChat(MessageEvent event);
	}

	@FunctionalInterface
	public interface ActionBarEvent {
		/** * Called when an action bar message is received.
		 * * @param event Object containing message data and cancellation control
		 */
		void onActionBar(MessageEvent event);
	}

	@FunctionalInterface
	public interface ServerJoinEvent {
		/** Called when the client connects to a server. */
		void onServerJoin();
	}

	@FunctionalInterface
	public interface ServerDisconnectEvent {
		/** Called when the client disconnects from a server. */
		void onServerDisconnect();
	}

	@FunctionalInterface
	public interface WorldLoadEvent {
		/** Called when a world instance is loaded. */
		void onWorldLoad();
	}

	@FunctionalInterface 
	public interface BlockUpdateEvent {
		void onBlockUpdate(BlockUpdateEventData event);
	}

	@FunctionalInterface
	public interface RenderExtractEvent {
		void onExtract(RenderExtractEventData event);
	}

	@FunctionalInterface
	public interface RenderLastEvent {
		void onRenderLast(RenderLastEventData event);
	}

	@FunctionalInterface 
	public interface BlockInteractEvent {
		void onBlockInteract(BlockInteractEventData event);
	}

	@FunctionalInterface 
	public interface KeyInputEvent {
		void onKeyInput(KeyInputEventData event);
	}

	@FunctionalInterface 
	public interface MessageSentEvent {
		void onMessageSent(MessageSentEventData event);
	}

	@FunctionalInterface
	public interface TabCompleteEvent {
		/** * Called to provide tab completion suggestions.
		 * * @param event Object containing tab completion data
		 */
		void onTabComplete(TabCompletionEvent event);
	}

	// ─────────────────────────── BossBar ──────────────────────────────────────

	public static class BossBarRenderEventData {
		public final BossEvent bossBar;
		private boolean canceled = false;
		public BossBarRenderEventData(BossEvent bossBar) { this.bossBar = bossBar; }
		public void cancel() { this.canceled = true; }
		public boolean isCanceled() { return canceled; }
	}

	public static final Event<BossBarRenderEvent> BOSS_BAR_RENDER_EVENT = createArrayBacked(
		BossBarRenderEvent.class, listeners -> event -> {
			for (BossBarRenderEvent l : listeners) {
				l.onBossBarRender(event);
				if (event.isCanceled()) break;
			}
		}
	);

	@FunctionalInterface
	public interface BossBarRenderEvent {
		void onBossBarRender(BossBarRenderEventData event);
	}

}