package silence.simsool.lucent.mixin.mixins.events.input.mouse;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.events.impl.InputEvent;
import silence.simsool.lucent.general.utils.useful.UMouse;
import silence.simsool.lucent.general.utils.useful.UScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

	@Inject(method = "onButton", at = @At("HEAD"), cancellable = true)
	private void lucent$onButton(long handle, MouseButtonInfo rawButtonInfo, int action, CallbackInfo ci) {
		if (mc.player == null || mc.level == null) return;
		int lucentButton = UMouse.toLucentButton(rawButtonInfo.button());
		UMouse.setButtonPressed(lucentButton, action != 0);
		if (UScreen.isScreenClose()) ModManager.handleMouseInput(lucentButton, action);
		InputEvent.MouseInputEvent event = new InputEvent.MouseInputEvent(lucentButton, action);
		InputEvent.MOUSE.invoker().onMouseInput(event);
		if (event.isCanceled()) ci.cancel();
	}
}