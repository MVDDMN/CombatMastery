package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.HomingShotEntity;
import com.combatmastery.entity.ModEntities;
import com.combatmastery.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class HomingShotHandler {

    private static final int MAX_ACTIVE_SHOTS_PER_TARGET = 3;
    private static final int FIRST_SHOT_SUMMON_DELAY = 5;
    private static final int SHOT_SPAWN_INTERVAL = 5;
    private static final int SHOT_FIRE_INTERVAL = 10;
    private static final int RELEASE_DELAY_AFTER_THIRD_SHOT = 20;

    private static final double DAMAGE_MULTIPLIER = 0.30D;

    private static final float CAST_MIN_PITCH = 0.90F;
    private static final float CAST_MAX_PITCH = 1.3F;
    private static final float CAST_VOLUME = 0.5F;

    private static final Map<UUID, PendingFirstShot> PENDING_FIRST_SHOTS =
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

        Entity directEntity =
                event.getSource().getDirectEntity();

        if (directEntity != attacker) {
            return;
        }

        if (attacker == target) {
            return;
        }

        ItemStack weapon =
                attacker.getMainHandItem();

        if (weapon.isEmpty()) {
            return;
        }

        AttributeInstance attribute =
                attacker.getAttribute(
                        ModAttributes.HOMING_SHOT.get()
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
                        1.0D
                );

        if (hasActiveSequence(attacker)) {
            return;
        }

        if (PENDING_FIRST_SHOTS.containsKey(
                attacker.getUUID()
        )) {
            return;
        }

        if (attacker.getRandom().nextDouble()
                >= chance) {
            return;
        }

        if (getActiveShotsForTarget(target)
                >= MAX_ACTIVE_SHOTS_PER_TARGET) {
            return;
        }

        spawnFirstHomingShot(
                attacker,
                target,
                weapon
        );
    }

    @SubscribeEvent
    public static void onServerTick(
            TickEvent.ServerTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (PENDING_FIRST_SHOTS.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, PendingFirstShot>> iterator =
                PENDING_FIRST_SHOTS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingFirstShot> entry =
                    iterator.next();

            PendingFirstShot pending =
                    entry.getValue();

            if (pending.level().getGameTime()
                    < pending.spawnGameTime()) {
                continue;
            }

            iterator.remove();

            LivingEntity attacker =
                    getLivingEntity(
                            pending.level(),
                            pending.attackerUUID()
                    );

            LivingEntity target =
                    getLivingEntity(
                            pending.level(),
                            pending.targetUUID()
                    );

            if (attacker == null
                    || target == null) {
                continue;
            }

            if (!attacker.isAlive()
                    || attacker.isDeadOrDying()) {
                continue;
            }

            if (!target.isAlive()
                    || target.isDeadOrDying()) {
                continue;
            }

            if (getActiveShotsForTarget(target)
                    >= MAX_ACTIVE_SHOTS_PER_TARGET) {
                continue;
            }

            createFirstHomingShot(
                    attacker,
                    target,
                    pending.weapon()
            );
        }
    }

    private static LivingEntity getLivingEntity(
            ServerLevel level,
            UUID uuid
    ) {
        Entity entity =
                level.getEntity(uuid);

        if (entity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        return null;
    }

    private static boolean hasActiveSequence(
            LivingEntity attacker
    ) {
        return attacker.level().getEntitiesOfClass(
                HomingShotEntity.class,
                attacker.getBoundingBox().inflate(64.0D),
                shot -> {
                    if (!shot.isAlive()) {
                        return false;
                    }

                    LivingEntity owner =
                            shot.getLivingOwner();

                    return owner == attacker;
                }
        ).stream().findAny().isPresent();
    }

    private static int getActiveShotsForTarget(
            LivingEntity target
    ) {
        return target.level().getEntitiesOfClass(
                HomingShotEntity.class,
                target.getBoundingBox().inflate(64.0D),
                shot -> shot.isAlive()
                        && shot.isTarget(target)
        ).size();
    }

    private static void spawnFirstHomingShot(
            LivingEntity attacker,
            LivingEntity target,
            ItemStack weapon
    ) {
        long spawnGameTime =
                target.level().getGameTime()
                        + FIRST_SHOT_SUMMON_DELAY;

        PENDING_FIRST_SHOTS.put(
                attacker.getUUID(),
                new PendingFirstShot(
                        (ServerLevel) target.level(),
                        attacker.getUUID(),
                        target.getUUID(),
                        weapon.copy(),
                        spawnGameTime
                )
        );

        playCastSound(attacker);
    }

    private static void createFirstHomingShot(
            LivingEntity attacker,
            LivingEntity target,
            ItemStack weapon
    ) {
        HomingShotEntity shot =
                ModEntities.HOMING_SHOT
                        .get()
                        .create(target.level());

        if (shot == null) {
            return;
        }

        shot.setOwner(attacker);

        shot.setTarget(target);

        shot.setWeapon(
                weapon.copy()
        );

        shot.setOriginalDamage(
                calculateWeaponDamage(
                        weapon,
                        attacker
                )
        );

        shot.setSequenceIndex(0);

        shot.setReleaseGameTime(
                target.level().getGameTime()
                        + (SHOT_SPAWN_INTERVAL * 2L)
                        + RELEASE_DELAY_AFTER_THIRD_SHOT
        );

        shot.setFormationPositionImmediately();

        target.level().addFreshEntity(
                shot
        );
    }

    private static void playCastSound(
            LivingEntity attacker
    ) {
        float pitch =
                CAST_MIN_PITCH
                        + attacker.getRandom().nextFloat()
                        * (
                        CAST_MAX_PITCH
                                - CAST_MIN_PITCH
                );

        attacker.level().playSound(
                null,
                attacker.getX(),
                attacker.getY(),
                attacker.getZ(),
                ModSounds.HOMING_SHOT_CAST.get(),
                SoundSource.HOSTILE,
                CAST_VOLUME,
                pitch
        );
    }

    public static void spawnNextHomingShot(
            HomingShotEntity previousShot
    ) {
        if (!(previousShot.level()
                instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity owner =
                previousShot.getLivingOwner();

        LivingEntity target =
                previousShot.getTarget();

        if (owner == null
                || target == null) {
            return;
        }

        if (!owner.isAlive()
                || !target.isAlive()
                || target.isDeadOrDying()) {
            return;
        }

        int nextIndex =
                previousShot.getSequenceIndex()
                        + 1;

        if (nextIndex >= MAX_ACTIVE_SHOTS_PER_TARGET) {
            return;
        }

        if (getActiveShotsForTarget(target)
                >= MAX_ACTIVE_SHOTS_PER_TARGET) {
            return;
        }

        ItemStack weapon =
                previousShot.getWeapon();

        if (weapon.isEmpty()) {
            return;
        }

        HomingShotEntity nextShot =
                ModEntities.HOMING_SHOT
                        .get()
                        .create(serverLevel);

        if (nextShot == null) {
            return;
        }

        nextShot.setOwner(owner);

        nextShot.setTarget(target);

        nextShot.setWeapon(
                weapon.copy()
        );

        nextShot.setOriginalDamage(
                previousShot.getOriginalDamage()
        );

        nextShot.setSequenceIndex(
                nextIndex
        );

        nextShot.setReleaseGameTime(
                previousShot.getReleaseGameTime()
                        + SHOT_FIRE_INTERVAL
        );

        nextShot.setFormationPositionImmediately();

        serverLevel.addFreshEntity(
                nextShot
        );
    }

    private static float calculateWeaponDamage(
            ItemStack weapon,
            LivingEntity attacker
    ) {
        double baseDamage =
                attacker.getAttributeBaseValue(
                        Attributes.ATTACK_DAMAGE
                );

        double addition =
                0.0D;

        double multiplyBase =
                0.0D;

        double multiplyTotal =
                0.0D;

        for (var modifier :
                weapon.getAttributeModifiers(
                        EquipmentSlot.MAINHAND
                ).get(
                        Attributes.ATTACK_DAMAGE
                )) {

            switch (modifier.getOperation()) {
                case ADDITION ->
                        addition +=
                                modifier.getAmount();

                case MULTIPLY_BASE ->
                        multiplyBase +=
                                modifier.getAmount();

                case MULTIPLY_TOTAL ->
                        multiplyTotal +=
                                modifier.getAmount();
            }
        }

        double damage =
                baseDamage
                        + addition;

        damage +=
                baseDamage
                        * multiplyBase;

        damage *=
                1.0D
                        + multiplyTotal;

        damage *=
                DAMAGE_MULTIPLIER;

        if (!Double.isFinite(damage)) {
            return 0.0F;
        }

        damage =
                Math.max(
                        0.0D,
                        damage
                );

        damage =
                Math.min(
                        1000.0D,
                        damage
                );

        return (float) damage;
    }

    private record PendingFirstShot(
            ServerLevel level,
            UUID attackerUUID,
            UUID targetUUID,
            ItemStack weapon,
            long spawnGameTime
    ) {
    }
}