package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.ModEntities;
import com.combatmastery.entity.RocketShotEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class RocketShotHandler {

    private static final double MAX_CHANCE = 1.0D;

    private static final double SPAWN_DISTANCE = 1.0D;
    private static final double MIN_LATERAL_SPAWN_DISTANCE = 1.0D;
    private static final double MAX_LATERAL_SPAWN_DISTANCE = 2.0D;

    private static final float DAMAGE_MULTIPLIER = 0.20F;

    private static final int ROCKET_COOLDOWN_TICKS = 5;

    private static final float CAST_SOUND_VOLUME = 0.7F;
    private static final float CAST_SOUND_MIN_PITCH = 0.5F;
    private static final float CAST_SOUND_MAX_PITCH = 1.5F;

    private static final Map<UUID, Long> ROCKET_COOLDOWNS =
            new HashMap<>();

    @SubscribeEvent
    public static void onLivingAttack(
            LivingAttackEvent event
    ) {
        LivingEntity target =
                event.getEntity();

        Level level =
                target.level();

        if (level.isClientSide) {
            return;
        }

        if (!target.isAlive()
                || target.isDeadOrDying()) {
            return;
        }

        Entity attackerEntity =
                event.getSource().getEntity();

        if (!(attackerEntity instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker == target) {
            return;
        }

        Entity directEntity =
                event.getSource().getDirectEntity();

        if (directEntity instanceof RocketShotEntity) {
            return;
        }

        boolean meleeAttack =
                directEntity == attacker;

        boolean rangedAttack =
                directEntity instanceof Projectile
                        && directEntity != attacker;

        if (!meleeAttack
                && !rangedAttack) {
            return;
        }

        if (isOnCooldown(
                attacker
        )) {
            return;
        }

        var attribute =
                attacker.getAttribute(
                        ModAttributes.ROCKET_SHOT.get()
                );

        if (attribute == null) {
            return;
        }

        double chance =
                attribute.getValue();

        if (chance <= 0.0D) {
            return;
        }

        chance =
                Math.min(
                        chance,
                        MAX_CHANCE
                );

        if (attacker.getRandom().nextDouble()
                >= chance) {
            return;
        }

        boolean spawned =
                spawnRocketShot(
                        attacker,
                        target,
                        event.getAmount()
                );

        if (spawned) {
            setCooldown(
                    attacker
            );

            playCastSound(
                    attacker
            );
        }
    }

    private static boolean isOnCooldown(
            LivingEntity attacker
    ) {
        UUID attackerUUID =
                attacker.getUUID();

        Long cooldownEnd =
                ROCKET_COOLDOWNS.get(
                        attackerUUID
                );

        if (cooldownEnd == null) {
            return false;
        }

        long currentGameTime =
                attacker.level()
                        .getGameTime();

        if (currentGameTime >= cooldownEnd) {
            ROCKET_COOLDOWNS.remove(
                    attackerUUID
            );

            return false;
        }

        return true;
    }

    private static void setCooldown(
            LivingEntity attacker
    ) {
        long currentGameTime =
                attacker.level()
                        .getGameTime();

        ROCKET_COOLDOWNS.put(
                attacker.getUUID(),
                currentGameTime
                        + ROCKET_COOLDOWN_TICKS
        );
    }

    private static boolean spawnRocketShot(
            LivingEntity attacker,
            LivingEntity target,
            float attackDamage
    ) {
        RocketShotEntity rocket =
                ModEntities.ROCKET_SHOT
                        .get()
                        .create(
                                attacker.level()
                        );

        if (rocket == null) {
            return false;
        }

        rocket.setOwner(
                attacker
        );

        rocket.setTarget(
                target
        );

        float damage =
                attackDamage
                        * DAMAGE_MULTIPLIER;

        if (damage <= 0.0F) {
            damage = 1.0F;
        }

        rocket.setDamage(
                damage
        );

        initializeRocketMotion(
                rocket,
                attacker,
                target
        );

        boolean added =
                attacker.level().addFreshEntity(
                        rocket
                );

        return added;
    }

    private static void initializeRocketMotion(
            RocketShotEntity rocket,
            LivingEntity attacker,
            LivingEntity target
    ) {
        Vec3 attackerLook =
                attacker.getLookAngle();

        Vec3 horizontalLook =
                new Vec3(
                        attackerLook.x,
                        0.0D,
                        attackerLook.z
                );

        if (horizontalLook.lengthSqr()
                < 0.000001D) {
            horizontalLook =
                    new Vec3(
                            0.0D,
                            0.0D,
                            1.0D
                    );
        } else {
            horizontalLook =
                    horizontalLook.normalize();
        }

        Vec3 right =
                new Vec3(
                        -horizontalLook.z,
                        0.0D,
                        horizontalLook.x
                ).normalize();

        int side =
                attacker.getRandom().nextBoolean()
                        ? 1
                        : -1;

        double lateralDistance =
                MIN_LATERAL_SPAWN_DISTANCE
                        + attacker.getRandom().nextDouble()
                        * (
                        MAX_LATERAL_SPAWN_DISTANCE
                                - MIN_LATERAL_SPAWN_DISTANCE
                );

        Vec3 lateralOffset =
                right.scale(
                        lateralDistance
                                * side
                );

        Vec3 start =
                attacker.position()
                        .add(
                                lateralOffset.x,
                                attacker.getBbHeight()
                                        * 0.60D,
                                lateralOffset.z
                        );

        Vec3 targetPosition =
                target.getBoundingBox()
                        .getCenter();

        Vec3 direction =
                targetPosition
                        .subtract(start);

        if (direction.lengthSqr()
                < 0.000001D) {

            direction =
                    attacker.getLookAngle();
        } else {
            direction =
                    direction.normalize();
        }

        rocket.setPos(
                start.x
                        + direction.x
                        * SPAWN_DISTANCE,
                start.y
                        + direction.y
                        * SPAWN_DISTANCE,
                start.z
                        + direction.z
                        * SPAWN_DISTANCE
        );

        rocket.setDeltaMovement(
                direction.scale(
                        RocketShotEntity.getInitialSpeed()
                )
        );
    }

    private static void playCastSound(
            LivingEntity attacker
    ) {
        float pitch =
                CAST_SOUND_MIN_PITCH
                        + attacker.getRandom().nextFloat()
                        * (
                        CAST_SOUND_MAX_PITCH
                                - CAST_SOUND_MIN_PITCH
                );

        attacker.level().playSound(
                null,
                attacker.getX(),
                attacker.getY(),
                attacker.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH,
                attacker.getSoundSource(),
                CAST_SOUND_VOLUME,
                pitch
        );
    }
}