package silence.simsool.lucent.mixin.mixins.events.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import silence.simsool.lucent.events.impl.LucentEvent;

@Mixin(KeyMapping.class)
public class MixinKeyMapping_KEY_INPUT_EVENT {

	@Inject(method = "click", at = @At("HEAD"), cancellable = true)
	private static void onKeyClick(InputConstants.Key key, CallbackInfo ci) {
		LucentEvent.KeyInputEventData event = new LucentEvent.KeyInputEventData(key);
		LucentEvent.KEY_INPUT_EVENT.invoker().onKeyInput(event);
		if (event.isCanceled()) ci.cancel();
	}

}