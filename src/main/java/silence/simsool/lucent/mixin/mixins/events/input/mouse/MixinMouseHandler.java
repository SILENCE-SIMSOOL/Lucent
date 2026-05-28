package silence.simsool.lucent.mixin.mixins.events.input.mouse;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.events.impl.MouseEvent;
import silence.simsool.lucent.general.models.data.events.mouseevent.ClickEvent;
import silence.simsool.lucent.general.utils.useful.UScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

	@Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
	private void onButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
		if (mc.player == null || mc.level == null) return;
		if (UScreen.isScreenClose()) ModManager.handleMouseInput(rawButtonInfo.button(), action);
		ClickEvent event = new ClickEvent(rawButtonInfo.button(), action);
		MouseEvent.CLICK.invoker().onMouseClick(event);
		if (event.isCanceled()) ci.cancel();
	}

}