package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.utils.useful.UChat;

@Mixin(ChatComponent.class)
public class MixinChatComponent {

	@Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("HEAD"), cancellable = true)
	private void onAddMessage(Component component, MessageSignature messageSignature, GuiMessageSource guiMessageSource, GuiMessageTag guiMessageTag, CallbackInfo ci) {
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