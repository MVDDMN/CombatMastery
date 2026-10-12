package com.combatmastery.entity;

import com.combatmastery.sound.ModSounds;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ChainLightningEntity extends Entity {

    private static final int MAX_LIFETIME = 10;

    private static final int MAX_TARGETS = 8;

    private static final double SEARCH_RANGE = 5.0D;

    private static final float STARTING_DAMAGE = 4.0F;

    private static final float MINIMUM_DAMAGE = 2.0F;

    private static final float DAMAGE_REDUCTION = 1.0F;

    private static final float CAST_VOLUME = 0.5F;

    private static final float CAST_MIN_PITCH = 0.92F;

    private static final float CAST_MAX_PITCH = 1.08F;

    private static final float BOUNCE_VOLUME = 0.2F;

    private static final float BOUNCE_MIN_PITCH = 0.88F;

    private static final float BOUNCE_MAX_PITCH = 1.12F;

    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(
                    ChainLightningEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Optional<UUID>> CURRENT_TARGET =
            SynchedEntityData.defineId(
                    ChainLightningEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Optional<UUID>> NEXT_TARGET =
            SynchedEntityData.defineId(
                    ChainLightningEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private final Set<UUID> chainTargets =
            new HashSet<>();

    private int targetNumber = 0;

    public ChainLightningEntity(
            EntityType<? extends ChainLightningEntity> entityType,
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
                CURRENT_TARGET,
                Optional.empty()
        );

        entityData.define(
                NEXT_TARGET,
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

    public void setCurrentTarget(
            LivingEntity target
    ) {
        entityData.set(
                CURRENT_TARGET,
                Optional.of(
                        target.getUUID()
                )
        );
    }

    public LivingEntity getCurrentTarget() {
        Optional<UUID> targetUUID =
                entityData.get(CURRENT_TARGET);

        if (targetUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                targetUUID.get();

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

    public void setNextTarget(
            LivingEntity target
    ) {
        entityData.set(
                NEXT_TARGET,
                Optional.of(
                        target.getUUID()
                )
        );
    }

    public LivingEntity getNextTarget() {
        Optional<UUID> targetUUID =
                entityData.get(NEXT_TARGET);

        if (targetUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                targetUUID.get();

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

    public void setTargetNumber(
            int targetNumber
    ) {
        this.targetNumber =
                targetNumber;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public Set<UUID> getChainTargets() {
        return chainTargets;
    }

    public void addChainTarget(
            UUID uuid
    ) {
        chainTargets.add(uuid);
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

        if (tag.hasUUID("CurrentTarget")) {
            entityData.set(
                    CURRENT_TARGET,
                    Optional.of(
                            tag.getUUID("CurrentTarget")
                    )
            );
        }

        if (tag.hasUUID("NextTarget")) {
            entityData.set(
                    NEXT_TARGET,
                    Optional.of(
                            tag.getUUID("NextTarget")
                    )
            );
        }

        targetNumber =
                tag.getInt("TargetNumber");

        chainTargets.clear();

        if (tag.contains("ChainTargets")) {
            long[] uuids =
                    tag.getLongArray(
                            "ChainTargets"
                    );

            for (
                    int i = 0;
                    i + 1 < uuids.length;
                    i += 2
            ) {
                chainTargets.add(
                        new UUID(
                                uuids[i],
                                uuids[i + 1]
                        )
                );
            }
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

        Optional<UUID> currentTargetUUID =
                entityData.get(
                        CURRENT_TARGET
                );

        if (currentTargetUUID.isPresent()) {
            tag.putUUID(
                    "CurrentTarget",
                    currentTargetUUID.get()
            );
        }

        Optional<UUID> nextTargetUUID =
                entityData.get(
                        NEXT_TARGET
                );

        if (nextTargetUUID.isPresent()) {
            tag.putUUID(
                    "NextTarget",
                    nextTargetUUID.get()
            );
        }

        tag.putInt(
                "TargetNumber",
                targetNumber
        );

        long[] uuids =
                new long[
                        chainTargets.size() * 2
                        ];

        int index = 0;

        for (UUID uuid : chainTargets) {
            uuids[index++] =
                    uuid.getMostSignificantBits();

            uuids[index++] =
                    uuid.getLeastSignificantBits();
        }

        tag.putLongArray(
                "ChainTargets",
                uuids
        );
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        if (tickCount >= MAX_LIFETIME) {
            discard();
            return;
        }

        LivingEntity currentTarget =
                getCurrentTarget();

        if (currentTarget == null) {
            discard();
            return;
        }

        if (!currentTarget.isAlive()) {
            discard();
            return;
        }

        if (nextTargetExists()) {
            return;
        }

        if (targetNumber >= MAX_TARGETS) {
            return;
        }

        LivingEntity nextTarget =
                findNextTarget(
                        currentTarget
                );

        if (nextTarget == null) {
            return;
        }

        setNextTarget(
                nextTarget
        );

        hitTarget(
                nextTarget
        );

        createNextLink(
                currentTarget,
                nextTarget
        );
    }

    private boolean nextTargetExists() {
        return entityData
                .get(NEXT_TARGET)
                .isPresent();
    }

    private LivingEntity findNextTarget(
            LivingEntity currentTarget
    ) {
        AABB searchBox =
                currentTarget
                        .getBoundingBox()
                        .inflate(
                                SEARCH_RANGE
                        );

        LivingEntity owner =
                getOwner();

        LivingEntity closest =
                null;

        double closestDistance =
                Double.MAX_VALUE;

        for (LivingEntity target :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox,
                        entity -> {

                            if (!entity.isAlive()) {
                                return false;
                            }

                            if (entity == owner) {
                                return false;
                            }

                            if (entity == currentTarget) {
                                return false;
                            }

                            if (chainTargets.contains(
                                    entity.getUUID()
                            )) {
                                return false;
                            }

                            return true;
                        }
                )) {

            double distance =
                    getBoundingBoxDistance(
                            currentTarget.getBoundingBox(),
                            target.getBoundingBox()
                    );

            if (distance <= SEARCH_RANGE
                    && distance < closestDistance) {

                closestDistance =
                        distance;

                closest =
                        target;
            }
        }

        return closest;
    }

    private double getBoundingBoxDistance(
            AABB first,
            AABB second
    ) {
        double dx = 0.0D;
        double dy = 0.0D;
        double dz = 0.0D;

        if (first.maxX < second.minX) {
            dx =
                    second.minX
                            - first.maxX;

        } else if (second.maxX < first.minX) {
            dx =
                    first.minX
                            - second.maxX;
        }

        if (first.maxY < second.minY) {
            dy =
                    second.minY
                            - first.maxY;

        } else if (second.maxY < first.minY) {
            dy =
                    first.minY
                            - second.maxY;
        }

        if (first.maxZ < second.minZ) {
            dz =
                    second.minZ
                            - first.maxZ;

        } else if (second.maxZ < first.minZ) {
            dz =
                    first.minZ
                            - second.maxZ;
        }

        return Math.sqrt(
                dx * dx
                        + dy * dy
                        + dz * dz
        );
    }

    private void hitTarget(
            LivingEntity target
    ) {
        LivingEntity owner =
                getOwner();

        if (owner == null) {
            return;
        }

        targetNumber++;

        float damage =
                Math.max(
                        MINIMUM_DAMAGE,
                        STARTING_DAMAGE
                                - (
                                DAMAGE_REDUCTION
                                        * (
                                        targetNumber
                                                - 1
                                )
                        )
                );

        target.hurt(
                damageSource(owner),
                damage
        );

        addChainTarget(
                target.getUUID()
        );
    }

    private void createNextLink(
            LivingEntity currentTarget,
            LivingEntity nextTarget
    ) {
        ChainLightningEntity nextLink =
                ModEntities.CHAIN_LIGHTNING
                        .get()
                        .create(level());

        if (nextLink == null) {
            return;
        }

        nextLink.moveTo(
                currentTarget.getX(),
                currentTarget.getY(),
                currentTarget.getZ(),
                0.0F,
                0.0F
        );

        LivingEntity owner =
                getOwner();

        if (owner == null) {
            return;
        }

        nextLink.setOwner(
                owner
        );

        nextLink.setCurrentTarget(
                nextTarget
        );

        nextLink.setTargetNumber(
                targetNumber
        );

        nextLink.chainTargets.addAll(
                chainTargets
        );

        level().addFreshEntity(
                nextLink
        );

        if (targetNumber == 1) {

            float pitch =
                    CAST_MIN_PITCH
                            + owner.getRandom().nextFloat()
                            * (
                            CAST_MAX_PITCH
                                    - CAST_MIN_PITCH
                    );

            level().playSound(
                    null,
                    owner.getX(),
                    owner.getY(),
                    owner.getZ(),
                    ModSounds.CHAIN_LIGHTNING_CAST.get(),
                    SoundSource.HOSTILE,
                    CAST_VOLUME,
                    pitch
            );

        } else {

            float pitch =
                    BOUNCE_MIN_PITCH
                            + nextTarget.getRandom().nextFloat()
                            * (
                            BOUNCE_MAX_PITCH
                                    - BOUNCE_MIN_PITCH
                    );

            level().playSound(
                    null,
                    nextTarget.getX(),
                    nextTarget.getY(),
                    nextTarget.getZ(),
                    ModSounds.CHAIN_LIGHTNING_BOUNCE.get(),
                    SoundSource.HOSTILE,
                    BOUNCE_VOLUME,
                    pitch
            );
        }
    }

    private DamageSource damageSource(
            LivingEntity attacker
    ) {
        Holder<DamageType> damageType =
                level()
                        .registryAccess()
                        .registryOrThrow(
                                Registries.DAMAGE_TYPE
                        )
                        .getHolderOrThrow(
                                DamageTypes.MAGIC
                        );

        return new DamageSource(
                damageType,
                this,
                attacker
        );
    }

    public static double getSearchRange() {
        return SEARCH_RANGE;
    }
}