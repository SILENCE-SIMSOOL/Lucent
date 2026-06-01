package silence.simsool.lucent.events;

import static silence.simsool.lucent.Lucent.mc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.events.impl.EntityEvent;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.PacketEvent;
import silence.simsool.lucent.general.enums.DropType;
import silence.simsool.lucent.general.utils.Pair;
import silence.simsool.lucent.general.utils.useful.UChat;

public class LucentEventRegister {

	private static int tickCounter = 0;
	private static final Map<Integer, EntityType<?>> entityTypes = new HashMap<>();
	private static final Map<Integer, net.minecraft.world.phys.Vec3> entityPos = new HashMap<>();

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

		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
			entityTypes.clear();
			entityPos.clear();
			LucentEvent.WORLD_LOAD_EVENT.invoker().onWorldLoad();
		});

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

		WorldRenderEvents.END_EXTRACTION.register(context -> {
			if (mc.level == null || mc.player == null) return;
			float partialTick = context.tickCounter().getGameTimeDeltaPartialTick(false);
			LucentEvent.WORLD_RENDER.invoker().onRenderWorld(new LucentEvent.RenderWorldEvent(context, context.worldRenderer(), partialTick));
		});

		WorldRenderEvents.END_MAIN.register(context -> {
			if (mc.level == null || mc.player == null) return;
			float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
			LucentEvent.WORLD_RENDER_LAST.invoker().onRenderWorldLast(new LucentEvent.RenderWorldLastEvent(context, context.worldRenderer(), partialTick));
		});

		// ───────────────────────────── GUI Screen ─────────────────────────────

		HudElementRegistry.attachElementBefore(VanillaHudElements.SLEEP, Identifier.fromNamespaceAndPath(Lucent.ID, "hud_element"), (graphics, tickDelta) -> {
			if (mc.options.hideGui || mc.level == null || mc.player == null) return;
			GUIEvent.RenderHUD.EVENT.invoker().onRenderHUD(new GUIEvent.RenderHUD(graphics));
		});

		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (mc.level == null || mc.player == null) return;

			if (screen != null) GUIEvent.OPEN.EVENT.invoker().onOpen(new GUIEvent.GUIOpenEvent(screen));

			ScreenMouseEvents.allowMouseClick(screen).register((s, click) -> {
				GUIEvent.GUIClickEvent event = new GUIEvent.GUIClickEvent(click.x(), click.y(), click.button(), true, s);
				GUIEvent.CLICK.EVENT.invoker().onClick(event);
				return !event.isCanceled();
			});

			ScreenMouseEvents.allowMouseRelease(screen).register((s, click) -> {
				GUIEvent.GUIClickEvent event = new GUIEvent.GUIClickEvent(click.x(), click.y(), click.button(), false, s);
				GUIEvent.CLICK.EVENT.invoker().onClick(event);
				return !event.isCanceled();
			});

			ScreenKeyboardEvents.allowKeyPress(screen).register((s, keyInput) -> {
				String keyName = GLFW.glfwGetKeyName(keyInput.key(), keyInput.scancode());
				char charTyped = (keyName != null && !keyName.isEmpty()) ? keyName.charAt(0) : '\u0000';
				GUIEvent.GUIKeyEvent event = new GUIEvent.GUIKeyEvent(keyName, keyInput.key(), charTyped, keyInput.scancode(), s);
				GUIEvent.KEY.EVENT.invoker().onKey(event);
				return !event.isCanceled();
			});
		});

		// ───────────────────────────── Drop Item ──────────────────────────────

		GUIEvent.SLOT.Click.EVENT.register((event) -> {
			if (event.actionType == ClickType.THROW && event.slot.hasItem()) {
				LucentEvent.DropItemEvent dropEvent = new LucentEvent.DropItemEvent(event.slot.getItem(), DropType.INVENTORY_KEY_DROP, event.button == 1);
				LucentEvent.DROP_ITEM_EVENT.invoker().onDropItem(dropEvent);
				if (dropEvent.isCanceled()) event.cancel();
			}
		});

		// ──────────────────────── Entity / Name Change ────────────────────────

		PacketEvent.RECEIVE.register(event -> {
			if (mc.player == null || mc.level == null) return;

			if (event.packet instanceof ClientboundAddEntityPacket packet) {
				entityTypes.put(packet.getId(), packet.getType());
				entityPos.put(packet.getId(), new Vec3(packet.getX(), packet.getY(), packet.getZ()));
				return;
			}

			if (event.packet instanceof ClientboundSetEntityDataPacket packet) {
				int id = packet.id();
				EntityType<?> type = entityTypes.get(id); if (type == null) return;
				List<SynchedEntityData.DataValue<?>> data = packet.packedItems();

				EntityEvent.ENTITY_DATA_EVENT.invoker().onEntityData(new EntityEvent.EntityDataEvent(id, type, data));

				Component nameText = getNameFromData(data);
				if (nameText != null) {
					EntityEvent.NAME_CHANGE_EVENT.invoker().onNameChange(
						new EntityEvent.NameChangeEvent(id, type, nameText, nameText.getString())
					);
				}
				return;
			}

			if (event.packet instanceof ClientboundSetEquipmentPacket packet) {
				int id = packet.getEntity();
				EntityType<?> type = entityTypes.get(id);
				if (type != null) {
					Vec3 pos = entityPos.get(id);
					if (pos != null) {
						List<Pair<EquipmentSlot, ItemStack>> slots = new ArrayList<>();
						for (com.mojang.datafixers.util.Pair<EquipmentSlot, ItemStack> p : packet.getSlots()) {
							slots.add(new Pair<>(p.getFirst(), p.getSecond()));
						}
						EntityEvent.ENTITY_EQUIPMENT_EVENT.invoker().onEntityEquipment(new EntityEvent.EntityEquipmentEvent(id, type, pos, slots));
					}
				}
				return;
			}

			if (event.packet instanceof ClientboundSoundPacket packet) {
				String soundStr = packet.getSound().value().location().toString();
				if (!soundStr.isEmpty()) {
					LucentEvent.SoundEvent soundEvent = new LucentEvent.SoundEvent(
						soundStr,
						packet.getPitch(),
						packet.getVolume(),
						packet.getSource(),
						packet.getX(), packet.getY(), packet.getZ(),
						packet.getSeed(),
						packet.getSound().value()
					);
					LucentEvent.SOUND_EVENT.invoker().onSound(soundEvent);
					if (soundEvent.isCanceled()) event.cancel();
				}
				return;
			}

			if (event.packet instanceof ClientboundSetPlayerTeamPacket packet) {
				packet.getParameters().ifPresent(parameters -> {
					String prefix = parameters.getPlayerPrefix().getString();
					String suffix = parameters.getPlayerSuffix().getString();
					if (!prefix.isEmpty()) {
						String cleanMsg = UChat.cleanColor(prefix + suffix.trim());
						LucentEvent.SCOREBOARD_EVENT.invoker().onScoreboard(new LucentEvent.ScoreboardEvent(cleanMsg));
					}
				});
				return;
			}
		});

		PacketEvent.SEND.register(event -> {
			if (event.packet instanceof net.minecraft.network.protocol.game.ServerboundUseItemOnPacket packet) {
				LucentEvent.USE_ITEM_ON_EVENT.invoker().onUseItemOn(new LucentEvent.UseItemOnEvent(packet.getHitResult(), packet.getHand()));
			}
			if (event.packet instanceof net.minecraft.network.protocol.game.ServerboundUseItemPacket packet) {
				LucentEvent.USE_ITEM_EVENT.invoker().onUseItem(new LucentEvent.UseItemEvent(packet.getHand()));
			}
		});

		// ────────────────── EntityEvent - Entity Join Leave ───────────────────
		ClientEntityEvents.ENTITY_LOAD.register((entity, client) -> {
			if (mc.player == null || mc.level == null) return;
			EntityEvent.ENTITY_JOIN_EVENT.invoker().onEntityJoin(new EntityEvent.EntityJoinEvent(entity));
		});

		ClientEntityEvents.ENTITY_UNLOAD.register((entity, client) -> {
			if (mc.player == null || mc.level == null) return;
			EntityEvent.ENTITY_LEAVE_EVENT.invoker().onEntityLeave(new EntityEvent.EntityLeaveEvent(entity));
		});
	}

	private static Component getNameFromData(List<SynchedEntityData.DataValue<?>> list) {
		int size = list.size();
		int idx = switch (size) {
			case 8  -> 7;
			case 9  -> 8;
			case 16 -> 10;
			case 17 -> 10;
			case 19 -> 11;
			case 22 -> 11;
			default -> -1;
		};

		SynchedEntityData.DataValue<?> entry = null;
		if (idx >= 0 && list.get(idx).id() == 2) {
			entry = list.get(idx);
		} else {
			for (SynchedEntityData.DataValue<?> v : list) {
				if (v.id() == 2) {
					entry = v;
					break;
				}
			}
		}
		if (entry == null) return null;
		Object value = entry.value();
		if (!(value instanceof Optional<?> opt)) return null;
		return opt.isPresent() ? (Component) opt.get() : null;
	}

}