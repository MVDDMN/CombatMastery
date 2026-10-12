package com.combatmastery.client.renderer;

import com.combatmastery.CombatMastery;
import com.combatmastery.entity.RocketShotEntity;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RocketShotRenderer extends EntityRenderer<RocketShotEntity> {

    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/rocket_shot_1.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/rocket_shot_2.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/rocket_shot_3.png"
            )
    };

    private static final float SIZE = 1.0F;

    private static final int TICKS_PER_FRAME = 2;

    public RocketShotRenderer(Context context) {
        super(context);

        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            RocketShotEntity entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light
    ) {
        poseStack.pushPose();

        Vec3 movement =
                entity.getDeltaMovement();

        if (movement.lengthSqr() > 0.000001D) {
            float flightYaw =
                    (float) (
                            Math.atan2(
                                    movement.x,
                                    movement.z
                            ) * (180.0D / Math.PI)
                    );

            poseStack.mulPose(
                    Axis.YP.rotationDegrees(
                            flightYaw
                    )
            );
        }

        drawRocketShot(
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

    private void drawRocketShot(
            Pose pose,
            MultiBufferSource bufferSource,
            int light,
            RocketShotEntity entity
    ) {
        Matrix4f poseMatrix =
                pose.pose();

        Matrix3f normalMatrix =
                pose.normal();

        VertexConsumer consumer =
                bufferSource.getBuffer(
                        RenderType.entityTranslucentEmissive(
                                getTextureLocation(entity)
                        )
                );

        float halfSize =
                SIZE * 0.5F;

        consumer.vertex(
                poseMatrix,
                -halfSize,
                0.0F,
                -halfSize
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
                0.0F,
                -halfSize
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
                0.0F,
                halfSize
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
                0.0F,
                halfSize
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
            RocketShotEntity entity
    ) {
        int frame =
                (entity.tickCount / TICKS_PER_FRAME)
                        % TEXTURES.length;

        return TEXTURES[frame];
    }
}