package silence.simsool.lucent.mixin.mixins.events.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.enums.DropType;

@Mixin(MultiPlayerGameMode.class)
public abstract class MixinMultiPlayerGameMode {

	@Inject(method = "dropItem(Lnet/minecraft/client/player/LocalPlayer;Z)V", at = @At("HEAD"), cancellable = true)
	private void lucent$onDropItem(LocalPlayer player, boolean all, CallbackInfo ci) {
		ItemStack stack = player.getInventory().getSelectedItem(); if (stack.isEmpty()) return;
		LucentEvent.DropItemEvent event = new LucentEvent.DropItemEvent(stack, DropType.DEFAULT_DROP, all);
		LucentEvent.DROP_ITEM_EVENT.invoker().onDropItem(event);
		if (event.isCanceled()) ci.cancel();
	}

}