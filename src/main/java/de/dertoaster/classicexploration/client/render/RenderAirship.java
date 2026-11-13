package de.dertoaster.classicexploration.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import de.dertoaster.classicexploration.ClassicExplorationMod;
import de.dertoaster.classicexploration.objects.entity.EntityAirship;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class RenderAirship extends GeoEntityRenderer<EntityAirship> {
    public RenderAirship(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(ClassicExplorationMod.prefix("airship")));
    }

    @Override
    public void preRender(PoseStack poseStack, EntityAirship animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        float yaw = Mth.rotLerp(partialTick, animatable.yRotO, animatable.getYRot());
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));

        // Balanceo al ser golpeado, como el barco
        int hurtTime = animatable.getHurtTime();
        if (hurtTime > 0) {
            float f = Mth.clampedLerp(animatable.getHurtDirO(), animatable.getHurtDir(), partialTick);
            poseStack.mulPose(Axis.XP.rotationDegrees(f * 3.0F));
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EntityAirship animatable, BakedGeoModel model, RenderType renderType,
            MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
            int packedOverlay, int colour) {
        var balloonOpt = model.getBone("balloon");
        if (balloonOpt.isEmpty()) {
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                    packedLight, packedOverlay, colour);
            return;
        }

        GeoBone balloon = balloonOpt.get();
        boolean prevHidden = balloon.isHidden();

        if (animatable.isBalloonDestroyed()) {
            balloon.setHidden(true);
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                    packedLight, packedOverlay, 0xFFFFFFFF);
            balloon.setHidden(prevHidden);
            return;
        }

        // 1) Todo excepto el globo, en blanco
        balloon.setHidden(true);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, 0xFFFFFFFF);

        // 2) Solo el globo, con el color del tinte asignado
        balloon.setHidden(false);
        int dyeRgb = animatable.getBalloonColor().getTextureDiffuseColor();
        int dyeColour = 0xFF000000 | (dyeRgb & 0x00FFFFFF);

        poseStack.pushPose();
        // Aplicar transformaciones de ancestros para que el globo se renderice en su posición correcta
        model.getBone("chasis").ifPresent(chasis -> {
            RenderUtil.translateMatrixToBone(poseStack, chasis);
            RenderUtil.rotateMatrixAroundBone(poseStack, chasis);
            RenderUtil.translateMatrixToBone(poseStack, balloon);
        });
        this.renderRecursively(poseStack, animatable, balloon, renderType, bufferSource, buffer, true, partialTick,
                packedLight, packedOverlay, dyeColour);
        poseStack.popPose();

        balloon.setHidden(prevHidden);

        // Renderizar el cofre como modelo de bloque si está instalado
        if (animatable.hasChest()) {
            this.renderChestModel(poseStack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        }
    }

    private void renderChestModel(PoseStack poseStack, EntityAirship animatable, MultiBufferSource bufferSource,
            float partialTick, int packedLight, int packedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher blockRenderer = minecraft.getBlockRenderer();
        BlockState chestState = Blocks.CHEST.defaultBlockState();

        poseStack.pushPose();
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.PI));
        poseStack.translate(0, 0.0D, 2/16F);
        poseStack.scale(0.7F,0.7F,0.7F);
        poseStack.translate(-0.5, 0.0D, 19/16F);

        blockRenderer.renderSingleBlock(chestState, poseStack, bufferSource, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityAirship animatable) {
        return ClassicExplorationMod.prefix("textures/entity/airship.png");
    }
}
