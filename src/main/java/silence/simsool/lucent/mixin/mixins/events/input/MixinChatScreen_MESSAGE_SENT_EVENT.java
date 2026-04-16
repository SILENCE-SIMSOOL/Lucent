package silence.simsool.lucent.mixin.mixins.events.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.ChatScreen;
import silence.simsool.lucent.events.impl.LucentEvent;

@Mixin(ChatScreen.class)
public class MixinChatScreen_MESSAGE_SENT_EVENT {

	@Inject(method = "handleChatInput", at = @At("HEAD"), cancellable = true)
	private void onHandleChatInput(String message, boolean addToHistory, CallbackInfo ci) {
		if (LucentEvent.MESSAGE_SENT_EVENT.invoker().onMessageSent(message)) ci.cancel();
	}

}