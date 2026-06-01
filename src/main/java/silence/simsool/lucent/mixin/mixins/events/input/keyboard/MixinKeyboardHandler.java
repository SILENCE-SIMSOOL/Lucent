package silence.simsool.lucent.mixin.mixins.events.input.keyboard;

import static silence.simsool.lucent.Lucent.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.events.impl.InputEvent;
import silence.simsool.lucent.general.utils.useful.UScreen;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {

	@Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
	private void onKey(long window, int action, KeyEvent event, CallbackInfo ci) {
		if (mc.player == null || mc.level == null) return;
		if (UScreen.isScreenClose()) {
			ModManager.handleKeyInput(event.key(), action);
			if (action == 1 || action == 0) {
				boolean state = (action == 1);
				InputEvent.KeyInputEvent keyEvent = new InputEvent.KeyInputEvent(event, state);
				InputEvent.KEY.invoker().onKeyInput(keyEvent);
				if (keyEvent.isCanceled()) ci.cancel();
			}
		}
	}

}