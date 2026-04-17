package silence.simsool.lucent.events.impl;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LucentEvent {

	// Init Finished Event
	public static final Event<InitFinishedEvent> INIT_FINISHED_EVENT = EventFactory.createArrayBacked(InitFinishedEvent.class,
		listeners -> () -> {
			for (InitFinishedEvent listener : listeners) listener.onInitFinished();
		}
	);

	// Resources Ready Event
	public static final Event<ResourcesReadyEvent> RESOURCES_READY_EVENT = EventFactory.createArrayBacked(ResourcesReadyEvent.class,
		listeners -> () -> {
			for (ResourcesReadyEvent listener : listeners) listener.onResourcesReady();
		}
	);

	// Tick Event
	public static final Event<TickEvent> TICK_EVENT = EventFactory.createArrayBacked(TickEvent.class,
		listeners -> () -> {
			for (TickEvent listener : listeners) listener.onTick();
		}
	);

	public static final Event<EverySecondEvent> EVERY_SECOND_EVENT = EventFactory.createArrayBacked(EverySecondEvent.class,
		listeners -> () -> {
			for (EverySecondEvent listener : listeners) listener.onEverySecond();
		}
	);

	// World Event
	public static final Event<WorldLoadEvent> WORLD_LOAD_EVENT = EventFactory.createArrayBacked(WorldLoadEvent.class,
		listeners -> () -> {
			for (WorldLoadEvent listener : listeners) listener.onWorldLoad();
		}
	);

	public static final Event<WorldUnloadEvent> WORLD_UNLOAD_EVENT = EventFactory.createArrayBacked(WorldUnloadEvent.class,
		listeners -> () -> {
			for (WorldUnloadEvent listener : listeners) listener.onWorldUnload();
		}
	);

	// Block Event
	public static final Event<BlockUpdateEvent> BLOCK_UPDATE_EVENT = EventFactory.createArrayBacked(BlockUpdateEvent.class,
		listeners -> (pos, oldState, newState) -> {
			for (BlockUpdateEvent listener : listeners) listener.onBlockUpdate(pos, oldState, newState);
		}
	);

	// Render Events
	public static final Event<RenderExtractEvent> RENDER_EXTRACT_EVENT = EventFactory.createArrayBacked(RenderExtractEvent.class,
		listeners -> partialTick -> {
			for (RenderExtractEvent listener : listeners) listener.onExtract(partialTick);
		}
	);

	public static final Event<RenderLastEvent> RENDER_LAST_EVENT = EventFactory.createArrayBacked(RenderLastEvent.class,
		listeners -> partialTick -> {
			for (RenderLastEvent listener : listeners) listener.onRenderLast(partialTick);
		}
	);

	public static final Event<BlockInteractEvent> BLOCK_INTERACT_EVENT = EventFactory.createArrayBacked(BlockInteractEvent.class,
		listeners -> pos -> {
			for (BlockInteractEvent listener : listeners) {
				if (listener.onBlockInteract(pos)) return true;
			}
			return false;
		}
	);

	// Input Event
	public static final Event<KeyInputEvent> KEY_INPUT_EVENT = EventFactory.createArrayBacked(KeyInputEvent.class,
		listeners -> key -> {
			for (KeyInputEvent listener : listeners) {
				if (listener.onKeyInput(key)) return true;
			}
			return false;
		}
	);

	public static final Event<MessageSentEvent> MESSAGE_SENT_EVENT = EventFactory.createArrayBacked(MessageSentEvent.class,
		listeners -> message -> {
			for (MessageSentEvent listener : listeners) {
				if (listener.onMessageSent(message)) return true;
			}
			return false;
		}
	);

	@FunctionalInterface
	public interface InitFinishedEvent {
		void onInitFinished();
	}

	@FunctionalInterface
	public interface ResourcesReadyEvent {
		void onResourcesReady();
	}

	@FunctionalInterface
	public interface TickEvent {
		void onTick();
	}

	@FunctionalInterface
	public interface EverySecondEvent {
		void onEverySecond();
	}

	@FunctionalInterface
	public interface WorldLoadEvent {
		void onWorldLoad();
	}

	@FunctionalInterface
	public interface WorldUnloadEvent {
		void onWorldUnload();
	}

	@FunctionalInterface public interface BlockUpdateEvent {
		void onBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState);
	}

	@FunctionalInterface
	public interface RenderExtractEvent {
		void onExtract(float partialTick);
	}

	@FunctionalInterface
	public interface RenderLastEvent {
		void onRenderLast(float partialTick);
	}

	@FunctionalInterface public interface BlockInteractEvent {
		/** @return true면 인터랙션 취소 */
		boolean onBlockInteract(BlockPos pos);
	}

	@FunctionalInterface public interface KeyInputEvent {
		/** @return true면 입력 취소 */
		boolean onKeyInput(InputConstants.Key key);
	}

	@FunctionalInterface public interface MessageSentEvent {
		/** @return true면 메시지 전송 취소 */
		boolean onMessageSent(String message);
	}

}