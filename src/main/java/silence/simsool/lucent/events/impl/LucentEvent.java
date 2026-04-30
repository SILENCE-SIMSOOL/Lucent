package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

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

	/**
	 * Event fired when a block state is updated.
	 */
	public static final Event<BlockUpdateEvent> BLOCK_UPDATE_EVENT = createArrayBacked(
		BlockUpdateEvent.class, listeners -> (pos, oldState, newState) -> {
			for (BlockUpdateEvent listener : listeners) listener.onBlockUpdate(pos, oldState, newState);
		}
	);

	/**
	 * Event fired during the render data extraction phase.
	 */
	public static final Event<RenderExtractEvent> RENDER_EXTRACT_EVENT = createArrayBacked(
		RenderExtractEvent.class, listeners -> partialTick -> {
			for (RenderExtractEvent listener : listeners) listener.onExtract(partialTick);
		}
	);

	/**
	 * Event fired after all rendering is completed.
	 */
	public static final Event<RenderLastEvent> RENDER_LAST_EVENT = createArrayBacked(
		RenderLastEvent.class, listeners -> partialTick -> {
			for (RenderLastEvent listener : listeners) listener.onRenderLast(partialTick);
		}
	);

	/**
	 * Event fired when interacting with a block.
	 */
	public static final Event<BlockInteractEvent> BLOCK_INTERACT_EVENT = createArrayBacked(
		BlockInteractEvent.class, listeners -> pos -> {
			for (BlockInteractEvent listener : listeners) {
				if (listener.onBlockInteract(pos)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired upon keyboard input.
	 */
	public static final Event<KeyInputEvent> KEY_INPUT_EVENT = createArrayBacked(
		KeyInputEvent.class, listeners -> key -> {
			for (KeyInputEvent listener : listeners) {
				if (listener.onKeyInput(key)) return true;
			}
			return false;
		}
	);

	/**
	 * Event fired when a message is being sent.
	 */
	public static final Event<MessageSentEvent> MESSAGE_SENT_EVENT = createArrayBacked(
		MessageSentEvent.class, listeners -> message -> {
			for (MessageSentEvent listener : listeners) {
				if (listener.onMessageSent(message)) return true;
			}
			return false;
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
		/** * Called when a block update occurs.
		 * * @param pos      The coordinates of the block
		 * @param oldState The previous state of the block
		 * @param newState The new state of the block
		 */
		void onBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState);
	}

	@FunctionalInterface
	public interface RenderExtractEvent {
		/** * Called during the rendering data extraction phase.
		 * * @param partialTick Progress between the last and next tick (0.0 - 1.0)
		 */
		void onExtract(float partialTick);
	}

	@FunctionalInterface
	public interface RenderLastEvent {
		/** * Called after all world and entity rendering is complete.
		 * * @param partialTick Progress between the last and next tick (0.0 - 1.0)
		 */
		void onRenderLast(float partialTick);
	}

	@FunctionalInterface 
	public interface BlockInteractEvent {
		/** * Called when interacting with a block.
		 * * @param pos The coordinates of the target block
		 * @return true to cancel the interaction; false otherwise
		 */
		boolean onBlockInteract(BlockPos pos);
	}

	@FunctionalInterface 
	public interface KeyInputEvent {
		/** * Called when a key is pressed.
		 * * @param key The key that was input
		 * @return true to consume the input; false otherwise
		 */
		boolean onKeyInput(InputConstants.Key key);
	}

	@FunctionalInterface 
	public interface MessageSentEvent {
		/** * Called when a message is about to be sent from the client.
		 * * @param message The message string to be sent
		 * @return true to cancel sending; false otherwise
		 */
		boolean onMessageSent(String message);
	}

}