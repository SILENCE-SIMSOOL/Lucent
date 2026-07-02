package silence.simsool.lucent.mixin.mixins.events.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import silence.simsool.lucent.events.impl.EntityEvent;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_RenderEntityEvent<T extends Entity> {

	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void onShouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
		if (entity == null) return;
		EntityEvent.RenderEntityAllowEvent event = new EntityEvent.RenderEntityAllowEvent(entity, frustum, x, y, z);
		EntityEvent.RENDER_ENTITY_ALLOW_EVENT.invoker().onRenderEntityAllow(event);
		if (event.isCanceled()) cir.setReturnValue(false);
	}

	@Inject(method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;", at = @At("TAIL"))
	private void postExtractRenderEntity(Entity entity, float f, CallbackInfoReturnable<EntityRenderState> cir, @Local EntityRenderState state) {
		if (entity == null) return;
		EntityEvent.RENDER_STATE_ENTITIES.put(state, entity);
		EntityEvent.ExtractRenderStatePost event = new EntityEvent.ExtractRenderStatePost(entity, state, f);
		EntityEvent.EXTRACT_RENDER_STATE_POST.invoker().onExtractRenderStatePost(event);
	}

}