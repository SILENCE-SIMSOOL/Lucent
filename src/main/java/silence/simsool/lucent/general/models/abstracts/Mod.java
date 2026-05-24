package silence.simsool.lucent.general.models.abstracts;

import silence.simsool.lucent.events.impl.DropItemEvent;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.MouseEvent;
import silence.simsool.lucent.events.impl.PacketEvent;

public abstract class Mod {
	public final String name;
	public final String description;
	public final String category;
	public final String searchTags;
	public final String icon;
	public boolean isEnabled = false; // 모드 자체의 활성화 여부

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
	public void onChat(LucentEvent.MessageEvent event) {}
	public void onActionBar(LucentEvent.MessageEvent event) {}
	public void onServerJoin() {}
	public void onServerDisconnect() {}
	public void onWorldLoad() {}
	public void onBlockUpdate(LucentEvent.BlockUpdateEventData event) {}
	public void onExtract(LucentEvent.RenderExtractEventData event) {}
	public void onRenderLast(LucentEvent.RenderLastEventData event) {}
	public void onBlockInteract(LucentEvent.BlockInteractEventData event) {}
	public void onKeyInput(LucentEvent.KeyInputEventData event) {}
	public void onMessageSent(LucentEvent.MessageSentEventData event) {}
	public void onTabComplete(LucentEvent.TabCompletionEvent event) {}
	public void onBossBarRender(LucentEvent.BossBarRenderEventData event) {}
	public void onRenderLivingPre(LucentEvent.RenderLivingPreEventData event) {}

	public void onRenderHUD(GUIEvent.RenderHUD event) {}
	public void onGUIOpen(GUIEvent.OPEN event) {}
	public void onGUIClose(GUIEvent.CLOSE event) {}
	public void onGUIClick(GUIEvent.CLICK event) {}
	public void onGUIKey(GUIEvent.KEY event) {}
	public void onSlotClick(GUIEvent.SLOT.Click event) {}
	public void onSlotRender(GUIEvent.SLOT.Render event) {}
	public void onRenderContainer(GUIEvent.CONTAINER.All event) {}
	public void onRenderInventory(GUIEvent.CONTAINER.Inventory event) {}
	public void onRenderChest(GUIEvent.CONTAINER.Chest event) {}

	public void onDropItem(DropItemEvent.DropItem event) {}

	public void onMouseClick(MouseEvent.ClickEvent event) {}

	public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {}
	public void onPacketSend(PacketEvent.PacketSendEvent event) {}
}