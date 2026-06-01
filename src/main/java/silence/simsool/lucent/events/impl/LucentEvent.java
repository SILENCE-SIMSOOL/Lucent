package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import java.util.ArrayList;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import silence.simsool.lucent.general.enums.DropType;
import silence.simsool.lucent.general.models.data.KeyBind;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IActionBarEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IBlockInteractEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IBlockUpdateEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IChatEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IDropItemEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IEverySecondEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IInitFinishedEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IKeybindEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IMessageSentEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IRenderBossBarEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IRenderWorldEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IRenderWorldLastEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IResourcesReadyEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IServerDisconnectEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IServerJoinEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.ITabCompleteEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.ITickEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IWorldLoadEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IItemPickupEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.ISoundEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IScoreboardEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IUseItemOnEvent;
import silence.simsool.lucent.general.models.interfaces.events.lucentevent.IUseItemEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;

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

	public static final Event<IRenderWorldEvent> WORLD_RENDER = createArrayBacked(
		IRenderWorldEvent.class, listeners -> event -> {
			for (IRenderWorldEvent listener : listeners) listener.onRenderWorld(event);
		}
	);

	public static final Event<IRenderWorldLastEvent> WORLD_RENDER_LAST = createArrayBacked(
		IRenderWorldLastEvent.class, listeners -> event -> {
			for (IRenderWorldLastEvent listener : listeners) listener.onRenderWorldLast(event);
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

	public static final Event<IRenderBossBarEvent> BOSS_BAR_RENDER_EVENT = createArrayBacked(
		IRenderBossBarEvent.class, listeners -> event -> {
			for (IRenderBossBarEvent l : listeners) {
				l.onRenderBossBar(event);
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

	public static final Event<IItemPickupEvent> ITEM_PICKUP_EVENT = createArrayBacked(
		IItemPickupEvent.class, listeners -> event -> {
			for (IItemPickupEvent listener : listeners) {
				listener.onItemPickup(event);
			}
		}
	);

	public static final Event<ISoundEvent> SOUND_EVENT = createArrayBacked(
		ISoundEvent.class, listeners -> event -> {
			for (ISoundEvent listener : listeners) {
				listener.onSound(event);
				if (event.isCanceled()) break;
			}
		}
	);

	public static final Event<IScoreboardEvent> SCOREBOARD_EVENT = createArrayBacked(
		IScoreboardEvent.class, listeners -> event -> {
			for (IScoreboardEvent listener : listeners) {
				listener.onScoreboard(event);
			}
		}
	);

	public static final Event<IUseItemOnEvent> USE_ITEM_ON_EVENT = createArrayBacked(
		IUseItemOnEvent.class, listeners -> event -> {
			for (IUseItemOnEvent listener : listeners) {
				listener.onUseItemOn(event);
			}
		}
	);

	public static final Event<IUseItemEvent> USE_ITEM_EVENT = createArrayBacked(
		IUseItemEvent.class, listeners -> event -> {
			for (IUseItemEvent listener : listeners) {
				listener.onUseItem(event);
			}
		}
	);

	public static class BlockInteractEvent {
		public final ItemStack itemStack;
		public final BlockPos pos;
		private boolean canceled = false;

		public BlockInteractEvent(ItemStack itemStack, BlockPos pos) {
			this.itemStack = itemStack;
			this.pos = pos;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class BlockUpdateEvent {
		public BlockPos pos;
		public BlockState oldState;
		public BlockState newState;

		public BlockUpdateEvent(BlockPos pos, BlockState oldState, BlockState newState) {
			this.pos = pos;
			this.oldState = oldState;
			this.newState = newState;
		}
	}

	public static class RenderBossBarEvent {
		public final BossEvent bossBar;
		private boolean canceled = false;

		public RenderBossBarEvent(BossEvent bossBar) {
			this.bossBar = bossBar;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class DropItemEvent {
		public final ItemStack stack;
		public final DropType dropType;
		public final boolean all;
		private boolean canceled = false;

		public DropItemEvent(ItemStack stack, DropType dropType, boolean all) {
			this.stack = stack;
			this.dropType = dropType;
			this.all = all;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class KeybindEvent {
		public final KeyBind keybind;
		private final boolean pressed;
		private final boolean keyDown;

		public KeybindEvent(KeyBind keybind, boolean pressed, boolean keyDown) {
			this.keybind = keybind;
			this.pressed = pressed;
			this.keyDown = keyDown;
		}

		public boolean isPressed() {
			return pressed;
		}

		public boolean isKeyDown() {
			return keyDown;
		}
	}

	public static class MessageEvent {
		public String message;
		public String chat;
		private boolean canceled = false;

		public MessageEvent(String message, String chat) {
			this.message = message;
			this.chat = chat;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class MessageSentEvent {
		public String message;
		private boolean canceled = false;

		public MessageSentEvent(String message) {
			this.message = message;
		}

		public void cancel() {
			this.canceled = true;
		}

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
			LucentEvent.TAB_COMPLETION_EVENT.invoker().onTabComplete(this);
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

	public static class RenderWorldEvent {
		public final WorldExtractionContext context;
		public final LevelRenderer handler;
		public final float partialTick;

		public RenderWorldEvent(WorldExtractionContext context, LevelRenderer handler, float partialTick) {
			this.context = context;
			this.handler = handler;
			this.partialTick = partialTick;
		}

		public float getFloat() {
			return partialTick;
		}
	}

	public static class RenderWorldLastEvent {
		public final WorldRenderContext context;
		public final LevelRenderer handler;
		public final float partialTick;

		public RenderWorldLastEvent(WorldRenderContext context, LevelRenderer handler, float partialTick) {
			this.context = context;
			this.handler = handler;
			this.partialTick = partialTick;
		}

		public float getFloat() {
			return partialTick;
		}
	}

	public static class ItemPickupEvent {
		public final ItemEntity entity;
		public final int entityId;

		public ItemPickupEvent(ItemEntity entity, int entityId) {
			this.entity = entity;
			this.entityId = entityId;
		}
	}

	public static class SoundEvent {
		public final String sound;
		public final float pitch;
		public final float volume;
		public final SoundSource category;
		public final double x;
		public final double y;
		public final double z;
		public final long seed;
		public final net.minecraft.sounds.SoundEvent underlyingEvent;
		private boolean canceled = false;

		public SoundEvent(String sound, float pitch, float volume, SoundSource category, double x, double y, double z, long seed, net.minecraft.sounds.SoundEvent underlyingEvent) {
			this.sound = sound;
			this.pitch = pitch;
			this.volume = volume;
			this.category = category;
			this.x = x;
			this.y = y;
			this.z = z;
			this.seed = seed;
			this.underlyingEvent = underlyingEvent;
		}

		public void cancel() {
			this.canceled = true;
		}

		public boolean isCanceled() {
			return canceled;
		}
	}

	public static class ScoreboardEvent {
		public final String message;

		public ScoreboardEvent(String message) {
			this.message = message;
		}
	}

	public static class UseItemOnEvent {
		public final BlockHitResult blockHitResult;
		public final InteractionHand hand;

		public UseItemOnEvent(BlockHitResult blockHitResult, InteractionHand hand) {
			this.blockHitResult = blockHitResult;
			this.hand = hand;
		}
	}

	public static class UseItemEvent {
		public final InteractionHand hand;

		public UseItemEvent(InteractionHand hand) {
			this.hand = hand;
		}
	}

}