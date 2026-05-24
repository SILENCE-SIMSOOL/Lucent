package silence.simsool.lucent.mixin.mixins.events.input.keyboard;

import static silence.simsool.lucent.Lucent.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.KeyboardHandler;
import silence.simsool.lucent.config.ModManager;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {

	@Inject(method = "key", at = @At("HEAD"))
	private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (mc.player == null || mc.level == null) return;
		if (mc.screen == null) ModManager.handleKeyInput(key, action);
	}

}