package silence.simsool.lucent.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import silence.simsool.lucent.mods.Translucent3DRenderFixMod;

@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
public abstract class MixinPreparedFrame_FIX_TRANS_3DRENDER {

	@Shadow private FeatureFrameContext context;
	@Shadow private SubmitNodeStorage submitNodeStorage;
	@Shadow abstract void executePhase(final FeatureRenderPhase<?> phase, final FeatureFrameContext context);

	@Inject(method = "executeTranslucentAfterTerrain", at = @At("TAIL"))
	private void lucent$renderCustomGeometryAfterTerrain(CallbackInfo ci) {
		if (!Translucent3DRenderFixMod.CustomGeometry) return;

		FeatureFrameContext ctx = Objects.requireNonNull(this.context);
		SubmitNodeStorage storage = Objects.requireNonNull(this.submitNodeStorage);
		ObjectIterator<?> iterator = storage.getSubmitsPerOrder().values().iterator();

		while (iterator.hasNext()) {
			SubmitNodeCollection collection = (SubmitNodeCollection) iterator.next();
			this.executePhase(collection.translucentCustomGeometry, ctx);
		}
	}

}