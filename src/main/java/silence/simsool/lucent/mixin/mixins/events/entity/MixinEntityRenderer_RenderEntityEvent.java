package silence.simsool.lucent.mixin.mixins.events.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import silence.simsool.lucent.events.impl.EntityEvent;
import silence.simsool.lucent.general.models.data.events.entityevent.RenderEntityEvent;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer_RenderEntityEvent<T extends Entity> {

	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void onShouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
		RenderEntityEvent event = new RenderEntityEvent(entity, frustum, x, y, z);
		EntityEvent.RENDER_ENTITY_EVENT.invoker().onRenderEntity(event);
		if (event.isCanceled()) cir.setReturnValue(false);
	}

}