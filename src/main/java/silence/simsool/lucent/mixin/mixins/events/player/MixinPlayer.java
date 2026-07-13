package silence.simsool.lucent.mixin.mixins.events.player;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import silence.simsool.lucent.init.PremiumCosmetics;

@Mixin(Player.class)
public abstract class MixinPlayer {

	@Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
	private void onGetDisplayName(CallbackInfoReturnable<Component> cir) {
		Player player = (Player) (Object) this;
		UUID uuid = player.getUUID();
		if (PremiumCosmetics.isPremium(uuid)) {
			Component original = cir.getReturnValue();
			if (original != null) {
				cir.setReturnValue(PremiumCosmetics.getFormattedDisplayName(PremiumCosmetics.nameTagCache, uuid, original));
			}
		}
	}

}