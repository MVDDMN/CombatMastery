package com.combatmastery.client.renderer;

import com.combatmastery.CombatMastery;
import com.combatmastery.entity.DiceRollEntity;
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

public class DiceRollRenderer extends EntityRenderer<DiceRollEntity> {

    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/dice_1.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/dice_2.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/dice_3.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/dice_4.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/dice_5.png"
            ),
            ResourceLocation.fromNamespaceAndPath(
                    CombatMastery.MOD_ID,
                    "textures/entity/dice_6.png"
            )
    };

    private static final float NORMAL_SIZE = 0.9F;
    private static final float POP_SIZE = 1.35F;

    private static final int POP_TICKS = 4;

    private static final double HEIGHT_OFFSET = 0.8D;

    public DiceRollRenderer(Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(
            DiceRollEntity entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int light
    ) {

        LivingEntity target =
                entity.getTarget();

        if (target == null) {
            return;
        }

        poseStack.pushPose();

        double targetX =
                Mth.lerp(
                        partialTicks,
                        target.xOld,
                        target.getX()
                );

        double targetY =
                Mth.lerp(
                        partialTicks,
                        target.yOld,
                        target.getY()
                )
                        + target.getBbHeight()
                        + HEIGHT_OFFSET;

        double targetZ =
                Mth.lerp(
                        partialTicks,
                        target.zOld,
                        target.getZ()
                );

        double entityX =
                Mth.lerp(
                        partialTicks,
                        entity.xOld,
                        entity.getX()
                );

        double entityY =
                Mth.lerp(
                        partialTicks,
                        entity.yOld,
                        entity.getY()
                );

        double entityZ =
                Mth.lerp(
                        partialTicks,
                        entity.zOld,
                        entity.getZ()
                );

        poseStack.translate(
                targetX - entityX,
                targetY - entityY,
                targetZ - entityZ
        );

        LivingEntity caster =
                entity.getCaster();

        if (caster != null) {

            double casterX =
                    Mth.lerp(
                            partialTicks,
                            caster.xOld,
                            caster.getX()
                    );

            double casterY =
                    Mth.lerp(
                            partialTicks,
                            caster.yOld,
                            caster.getY()
                    )
                            + caster.getBbHeight() * 0.5D;

            double casterZ =
                    Mth.lerp(
                            partialTicks,
                            caster.zOld,
                            caster.getZ()
                    );

            double diceX =
                    targetX;

            double diceY =
                    targetY;

            double diceZ =
                    targetZ;

            double dx =
                    casterX - diceX;

            double dy =
                    casterY - diceY;

            double dz =
                    casterZ - diceZ;

            double horizontalDistance =
                    Math.sqrt(
                            dx * dx
                                    + dz * dz
                    );

            if (horizontalDistance > 0.001D
                    || Math.abs(dy) > 0.001D) {

                float yaw =
                        (float) (
                                Math.atan2(
                                        dx,
                                        dz
                                )
                                        * 180.0D
                                        / Math.PI
                        );

                float pitch =
                        (float) (
                                -Math.atan2(
                                        dy,
                                        horizontalDistance
                                )
                                        * 180.0D
                                        / Math.PI
                        );

                poseStack.mulPose(
                        Axis.YP.rotationDegrees(yaw)
                );

                poseStack.mulPose(
                        Axis.XP.rotationDegrees(pitch)
                );
            }
        }

        drawDice(
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

    private void drawDice(
            Pose pose,
            MultiBufferSource bufferSource,
            int light,
            DiceRollEntity entity,
            float partialTicks
    ) {

        int face =
                entity.getDisplayedFace();

        ResourceLocation texture =
                TEXTURES[
                        Math.max(
                                0,
                                Math.min(
                                        5,
                                        face - 1
                                )
                        )
                        ];

        VertexConsumer consumer =
                bufferSource.getBuffer(
                        RenderType.entityTranslucentEmissive(
                                texture
                        )
                );

        Matrix4f poseMatrix =
                pose.pose();

        Matrix3f normalMatrix =
                pose.normal();

        float scale =
                getScale(
                        entity,
                        partialTicks
                );

        float halfSize =
                scale * 0.5F;

        consumer.vertex(
                        poseMatrix,
                        -halfSize,
                        -halfSize,
                        0.0F
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
                        0.0F,
                        1.0F
                )
                .endVertex();

        consumer.vertex(
                        poseMatrix,
                        halfSize,
                        -halfSize,
                        0.0F
                )
                .color(
                        255,
                        255,
                        255,
                        255
                )
                .uv(
                        1.0F,
                        1.0F
                )
                .overlayCoords(
                        OverlayTexture.NO_OVERLAY
                )
                .uv2(light)
                .normal(
                        normalMatrix,
                        0.0F,
                        0.0F,
                        1.0F
                )
                .endVertex();

        consumer.vertex(
                        poseMatrix,
                        halfSize,
                        halfSize,
                        0.0F
                )
                .color(
                        255,
                        255,
                        255,
                        255
                )
                .uv(
                        1.0F,
                        0.0F
                )
                .overlayCoords(
                        OverlayTexture.NO_OVERLAY
                )
                .uv2(light)
                .normal(
                        normalMatrix,
                        0.0F,
                        0.0F,
                        1.0F
                )
                .endVertex();

        consumer.vertex(
                        poseMatrix,
                        -halfSize,
                        halfSize,
                        0.0F
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
                        0.0F,
                        1.0F
                )
                .endVertex();
    }

    private float getScale(
            DiceRollEntity entity,
            float partialTicks
    ) {

        if (entity.isRolling()) {

            float animationProgress =
                    Mth.clamp(
                            (
                                    entity.tickCount
                                            + partialTicks
                            ) / 4.0F,
                            0.0F,
                            1.0F
                    );

            return Mth.lerp(
                    animationProgress,
                    0.25F,
                    NORMAL_SIZE
            );
        }

        if (entity.isShowingResult()) {

            float resultProgress =
                    Mth.clamp(
                            (
                                    entity.tickCount
                                            - 20
                                            + partialTicks
                            ) / (float) POP_TICKS,
                            0.0F,
                            1.0F
                    );

            if (resultProgress < 0.5F) {

                float expandProgress =
                        resultProgress / 0.5F;

                expandProgress =
                        expandProgress
                                * expandProgress
                                * (
                                3.0F
                                        - 2.0F
                                        * expandProgress
                        );

                return Mth.lerp(
                        expandProgress,
                        NORMAL_SIZE,
                        POP_SIZE
                );

            } else {

                float settleProgress =
                        (
                                resultProgress
                                        - 0.5F
                        ) / 0.5F;

                settleProgress =
                        settleProgress
                                * settleProgress
                                * (
                                3.0F
                                        - 2.0F
                                        * settleProgress
                        );

                return Mth.lerp(
                        settleProgress,
                        POP_SIZE,
                        NORMAL_SIZE
                );
            }
        }

        return NORMAL_SIZE;
    }

    @Override
    public ResourceLocation getTextureLocation(
            DiceRollEntity entity
    ) {

        int face =
                entity.getDisplayedFace();

        return TEXTURES[
                Math.max(
                        0,
                        Math.min(
                                5,
                                face - 1
                        )
                )
                ];
    }
}