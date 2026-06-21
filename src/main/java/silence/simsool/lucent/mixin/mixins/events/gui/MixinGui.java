package silence.simsool.lucent.mixin.mixins.events.gui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.events.impl.GUIEvent;

@Mixin(Hud.class)
public class MixinGui {

	@Inject(method = "extractSlot", at = @At("HEAD"), cancellable = true)
	private void onRenderHotbarSlot(GuiGraphicsExtractor guiGraphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int k, CallbackInfo ci) {
		GUIEvent.RenderHotbarPreEvent event = new GUIEvent.RenderHotbarPreEvent(itemStack, i, j, guiGraphics);
		GUIEvent.SLOT.RenderHotbarPre.EVENT.invoker().onHotbarRenderPre(event);
		if (event.isCanceled())
			ci.cancel();
	}

	@Inject(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", shift = At.Shift.AFTER))
	private void onPostRenderHotbarSlot(GuiGraphicsExtractor guiGraphics, int i, int j, DeltaTracker deltaTracker, Player player, ItemStack itemStack, int k, CallbackInfo ci) {
		GUIEvent.RenderHotbarPostEvent event = new GUIEvent.RenderHotbarPostEvent(itemStack, i, j, guiGraphics);
		GUIEvent.SLOT.RenderHotbarPost.EVENT.invoker().onHotbarRenderPost(event);
	}

}