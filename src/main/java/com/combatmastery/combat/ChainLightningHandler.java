package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.ChainLightningEntity;
import com.combatmastery.entity.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ChainLightningHandler {

    private static final int PROC_COOLDOWN = 10;

    private static final String CHAIN_LIGHTNING_COOLDOWN =
            "combat_mastery_chain_lightning_cooldown";

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

        Entity sourceEntity =
                event.getSource().getEntity();

        if (!(sourceEntity instanceof LivingEntity attacker)) {
            return;
        }

        if (sourceEntity instanceof ChainLightningEntity) {
            return;
        }

        Entity directEntity =
                event.getSource().getDirectEntity();

        if (directEntity instanceof ChainLightningEntity) {
            return;
        }

        int cooldown =
                attacker.getPersistentData().getInt(
                        CHAIN_LIGHTNING_COOLDOWN
                );

        if (cooldown > 0) {
            return;
        }

        if (hasActiveChainLightning(target)) {
            return;
        }

        var attribute =
                attacker.getAttribute(
                        ModAttributes.CHAIN_LIGHTNING.get()
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

        if (attacker.getRandom().nextDouble()
                >= chance) {
            return;
        }

        if (spawnChainLightning(
                attacker,
                target
        )) {
            attacker.getPersistentData().putInt(
                    CHAIN_LIGHTNING_COOLDOWN,
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
                        CHAIN_LIGHTNING_COOLDOWN
                );

        if (cooldown > 0) {
            entity.getPersistentData().putInt(
                    CHAIN_LIGHTNING_COOLDOWN,
                    cooldown - 1
            );
        }
    }

    private static boolean hasActiveChainLightning(
            LivingEntity target
    ) {
        return !target.level().getEntitiesOfClass(
                ChainLightningEntity.class,
                target.getBoundingBox().inflate(
                        ChainLightningEntity.getSearchRange()
                ),
                ChainLightningEntity::isAlive
        ).isEmpty();
    }

    private static boolean spawnChainLightning(
            LivingEntity attacker,
            LivingEntity target
    ) {
        ChainLightningEntity chainLightning =
                ModEntities.CHAIN_LIGHTNING
                        .get()
                        .create(
                                target.level()
                        );

        if (chainLightning == null) {
            return false;
        }

        chainLightning.moveTo(
                target.getX(),
                target.getY(),
                target.getZ(),
                0.0F,
                0.0F
        );

        chainLightning.setOwner(
                attacker
        );

        chainLightning.setCurrentTarget(
                target
        );

        chainLightning.setTargetNumber(
                0
        );

        chainLightning.addChainTarget(
                target.getUUID()
        );

        target.level().addFreshEntity(
                chainLightning
        );

        return true;
    }
}