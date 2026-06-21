package silence.simsool.lucent.mixin.mixins.events.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import silence.simsool.lucent.events.impl.EntityEvent;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

	@ModifyArgs(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
	private void modifyModelTint(Args args, LivingEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitter, CameraRenderState cameraState) {
		Entity entity = EntityEvent.RENDER_STATE_ENTITIES.get(state);
		EntityEvent.RenderEntityColorEvent event = new EntityEvent.RenderEntityColorEvent(entity, state);
		EntityEvent.RENDER_ENTITY_COLOR_EVENT.invoker().onRenderEntityColor(event);
		if (event.hasColor()) args.set(6, event.getColor());
	}

}