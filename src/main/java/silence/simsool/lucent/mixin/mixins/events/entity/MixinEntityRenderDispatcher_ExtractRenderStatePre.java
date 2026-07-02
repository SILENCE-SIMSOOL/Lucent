package silence.simsool.lucent.mixin.mixins.events.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import silence.simsool.lucent.events.impl.EntityEvent;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher_ExtractRenderStatePre {

	@Inject(method = "submit", at = @At("HEAD"))
	private void preRenderEntity(EntityRenderState entityRenderState, CameraRenderState cameraRenderState, double d, double e, double f, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CallbackInfo ci) {
		EntityEvent.RenderEntityPreEvent event = new EntityEvent.RenderEntityPreEvent(entityRenderState, cameraRenderState, poseStack, submitNodeCollector);
		EntityEvent.RENDER_ENTITY_PRE_EVENT.invoker().onRenderEntity(event);
	}

	@Inject(method = "extractEntity", at = @At("HEAD"), cancellable = true)
	private void preExtractRenderEntity(Entity entity, float f, CallbackInfoReturnable<EntityRenderState> cir) {
		if (entity == null) return;
		EntityEvent.ExtractRenderStatePre event = new EntityEvent.ExtractRenderStatePre(entity, f);
		EntityEvent.EXTRACT_RENDER_STATE_PRE.invoker().onExtractRenderStatePre(event);
		if (event.isCanceled()) {
			EntityRenderState state = new EntityRenderState();
			state.entityType = EntityType.AREA_EFFECT_CLOUD;
			cir.setReturnValue(state);
		}
	}

}