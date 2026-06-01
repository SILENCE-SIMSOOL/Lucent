package silence.simsool.lucent.mixin.mixins.events.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.enums.DropType;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {

	@Inject(method = "drop", at = @At("HEAD"), cancellable = true)
	private void onDrop(boolean all, CallbackInfoReturnable<Boolean> cir) {
		LocalPlayer player = (LocalPlayer) (Object) this;
		ItemStack stack = player.getInventory().getSelectedItem();

		if (stack != null && !stack.isEmpty()) {
			LucentEvent.DropItemEvent event = new LucentEvent.DropItemEvent(stack, DropType.DEFAULT_DROP, all);
			LucentEvent.DROP_ITEM_EVENT.invoker().onDropItem(event);
			if (event.isCanceled()) cir.setReturnValue(false);
		}
	}

}