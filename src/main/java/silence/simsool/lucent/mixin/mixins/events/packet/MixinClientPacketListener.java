package silence.simsool.lucent.mixin.mixins.events.packet;

import static silence.simsool.lucent.Lucent.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.PacketEvent;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

	/** GUI 슬롯 업데이트 이벤트 */
	@Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
	private void onHandleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		if (mc.screen instanceof AbstractContainerScreen<?> container) GUIEvent.SLOT_UPDATE.invoker().onSlotUpdate(mc.screen, packet, container.getMenu());
	}

	/** 번들 패킷 내 수신 패킷 이벤트 */
	@WrapOperation(method = "handleBundlePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"))
	private void onHandleBundlePacket(Packet<?> packet, PacketListener listener, Operation<Void> original) {
		if (PacketEvent.RECEIVE.invoker().onPacketReceive(packet)) return;
		original.call(packet, listener);
	}

}