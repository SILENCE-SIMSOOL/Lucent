package silence.simsool.lucent.mixin.mixins.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silence.simsool.lucent.events.impl.LucentEvent;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {

    @Inject(
            method = "submit",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onSubmit(
            LivingEntityRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState cameraState,
            CallbackInfo ci
    ) {
        LucentEvent.RenderLivingPreEventData event =
                new LucentEvent.RenderLivingPreEventData(state, poseStack);
        LucentEvent.RENDER_LIVING_PRE_EVENT.invoker().onRenderLivingPre(event);
        if (event.isCanceled()) ci.cancel();
    }
}
