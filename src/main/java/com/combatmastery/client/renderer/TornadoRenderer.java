package com.combatmastery.client.renderer;

import com.combatmastery.entity.TornadoEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TornadoRenderer extends EntityRenderer<TornadoEntity> {

    private static final ResourceLocation TORNADO_1 =
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/tornado_1.png"
            );

    private static final ResourceLocation TORNADO_2 =
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/tornado_2.png"
            );

    private static final ResourceLocation TORNADO_3 =
            ResourceLocation.fromNamespaceAndPath(
                    "combat_mastery",
                    "textures/entity/tornado_3.png"
            );

    private static final float MIN_SIZE = 3.0F;

    private static final float MAX_SIZE = 5.00F;

    private static final float PULSE_SPEED = 0.15F;

    private static final int TEXTURE_CHANGE_INTERVAL = 4;

    private static final float GROUND_OFFSET = 0.025F;

    public TornadoRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context);

        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            TornadoEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float pulseTime =
                (entity.tickCount + partialTick)
                        * PULSE_SPEED;

        float pulse =
                (Mth.sin(pulseTime) + 1.0F)
                        * 0.5F;

        float size =
                Mth.lerp(
                        pulse,
                        MIN_SIZE,
                        MAX_SIZE
                );

        ResourceLocation texture;

        int textureIndex =
                entity.tickCount
                        / TEXTURE_CHANGE_INTERVAL
                        % 3;

        if (textureIndex == 0) {
            texture = TORNADO_1;
        } else if (textureIndex == 1) {
            texture = TORNADO_2;
        } else {
            texture = TORNADO_3;
        }

        VertexConsumer vertexConsumer =
                bufferSource.getBuffer(
                        RenderType.entityTranslucent(texture)
                );

        poseStack.pushPose();

        poseStack.translate(
                0.0D,
                GROUND_OFFSET,
                0.0D
        );

        poseStack.scale(
                size,
                size,
                size
        );

        PoseStack.Pose pose =
                poseStack.last();

        float halfSize =
                0.5F;

        vertexConsumer.vertex(
                pose.pose(),
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
                0.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                packedLight
        ).normal(
                pose.normal(),
                0.0F,
                1.0F,
                0.0F
        ).endVertex();

        vertexConsumer.vertex(
                pose.pose(),
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
                1.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                packedLight
        ).normal(
                pose.normal(),
                0.0F,
                1.0F,
                0.0F
        ).endVertex();

        vertexConsumer.vertex(
                pose.pose(),
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
                1.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                packedLight
        ).normal(
                pose.normal(),
                0.0F,
                1.0F,
                0.0F
        ).endVertex();

        vertexConsumer.vertex(
                pose.pose(),
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
                0.0F
        ).overlayCoords(
                OverlayTexture.NO_OVERLAY
        ).uv2(
                packedLight
        ).normal(
                pose.normal(),
                0.0F,
                1.0F,
                0.0F
        ).endVertex();

        poseStack.popPose();

        super.render(
                entity,
                entityYaw,
                partialTick,
                poseStack,
                bufferSource,
                packedLight
        );
    }

    @Override
    public ResourceLocation getTextureLocation(
            TornadoEntity entity
    ) {
        return TORNADO_1;
    }
}