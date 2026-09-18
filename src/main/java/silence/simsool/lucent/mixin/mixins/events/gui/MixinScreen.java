package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import silence.simsool.lucent.general.utils.notification.NotificationRenderer;

@Mixin(Screen.class)
public abstract class MixinScreen {

	@Inject(method = "extractRenderState", at = @At("TAIL"), remap = false)
	private void onExtractRenderStateTail(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
		NotificationRenderer.render(graphics);
	}

}
