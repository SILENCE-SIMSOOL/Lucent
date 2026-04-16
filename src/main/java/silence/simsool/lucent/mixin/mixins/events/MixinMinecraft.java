package silence.simsool.lucent.mixin.mixins.events;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import silence.simsool.lucent.events.impl.LucentEvent;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Shadow
	@Nullable
	public HitResult hitResult;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void onInitFinished(GameConfig gameConfig, CallbackInfo ci) {
		LucentEvent.INIT_FINISHED_EVENT.invoker().onInitFinished();
	}

	@Inject(at = @At("RETURN"), method = "onResourceLoadFinished")
	private void onResourcesReady(CallbackInfo info) {
		LucentEvent.RESOURCES_READY_EVENT.invoker().onResourcesReady();
	}

	@Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
	private void onStartUseItemBlock(CallbackInfo ci) {
		if (!(this.hitResult instanceof BlockHitResult blockHit)) return;
		if (LucentEvent.BLOCK_INTERACT_EVENT.invoker().onBlockInteract(blockHit.getBlockPos())) ci.cancel();
	}

}