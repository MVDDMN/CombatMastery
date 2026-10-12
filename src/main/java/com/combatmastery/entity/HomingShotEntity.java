package com.combatmastery.entity;

import com.combatmastery.combat.HomingShotHandler;
import com.combatmastery.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class HomingShotEntity extends Projectile {

    private static final int MAX_LIFETIME = 100;
    private static final double HOMING_STRENGTH = 0.35D;
    private static final double PROJECTILE_SPEED = 2.0D;
    private static final double HIT_RADIUS = 0.65D;
    private static final int PIERCING_TICKS = 5;
    private static final int SHOT_SPAWN_INTERVAL = 5;

    private static final double SIDE_OFFSET = 2.0D;
    private static final double OFFSET = 2.0D;
    private static final double HEIGHT_OFFSET = 0.15D;

    private static final double FORMATION_SMOOTHING = 0.45D;

    private static final float FLY_MIN_PITCH = 0.5F;
    private static final float FLY_MAX_PITCH = 1.10F;
    private static final float FLY_VOLUME = 0.2F;

    private static final float SUMMON_MIN_PITCH = 0.90F;
    private static final float SUMMON_MAX_PITCH = 1.2F;
    private static final float SUMMON_VOLUME = 0.4F;

    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Optional<UUID>> TARGET =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<ItemStack> WEAPON =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.ITEM_STACK
            );

    private static final EntityDataAccessor<Float> ORIGINAL_DAMAGE =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.FLOAT
            );

    private static final EntityDataAccessor<Integer> SEQUENCE_INDEX =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Long> RELEASE_GAME_TIME =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.LONG
            );

    private static final EntityDataAccessor<Boolean> PIERCING =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    private static final EntityDataAccessor<Integer> PIERCING_TICKS_REMAINING =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Optional<UUID>> HIT_TARGET =
            SynchedEntityData.defineId(
                    HomingShotEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    public HomingShotEntity(
            EntityType<? extends HomingShotEntity> entityType,
            Level level
    ) {
        super(entityType, level);
        setNoGravity(true);
    }

    public static double getProjectileSpeed() {
        return PROJECTILE_SPEED;
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
                WEAPON,
                ItemStack.EMPTY
        );

        entityData.define(
                ORIGINAL_DAMAGE,
                1.0F
        );

        entityData.define(
                SEQUENCE_INDEX,
                0
        );

        entityData.define(
                RELEASE_GAME_TIME,
                0L
        );

        entityData.define(
                PIERCING,
                false
        );

        entityData.define(
                PIERCING_TICKS_REMAINING,
                0
        );

        entityData.define(
                HIT_TARGET,
                Optional.empty()
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

        setOwner(
                (Entity) owner
        );
    }

    @Override
    public void setOwner(
            Entity owner
    ) {
        super.setOwner(owner);

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
                entityData.get(OWNER);

        if (ownerUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                ownerUUID.get();

        if (level() instanceof ServerLevel serverLevel) {
            Entity entity =
                    serverLevel.getEntity(uuid);

            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        for (LivingEntity entity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(64.0D)
                )) {

            if (entity.getUUID().equals(uuid)) {
                return entity;
            }
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

    public boolean isTarget(
            LivingEntity target
    ) {
        Optional<UUID> targetUUID =
                entityData.get(TARGET);

        return targetUUID.isPresent()
                && targetUUID.get()
                .equals(target.getUUID());
    }

    public LivingEntity getTarget() {
        Optional<UUID> targetUUID =
                entityData.get(TARGET);

        if (targetUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                targetUUID.get();

        if (level() instanceof ServerLevel serverLevel) {
            Entity entity =
                    serverLevel.getEntity(uuid);

            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        for (LivingEntity entity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(64.0D)
                )) {

            if (entity.getUUID().equals(uuid)) {
                return entity;
            }
        }

        return null;
    }

    public void setWeapon(
            ItemStack weapon
    ) {
        entityData.set(
                WEAPON,
                weapon.copy()
        );
    }

    public ItemStack getWeapon() {
        return entityData.get(WEAPON);
    }

    public void setOriginalDamage(
            float damage
    ) {
        entityData.set(
                ORIGINAL_DAMAGE,
                Math.max(
                        0.0F,
                        damage
                )
        );
    }

    public float getOriginalDamage() {
        return entityData.get(
                ORIGINAL_DAMAGE
        );
    }

    public void setSequenceIndex(
            int sequenceIndex
    ) {
        entityData.set(
                SEQUENCE_INDEX,
                Math.max(
                        0,
                        sequenceIndex
                )
        );
    }

    public int getSequenceIndex() {
        return entityData.get(
                SEQUENCE_INDEX
        );
    }

    public void setReleaseGameTime(
            long releaseGameTime
    ) {
        entityData.set(
                RELEASE_GAME_TIME,
                releaseGameTime
        );
    }

    public long getReleaseGameTime() {
        return entityData.get(
                RELEASE_GAME_TIME
        );
    }

    public boolean isPiercing() {
        return entityData.get(PIERCING);
    }

    private void startPiercing(
            LivingEntity target
    ) {
        entityData.set(
                PIERCING,
                true
        );

        entityData.set(
                PIERCING_TICKS_REMAINING,
                PIERCING_TICKS
        );

        entityData.set(
                HIT_TARGET,
                Optional.of(
                        target.getUUID()
                )
        );
    }

    private boolean hasHitTarget(
            LivingEntity target
    ) {
        Optional<UUID> hitUUID =
                entityData.get(HIT_TARGET);

        return hitUUID.isPresent()
                && hitUUID.get()
                .equals(
                        target.getUUID()
                );
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount >= MAX_LIFETIME) {
            discard();
            return;
        }

        if (level().isClientSide) {
            spawnClientParticles();
            return;
        }

        if (tickCount == 1) {
            playSummonSound();
        }

        if (isPiercing()) {
            tickPiercing();
            updateRotation();
            return;
        }

        LivingEntity target =
                getTarget();

        if (target == null
                || !target.isAlive()
                || target.isDeadOrDying()) {
            discard();
            return;
        }

        if (tickCount == SHOT_SPAWN_INTERVAL
                && getSequenceIndex() < 2) {

            HomingShotHandler.spawnNextHomingShot(
                    this
            );
        }

        if (level().getGameTime()
                < getReleaseGameTime()) {

            followCasterFormation();

            return;
        }

        if (level().getGameTime()
                == getReleaseGameTime()) {
            playFlySound();
        }

        Vec3 movement =
                getDeltaMovement();

        if (movement.lengthSqr()
                < 0.000001D) {

            Vec3 targetDirection =
                    target.getBoundingBox()
                            .getCenter()
                            .subtract(position());

            if (targetDirection.lengthSqr()
                    < 0.000001D) {

                targetDirection =
                        new Vec3(
                                0.0D,
                                0.0D,
                                1.0D
                        );
            }

            movement =
                    targetDirection.normalize()
                            .scale(
                                    PROJECTILE_SPEED
                            );

            setDeltaMovement(
                    movement
            );
        }

        updateHoming(target);

        movement =
                getDeltaMovement();

        setPos(
                position().add(movement)
        );

        updateRotation();

        checkHit(target);
    }

    private void playSummonSound() {
        float pitch =
                SUMMON_MIN_PITCH
                        + random.nextFloat()
                        * (
                        SUMMON_MAX_PITCH
                                - SUMMON_MIN_PITCH
                );

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.HOMING_SHOT_SUMMON.get(),
                SoundSource.HOSTILE,
                SUMMON_VOLUME,
                pitch
        );
    }

    private void playFlySound() {
        float pitch =
                FLY_MIN_PITCH
                        + random.nextFloat()
                        * (
                        FLY_MAX_PITCH
                                - FLY_MIN_PITCH
                );

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.HOMING_SHOT_FLY.get(),
                SoundSource.HOSTILE,
                FLY_VOLUME,
                pitch
        );
    }

    private void spawnClientParticles() {
        if (tickCount == 1) {
            spawnSummonParticles();
        }

        if (level().getGameTime()
                >= getReleaseGameTime()
                && !isPiercing()) {

            spawnHomingTrail();
        }
    }

    private void spawnSummonParticles() {
        for (int i = 0; i < 12; i++) {
            double offsetX =
                    (random.nextDouble() - 0.5D)
                            * 0.6D;

            double offsetY =
                    random.nextDouble()
                            * 0.8D;

            double offsetZ =
                    (random.nextDouble() - 0.5D)
                            * 0.6D;

            level().addParticle(
                    ParticleTypes.SOUL,
                    getX() + offsetX,
                    getY() + offsetY,
                    getZ() + offsetZ,
                    0.0D,
                    0.04D,
                    0.0D
            );
        }
    }

    private void spawnHomingTrail() {
        Vec3 movement =
                getDeltaMovement();

        if (movement.lengthSqr()
                < 0.000001D) {
            return;
        }

        Vec3 direction =
                movement.normalize();

        Vec3 trailPosition =
                position()
                        .subtract(
                                direction.scale(
                                        0.35D
                                )
                        );

        level().addParticle(
                ParticleTypes.CLOUD,
                trailPosition.x,
                trailPosition.y,
                trailPosition.z,
                0.0D,
                0.0D,
                0.0D
        );
    }

    public void setFormationPositionImmediately() {
        LivingEntity owner =
                getLivingOwner();

        if (owner == null
                || !owner.isAlive()) {
            return;
        }

        setPos(
                getFormationPosition(owner)
        );

        setDeltaMovement(
                Vec3.ZERO
        );

        setYRot(
                owner.getYRot()
        );

        setXRot(
                0.0F
        );

        setYHeadRot(
                owner.getYRot()
        );
    }

    private Vec3 getFormationPosition(
            LivingEntity owner
    ) {
        float yawRadians =
                owner.getYRot()
                        * ((float) Math.PI / 180.0F);

        Vec3 forward =
                new Vec3(
                        -Math.sin(yawRadians),
                        0.0D,
                        Math.cos(yawRadians)
                );

        Vec3 right =
                new Vec3(
                        Math.cos(yawRadians),
                        0.0D,
                        Math.sin(yawRadians)
                );

        double sideDistance =
                (getSequenceIndex() - 1)
                        * SIDE_OFFSET;

        Vec3 front =
                forward.scale(
                        OFFSET
                );

        Vec3 side =
                right.scale(
                        sideDistance
                );

        Vec3 height =
                new Vec3(
                        0.0D,
                        owner.getBbHeight()
                                * 0.55D
                                + HEIGHT_OFFSET,
                        0.0D
                );

        return owner.position()
                .add(front)
                .add(side)
                .add(height);
    }

    private void followCasterFormation() {
        LivingEntity owner =
                getLivingOwner();

        if (owner == null
                || !owner.isAlive()) {
            discard();
            return;
        }

        Vec3 targetPosition =
                getFormationPosition(owner);

        Vec3 currentPosition =
                position();

        Vec3 difference =
                targetPosition.subtract(
                        currentPosition
                );

        if (difference.lengthSqr()
                < 0.000001D) {

            setPos(
                    targetPosition
            );

        } else {

            Vec3 smoothedPosition =
                    currentPosition.add(
                            difference.scale(
                                    FORMATION_SMOOTHING
                            )
                    );

            setPos(
                    smoothedPosition
            );
        }

        setDeltaMovement(
                Vec3.ZERO
        );

        setYRot(
                owner.getYRot()
        );

        setXRot(
                0.0F
        );

        setYHeadRot(
                owner.getYRot()
        );
    }

    private void updateHoming(
            LivingEntity target
    ) {
        Vec3 targetCenter =
                target.getBoundingBox()
                        .getCenter();

        Vec3 desiredDirection =
                targetCenter.subtract(position());

        if (desiredDirection.lengthSqr()
                < 0.000001D) {
            return;
        }

        desiredDirection =
                desiredDirection.normalize();

        Vec3 currentVelocity =
                getDeltaMovement();

        Vec3 currentDirection;

        if (currentVelocity.lengthSqr()
                < 0.000001D) {

            currentDirection =
                    desiredDirection;

        } else {

            currentDirection =
                    currentVelocity.normalize();
        }

        Vec3 homingDirection =
                currentDirection
                        .scale(
                                1.0D
                                        - HOMING_STRENGTH
                        )
                        .add(
                                desiredDirection
                                        .scale(
                                                HOMING_STRENGTH
                                        )
                        );

        if (homingDirection.lengthSqr()
                < 0.000001D) {

            homingDirection =
                    desiredDirection;
        }

        homingDirection =
                homingDirection.normalize();

        setDeltaMovement(
                homingDirection.scale(
                        PROJECTILE_SPEED
                )
        );
    }

    private void tickPiercing() {
        Vec3 movement =
                getDeltaMovement();

        if (movement.lengthSqr()
                < 0.000001D) {
            discard();
            return;
        }

        setPos(
                position().add(movement)
        );

        int remaining =
                entityData.get(
                        PIERCING_TICKS_REMAINING
                );

        remaining--;

        entityData.set(
                PIERCING_TICKS_REMAINING,
                remaining
        );

        if (remaining <= 0) {
            discard();
        }
    }

    @Override
    protected void updateRotation() {
        Vec3 movement =
                getDeltaMovement();

        if (movement.lengthSqr()
                < 0.000001D) {
            return;
        }

        double horizontalDistance =
                Math.sqrt(
                        movement.x * movement.x
                                + movement.z * movement.z
                );

        float yaw =
                (float) (
                        Math.atan2(
                                movement.z,
                                movement.x
                        )
                                * (180.0D / Math.PI)
                                - 90.0D
                );

        float pitch =
                (float) (
                        -Math.atan2(
                                movement.y,
                                horizontalDistance
                        )
                                * (180.0D / Math.PI)
                );

        setYRot(yaw);
        setXRot(pitch);
        setYHeadRot(yaw);
    }

    private void checkHit(
            LivingEntity target
    ) {
        if (hasHitTarget(target)) {
            return;
        }

        AABB hitbox =
                getBoundingBox()
                        .inflate(HIT_RADIUS);

        if (!target.getBoundingBox()
                .intersects(hitbox)) {
            return;
        }

        LivingEntity owner =
                getLivingOwner();

        if (owner == null) {
            discard();
            return;
        }

        if (target == owner) {
            return;
        }

        target.hurt(
                level().damageSources()
                        .mobAttack(owner),
                getOriginalDamage()
        );

        startPiercing(target);
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

        if (tag.contains("Weapon")) {
            setWeapon(
                    ItemStack.of(
                            tag.getCompound("Weapon")
                    )
            );
        }

        if (tag.contains("OriginalDamage")) {
            setOriginalDamage(
                    tag.getFloat("OriginalDamage")
            );
        }

        if (tag.contains("SequenceIndex")) {
            setSequenceIndex(
                    tag.getInt("SequenceIndex")
            );
        }

        if (tag.contains("ReleaseGameTime")) {
            setReleaseGameTime(
                    tag.getLong("ReleaseGameTime")
            );
        }

        if (tag.contains("Piercing")) {
            entityData.set(
                    PIERCING,
                    tag.getBoolean("Piercing")
            );
        }

        if (tag.contains("PiercingTicksRemaining")) {
            entityData.set(
                    PIERCING_TICKS_REMAINING,
                    tag.getInt(
                            "PiercingTicksRemaining"
                    )
            );
        }

        if (tag.hasUUID("HitTarget")) {
            entityData.set(
                    HIT_TARGET,
                    Optional.of(
                            tag.getUUID("HitTarget")
                    )
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(
            CompoundTag tag
    ) {
        Optional<UUID> ownerUUID =
                entityData.get(OWNER);

        if (ownerUUID.isPresent()) {
            tag.putUUID(
                    "Owner",
                    ownerUUID.get()
            );
        }

        Optional<UUID> targetUUID =
                entityData.get(TARGET);

        if (targetUUID.isPresent()) {
            tag.putUUID(
                    "Target",
                    targetUUID.get()
            );
        }

        if (!getWeapon().isEmpty()) {
            tag.put(
                    "Weapon",
                    getWeapon().save(
                            new CompoundTag()
                    )
            );
        }

        tag.putFloat(
                "OriginalDamage",
                getOriginalDamage()
        );

        tag.putInt(
                "SequenceIndex",
                getSequenceIndex()
        );

        tag.putLong(
                "ReleaseGameTime",
                getReleaseGameTime()
        );

        tag.putBoolean(
                "Piercing",
                isPiercing()
        );

        tag.putInt(
                "PiercingTicksRemaining",
                entityData.get(
                        PIERCING_TICKS_REMAINING
                )
        );

        Optional<UUID> hitTargetUUID =
                entityData.get(HIT_TARGET);

        if (hitTargetUUID.isPresent()) {
            tag.putUUID(
                    "HitTarget",
                    hitTargetUUID.get()
            );
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(
            net.minecraft.world.damagesource.DamageSource source,
            float amount
    ) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener>
    getAddEntityPacket() {
        return NetworkHooks
                .getEntitySpawningPacket(
                        this
                );
    }
}