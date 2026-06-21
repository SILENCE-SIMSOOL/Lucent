package silence.simsool.lucent.mixin.mixins.events.gui;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.enums.DropType;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinAbstractContainerScreen_GUIEvent {

	@Shadow protected int leftPos;
	@Shadow protected int topPos;
	@Shadow protected int imageWidth;
	@Shadow protected int imageHeight;
	@Shadow @Nullable protected Slot hoveredSlot;

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
		if (event.button() == 2 && this.hoveredSlot != null) {
			AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
			GUIEvent.SlotClickEvent clickEvent = new GUIEvent.SlotClickEvent(this.hoveredSlot, this.hoveredSlot.index, event.button(), ContainerInput.CLONE, screen.getMenu(), screen);
			GUIEvent.SLOT.Click.EVENT.invoker().onSlotClick(clickEvent);
			if (clickEvent.isCanceled()) {
				cir.setReturnValue(true);
			}
		}
	}

	@Inject(method = "removed", at = @At("HEAD"), cancellable = false)
	public void onRemoved(CallbackInfo ci) {
		Screen self = (Screen) (Object) this;
		AbstractContainerMenu menu = ((AbstractContainerScreen<?>) (Object) this).getMenu();
		GUIEvent.GUICloseEvent event = new GUIEvent.GUICloseEvent(self, menu);
		GUIEvent.CLOSE.EVENT.invoker().onClose(event);
		if (event.isCanceled()) ci.cancel();
	}

	@Inject(method = "extractContents", at = @At("TAIL"))
	public void onRenderContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		Screen self = (Screen) (Object) this;

		GUIEvent.CONTAINER.All.EVENT.invoker().onContainer(
			new GUIEvent.RenderContainer(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
		);

		if (self instanceof InventoryScreen) {
			GUIEvent.CONTAINER.Inventory.EVENT.invoker().onInventory(
				new GUIEvent.RenderInventory(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
			);
		}
		else {
			GUIEvent.CONTAINER.Chest.EVENT.invoker().onChest(
				new GUIEvent.RenderChest(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
			);
		}
	}

	@Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
	private void onSlotClicked(Slot slot, int slotId, int button, ContainerInput clickType, CallbackInfo ci) {
		AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;

		if (slot == null) {
			if (slotId == -999 && clickType == ContainerInput.PICKUP) {
				ItemStack carried = screen.getMenu().getCarried();
				if (!carried.isEmpty()) {
					boolean all = button == 0;
					LucentEvent.DropItemEvent event = new LucentEvent.DropItemEvent(carried, DropType.INVENTORY_CLICK_OUTSIDE, all);
					LucentEvent.DROP_ITEM_EVENT.invoker().onDropItem(event);
					if (event.isCanceled()) ci.cancel();
				}
			}
		}

		else {
			GUIEvent.SlotClickEvent event = new GUIEvent.SlotClickEvent(slot, slotId, button, clickType, screen.getMenu(), screen);
			GUIEvent.SLOT.Click.EVENT.invoker().onSlotClick(event);
			if (event.isCanceled()) ci.cancel();
		}
	}

	@WrapOperation(method = "extractSlots", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;extractSlot(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/inventory/Slot;II)V"))
	private void onRenderSlot(AbstractContainerScreen<?> instance, GuiGraphicsExtractor guiGraphics, Slot slot, int i, int j, Operation<Void> original) {
		GUIEvent.RenderSlotPreEvent preEvent = new GUIEvent.RenderSlotPreEvent(slot, guiGraphics, instance);
		GUIEvent.SLOT.RenderPre.EVENT.invoker().onSlotRenderPre(preEvent);
		if (preEvent.isCanceled()) return;
		original.call(instance, guiGraphics, slot, i, j);
		GUIEvent.RenderSlotPostEvent postEvent = new GUIEvent.RenderSlotPostEvent(slot, guiGraphics, instance);
		GUIEvent.SLOT.RenderPost.EVENT.invoker().onSlotRenderPost(postEvent);
	}

}