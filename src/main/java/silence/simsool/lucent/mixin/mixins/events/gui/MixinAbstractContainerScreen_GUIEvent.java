package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import silence.simsool.lucent.events.impl.GUIEvent;

@Mixin(AbstractContainerScreen.class)
public class MixinAbstractContainerScreen_GUIEvent {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		if (GUIEvent.RENDER.invoker().onGuiRender((Screen) (Object) this, guiGraphics, mouseX, mouseY)) ci.cancel();
	}

	@Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
	private void onRenderSlot(GuiGraphics guiGraphics, Slot slot, int i, int j, CallbackInfo ci) {
		if (GUIEvent.RENDER_SLOT.invoker().onRenderSlot((Screen) (Object) this, guiGraphics, slot)) ci.cancel();
	}

	@Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
	public void onSlotClicked(Slot slot, int slotId, int button, ClickType actionType, CallbackInfo ci) {
		if (GUIEvent.SLOT_CLICK.invoker().onSlotClick((Screen) (Object) this, slotId, button)) ci.cancel();
	}

	@Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
	public void onRenderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
		if (GUIEvent.DRAW_TOOLTIP.invoker().onDrawTooltip((Screen) (Object) this, guiGraphics, mouseX, mouseY)) ci.cancel();
	}

}