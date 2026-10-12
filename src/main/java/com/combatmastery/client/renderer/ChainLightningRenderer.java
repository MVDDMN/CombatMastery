package com.combatmastery.client.renderer;

import com.combatmastery.entity.ChainLightningEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChainLightningRenderer
        extends EntityRenderer<ChainLightningEntity> {

    private static final float WIDTH = 1.5F;

    private static final float TEXTURE_LENGTH = 5.0F;

    private static final int TICKS_PER_FRAME = 1;

    private final List<ResourceLocation> textures;

    public ChainLightningRenderer(Context context) {
        super(context);

        this.shadowRadius = 0.0F;

        textures =
                new ArrayList<>(
                        Minecraft.getInstance()
                                .getResourceManager()
                                .listResources(
                                        "textures/entity",
                                        location ->
                                                location.getPath()
                                                        .matches(
                                                                "textures/entity/chain_lightning_\\d+\\.png"
                                                        )
                                )
                                .keySet()
                );

        textures.sort(
                Comparator.comparingInt(
                        this::getFrameNumber
                )
        );
    }

    @Override
    public void render(
            ChainLightningEntity entity,
            float yaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light
    ) {
        if (textures.isEmpty()) {
            return;
        }

        LivingEntity currentTarget =
                entity.getCurrentTarget();

        LivingEntity nextTarget =
                entity.getNextTarget();

        if (currentTarget == null || nextTarget == null) {
            return;
        }

        if (!currentTarget.isAlive()
                || !nextTarget.isAlive()) {
            return;
        }

        double startX =
                currentTarget.getX();

        double startY =
                currentTarget.getY()
                        + currentTarget.getBbHeight() * 0.45D;

        double startZ =
                currentTarget.getZ();

        double endX =
                nextTarget.getX();

        double endY =
                nextTarget.getY()
                        + nextTarget.getBbHeight() * 0.45D;

        double endZ =
                nextTarget.getZ();

        double deltaX =
                endX - startX;

        double deltaY =
                endY - startY;

        double deltaZ =
                endZ - startZ;

        double horizontalLength =
                Math.sqrt(
                        deltaX * deltaX
                                + deltaZ * deltaZ
                );

        double length =
                Math.sqrt(
                        deltaX * deltaX
                                + deltaY * deltaY
                                + deltaZ * deltaZ
                );

        if (length <= 0.001D) {
            return;
        }

        float centerX =
                (float) (
                        (startX - entity.getX())
                                + deltaX * 0.5D
                );

        float centerY =
                (float) (
                        (startY - entity.getY())
                                + deltaY * 0.5D
                );

        float centerZ =
                (float) (
                        (startZ - entity.getZ())
                                + deltaZ * 0.5D
                );

        float horizontalAngle =
                (float) Math.atan2(
                        deltaZ,
                        deltaX
                );

        float verticalAngle =
                (float) Math.atan2(
                        deltaY,
                        horizontalLength
                );

        poseStack.pushPose();

        poseStack.translate(
                centerX,
                centerY,
                centerZ
        );

        poseStack.mulPose(
                new Quaternionf()
                        .rotateY(
                                -horizontalAngle
                        )
        );

        poseStack.mulPose(
                new Quaternionf()
                        .rotateZ(
                                verticalAngle
                        )
        );

        drawChainLightning(
                poseStack.last(),
                bufferSource,
                light,
                (float) length,
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

    private void drawChainLightning(
            Pose pose,
            MultiBufferSource bufferSource,
            int light,
            float length,
            ChainLightningEntity entity
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

        float halfLength =
                length * 0.5F;

        float halfWidth =
                WIDTH * 0.5F;

        float textureU =
                Math.min(
                        length / TEXTURE_LENGTH,
                        1.0F
                );

        consumer.vertex(
                        poseMatrix,
                        -halfLength,
                        0.0F,
                        -halfWidth
                )
                .color(
                        255,
                        255,
                        255,
                        255
                )
                .uv(
                        0.0F,
                        1.0F
                )
                .overlayCoords(
                        OverlayTexture.NO_OVERLAY
                )
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
                        halfLength,
                        0.0F,
                        -halfWidth
                )
                .color(
                        255,
                        255,
                        255,
                        255
                )
                .uv(
                        textureU,
                        1.0F
                )
                .overlayCoords(
                        OverlayTexture.NO_OVERLAY
                )
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
                        halfLength,
                        0.0F,
                        halfWidth
                )
                .color(
                        255,
                        255,
                        255,
                        255
                )
                .uv(
                        textureU,
                        0.0F
                )
                .overlayCoords(
                        OverlayTexture.NO_OVERLAY
                )
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
                        -halfLength,
                        0.0F,
                        halfWidth
                )
                .color(
                        255,
                        255,
                        255,
                        255
                )
                .uv(
                        0.0F,
                        0.0F
                )
                .overlayCoords(
                        OverlayTexture.NO_OVERLAY
                )
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
            ChainLightningEntity entity
    ) {
        if (textures.isEmpty()) {
            return ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/misc/unknown_server.png"
            );
        }

        int frame =
                (entity.tickCount / TICKS_PER_FRAME)
                        % textures.size();

        return textures.get(frame);
    }

    private int getFrameNumber(
            ResourceLocation location
    ) {
        String path =
                location.getPath();

        int start =
                path.lastIndexOf(
                        "chain_lightning_"
                )
                        + "chain_lightning_".length();

        int end =
                path.lastIndexOf(
                        ".png"
                );

        return Integer.parseInt(
                path.substring(
                        start,
                        end
                )
        );
    }
}