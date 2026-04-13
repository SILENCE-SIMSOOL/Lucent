package silence.simsool.lucent.mixin;

import static silence.simsool.lucent.Lucent.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.api.LucentAPI;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

@Mixin(KeyMapping.class)
public class MixinKeyBinding {

	@Inject(method = "click", at = @At("HEAD"), cancellable = true)
	private static void onKeyPressed(InputConstants.Key key, CallbackInfo ci) {
		if (KeyBindingHelper.getBoundKeyOf(Lucent.CONFIG_KEY).equals(key)) {
			mc.schedule(() -> mc.setScreen(LucentAPI.createEditHUDScreen(Lucent.config)));
		}
	}

}