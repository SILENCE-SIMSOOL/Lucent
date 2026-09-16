package silence.simsool.lucent.mixin.skija;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import silence.simsool.lucent.skija.compositor.SkijaCompositor;

@Mixin(Gui.class)
public class MixinGuiExtract {

	@Shadow
	@Final
	private GuiRenderState guiRenderState;

	@Inject(method = "extractRenderState", at = @At("HEAD"))
	private void lucent$beginHud(CallbackInfo ci) {
		SkijaCompositor.INSTANCE.beginHud();
	}

	@Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Hud;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER))
	private void lucent$markHudLayer(CallbackInfo ci) {
		SkijaCompositor.INSTANCE.markHudLayer(this.guiRenderState);
	}

}