package silence.simsool.lucent.mixin.mixins.events.packet;

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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.events.impl.PacketEvent;
import silence.simsool.lucent.general.utils.useful.UScreen;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

	@Inject(method = "handleContainerSetSlot", at = @At("TAIL"))
	private void onHandleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
		if (UScreen.getScreen() instanceof AbstractContainerScreen<?> container) {
			GUIEvent.SlotUpdateEvent event = new GUIEvent.SlotUpdateEvent(UScreen.getScreen(), packet, container.getMenu());
			GUIEvent.SLOT.Update.EVENT.invoker().onSlotUpdate(event);
		}
	}

	@WrapOperation(method = "handleBundlePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/Packet;handle(Lnet/minecraft/network/PacketListener;)V"))
	private void onHandleBundlePacket(Packet<?> packet, PacketListener listener, Operation<Void> original) {
		PacketEvent.ReceiveEvent event = new PacketEvent.ReceiveEvent(packet);
		PacketEvent.RECEIVE.invoker().onReceivePacket(event);
		if (event.isCanceled()) return;
		original.call(packet, listener);
	}

	@WrapOperation(method = "handleTakeItemEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;getItem()Lnet/minecraft/world/item/ItemStack;"))
	private ItemStack onItemPickupEventNormal(ItemEntity instance, Operation<ItemStack> original) {
		ItemStack stack = original.call(instance);
		LucentEvent.ITEM_PICKUP_EVENT.invoker().onItemPickup(new LucentEvent.ItemPickupEvent(instance, instance.getId()));
		return stack;
	}

}