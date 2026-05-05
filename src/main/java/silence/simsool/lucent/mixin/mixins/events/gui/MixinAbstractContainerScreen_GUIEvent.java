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
import silence.simsool.lucent.events.impl.GUIEvent;

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
		GUIEvent.Close event = new GUIEvent.Close(self, menu);
		GUIEvent.Close.EVENT.invoker().onClose(event);
		if (event.isCanceled()) ci.cancel();
	}

	@Inject(method = "renderContents", at = @At("TAIL"))
	public void onRenderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		Screen self = (Screen) (Object) this;

		GUIEvent.Container.All.EVENT.invoker().onContainer(
			new GUIEvent.Container.All(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
		);

		if (self instanceof InventoryScreen) {
			GUIEvent.Container.Inventory.EVENT.invoker().onInventory(
				new GUIEvent.Container.Inventory(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
			);
		}
		else {
			GUIEvent.Container.Chest.EVENT.invoker().onChest(
				new GUIEvent.Container.Chest(guiGraphics, self, mouseX, mouseY, leftPos, topPos, imageWidth, imageHeight)
			);
		}
	}

}