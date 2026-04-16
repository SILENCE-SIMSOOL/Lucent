package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.events.impl.ScreenEvent;

@Mixin(Screen.class)
public abstract class MixinScreen_ScreenEvent {

	@Inject(method = "renderWithTooltipAndSubtitles", at = @At("HEAD"), cancellable = true)
	private void onRenderWithTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
		if (ScreenEvent.RENDER.invoker().onScreenRender((Screen) (Object) this, guiGraphics, mouseX, mouseY)) ci.cancel();
	}

}