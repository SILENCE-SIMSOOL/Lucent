package silence.simsool.lucent.mixin.mixins.events.packet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.PacketEvent;

@Mixin(value = Connection.class, priority = 999)
public abstract class MixinConnection {

	@Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V"), cancellable = true)
	private void onChannelRead0(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
		if (packet instanceof ClientboundPingPacket pingPacket && pingPacket.getId() != 0) LucentEvent.SERVER_TICK_EVENT.invoker().onTick();
		PacketEvent.PacketReceiveEvent event = new PacketEvent.PacketReceiveEvent(packet);
		PacketEvent.RECEIVE.invoker().onPacketReceive(event);
		if (event.isCanceled()) ci.cancel();
	}

	@Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
	private void onSendPacket(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
		PacketEvent.PacketSendEvent event = new PacketEvent.PacketSendEvent(packet);
		PacketEvent.SEND.invoker().onPacketSend(event);
		if (event.isCanceled()) ci.cancel();
	}

}