package com.combatmastery.client.renderer;

import com.combatmastery.entity.HomingShotEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class HomingShotRenderer
        extends EntityRenderer<HomingShotEntity> {

    private static final float SPIN_SPEED = 20.0F;

    private final ItemRenderer itemRenderer;

    public HomingShotRenderer(
            EntityRendererProvider.Context context
    ) {
        super(context);

        this.itemRenderer =
                Minecraft.getInstance()
                        .getItemRenderer();

        this.shadowRadius =
                0.0F;
    }

    @Override
    public void render(
            HomingShotEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        ItemStack weapon =
                entity.getWeapon();

        if (weapon.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        LivingEntity target =
                entity.getTarget();

        if (target != null
                && target.isAlive()) {

            Vec3 entityPosition =
                    entity.getPosition(
                            partialTick
                    );

            Vec3 targetPosition =
                    target.getBoundingBox()
                            .getCenter();

            double directionX =
                    targetPosition.x
                            - entityPosition.x;

            double directionZ =
                    targetPosition.z
                            - entityPosition.z;

            if (Math.abs(directionX)
                    > 0.000001D
                    || Math.abs(directionZ)
                    > 0.000001D) {

                float yaw =
                        (float) (
                                Math.atan2(
                                        directionZ,
                                        directionX
                                )
                                        * (
                                        180.0D
                                                / Math.PI
                                )
                                        - 90.0D
                        );

                poseStack.mulPose(
                        Axis.YP.rotationDegrees(
                                yaw
                        )
                );
            }
        }

        poseStack.mulPose(
                Axis.XP.rotationDegrees(
                        90.0F
                )
        );

        float spin =
                (entity.tickCount + partialTick)
                        * SPIN_SPEED;

        poseStack.mulPose(
                Axis.ZP.rotationDegrees(
                        spin
                )
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(
                        180.0F
                )
        );

        itemRenderer.renderStatic(
                weapon,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(
            HomingShotEntity entity
    ) {
        return ResourceLocation.fromNamespaceAndPath(
                "minecraft",
                "textures/misc/white.png"
        );
    }
}