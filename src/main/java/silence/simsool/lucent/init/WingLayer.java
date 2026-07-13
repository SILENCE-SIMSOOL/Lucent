package silence.simsool.lucent.init;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import silence.simsool.lucent.general.models.data.cosmetics.CosmeticSetting;
import silence.simsool.lucent.ui.utils.UIColors;

public class WingLayer extends RenderLayer<AvatarRenderState, PlayerModel> {

	private final WingModel wingModel;

	public WingLayer(RenderLayerParent<AvatarRenderState, PlayerModel> parent) {
		super(parent);
		this.wingModel = new WingModel(WingModel.createModelPart());
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AvatarRenderState state, float netHeadYaw, float headPitch) {
		boolean debug = false;

		String mode;
		Identifier texture;

		if (debug) {
			mode = "ender";
			texture = PremiumCosmetics.getCosmeticTextureForced("wings", mode); if (texture == null) return;
		}

		else {
			UUID uuid = PremiumCosmetics.getUuidFromState(state.id); if (uuid == null) return;
			CosmeticSetting setting = PremiumCosmetics.getCosmeticSetting(uuid, "wings"); if (setting == null || !setting.enabled) return;
			mode = setting.mode; if (mode == null) return;
			texture = PremiumCosmetics.getCosmeticTexture("wings", setting); if (texture == null) return;
		}

		float rScale;
		float height = 0.16f;

		if (mode.equalsIgnoreCase("ender")) {
			rScale = 0.65F;
		}
		else if (mode.equalsIgnoreCase("mage")) {
			rScale = 0.38F;
			height = 0.14f;
		}
		else return;

		poseStack.pushPose();
		getParentModel().body.translateAndRotate(poseStack);

		poseStack.scale(-rScale, -rScale, rScale);
		poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
		poseStack.translate(0.0f, (float)(height / rScale), (float)(0.2f / rScale));

		if (state.isCrouching) {
			poseStack.translate(0.0f, (float)(0.16f / rScale), 0.0f);
		}

		for (int j = 0; j < 2; ++j) {
			submitNodeCollector.submitModel(
				this.wingModel,
				state,
				poseStack,
				RenderTypes.entityTranslucent(texture, false),
				packedLight,
				OverlayTexture.NO_OVERLAY,
				UIColors.TRANSPARENT,
				null
			);

			poseStack.scale(-1.0F, 1.0F, 1.0F);
		}

		poseStack.popPose();
	}

}