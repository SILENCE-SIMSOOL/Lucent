package silence.simsool.lucent.mixin.mixins.events.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import silence.simsool.lucent.events.impl.GUIEvent;
import silence.simsool.lucent.general.models.data.events.guievent.ItemTooltipEvent;

@Mixin(ItemStack.class)
public class MixinItemStack {

	@Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
	private void onGetTooltipLines(TooltipContext context, @Nullable Player player, TooltipFlag flags, CallbackInfoReturnable<List<Component>> cir) {
		List<Component> list = cir.getReturnValue();
		if (list != null) {
			ItemTooltipEvent event = new ItemTooltipEvent((ItemStack) (Object) this, list, context, flags, player);
			GUIEvent.Tooltip.EVENT.invoker().onTooltip(event);
			if (event.isCanceled()) {
				cir.setReturnValue(List.of());
			}
		}
	}

}