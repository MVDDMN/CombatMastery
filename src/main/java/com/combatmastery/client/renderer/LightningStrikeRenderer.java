package com.combatmastery.client.renderer;

import com.combatmastery.CombatMastery;
import com.combatmastery.entity.LightningStrikeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class LightningStrikeRenderer extends EntityRenderer<LightningStrikeEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/lightning_strike_ring.png"
            );

    private static final float MIN_SIZE = 0.5F;
    private static final float MAX_SIZE = 3.0F;

    private static final int GROWTH_TICKS = 20;

    private static final double GROUND_OFFSET = 0.025D;

    public LightningStrikeRenderer(Context context) {
        super(context);

        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            LightningStrikeEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light
    ) {
        LivingEntity target = entity.getTarget();

        if (target == null) {
            return;
        }

        poseStack.pushPose();

        double targetX =
                target.getX() - entity.getX();

        double targetY =
                target.getY() - entity.getY();

        double targetZ =
                target.getZ() - entity.getZ();

        poseStack.translate(
                targetX,
                targetY + GROUND_OFFSET,
                targetZ
        );

        poseStack.mulPose(
                Axis.XP.rotationDegrees(90.0F)
        );

        drawRing(
                poseStack.last(),
                bufferSource,
                light,
                entity,
                partialTicks
        );

        poseStack.popPose();

        super.render(
                entity,
                entityYaw,
                partialTicks,
                poseStack,
                bufferSource,
                light
        );
    }

    private void drawRing(
            Pose pose,
            MultiBufferSource bufferSource,
            int light,
            LightningStrikeEntity entity,
            float partialTicks
    ) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer =
                bufferSource.getBuffer(
                        RenderType.entityTranslucentEmissive(
                                getTextureLocation(entity)
                        )
                );

        float growthProgress = Mth.clamp(
                (entity.tickCount + partialTicks)
                        / (float) GROWTH_TICKS,
                0.0F,
                1.0F
        );

        float smoothProgress =
                growthProgress
                        * growthProgress
                        * (3.0F - 2.0F * growthProgress);

        float size = Mth.lerp(
                smoothProgress,
                MIN_SIZE,
                MAX_SIZE
        );

        float halfSize = size * 0.5F;

        consumer.vertex(
                poseMatrix,
                -halfSize,
                -halfSize,
                0.0F
        ).color(
                255,
                255,
                255,
                255
        ).uv(
                0.0F,
                1.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                light
        ).normal(
                normalMatrix,
                0.0F,
                1.0F,
                0.0F
        ).endVertex();

        consumer.vertex(
                poseMatrix,
                halfSize,
                -halfSize,
                0.0F
        ).color(
                255,
                255,
                255,
                255
        ).uv(
                1.0F,
                1.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                light
        ).normal(
                normalMatrix,
                0.0F,
                1.0F,
                0.0F
        ).endVertex();

        consumer.vertex(
                poseMatrix,
                halfSize,
                halfSize,
                0.0F
        ).color(
                255,
                255,
                255,
                255
        ).uv(
                1.0F,
                0.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                light
        ).normal(
                normalMatrix,
                0.0F,
                1.0F,
                0.0F
        ).endVertex();

        consumer.vertex(
                poseMatrix,
                -halfSize,
                halfSize,
                0.0F
        ).color(
                255,
                255,
                255,
                255
        ).uv(
                0.0F,
                0.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                light
        ).normal(
                normalMatrix,
                0.0F,
                1.0F,
                0.0F
        ).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(
            LightningStrikeEntity entity
    ) {
        return TEXTURE;
    }
}