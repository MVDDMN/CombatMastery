package com.combatmastery.entity;

import com.combatmastery.particle.ModParticles;
import com.combatmastery.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TornadoEntity extends Projectile {

    private static final int MAX_LIFETIME = 80;

    private static final double FORWARD_SPEED = 0.25D;

    private static final double TORNADO_HEIGHT = 3.0D;

    private static final double MAX_RADIUS = 1.75D;

    private static final double MIN_RADIUS = 0.08D;

    private static final double HIT_RADIUS = 1.5D;

    private static final double HIT_HEIGHT = 3.0D;

    private static final double LAUNCH_VELOCITY = 1.0D;

    private static final int PARTICLES_PER_LAYER = 2;

    private static final int PARTICLE_LAYERS = 7;

    private static final double ROTATION_SPEED = 0.42D;

    private static final int FLY_SOUND_INTERVAL = 20;

    private static final float TORNADO_FLY_VOLUME = 0.6F;

    private static final float TORNADO_FLY_MIN_PITCH = 0.90F;

    private static final float TORNADO_FLY_MAX_PITCH = 1.10F;

    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(
                    TornadoEntity.class,
                    EntityDataSerializers.OPTIONAL_UUID
            );

    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(
                    TornadoEntity.class,
                    EntityDataSerializers.FLOAT
            );

    private final Set<UUID> hitEntities =
            new HashSet<>();

    private boolean hasRicocheted = false;

    public TornadoEntity(
            EntityType<? extends TornadoEntity> entityType,
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
                DAMAGE,
                1.0F
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

    public LivingEntity getOwnerLivingEntity() {
        if (level().isClientSide) {
            return null;
        }

        Optional<UUID> ownerUUID =
                entityData.get(
                        OWNER
                );

        if (ownerUUID.isEmpty()) {
            return null;
        }

        Entity entity =
                ((net.minecraft.server.level.ServerLevel) level())
                        .getEntity(
                                ownerUUID.get()
                        );

        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        return null;
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

    @Override
    public void tick() {
        super.tick();

        if (tickCount >= MAX_LIFETIME) {
            discard();
            return;
        }

        if (level().isClientSide) {
            spawnTornadoParticles();
            moveForward();
            return;
        }

        moveForward();

        playFlyingSound();

        handleEntityHits();
    }

    private void playFlyingSound() {
        if (tickCount != 1 && tickCount % FLY_SOUND_INTERVAL != 0) {
            return;
        }

        float pitch =
                TORNADO_FLY_MIN_PITCH
                        + random.nextFloat()
                        * (
                        TORNADO_FLY_MAX_PITCH
                                - TORNADO_FLY_MIN_PITCH
                );

        level().playSound(
                null,
                getX(),
                getY(),
                getZ(),
                ModSounds.TORNADO_FLY.get(),
                SoundSource.PLAYERS,
                TORNADO_FLY_VOLUME,
                pitch
        );
    }

    private void moveForward() {
        Vec3 forward =
                getForwardDirection();

        Vec3 movement =
                forward.scale(
                        FORWARD_SPEED
                );

        if (!hasRicocheted) {
            BlockHitResult hitResult =
                    level().clip(
                            new net.minecraft.world.level.ClipContext(
                                    position(),
                                    position().add(movement),
                                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                                    this
                            )
                    );

            if (hitResult.getType() != BlockHitResult.Type.MISS) {
                ricochet(
                        hitResult.getDirection(),
                        forward
                );

                forward =
                        getForwardDirection();

                movement =
                        forward.scale(
                                FORWARD_SPEED
                        );
            }
        }

        setDeltaMovement(
                movement
        );

        setPos(
                getX() + movement.x,
                getY() + movement.y,
                getZ() + movement.z
        );
    }

    private void ricochet(
            net.minecraft.core.Direction hitDirection,
            Vec3 currentDirection
    ) {
        Vec3 reflectedDirection =
                currentDirection;

        if (hitDirection.getAxis()
                == net.minecraft.core.Direction.Axis.X) {
            reflectedDirection =
                    new Vec3(
                            -currentDirection.x,
                            currentDirection.y,
                            currentDirection.z
                    );
        } else if (hitDirection.getAxis()
                == net.minecraft.core.Direction.Axis.Z) {
            reflectedDirection =
                    new Vec3(
                            currentDirection.x,
                            currentDirection.y,
                            -currentDirection.z
                    );
        } else if (hitDirection.getAxis()
                == net.minecraft.core.Direction.Axis.Y) {
            reflectedDirection =
                    new Vec3(
                            currentDirection.x,
                            -currentDirection.y,
                            currentDirection.z
                    );
        }

        reflectedDirection =
                reflectedDirection.normalize();

        setYRot(
                (float) Math.toDegrees(
                        Math.atan2(
                                -reflectedDirection.x,
                                reflectedDirection.z
                        )
                )
        );

        setXRot(0.0F);

        hasRicocheted = true;
    }

    private Vec3 getForwardDirection() {
        float yaw =
                getYRot();

        double radians =
                Math.toRadians(
                        yaw
                );

        return new Vec3(
                -Math.sin(radians),
                0.0D,
                Math.cos(radians)
        ).normalize();
    }

    private void handleEntityHits() {
        LivingEntity owner =
                getOwnerLivingEntity();

        AABB tornadoBox =
                new AABB(
                        getX() - HIT_RADIUS,
                        getY(),
                        getZ() - HIT_RADIUS,
                        getX() + HIT_RADIUS,
                        getY() + HIT_HEIGHT,
                        getZ() + HIT_RADIUS
                );

        for (LivingEntity target :
                level().getEntitiesOfClass(
                        LivingEntity.class,
                        tornadoBox
                )) {

            if (!target.isAlive()
                    || target.isDeadOrDying()) {
                continue;
            }

            if (target == owner) {
                continue;
            }

            if (hitEntities.contains(
                    target.getUUID()
            )) {
                continue;
            }

            if (!isInsideHitbox(
                    target
            )) {
                continue;
            }

            hitEntities.add(
                    target.getUUID()
            );

            damageTarget(
                    target,
                    owner
            );

            launchTarget(
                    target
            );
        }
    }

    private boolean isInsideHitbox(
            LivingEntity target
    ) {
        double relativeHeight =
                target.getY()
                        - getY();

        if (relativeHeight < 0.0D
                || relativeHeight > HIT_HEIGHT) {
            return false;
        }

        double dx =
                target.getX()
                        - getX();

        double dz =
                target.getZ()
                        - getZ();

        double horizontalDistance =
                Math.sqrt(
                        dx * dx
                                + dz * dz
                );

        return horizontalDistance
                <= HIT_RADIUS;
    }

    private void damageTarget(
            LivingEntity target,
            LivingEntity owner
    ) {
        if (owner == null) {
            return;
        }

        DamageSource damageSource =
                level().damageSources().magic();

        target.hurt(
                damageSource,
                getDamage()
        );
    }

    private void launchTarget(
            LivingEntity target
    ) {
        Vec3 currentVelocity =
                target.getDeltaMovement();

        target.setDeltaMovement(
                currentVelocity.x,
                LAUNCH_VELOCITY,
                currentVelocity.z
        );

        target.hasImpulse = true;
    }

    private void spawnTornadoParticles() {
        Vec3 center =
                position();

        double time =
                tickCount
                        * ROTATION_SPEED;

        for (int layer = 0;
             layer < PARTICLE_LAYERS;
             layer++) {

            double height =
                    (
                            layer
                                    / (double)
                                    (
                                            PARTICLE_LAYERS
                                                    - 1
                                    )
                    )
                            * TORNADO_HEIGHT;

            double normalizedHeight =
                    height
                            / TORNADO_HEIGHT;

            double radius =
                    MIN_RADIUS
                            + (
                            MAX_RADIUS
                                    - MIN_RADIUS
                    ) * normalizedHeight;

            double layerRotation =
                    time
                            + layer
                            * (
                            Math.PI
                                    * 2.0D
                                    / PARTICLE_LAYERS
                    );

            for (int i = 0;
                 i < PARTICLES_PER_LAYER;
                 i++) {

                double angle =
                        layerRotation
                                + (
                                i
                                        * (
                                        Math.PI
                                                * 2.0D
                                                / PARTICLES_PER_LAYER
                                )
                        );

                double wobble =
                        Math.sin(
                                tickCount
                                        * 0.25D
                                        + i
                                        + layer
                        ) * 0.08D;

                double currentRadius =
                        Math.max(
                                MIN_RADIUS,
                                radius
                                        + wobble
                        );

                double x =
                        center.x
                                + Math.cos(angle)
                                * currentRadius;

                double y =
                        center.y
                                + height;

                double z =
                        center.z
                                + Math.sin(angle)
                                * currentRadius;

                level().addParticle(
                        ModParticles.CM_CLOUD.get(),
                        x,
                        y,
                        z,
                        0.0D,
                        0.0D,
                        0.0D
                );
            }
        }

        spawnInnerSpiral(
                center,
                time
        );
    }

    private void spawnInnerSpiral(
            Vec3 center,
            double time
    ) {
        for (int i = 0; i < 5; i++) {

            double normalizedHeight =
                    (
                            (
                                    tickCount
                                            + i * 2
                            )
                                    % 20
                    ) / 20.0D;

            double height =
                    normalizedHeight
                            * TORNADO_HEIGHT;

            double radius =
                    MIN_RADIUS
                            + (
                            MAX_RADIUS
                                    - MIN_RADIUS
                    ) * normalizedHeight;

            double angle =
                    time
                            * 1.4D
                            + i
                            * (
                            Math.PI
                                    * 2.0D
                                    / 5.0D
                    );

            level().addParticle(
                    ModParticles.CM_CLOUD.get(),
                    center.x
                            + Math.cos(angle)
                            * radius,
                    center.y
                            + height,
                    center.z
                            + Math.sin(angle)
                            * radius,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }

    @Override
    protected void readAdditionalSaveData(
            CompoundTag tag
    ) {
        if (tag.hasUUID(
                "Owner"
        )) {
            entityData.set(
                    OWNER,
                    Optional.of(
                            tag.getUUID(
                                    "Owner"
                            )
                    )
            );
        }

        if (tag.contains(
                "Damage"
        )) {
            setDamage(
                    tag.getFloat(
                            "Damage"
                    )
            );
        }

        hasRicocheted =
                tag.getBoolean(
                        "HasRicocheted"
                );
    }

    @Override
    protected void addAdditionalSaveData(
            CompoundTag tag
    ) {
        entityData.get(
                OWNER
        ).ifPresent(
                uuid ->
                        tag.putUUID(
                                "Owner",
                                uuid
                        )
        );

        tag.putFloat(
                "Damage",
                getDamage()
        );

        tag.putBoolean(
                "HasRicocheted",
                hasRicocheted
        );
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt(
            DamageSource source,
            float amount
    ) {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener>
    getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(
                this
        );
    }
}