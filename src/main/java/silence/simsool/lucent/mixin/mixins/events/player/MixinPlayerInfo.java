package silence.simsool.lucent.mixin.mixins.events.player;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import silence.simsool.lucent.init.PremiumCosmetics;

@Mixin(PlayerInfo.class)
public abstract class MixinPlayerInfo {

	@Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
	private void onGetTabListDisplayName(CallbackInfoReturnable<Component> cir) {
		PlayerInfo info = (PlayerInfo) (Object) this;
		UUID uuid = info.getProfile().id();
		if (PremiumCosmetics.isPremium(uuid)) {
			Component current = cir.getReturnValue();
			Component original;
			if (current != null) {
				original = current;
			}
			else {
				PlayerTeam team = info.getTeam();
				original = PlayerTeam.formatNameForTeam(team, Component.literal(info.getProfile().name()));
			}
			cir.setReturnValue(PremiumCosmetics.getFormattedDisplayName(PremiumCosmetics.tabListCache, uuid, original));
		}
	}

}