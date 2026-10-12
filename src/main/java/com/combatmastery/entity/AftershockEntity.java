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

public class AftershockEntity extends Entity {

    private static final int MAX_LIFETIME = 5;

    private static final double HITBOX_WIDTH = 5.0D;
    private static final double HITBOX_HEIGHT = 1.0D;
    private static final double HITBOX_DEPTH = 5.0D;

    private static final double DAMAGE_PERCENT = 0.10D;

    private static final float SOUND_VOLUME = 0.5F;

    private static final float MIN_PITCH = 0.7F;
    private static final float MAX_PITCH = 1.5F;

    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(
                    AftershockEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private final Set<UUID> hitEntities = new HashSet<>();

    public AftershockEntity(
            EntityType<? extends AftershockEntity> entityType,
            Level level
    ) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER, Optional.empty());
    }

    public void setOwner(LivingEntity owner) {
        entityData.set(
                OWNER,
                Optional.of(owner.getUUID())
        );
    }

    public LivingEntity getOwner() {
        Optional<UUID> ownerUUID = entityData.get(OWNER);

        if (ownerUUID.isEmpty()) {
            return null;
        }

        UUID uuid = ownerUUID.get();

        for (LivingEntity entity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(64.0D),
                        entity -> entity.getUUID().equals(uuid)
                )) {
            return entity;
        }

        return null;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            entityData.set(
                    OWNER,
                    Optional.of(tag.getUUID("Owner"))
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        Optional<UUID> ownerUUID = entityData.get(OWNER);

        if (ownerUUID.isPresent()) {
            tag.putUUID(
                    "Owner",
                    ownerUUID.get()
            );
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        if (tickCount == 1) {
            playAftershockSound();
        }

        if (tickCount >= MAX_LIFETIME) {
            discard();
            return;
        }

        checkHitEntities();
    }

    private void playAftershockSound() {
        float pitch =
                MIN_PITCH
                        + (
                        random.nextFloat()
                                * (MAX_PITCH - MIN_PITCH)
                );

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.AFTERSHOCK.get(),
                SoundSource.HOSTILE,
                SOUND_VOLUME,
                pitch
        );
    }

    private void checkHitEntities() {
        LivingEntity owner = getOwner();

        AABB hitbox =
                new AABB(
                        getX() - HITBOX_WIDTH / 2.0D,
                        getY(),
                        getZ() - HITBOX_DEPTH / 2.0D,
                        getX() + HITBOX_WIDTH / 2.0D,
                        getY() + HITBOX_HEIGHT,
                        getZ() + HITBOX_DEPTH / 2.0D
                );

        for (LivingEntity target :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        hitbox,
                        entity -> {
                            if (entity == owner) return false;
                            if (!entity.isAlive()) return false;

                            return !hitEntities.contains(
                                    entity.getUUID()
                            );
                        }
                )) {
            hitEntity(target);
        }
    }

    private void hitEntity(LivingEntity target) {
        LivingEntity owner = getOwner();

        if (owner == null) return;

        // Aftershock damage is based on the attacker's MAXIMUM HEALTH.
        float damage =
                (float) (
                        owner.getMaxHealth()
                                * DAMAGE_PERCENT
                );

        if (damage <= 0.0F) return;

        target.hurt(
                damageSource(owner),
                damage
        );

        hitEntities.add(
                target.getUUID()
        );
    }

    private DamageSource damageSource(
            LivingEntity attacker
    ) {
        Holder<DamageType> damageType;

        if (attacker instanceof net.minecraft.world.entity.player.Player) {
            damageType =
                    level()
                            .registryAccess()
                            .registryOrThrow(
                                    Registries.DAMAGE_TYPE
                            )
                            .getHolderOrThrow(
                                    DamageTypes.PLAYER_ATTACK
                            );
        } else {
            damageType =
                    level()
                            .registryAccess()
                            .registryOrThrow(
                                    Registries.DAMAGE_TYPE
                            )
                            .getHolderOrThrow(
                                    DamageTypes.MOB_ATTACK
                            );
        }

        return new DamageSource(
                damageType,
                this,
                attacker
        );
    }
}