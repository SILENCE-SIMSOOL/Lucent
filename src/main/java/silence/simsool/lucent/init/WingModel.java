package silence.simsool.lucent.init;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class WingModel extends Model<AvatarRenderState> {

	public final ModelPart wing;
	public final ModelPart wingTip;

	public WingModel(ModelPart root) {
		super(root, RenderTypes::armorCutoutNoCull);
		this.wing = root.getChild("wing");
		this.wingTip = this.wing.getChild("wingtip");
	}

	public static ModelPart createModelPart() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition root = meshDefinition.getRoot();
		
		PartDefinition wingPart = root.addOrReplaceChild(
			"wing",
			CubeListBuilder.create()
				.texOffs(0, 0).addBox("bone", -10.0F, -1.0F, -1.0F, 10.0F, 2.0F, 2.0F)
				.texOffs(20, 8).addBox("skin", -10.0F, 0.0F, 0.5F, 10.0F, 0.0F, 10.0F),
			PartPose.offset(-2.0F, 0.0F, 0.0F)
		);
		
		wingPart.addOrReplaceChild(
			"wingtip",
			CubeListBuilder.create()
				.texOffs(0, 5).addBox("bone", -10.0F, -0.5F, -0.5F, 10.0F, 1.0F, 1.0F)
				.texOffs(20, 18).addBox("skin", -10.0F, 0.0F, 0.5F, 10.0F, 0.0F, 10.0F),
			PartPose.offset(-10.0F, 0.0F, 0.0F)
		);

		return LayerDefinition.create(meshDefinition, 30, 30).bakeRoot();
	}

	@Override
	public void setupAnim(AvatarRenderState state) {
		super.setupAnim(state);
		float angleX = -85.0F;
		float angleY = 40.0F;
		float angleZ = 20.0F;
		
		float f11 = (System.currentTimeMillis() % 1000L) / 1000.0F * (float) Math.PI * 2.0F;
		this.wing.xRot = (float) Math.toRadians(angleX) - (float) Math.cos(f11) * 0.2F;
		this.wing.yRot = (float) Math.toRadians(angleY) + (float) Math.sin(f11) * 0.4F;
		this.wing.zRot = (float) Math.toRadians(angleZ);
		this.wingTip.zRot = -((float) (Math.sin(f11 + 2.0F) + 0.5D)) * 0.75F;
	}

}