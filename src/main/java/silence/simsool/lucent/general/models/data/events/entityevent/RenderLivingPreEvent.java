package silence.simsool.lucent.general.models.data.events.entityevent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

public class RenderLivingPreEvent {

	public final LivingEntityRenderState state;
	public final PoseStack poseStack;
	private boolean canceled = false;

	public RenderLivingPreEvent(LivingEntityRenderState state, PoseStack poseStack) {
		this.state = state;
		this.poseStack = poseStack;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}