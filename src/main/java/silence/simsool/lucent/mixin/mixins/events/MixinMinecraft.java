package silence.simsool.lucent.mixin.mixins.events;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import silence.simsool.lucent.events.impl.EntityEvent;
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

	@Inject(
		method = "startUseItem",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"),
		cancellable = true
	)
	private void onEntityInteract(CallbackInfo ci, @Local Entity entity) {
		EntityEvent.EntityInteractEvent event = new EntityEvent.EntityInteractEvent(entity);
		EntityEvent.ENTITY_INTERACT_EVENT.invoker().onEntityInteract(event);
		if (event.isCanceled()) ci.cancel();
	}

	@WrapOperation(
		method = "startUseItem",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;")
	)
	private InteractionResult onBlockInteract(MultiPlayerGameMode instance, LocalPlayer localPlayer, InteractionHand interactionHand, BlockHitResult blockHitResult, Operation<InteractionResult> original) {
		ItemStack item = localPlayer.getItemInHand(interactionHand);
		net.minecraft.core.BlockPos pos = blockHitResult.getBlockPos();
		LucentEvent.BlockInteractEvent event = new LucentEvent.BlockInteractEvent(item, pos);
		LucentEvent.BLOCK_INTERACT_EVENT.invoker().onBlockInteract(event);
		if (event.isCanceled()) return InteractionResult.PASS;
		return original.call(instance, localPlayer, interactionHand, blockHitResult);
	}

}