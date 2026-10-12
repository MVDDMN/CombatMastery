package com.combatmastery.client.renderer;

import com.combatmastery.entity.GreatSweepEntity;
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
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class GreatSweepRenderer extends EntityRenderer<GreatSweepEntity> {

    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/great_sweep_1.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/great_sweep_2.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/great_sweep_3.png"
            )
    };

    private static final float MIN_WIDTH = 1.0F;
    private static final float MAX_WIDTH = 6.0F;

    private static final float MIN_HEIGHT = 1.0F;
    private static final float MAX_HEIGHT = 6.0F;

    private static final float ROTATION_X = 0.0F;
    private static final float ROTATION_Y = 0.0F;
    private static final float ROTATION_Z = 0.0F;

    private static final int GROWTH_TICKS = 20;

    private static final int TICKS_PER_FRAME = 2;

    public GreatSweepRenderer(Context context) {
        super(context);

        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            GreatSweepEntity entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light
    ) {
        poseStack.pushPose();

        float projectileYaw = Mth.lerp(
                partialTicks,
                entity.yRotO,
                entity.getYRot()
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        projectileYaw
                )
        );

        poseStack.mulPose(
                Axis.XP.rotationDegrees(
                        ROTATION_X
                )
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        ROTATION_Y
                )
        );

        poseStack.mulPose(
                Axis.ZP.rotationDegrees(
                        ROTATION_Z
                )
        );

        drawSweep(
                poseStack.last(),
                bufferSource,
                light,
                entity
        );

        poseStack.popPose();

        super.render(
                entity,
                yaw,
                partialTicks,
                poseStack,
                bufferSource,
                light
        );
    }

    private void drawSweep(
            Pose pose,
            MultiBufferSource bufferSource,
            int light,
            GreatSweepEntity entity
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
                (entity.tickCount + 0.0F) / GROWTH_TICKS,
                0.0F,
                1.0F
        );

        float width = Mth.lerp(
                growthProgress,
                MIN_WIDTH,
                MAX_WIDTH
        );

        float height = Mth.lerp(
                growthProgress,
                MIN_HEIGHT,
                MAX_HEIGHT
        );

        float halfWidth = width * 0.5F;
        float halfHeight = height * 0.5F;

        consumer.vertex(
                poseMatrix,
                -halfWidth,
                0.0F,
                -halfHeight
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
                halfWidth,
                0.0F,
                -halfHeight
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
                halfWidth,
                0.0F,
                halfHeight
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
                -halfWidth,
                0.0F,
                halfHeight
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
            GreatSweepEntity entity
    ) {
        int frame =
                (entity.tickCount / TICKS_PER_FRAME)
                        % TEXTURES.length;

        return TEXTURES[frame];
    }
}