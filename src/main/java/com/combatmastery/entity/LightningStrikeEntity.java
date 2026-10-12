package com.combatmastery.entity;

import com.combatmastery.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Optional;
import java.util.UUID;

public class LightningStrikeEntity extends Entity {

    private static final int DELAY = 20;

    private static final int PARTICLE_INTERVAL = 4;

    private static final float PING_VOLUME = 0.3F;

    private static final float PING_PITCH_START = 1.00F;

    private static final float PING_PITCH_STEP = 0.25F;

    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(
                    LightningStrikeEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Optional<UUID>> TARGET =
            SynchedEntityData.defineId(
                    LightningStrikeEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    public LightningStrikeEntity(
            EntityType<? extends LightningStrikeEntity> entityType,
            Level level
    ) {
        super(entityType, level);
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
    }

    public LivingEntity getOwner() {

        Optional<UUID> ownerUUID =
                entityData.get(OWNER);

        if (ownerUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                ownerUUID.get();

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
                entityData.get(TARGET);

        if (targetUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                targetUUID.get();

        AABB searchBox =
                getBoundingBox().inflate(64.0D);

        for (LivingEntity entity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox,
                        entity ->
                                entity.getUUID().equals(uuid)
                )) {

            return entity;
        }

        return null;
    }

    public static boolean hasActiveStrike(
            LivingEntity target
    ) {

        return !target.level().getEntitiesOfClass(
                LightningStrikeEntity.class,
                target.getBoundingBox().inflate(64.0D),
                strike -> {

                    if (!strike.isAlive()) {
                        return false;
                    }

                    LivingEntity strikeTarget =
                            strike.getTarget();

                    return strikeTarget != null
                            && strikeTarget.getUUID()
                            .equals(target.getUUID());
                }
        ).isEmpty();
    }

    @Override
    public void tick() {

        super.tick();

        if (level().isClientSide) {
            return;
        }

        LivingEntity target =
                getTarget();

        if (target == null) {
            discard();
            return;
        }

        if (!target.isAlive()) {
            discard();
            return;
        }

        moveTo(
                target.getX(),
                target.getY(),
                target.getZ()
        );

        if (tickCount > 0
                && tickCount < DELAY
                && tickCount % PARTICLE_INTERVAL == 0) {

            spawnCloudParticles(target);

            int pingNumber =
                    tickCount / PARTICLE_INTERVAL;

            float pitch =
                    PING_PITCH_START
                            + ((pingNumber - 1)
                            * PING_PITCH_STEP);

            level().playSound(
                    null,
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    ModSounds.LIGHTNING_STRIKE_PING.get(),
                    SoundSource.HOSTILE,
                    PING_VOLUME,
                    pitch
            );
        }

        if (tickCount >= DELAY) {

            strike(target);

            discard();
        }
    }

    private void spawnCloudParticles(
            LivingEntity target
    ) {

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        double x =
                target.getX();

        double y =
                target.getY()
                        + 0.15D;

        double z =
                target.getZ();

        serverLevel.sendParticles(
                net.minecraft.core.particles.ParticleTypes.CLOUD,
                x,
                y,
                z,
                8,
                0.45D,
                0.05D,
                0.45D,
                0.01D
        );
    }

    private void strike(
            LivingEntity target
    ) {

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        net.minecraft.world.entity.LightningBolt lightning =
                EntityType.LIGHTNING_BOLT.create(
                        serverLevel
                );

        if (lightning == null) {
            return;
        }

        lightning.moveTo(
                target.getX(),
                target.getY(),
                target.getZ()
        );

        lightning.setVisualOnly(false);

        serverLevel.addFreshEntity(
                lightning
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
    }
}