package com.combatmastery.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class RocketShotEntity extends Projectile {

    private static final int MAX_LIFETIME = 100;

    private static final double INITIAL_SPEED = 1.0D;
    private static final double MAX_SPEED = 2.0D;
    private static final double ACCELERATION = 0.10D;
    private static final double HOMING_STRENGTH = 0.16D;
    private static final double HIT_RADIUS = 0.65D;

    private static final double TRAIL_DISTANCE = 0.45D;
    private static final double TRAIL_VERTICAL_OFFSET = 0.0D;
    private static final double TRAIL_SPEED = 0.02D;

    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(
                    RocketShotEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Optional<UUID>> TARGET =
            SynchedEntityData.defineId(
                    RocketShotEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(
                    RocketShotEntity.class,
                    EntityDataSerializers.FLOAT
            );

    private static final EntityDataAccessor<Integer> ANIMATION_TICK =
            SynchedEntityData.defineId(
                    RocketShotEntity.class,
                    EntityDataSerializers.INT
            );

    public RocketShotEntity(
            EntityType<? extends RocketShotEntity> entityType,
            Level level
    ) {
        super(
                entityType,
                level
        );

        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(
                OWNER,
                Optional.empty()
        );

        entityData.define(
                TARGET,
                Optional.empty()
        );

        entityData.define(
                DAMAGE,
                1.0F
        );

        entityData.define(
                ANIMATION_TICK,
                0
        );
    }

    public void setOwner(
            LivingEntity owner
    ) {
        entityData.set(
                OWNER,
                Optional.of(
                        owner.getUUID()
                )
        );

        super.setOwner(
                owner
        );
    }

    @Override
    public void setOwner(
            Entity owner
    ) {
        super.setOwner(
                owner
        );

        if (owner instanceof LivingEntity livingEntity) {
            entityData.set(
                    OWNER,
                    Optional.of(
                            livingEntity.getUUID()
                    )
            );
        }
    }

    public LivingEntity getLivingOwner() {
        Optional<UUID> ownerUUID =
                entityData.get(
                        OWNER
                );

        if (ownerUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                ownerUUID.get();

        if (level() instanceof ServerLevel serverLevel) {
            Entity entity =
                    serverLevel.getEntity(
                            uuid
                    );

            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        for (LivingEntity entity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(64.0D),
                        entity ->
                                entity.getUUID().equals(uuid)
                )) {

            return entity;
        }

        return null;
    }

    public void setTarget(
            LivingEntity target
    ) {
        entityData.set(
                TARGET,
                Optional.of(
                        target.getUUID()
                )
        );
    }

    public LivingEntity getTarget() {
        Optional<UUID> targetUUID =
                entityData.get(
                        TARGET
                );

        if (targetUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                targetUUID.get();

        if (level() instanceof ServerLevel serverLevel) {
            Entity entity =
                    serverLevel.getEntity(
                            uuid
                    );

            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        for (LivingEntity entity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(64.0D),
                        entity ->
                                entity.getUUID().equals(uuid)
                )) {

            return entity;
        }

        return null;
    }

    public boolean isTarget(
            LivingEntity target
    ) {
        Optional<UUID> targetUUID =
                entityData.get(
                        TARGET
                );

        return targetUUID.isPresent()
                && targetUUID.get().equals(
                target.getUUID()
        );
    }

    public void setDamage(
            float damage
    ) {
        entityData.set(
                DAMAGE,
                damage
        );
    }

    public float getDamage() {
        return entityData.get(
                DAMAGE
        );
    }

    public int getAnimationTick() {
        return entityData.get(
                ANIMATION_TICK
        );
    }

    public static double getInitialSpeed() {
        return INITIAL_SPEED;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            entityData.set(
                    ANIMATION_TICK,
                    tickCount
            );

            createTrailParticle();

            return;
        }

        if (tickCount >= MAX_LIFETIME) {
            discard();
            return;
        }

        LivingEntity owner =
                getLivingOwner();

        LivingEntity target =
                getTarget();

        if (owner == null
                || !owner.isAlive()) {

            discard();
            return;
        }

        if (target == null
                || !target.isAlive()
                || target.isDeadOrDying()) {

            discard();
            return;
        }

        Vec3 direction =
                calculateHomingDirection(
                        target
                );

        if (direction.lengthSqr() < 0.000001D) {
            discard();
            return;
        }

        double currentSpeed =
                getDeltaMovement().length();

        double speed;

        if (currentSpeed < 0.000001D) {
            speed = INITIAL_SPEED;
        } else {
            speed =
                    Math.min(
                            currentSpeed + ACCELERATION,
                            MAX_SPEED
                    );

            if (speed < INITIAL_SPEED) {
                speed = INITIAL_SPEED;
            }
        }

        applyDirection(
                direction,
                speed
        );

        moveProjectile();

        checkTargetCollision(
                target
        );
    }

    private void createTrailParticle() {
        Vec3 movement =
                getDeltaMovement();

        if (movement.lengthSqr() < 0.000001D) {
            level().addParticle(
                    ParticleTypes.CLOUD,
                    getX(),
                    getY() + TRAIL_VERTICAL_OFFSET,
                    getZ(),
                    0.0D,
                    TRAIL_SPEED,
                    0.0D
            );

            return;
        }

        Vec3 direction =
                movement.normalize();

        Vec3 trailPosition =
                position()
                        .subtract(
                                direction.scale(
                                        TRAIL_DISTANCE
                                )
                        );

        level().addParticle(
                ParticleTypes.CLOUD,
                trailPosition.x,
                trailPosition.y + TRAIL_VERTICAL_OFFSET,
                trailPosition.z,
                -direction.x * TRAIL_SPEED,
                -direction.y * TRAIL_SPEED,
                -direction.z * TRAIL_SPEED
        );
    }

    private Vec3 calculateHomingDirection(
            LivingEntity target
    ) {
        Vec3 targetPosition =
                target.getBoundingBox()
                        .getCenter();

        Vec3 currentPosition =
                position();

        Vec3 desiredDirection =
                targetPosition
                        .subtract(currentPosition);

        if (desiredDirection.lengthSqr()
                < 0.000001D) {
            return Vec3.ZERO;
        }

        desiredDirection =
                desiredDirection.normalize();

        Vec3 currentVelocity =
                getDeltaMovement();

        if (currentVelocity.lengthSqr()
                < 0.000001D) {
            return desiredDirection;
        }

        Vec3 currentDirection =
                currentVelocity.normalize();

        Vec3 adjustedDirection =
                currentDirection
                        .scale(
                                1.0D
                                        - HOMING_STRENGTH
                        )
                        .add(
                                desiredDirection.scale(
                                        HOMING_STRENGTH
                                )
                        );

        if (adjustedDirection.lengthSqr()
                < 0.000001D) {
            return desiredDirection;
        }

        return adjustedDirection.normalize();
    }

    private void applyDirection(
            Vec3 direction,
            double speed
    ) {
        Vec3 velocity =
                direction.scale(
                        speed
                );

        setDeltaMovement(
                velocity
        );

        updateRotationFromDirection(
                direction
        );
    }

    private void updateRotationFromDirection(
            Vec3 direction
    ) {
        double horizontalDistance =
                Math.sqrt(
                        direction.x
                                * direction.x
                                + direction.z
                                * direction.z
                );

        float yaw =
                (float) (
                        Math.atan2(
                                direction.z,
                                direction.x
                        )
                                * (180.0D / Math.PI)
                                - 90.0D
                );

        float pitch =
                (float) (
                        -Math.atan2(
                                direction.y,
                                horizontalDistance
                        )
                                * (180.0D / Math.PI)
                );

        this.setYRot(
                yaw
        );

        this.setXRot(
                pitch
        );

        this.yRotO =
                yaw;

        this.xRotO =
                pitch;
    }

    private void moveProjectile() {
        Vec3 movement =
                getDeltaMovement();

        setPos(
                getX() + movement.x,
                getY() + movement.y,
                getZ() + movement.z
        );
    }

    private void checkTargetCollision(
            LivingEntity target
    ) {
        AABB targetBox =
                target.getBoundingBox()
                        .inflate(
                                HIT_RADIUS
                        );

        if (targetBox.contains(
                position()
        )) {
            explode(
                    target
            );
        }
    }

    private void explode(
            LivingEntity primaryTarget
    ) {
        LivingEntity owner =
                getLivingOwner();

        if (owner == null) {
            discard();
            return;
        }

        float damage =
                getDamage();

        if (damage > 0.0F) {
            primaryTarget.hurt(
                    damageSource(
                            owner
                    ),
                    damage
            );
        }

        primaryTarget.setSecondsOnFire(
                5
        );

        createExplosionVisual();

        applyAreaFire(
                primaryTarget
        );

        discard();
    }

    private void applyAreaFire(
            LivingEntity primaryTarget
    ) {
        AABB area =
                primaryTarget.getBoundingBox()
                        .inflate(
                                1.75D
                        );

        for (LivingEntity entity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        candidate ->
                                candidate != getLivingOwner()
                                        && candidate != primaryTarget
                                        && candidate.isAlive()
                )) {

            entity.setSecondsOnFire(
                    3
            );
        }
    }

    private void createExplosionVisual() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.sendParticles(
                ParticleTypes.FIREWORK,
                getX(),
                getY(),
                getZ(),
                40,
                0.35D,
                0.35D,
                0.35D,
                0.08D
        );

        serverLevel.sendParticles(
                ParticleTypes.FLAME,
                getX(),
                getY(),
                getZ(),
                20,
                0.25D,
                0.25D,
                0.25D,
                0.05D
        );

        serverLevel.playSound(
                null,
                getX(),
                getY(),
                getZ(),
                net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_BLAST,
                net.minecraft.sounds.SoundSource.HOSTILE,
                1.0F,
                1.0F
        );
    }

    private net.minecraft.world.damagesource.DamageSource damageSource(
            LivingEntity owner
    ) {
        var registry =
                level()
                        .registryAccess()
                        .registryOrThrow(
                                net.minecraft.core.registries.Registries.DAMAGE_TYPE
                        );

        var damageType =
                registry.getHolderOrThrow(
                        net.minecraft.world.damagesource.DamageTypes.IN_FIRE
                );

        return new net.minecraft.world.damagesource.DamageSource(
                damageType,
                this,
                owner
        );
    }

    @Override
    protected void readAdditionalSaveData(
            CompoundTag tag
    ) {
        if (tag.hasUUID("Owner")) {
            entityData.set(
                    OWNER,
                    Optional.of(
                            tag.getUUID("Owner")
                    )
            );
        }

        if (tag.hasUUID("Target")) {
            entityData.set(
                    TARGET,
                    Optional.of(
                            tag.getUUID("Target")
                    )
            );
        }

        if (tag.contains("Damage")) {
            entityData.set(
                    DAMAGE,
                    tag.getFloat(
                            "Damage"
                    )
            );
        }

        if (tag.contains("AnimationTick")) {
            entityData.set(
                    ANIMATION_TICK,
                    tag.getInt(
                            "AnimationTick"
                    )
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(
            CompoundTag tag
    ) {
        Optional<UUID> ownerUUID =
                entityData.get(
                        OWNER
                );

        if (ownerUUID.isPresent()) {
            tag.putUUID(
                    "Owner",
                    ownerUUID.get()
            );
        }

        Optional<UUID> targetUUID =
                entityData.get(
                        TARGET
                );

        if (targetUUID.isPresent()) {
            tag.putUUID(
                    "Target",
                    targetUUID.get()
            );
        }

        tag.putFloat(
                "Damage",
                getDamage()
        );

        tag.putInt(
                "AnimationTick",
                getAnimationTick()
        );
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(
            net.minecraft.world.damagesource.DamageSource source,
            float amount
    ) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(
                this
        );
    }
}