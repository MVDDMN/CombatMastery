package com.combatmastery.entity;

import com.combatmastery.combat.DiceRollHandler;
import com.combatmastery.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;

public class DiceRollEntity extends Entity {

    private static final int ROLLING_TICKS = 20;
    private static final int RESULT_TICKS = 10;
    private static final int TOTAL_TICKS =
            ROLLING_TICKS + RESULT_TICKS;

    private static final int ROLL_INTERVAL = 2;

    private static final float ROLL_VOLUME = 0.65F;

    private static final EntityDataAccessor<Integer> RESULT =
            SynchedEntityData.defineId(
                    DiceRollEntity.class,
                    EntityDataSerializers.INT
            );

    private static final EntityDataAccessor<Float> ORIGINAL_DAMAGE =
            SynchedEntityData.defineId(
                    DiceRollEntity.class,
                    EntityDataSerializers.FLOAT
            );

    private static final EntityDataAccessor<Optional<UUID>> TARGET_UUID =
            SynchedEntityData.defineId(
                    DiceRollEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Optional<UUID>> CASTER_UUID =
            SynchedEntityData.defineId(
                    DiceRollEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    public DiceRollEntity(
            EntityType<? extends DiceRollEntity> entityType,
            Level level
    ) {
        super(entityType, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData() {

        this.entityData.define(
                RESULT,
                1
        );

        this.entityData.define(
                ORIGINAL_DAMAGE,
                0.0F
        );

        this.entityData.define(
                TARGET_UUID,
                Optional.empty()
        );

        this.entityData.define(
                CASTER_UUID,
                Optional.empty()
        );
    }

    public void setTarget(
            LivingEntity target
    ) {

        this.entityData.set(
                TARGET_UUID,
                Optional.of(
                        target.getUUID()
                )
        );

        updatePosition(target);
    }

    public void setCaster(
            LivingEntity caster
    ) {

        this.entityData.set(
                CASTER_UUID,
                Optional.of(
                        caster.getUUID()
                )
        );
    }

    public LivingEntity getTarget() {

        Optional<UUID> optionalUUID =
                this.entityData.get(
                        TARGET_UUID
                );

        if (optionalUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                optionalUUID.get();

        if (level() instanceof ServerLevel serverLevel) {

            Entity entity =
                    serverLevel.getEntity(uuid);

            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        for (LivingEntity livingEntity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(64.0D)
                )) {

            if (livingEntity.getUUID().equals(uuid)) {
                return livingEntity;
            }
        }

        return null;
    }

    public LivingEntity getCaster() {

        Optional<UUID> optionalUUID =
                this.entityData.get(
                        CASTER_UUID
                );

        if (optionalUUID.isEmpty()) {
            return null;
        }

        UUID uuid =
                optionalUUID.get();

        if (level() instanceof ServerLevel serverLevel) {

            Entity entity =
                    serverLevel.getEntity(uuid);

            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        for (LivingEntity livingEntity :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().inflate(64.0D)
                )) {

            if (livingEntity.getUUID().equals(uuid)) {
                return livingEntity;
            }
        }

        return null;
    }

    public void setResult(
            int result
    ) {

        this.entityData.set(
                RESULT,
                Math.max(
                        1,
                        Math.min(
                                6,
                                result
                        )
                )
        );
    }

    public int getResult() {
        return this.entityData.get(RESULT);
    }

    public void setOriginalDamage(
            float damage
    ) {

        this.entityData.set(
                ORIGINAL_DAMAGE,
                Math.max(
                        0.0F,
                        damage
                )
        );
    }

    public float getOriginalDamage() {
        return this.entityData.get(
                ORIGINAL_DAMAGE
        );
    }

    public boolean isRolling() {

        return tickCount < ROLLING_TICKS;
    }

    public boolean isShowingResult() {

        return tickCount >= ROLLING_TICKS
                && tickCount < TOTAL_TICKS;
    }

    public int getDisplayedFace() {

        if (!isRolling()) {
            return getResult();
        }

        int animationStep =
                tickCount / ROLL_INTERVAL;

        return (animationStep % 6) + 1;
    }

    private void updatePosition(
            LivingEntity target
    ) {

        setPos(
                target.getX(),
                target.getY()
                        + target.getBbHeight()
                        + 0.8D,
                target.getZ()
        );
    }

    private void updateClientPosition(
            LivingEntity target
    ) {

        double targetX =
                Mth.lerp(
                        0.5D,
                        target.xOld,
                        target.getX()
                );

        double targetY =
                Mth.lerp(
                        0.5D,
                        target.yOld,
                        target.getY()
                );

        double targetZ =
                Mth.lerp(
                        0.5D,
                        target.zOld,
                        target.getZ()
                );

        setPos(
                targetX,
                targetY
                        + target.getBbHeight()
                        + 0.8D,
                targetZ
        );
    }

    private void playRollSound() {

        if (level().isClientSide) {
            return;
        }

        float pitch =
                0.90F
                        + level().getRandom().nextFloat()
                        * 0.20F;

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.DICE_ROLL_ROLL.get(),
                SoundSource.HOSTILE,
                ROLL_VOLUME,
                pitch
        );
    }

    @Override
    public void tick() {

        super.tick();

        LivingEntity target =
                getTarget();

        if (target == null
                || !target.isAlive()) {

            discard();
            return;
        }

        if (level().isClientSide) {

            updateClientPosition(target);

        } else {

            updatePosition(target);

            if (tickCount == 1) {

                playRollSound();
            }

            if (tickCount >= TOTAL_TICKS) {

                DiceRollHandler.playResultSound(this);

                DiceRollHandler.applyResult(
                        this
                );

                discard();

                return;
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(
            CompoundTag tag
    ) {

        if (tag.contains("Result")) {

            setResult(
                    tag.getInt("Result")
            );
        }

        if (tag.contains("OriginalDamage")) {

            setOriginalDamage(
                    tag.getFloat(
                            "OriginalDamage"
                    )
            );
        }

        if (tag.hasUUID("Target")) {

            this.entityData.set(
                    TARGET_UUID,
                    Optional.of(
                            tag.getUUID("Target")
                    )
            );
        }

        if (tag.hasUUID("Caster")) {

            this.entityData.set(
                    CASTER_UUID,
                    Optional.of(
                            tag.getUUID("Caster")
                    )
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(
            CompoundTag tag
    ) {

        tag.putInt(
                "Result",
                getResult()
        );

        tag.putFloat(
                "OriginalDamage",
                getOriginalDamage()
        );

        Optional<UUID> targetUUID =
                this.entityData.get(
                        TARGET_UUID
                );

        if (targetUUID.isPresent()) {

            tag.putUUID(
                    "Target",
                    targetUUID.get()
            );
        }

        Optional<UUID> casterUUID =
                this.entityData.get(
                        CASTER_UUID
                );

        if (casterUUID.isPresent()) {

            tag.putUUID(
                    "Caster",
                    casterUUID.get()
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
}