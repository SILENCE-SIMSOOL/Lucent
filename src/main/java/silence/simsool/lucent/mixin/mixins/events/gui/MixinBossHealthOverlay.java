package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.world.BossEvent;
import silence.simsool.lucent.events.impl.LucentEvent;

@Mixin(BossHealthOverlay.class)
public abstract class MixinBossHealthOverlay {

	@Inject(method = "drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V", at = @At("HEAD"), cancellable = true)
	private void onDrawBar(GuiGraphics guiGraphics, int i, int j, BossEvent bossBar, CallbackInfo ci) {
		LucentEvent.BossBarRenderEventData event = new LucentEvent.BossBarRenderEventData(bossBar);
		LucentEvent.BOSS_BAR_RENDER_EVENT.invoker().onBossBarRender(event);
		if (event.isCanceled()) ci.cancel();
	}

}
