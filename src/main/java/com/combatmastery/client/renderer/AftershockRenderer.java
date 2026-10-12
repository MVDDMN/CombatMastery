package com.combatmastery.client.renderer;

import com.combatmastery.entity.AftershockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class AftershockRenderer
        extends EntityRenderer<AftershockEntity> {

    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/aftershock_1.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/aftershock_2.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/aftershock_3.png"
            )
    };

    private static final float WIDTH = 5.0F;
    private static final float DEPTH = 5.0F;
    private static final float HEIGHT_OFFSET = 0.05F;

    private static final float MIN_SCALE = 0.15F;
    private static final float MAX_SCALE = 1.3F;

    private static final float ANIMATION_DURATION = 5.0F; //5 ticks

    public AftershockRenderer(Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            AftershockEntity entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light
    ) {
        poseStack.pushPose();

        float scale = getExpansionScale(
                entity,
                partialTicks
        );

        poseStack.translate(
                0.0F,
                HEIGHT_OFFSET,
                0.0F
        );

        poseStack.scale(
                scale,
                1.0F,
                scale
        );

        drawAftershock(
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

    private float getExpansionScale(
            AftershockEntity entity,
            float partialTicks
    ) {
        float progress =
                (entity.tickCount + partialTicks)
                        / ANIMATION_DURATION;

        progress = Math.min(
                progress,
                1.0F
        );

        return MIN_SCALE
                + (
                (MAX_SCALE - MIN_SCALE)
                        * progress
        );
    }

    private void drawAftershock(
            Pose pose,
            MultiBufferSource bufferSource,
            int light,
            AftershockEntity entity
    ) {
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        VertexConsumer consumer =
                bufferSource.getBuffer(
                        RenderType.entityTranslucentEmissive(
                                getTextureLocation(entity)
                        )
                );

        float halfWidth = WIDTH * 0.5F;
        float halfDepth = DEPTH * 0.5F;

        consumer.vertex(
                        poseMatrix,
                        -halfWidth,
                        0.0F,
                        -halfDepth
                )
                .color(255, 255, 255, 255)
                .uv(0.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(
                        normalMatrix,
                        0.0F,
                        1.0F,
                        0.0F
                )
                .endVertex();

        consumer.vertex(
                        poseMatrix,
                        halfWidth,
                        0.0F,
                        -halfDepth
                )
                .color(255, 255, 255, 255)
                .uv(1.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(
                        normalMatrix,
                        0.0F,
                        1.0F,
                        0.0F
                )
                .endVertex();

        consumer.vertex(
                        poseMatrix,
                        halfWidth,
                        0.0F,
                        halfDepth
                )
                .color(255, 255, 255, 255)
                .uv(1.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(
                        normalMatrix,
                        0.0F,
                        1.0F,
                        0.0F
                )
                .endVertex();

        consumer.vertex(
                        poseMatrix,
                        -halfWidth,
                        0.0F,
                        halfDepth
                )
                .color(255, 255, 255, 255)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(
                        normalMatrix,
                        0.0F,
                        1.0F,
                        0.0F
                )
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(
            AftershockEntity entity
    ) {
        int frame;

        if (entity.tickCount < 2) {
            frame = 0;
        } else if (entity.tickCount < 5) {
            frame = 1;
        } else {
            frame = 2;
        }

        return TEXTURES[frame];
    }
}