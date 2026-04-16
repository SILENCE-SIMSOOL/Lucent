package silence.simsool.lucent.mixin.mixins.events.packet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import silence.simsool.lucent.events.impl.PacketEvent;

@Mixin(value = Connection.class, priority = 500)
public abstract class MixinConnection_PacketEvent {

	@Inject(
		method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V"),
		cancellable = true
	)
	private void onChannelRead0(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
		if (PacketEvent.RECEIVE.invoker().onPacketReceive(packet)) ci.cancel();
	}

	@Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
	private void onSendPacket(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
		if (PacketEvent.SEND.invoker().onPacketSend(packet)) ci.cancel();
	}

}