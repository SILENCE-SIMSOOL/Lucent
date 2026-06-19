package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.utils.useful.UChat;

@Mixin(ChatComponent.class)
public class MixinChatComponent {

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
	private void onAddMessage(Component component, CallbackInfo ci) {
		if (component != null) {
			String chat = component.getString();
			String format = UChat.componentToLegacy(component);

			LucentEvent.ModMessageEvent event = new LucentEvent.ModMessageEvent(chat, format);
			LucentEvent.MOD_MESSAGE_EVENT.invoker().onMessage(event);

			if (event.isCanceled()) {
				ci.cancel();
			}
		}
	}

}