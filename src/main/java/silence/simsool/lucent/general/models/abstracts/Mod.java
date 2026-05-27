package silence.simsool.lucent.general.models.abstracts;

import silence.simsool.lucent.general.models.data.events.guievent.*;
import silence.simsool.lucent.general.models.data.events.lucentevent.*;
import silence.simsool.lucent.general.models.data.events.mouseevent.*;
import silence.simsool.lucent.general.models.data.events.packetevent.*;
import silence.simsool.lucent.general.models.data.events.entityevent.*;

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
	public void onChat(MessageEvent event) {}
	public void onActionBar(MessageEvent event) {}
	public void onServerJoin() {}
	public void onServerDisconnect() {}
	public void onWorldLoad() {}
	public void onBlockUpdate(BlockUpdateEvent event) {}
	public void onWorldRender(WorldRenderEvent event) {}
	public void onWorldRenderLast(WorldRenderLastEvent event) {}
	public void onBlockInteract(BlockInteractEvent event) {}
	public void onKeyInput(KeyInputEvent event) {}
	public void onMessageSent(MessageSentEvent event) {}
	public void onTabComplete(TabCompletionEvent event) {}
	public void onBossBarRender(BossBarRenderEvent event) {}
	public void onRenderLivingPre(RenderLivingPreEvent event) {}
	public void onRenderEntity(RenderEntityEvent event) {}
	public void onKeybind(KeybindEvent event) {}

	public void onRenderHUD(RenderHUD event) {}
	public void onGUIOpen(GUIOpenEvent event) {}
	public void onGUIClose(GUICloseEvent event) {}
	public void onGUIClick(GUIClickEvent event) {}
	public void onGUIKey(GUIKeyEvent event) {}
	public void onSlotClick(GUISlotClickEvent event) {}
	public void onSlotRender(GUISlotRenderEvent event) {}
	public void onRenderContainer(GUIContainerAllEvent event) {}
	public void onRenderInventory(GUIContainerInventoryEvent event) {}
	public void onRenderChest(GUIContainerChestEvent event) {}

	public void onDropItem(DropItemEvent event) {}

	public void onMouseClick(ClickEvent event) {}

	public void onPacketReceive(PacketReceiveEvent event) {}
	public void onPacketSend(PacketSendEvent event) {}
}