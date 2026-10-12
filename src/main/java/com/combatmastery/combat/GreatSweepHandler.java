package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.GreatSweepEntity;
import com.combatmastery.entity.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class GreatSweepHandler {

    private static final float GREAT_SWEEP_SPEED = 1.5F;

    private static final int PROC_COOLDOWN = 10;

    private static final String GREAT_SWEEP_COOLDOWN =
            "combat_mastery_great_sweep_cooldown";

    @SubscribeEvent
    public static void onLivingAttack(
            LivingAttackEvent event
    ) {

        if (event.getEntity().level().isClientSide) {
            return;
        }

        Entity sourceEntity =
                event.getSource().getEntity();

        Entity directEntity =
                event.getSource().getDirectEntity();

        if (!(sourceEntity instanceof LivingEntity attacker)) {
            return;
        }

        if (directEntity != attacker) {
            return;
        }

        int cooldown =
                attacker.getPersistentData().getInt(
                        GREAT_SWEEP_COOLDOWN
                );

        if (cooldown > 0) {
            return;
        }

        if (hasActiveGreatSweep(attacker)) {
            return;
        }

        var attribute =
                attacker.getAttribute(
                        ModAttributes.GREAT_SWEEP_CHANCE.get()
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

        if (attacker.getRandom().nextDouble() >= chance) {
            return;
        }

        if (spawnGreatSweep(attacker)) {

            attacker.getPersistentData().putInt(
                    GREAT_SWEEP_COOLDOWN,
                    PROC_COOLDOWN
            );
        }
    }

    @SubscribeEvent
    public static void onLivingTick(
            LivingEvent.LivingTickEvent event
    ) {

        LivingEntity entity =
                event.getEntity();

        if (entity.level().isClientSide) {
            return;
        }

        int cooldown =
                entity.getPersistentData().getInt(
                        GREAT_SWEEP_COOLDOWN
                );

        if (cooldown > 0) {

            entity.getPersistentData().putInt(
                    GREAT_SWEEP_COOLDOWN,
                    cooldown - 1
            );
        }
    }

    private static boolean hasActiveGreatSweep(
            LivingEntity attacker
    ) {

        Level level =
                attacker.level();

        double searchRadius =
                64.0D;

        return !level.getEntitiesOfClass(
                GreatSweepEntity.class,
                attacker.getBoundingBox().inflate(
                        searchRadius
                ),
                sweep ->
                        sweep.isAlive()
                                && sweep.getOwner() == attacker
        ).isEmpty();
    }

    private static boolean spawnGreatSweep(
            LivingEntity attacker
    ) {

        GreatSweepEntity sweep =
                ModEntities.GREAT_SWEEP.get().create(
                        attacker.level()
                );

        if (sweep == null) {
            return false;
        }

        Vec3 direction =
                attacker.getLookAngle()
                        .normalize();

        Vec3 spawnPosition =
                attacker.position()
                        .add(
                                0.0D,
                                attacker.getBbHeight() * 0.5D,
                                0.0D
                        )
                        .subtract(direction);

        sweep.moveTo(
                spawnPosition.x,
                spawnPosition.y,
                spawnPosition.z,
                attacker.getYRot(),
                attacker.getXRot()
        );

        sweep.setOwner(attacker);

        sweep.launchFromEntity(
                attacker,
                GREAT_SWEEP_SPEED
        );

        attacker.level().addFreshEntity(
                sweep
        );

        return true;
    }
}