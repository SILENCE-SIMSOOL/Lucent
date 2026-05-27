package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.enums.DropType;
import silence.simsool.lucent.general.models.data.events.guievent.*;
import silence.simsool.lucent.general.models.data.events.lucentevent.DropItemEvent;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen_GUIEvent {

	@Shadow protected int leftPos;
	@Shadow protected int topPos;
	@Shadow protected int imageWidth;
	@Shadow protected int imageHeight;

	@Inject(method = "removed", at = @At("HEAD"), cancellable = false)
	public void onRemoved(CallbackInfo ci) {
		Screen self = (Screen) (Object) this;
		AbstractContainerMenu menu = ((AbstractContainerScreen<?>) (Object) this).getMenu();
		GUICloseEvent event = new GUICloseEvent(self, menu);
		GUIEvent.CLOSE.EVENT.invoker().onClose(event);
		if (event.isCanceled()) ci.cancel();
	}

	@Inject(method = "renderContents", at = @At("TAIL"))
	public void onRenderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		Screen self = (Screen) (Object) this;

		GUIEvent.CONTAINER.All.EVENT.invoker().onContainer(
			new GUIContainerAllEvent(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
		);

		if (self instanceof InventoryScreen) {
			GUIEvent.CONTAINER.Inventory.EVENT.invoker().onInventory(
				new GUIContainerInventoryEvent(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
			);
		}
		else {
			GUIEvent.CONTAINER.Chest.EVENT.invoker().onChest(
				new GUIContainerChestEvent(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
			);
		}
	}

	@Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
	private void onSlotClicked(Slot slot, int slotId, int button, ClickType clickType, CallbackInfo ci) {
		AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;

		if (slot == null) {
			if (slotId == -999 && clickType == ClickType.PICKUP) {
				ItemStack carried = screen.getMenu().getCarried();
				if (!carried.isEmpty()) {
					boolean all = button == 0;
					DropItemEvent event = new DropItemEvent(carried, DropType.INVENTORY_CLICK_OUTSIDE, all);
					LucentEvent.DROP_ITEM_EVENT.invoker().onDropItem(event);
					if (event.isCanceled()) ci.cancel();
				}
			}
		}

		else {
			GUISlotClickEvent event = new GUISlotClickEvent(slot, slotId, button, clickType, screen.getMenu(), screen);
			GUIEvent.SLOT.Click.EVENT.invoker().onSlotClick(event);
			if (event.isCanceled()) ci.cancel();
		}
	}

}