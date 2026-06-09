package silence.simsool.lucent.general.models.abstracts;

import silence.simsool.lucent.events.impl.EntityEvent;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.InputEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.PacketEvent;

public abstract class Mod {
	public final String name;
	public final String description;
	public final String category;
	public final String searchTags;
	public final String icon;
	public boolean isEnabled = false;

	public Mod(String name, String description, String category, String searchTags, String icon) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.searchTags = searchTags;
		this.icon = icon;
	}

	public void onInitFinished() {}
	public void onResourcesReady() {}
	public void onTick() {}
	public void onMediumTick() {}
	public void onHighTick() {}
	public void onEverySecond() {}
	public void onServerTick() {}
	public void onServerJoin() {}
	public void onServerJoinMod() {}
	public void onServerDisconnect() {}
	public void onServerDisconnectMod() {}
	public void onInitFinishedMod() {}
	public void onResourcesReadyMod() {}
	public void onWorldLoad() {}
	public void onWorldLoadMod() {}
	public void onChat(LucentEvent.MessageEvent event) {}
	public void onActionBar(LucentEvent.MessageEvent event) {}
	public void onBlockUpdate(LucentEvent.BlockUpdateEvent event) {}
	public void onRenderWorld(LucentEvent.RenderWorldEvent event) {}
	public void onRenderWorldLast(LucentEvent.RenderWorldLastEvent event) {}
	public void onBlockInteract(LucentEvent.BlockInteractEvent event) {}
	public void onMessageSent(LucentEvent.MessageSentEvent event) {}
	public void onTabComplete(LucentEvent.TabCompletionEvent event) {}
	public void onRenderBossBar(LucentEvent.RenderBossBarEvent event) {}
	public void onKeybind(LucentEvent.KeybindEvent event) {}
	public void onDropItem(LucentEvent.DropItemEvent event) {}

	public void onMouseInput(InputEvent.MouseInputEvent event) {}
	public void onKeyInput(InputEvent.KeyInputEvent event) {}

	public void onRenderHUD(GUIEvent.RenderHUD event) {}
	public void onGUIOpen(GUIEvent.GUIOpenEvent event) {}
	public void onGUIClose(GUIEvent.GUICloseEvent event) {}
	public void onGUIClick(GUIEvent.GUIClickEvent event) {}
	public void onGUIKey(GUIEvent.GUIKeyEvent event) {}
	public void onSlotClick(GUIEvent.SlotClickEvent event) {}
	public void onSlotRenderPre(GUIEvent.RenderSlotPreEvent event) {}
	public void onSlotRenderPost(GUIEvent.RenderSlotPostEvent event) {}
	public void onSlotUpdate(GUIEvent.SlotUpdateEvent event) {}
	public void onHotbarRenderPre(GUIEvent.RenderHotbarPreEvent event) {}
	public void onHotbarRenderPost(GUIEvent.RenderHotbarPostEvent event) {}
	public void onRenderContainer(GUIEvent.RenderContainer event) {}
	public void onRenderInventory(GUIEvent.RenderInventory event) {}
	public void onRenderChest(GUIEvent.RenderChest event) {}
	public void onRenderTooltip(GUIEvent.TooltipEvent event) {}

	public void onRenderEntity(EntityEvent.RenderEntityAllowEvent event) {}
	public void onRenderEntityColor(EntityEvent.RenderEntityColorEvent event) {}
	public void onNameChange(EntityEvent.NameChangeEvent event) {}
	public void onExtractRenderStatePre(EntityEvent.ExtractRenderStatePre event) {}
	public void onExtractRenderStatePost(EntityEvent.ExtractRenderStatePost event) {}
	public void onRenderEntityPre(EntityEvent.RenderEntityPreEvent event) {}
	public void onEntityJoin(EntityEvent.EntityJoinEvent event) {}
	public void onEntityLeave(EntityEvent.EntityLeaveEvent event) {}
	public void onEntityDeath(EntityEvent.EntityDeathEvent event) {}
	public void onEntityData(EntityEvent.EntityDataEvent event) {}
	public void onEntityEquipment(EntityEvent.EntityEquipmentEvent event) {}
	public void onEntityInteract(EntityEvent.EntityInteractEvent event) {}

	public void onReceivePacket(PacketEvent.ReceiveEvent event) {}
	public void onSendPacket(PacketEvent.SendEvent event) {}

	public void onItemPickup(LucentEvent.ItemPickupEvent event) {}
	public void onSound(LucentEvent.SoundEvent event) {}
	public void onScoreboard(LucentEvent.ScoreboardEvent event) {}
	public void onUseItemOn(LucentEvent.UseItemOnEvent event) {}
	public void onUseItem(LucentEvent.UseItemEvent event) {}
}