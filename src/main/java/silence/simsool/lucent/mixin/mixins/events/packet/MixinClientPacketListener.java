package silence.simsool.lucent.mixin.mixins.events.packet;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

//	@Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
//	private void onHandleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
//		if (mc.screen instanceof AbstractContainerScreen<?> container) {
//			GUIEvent.SlotUpdateEvent event = new GUIEvent.SlotUpdateEvent(mc.screen, packet, container.getMenu());
//			GUIEvent.SLOT_UPDATE.invoker().onSlotUpdate(event);
//		}
//	}

//	@WrapOperation(method = "handleBundlePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"))
//	private void onHandleBundlePacket(Packet<?> packet, PacketListener listener, Operation<Void> original) {
//		PacketEvent.PacketReceiveEvent event = new PacketEvent.PacketReceiveEvent(packet);
//		PacketEvent.RECEIVE.invoker().onPacketReceive(event);
//		if (event.isCanceled()) return;
//		original.call(packet, listener);
//	}

}