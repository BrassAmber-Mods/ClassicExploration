package grillo78.classicexploration.client.entity.model;// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import grillo78.classicexploration.entities.Airship;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class AirshipModel<T extends Airship> extends EntityModel<T> {
	private final ModelPart chasis;
	private final ModelPart balloon;
	private final ModelPart engineLeft;
	private final ModelPart rotarLeft;
	private final ModelPart engineRight;
	private final ModelPart rotarRight;

	public AirshipModel(ModelPart root) {
		this.chasis = root.getChild("chasis");
		this.balloon = this.chasis.getChild("balloon");
		this.engineLeft = this.chasis.getChild("engineLeft");
		this.rotarLeft = this.engineLeft.getChild("rotarLeft");
		this.engineRight = this.chasis.getChild("engineRight");
		this.rotarRight = this.engineRight.getChild("rotarRight");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition chasis = partdefinition.addOrReplaceChild("chasis", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -3.0F, -12.0F, 16.0F, 3.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(41, 33).addBox(-10.0F, -9.0F, 16.0F, 18.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 33).addBox(-9.0F, -9.0F, -14.0F, 16.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 42).addBox(7.0F, -9.0F, -12.0F, 2.0F, 6.0F, 28.0F, new CubeDeformation(0.0F))
		.texOffs(62, 42).addBox(-11.0F, -9.0F, -12.0F, 2.0F, 6.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition balloon = chasis.addOrReplaceChild("balloon", CubeListBuilder.create().texOffs(16, 86).addBox(-25.0F, -77.0F, -22.0F, 48.0F, 48.0F, 48.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition rope_4_r1 = balloon.addOrReplaceChild("rope_4_r1", CubeListBuilder.create().texOffs(0, 82).addBox(-9.0F, -36.5F, 12.5F, 2.0F, 22.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, -0.2182F));

		PartDefinition rope_3_r1 = balloon.addOrReplaceChild("rope_3_r1", CubeListBuilder.create().texOffs(0, 82).addBox(5.0F, -36.5F, 12.5F, 2.0F, 22.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2618F, 0.0F, 0.2182F));

		PartDefinition rope_2_r1 = balloon.addOrReplaceChild("rope_2_r1", CubeListBuilder.create().texOffs(0, 82).addBox(-8.0F, -35.5F, -10.5F, 2.0F, 22.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, -0.2182F));

		PartDefinition rope_1_r1 = balloon.addOrReplaceChild("rope_1_r1", CubeListBuilder.create().texOffs(0, 82).addBox(4.0F, -35.0F, -10.5F, 2.0F, 22.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.2182F));

		PartDefinition engineLeft = chasis.addOrReplaceChild("engineLeft", CubeListBuilder.create().texOffs(168, 57).addBox(9.0F, -6.0F, -5.0F, 4.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(102, 25).addBox(0.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -2.0F, 2.0F));

		PartDefinition rotarLeft = engineLeft.addOrReplaceChild("rotarLeft", CubeListBuilder.create(), PartPose.offsetAndRotation(11.0F, -1.0F, 6.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition rotar8_r1 = rotarLeft.addOrReplaceChild("rotar8_r1", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.3562F));

		PartDefinition rotar6_r1 = rotarLeft.addOrReplaceChild("rotar6_r1", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition rotar4_r1 = rotarLeft.addOrReplaceChild("rotar4_r1", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		PartDefinition rotar2_r1 = rotarLeft.addOrReplaceChild("rotar2_r1", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		PartDefinition engineRight = chasis.addOrReplaceChild("engineRight", CubeListBuilder.create().texOffs(197, 57).addBox(-13.0F, -6.0F, -5.0F, 4.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(102, 21).addBox(-9.0F, -1.0F, -1.0F, 9.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, -2.0F, 2.0F));

		PartDefinition rotarRight = engineRight.addOrReplaceChild("rotarRight", CubeListBuilder.create(), PartPose.offsetAndRotation(-11.0F, -1.0F, 6.0F, 0.0F, 0.0F, 1.5708F));

		PartDefinition rotar8_r2 = rotarRight.addOrReplaceChild("rotar8_r2", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -2.3562F));

		PartDefinition rotar6_r2 = rotarRight.addOrReplaceChild("rotar6_r2", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		PartDefinition rotar4_r2 = rotarRight.addOrReplaceChild("rotar4_r2", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -3.1416F));

		PartDefinition rotar2_r2 = rotarRight.addOrReplaceChild("rotar2_r2", CubeListBuilder.create().texOffs(6, 121).addBox(-0.5F, -5.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 121).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void setupAnim(Airship entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if(entity.getDeltaMovement().x != 0 || entity.getDeltaMovement().z != 0){
			rotarLeft.zRot += entity.getDeltaMovement().length();
			rotarRight.zRot += entity.getDeltaMovement().length();
		} else {
			rotarLeft.zRot += 0;
			rotarRight.zRot += 0;
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		chasis.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}