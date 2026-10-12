package com.combatmastery.entity;

import com.combatmastery.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GreatSweepEntity extends Projectile {

    private static final int MAX_LIFETIME = 40;

    private static final double HITBOX_WIDTH = 3.0D;
    private static final double HITBOX_HEIGHT = 3.0D;

    private static final double TRAIL_PARTICLE_SPEED = 0.15D;
    private static final double TRAIL_OFFSET = 0.35D;

    private static final int FLY_SOUND_INTERVAL = 6;

    private static final float CAST_VOLUME = 0.5F;
    private static final float CAST_PITCH = 1.0F;
    private static final float CAST_PITCH_VARIATION = 0.1F;

    private static final float FLY_VOLUME = 0.5F;
    private static final float FLY_PITCH = 1.0F;
    private static final float FLY_PITCH_VARIATION = 0.08F;

    private static final float IMPACT_VOLUME = 0.3F;
    private static final float IMPACT_PITCH = 1.0F;
    private static final float IMPACT_PITCH_VARIATION = 0.1F;

    private final Set<UUID> hitEntities = new HashSet<>();

    public GreatSweepEntity(
            EntityType<? extends GreatSweepEntity> entityType,
            Level level
    ) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    protected void readAdditionalSaveData(
            CompoundTag tag
    ) {
    }

    @Override
    protected void addAdditionalSaveData(
            CompoundTag tag
    ) {
    }

    @Override
    public void tick() {

        super.tick();

        setNoGravity(true);

        if (level().isClientSide) {

            spawnTrailParticles();

            return;
        }

        if (tickCount >= MAX_LIFETIME) {

            spawnEndParticles();

            playImpactSound();

            discard();

            return;
        }

        playFlyingSound();

        Vec3 currentPosition =
                position();

        Vec3 velocity =
                getDeltaMovement();

        Vec3 nextPosition =
                currentPosition.add(velocity);

        BlockHitResult blockHit =
                level().clip(
                        new ClipContext(
                                currentPosition,
                                nextPosition,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                this
                        )
                );

        if (blockHit.getType() ==
                HitResult.Type.BLOCK) {

            setPos(
                    blockHit.getLocation()
            );

            spawnEndParticles();

            playImpactSound();

            discard();

            return;
        }

        setPos(nextPosition);

        updateRotation();

        checkHitEntities();
    }

    private void playFlyingSound() {

        if (tickCount % FLY_SOUND_INTERVAL != 0) {
            return;
        }

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.GREAT_SWEEP_FLY.get(),
                SoundSource.HOSTILE,
                FLY_VOLUME,
                randomPitch(
                        FLY_PITCH,
                        FLY_PITCH_VARIATION
                )
        );
    }

    private void playImpactSound() {

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.GREAT_SWEEP_IMPACT.get(),
                SoundSource.HOSTILE,
                IMPACT_VOLUME,
                randomPitch(
                        IMPACT_PITCH,
                        IMPACT_PITCH_VARIATION
                )
        );
    }

    private void playCastSound() {

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.GREAT_SWEEP_CAST.get(),
                SoundSource.HOSTILE,
                CAST_VOLUME,
                randomPitch(
                        CAST_PITCH,
                        CAST_PITCH_VARIATION
                )
        );
    }

    private float randomPitch(
            float basePitch,
            float variation
    ) {

        return basePitch
                + (random.nextFloat() * 2.0F - 1.0F)
                * variation;
    }

    private void spawnTrailParticles() {

        Vec3 direction =
                getDeltaMovement();

        if (direction.lengthSqr()
                < 0.000001D) {

            return;
        }

        direction =
                direction.normalize();

        Vec3 up =
                new Vec3(
                        0.0D,
                        1.0D,
                        0.0D
                );

        Vec3 right =
                direction.cross(up);

        if (right.lengthSqr()
                < 0.000001D) {

            right =
                    new Vec3(
                            1.0D,
                            0.0D,
                            0.0D
                    );

        } else {

            right =
                    right.normalize();
        }

        Vec3 correctedUp =
                right.cross(direction)
                        .normalize();

        Vec3 trailCenter =
                position().subtract(
                        direction.scale(
                                TRAIL_OFFSET
                        )
                );

        double halfWidth =
                HITBOX_WIDTH / 2.0D;

        double halfHeight =
                HITBOX_HEIGHT / 2.0D;

        for (int i = 0; i < 4; i++) {

            double widthOffset =
                    (random.nextDouble() * 2.0D - 1.0D)
                            * halfWidth;

            double heightOffset =
                    (random.nextDouble() * 2.0D - 1.0D)
                            * halfHeight;

            Vec3 particlePosition =
                    trailCenter
                            .add(
                                    right.scale(
                                            widthOffset
                                    )
                            )
                            .add(
                                    correctedUp.scale(
                                            heightOffset
                                    )
                            );

            level().addParticle(
                    ParticleTypes.ENCHANTED_HIT,
                    particlePosition.x,
                    particlePosition.y,
                    particlePosition.z,
                    -direction.x
                            * TRAIL_PARTICLE_SPEED,
                    -direction.y
                            * TRAIL_PARTICLE_SPEED,
                    -direction.z
                            * TRAIL_PARTICLE_SPEED
            );
        }
    }

    private void spawnEndParticles() {

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 direction =
                getDeltaMovement();

        if (direction.lengthSqr()
                < 0.000001D) {

            direction =
                    Vec3.directionFromRotation(
                            getXRot(),
                            getYRot()
                    );
        }

        direction =
                direction.normalize();

        Vec3 up =
                new Vec3(
                        0.0D,
                        1.0D,
                        0.0D
                );

        Vec3 right =
                direction.cross(up);

        if (right.lengthSqr()
                < 0.000001D) {

            right =
                    new Vec3(
                            1.0D,
                            0.0D,
                            0.0D
                    );

        } else {

            right =
                    right.normalize();
        }

        Vec3 correctedUp =
                right.cross(direction)
                        .normalize();

        Vec3 center =
                position();

        double halfWidth =
                HITBOX_WIDTH / 2.0D;

        double halfHeight =
                HITBOX_HEIGHT / 2.0D;

        for (int i = 0; i < 20; i++) {

            double widthOffset =
                    (random.nextDouble() * 2.0D - 1.0D)
                            * halfWidth;

            double heightOffset =
                    (random.nextDouble() * 2.0D - 1.0D)
                            * halfHeight;

            Vec3 particlePosition =
                    center
                            .add(
                                    right.scale(
                                            widthOffset
                                    )
                            )
                            .add(
                                    correctedUp.scale(
                                            heightOffset
                                    )
                            );

            serverLevel.sendParticles(
                    ParticleTypes.CLOUD,
                    particlePosition.x,
                    particlePosition.y,
                    particlePosition.z,
                    1,
                    0.0D,
                    0.08D,
                    0.0D,
                    0.03D
            );
        }
    }

    private void checkHitEntities() {

        Vec3 direction =
                getDeltaMovement();

        if (direction.lengthSqr()
                < 0.000001D) {

            return;
        }

        direction =
                direction.normalize();

        Vec3 up =
                new Vec3(
                        0.0D,
                        1.0D,
                        0.0D
                );

        Vec3 right =
                direction.cross(up);

        if (right.lengthSqr()
                < 0.000001D) {

            right =
                    new Vec3(
                            1.0D,
                            0.0D,
                            0.0D
                    );

        } else {

            right =
                    right.normalize();
        }

        Vec3 correctedUp =
                right.cross(direction)
                        .normalize();

        double halfWidth =
                HITBOX_WIDTH / 2.0D;

        double halfHeight =
                HITBOX_HEIGHT / 2.0D;

        double halfLength =
                HITBOX_WIDTH / 2.0D;

        Vec3 center =
                position();

        Vec3 extent =
                new Vec3(
                        Math.abs(direction.x)
                                * halfLength
                                + Math.abs(right.x)
                                * halfWidth
                                + Math.abs(correctedUp.x)
                                * halfHeight,

                        Math.abs(direction.y)
                                * halfLength
                                + Math.abs(right.y)
                                * halfWidth
                                + Math.abs(correctedUp.y)
                                * halfHeight,

                        Math.abs(direction.z)
                                * halfLength
                                + Math.abs(right.z)
                                * halfWidth
                                + Math.abs(correctedUp.z)
                                * halfHeight
                );

        AABB searchBox =
                new AABB(
                        center.x - extent.x,
                        center.y - extent.y,
                        center.z - extent.z,
                        center.x + extent.x,
                        center.y + extent.y,
                        center.z + extent.z
                );

        Entity owner =
                getOwner();

        for (LivingEntity target :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox,
                        entity -> {

                            if (entity == owner) {
                                return false;
                            }

                            if (hitEntities.contains(
                                    entity.getUUID()
                            )) {
                                return false;
                            }

                            return entity.isAlive();
                        }
                )) {

            if (isInsideHitbox(
                    target.getBoundingBox(),
                    center,
                    direction,
                    right,
                    correctedUp,
                    halfLength,
                    halfWidth,
                    halfHeight
            )) {

                hitEntity(target);
            }
        }
    }

    private boolean isInsideHitbox(
            AABB entityBox,
            Vec3 center,
            Vec3 direction,
            Vec3 right,
            Vec3 up,
            double halfLength,
            double halfWidth,
            double halfHeight
    ) {

        Vec3 entityCenter =
                entityBox.getCenter();

        Vec3 relative =
                entityCenter.subtract(center);

        double forwardDistance =
                relative.dot(direction);

        double widthDistance =
                relative.dot(right);

        double heightDistance =
                relative.dot(up);

        return Math.abs(forwardDistance)
                <= halfLength
                && Math.abs(widthDistance)
                <= halfWidth
                && Math.abs(heightDistance)
                <= halfHeight;
    }

    private void hitEntity(
            LivingEntity target
    ) {

        Entity owner =
                getOwner();

        if (owner instanceof LivingEntity attacker) {

            float damage =
                    (float) attacker.getAttributeValue(
                            net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE
                    );

            if (damage <= 0.0F) {
                damage = 1.0F;
            }

            target.hurt(
                    damageSource(),
                    damage
            );

            hitEntities.add(
                    target.getUUID()
            );
        }
    }

    private net.minecraft.world.damagesource.DamageSource damageSource() {

        Entity owner =
                getOwner();

        if (owner instanceof LivingEntity livingOwner) {

            return level()
                    .damageSources()
                    .mobAttack(
                            livingOwner
                    );
        }

        return level()
                .damageSources()
                .generic();
    }

    public void launchFromEntity(
            LivingEntity attacker,
            float velocity
    ) {

        Vec3 direction =
                attacker.getLookAngle()
                        .normalize();

        Vec3 spawnPosition =
                attacker.position()
                        .add(
                                0.0D,
                                attacker.getBbHeight()
                                        * 0.5D,
                                0.0D
                        )
                        .subtract(direction);

        setPos(spawnPosition);

        setOwner(attacker);

        setDeltaMovement(
                direction.scale(velocity)
        );

        setYRot(
                attacker.getYRot()
        );

        setXRot(
                attacker.getXRot()
        );

        yRotO =
                getYRot();

        xRotO =
                getXRot();

        playCastSound();
    }
}