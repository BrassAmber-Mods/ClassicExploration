package grillo78.classicexploration.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import grillo78.classicexploration.ClassicExploration;
import grillo78.classicexploration.client.ModModelLayers;
import grillo78.classicexploration.client.entity.model.AirshipModel;
import grillo78.classicexploration.entities.Airship;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

public class AirshipRenderer extends EntityRenderer<Airship> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ClassicExploration.MOD_ID, "textures/entity/airship.png");
    private AirshipModel model;

    public AirshipRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        model = new AirshipModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModModelLayers.AIRSHIP));
    }

    @Override
    public void render(Airship pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.pushPose();
        pPoseStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(180)));
        pPoseStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(pEntity.getViewYRot(pPartialTick))));
        pPoseStack.translate(0,-1.5,0);
        model.setupAnim(pEntity, 0,0, pPartialTick, 0,0);
        model.renderToBuffer(pPoseStack, pBuffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(pEntity))), pPackedLight, OverlayTexture.NO_OVERLAY, 1,1,1,1);
        pPoseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(Airship p_114482_) {
        return TEXTURE;
    }
}
