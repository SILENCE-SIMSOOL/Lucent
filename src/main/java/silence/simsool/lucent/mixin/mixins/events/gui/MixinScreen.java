package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.general.utils.notification.NotificationRenderer;

@Mixin(Screen.class)
public abstract class MixinScreen {

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderTail(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		NotificationRenderer.render(guiGraphics);
	}

}
