package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.AftershockEntity;
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
public class AftershockHandler {

    private static final int PROC_COOLDOWN = 1;

    private static final String AFTERSHOCK_COOLDOWN =
            "combat_mastery_aftershock_cooldown";

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide) return;

        Entity sourceEntity = event.getSource().getEntity();
        Entity directEntity = event.getSource().getDirectEntity();

        if (!(sourceEntity instanceof LivingEntity attacker)) return;
        if (directEntity != attacker) return;

        LivingEntity target = event.getEntity();

        int cooldown =
                attacker.getPersistentData().getInt(
                        AFTERSHOCK_COOLDOWN
                );

        if (cooldown > 0) return;

        if (hasActiveAftershock(target)) return;

        var attribute =
                attacker.getAttribute(
                        ModAttributes.AFTERSHOCK.get()
                );

        if (attribute == null) return;

        double chance = attribute.getValue();

        if (chance <= 0.0D) return;

        chance = Math.min(chance, 1.0D);

        if (attacker.getRandom().nextDouble() >= chance) return;

        if (spawnAftershock(attacker, target)) {
            attacker.getPersistentData().putInt(
                    AFTERSHOCK_COOLDOWN,
                    PROC_COOLDOWN
            );
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) return;

        int cooldown =
                entity.getPersistentData().getInt(
                        AFTERSHOCK_COOLDOWN
                );

        if (cooldown > 0) {
            entity.getPersistentData().putInt(
                    AFTERSHOCK_COOLDOWN,
                    cooldown - 1
            );
        }
    }

    private static boolean hasActiveAftershock(LivingEntity target) {
        Level level = target.level();

        return !level.getEntitiesOfClass(
                AftershockEntity.class,
                target.getBoundingBox().inflate(4.0D),
                AftershockEntity::isAlive
        ).isEmpty();
    }

    private static boolean spawnAftershock(
            LivingEntity attacker,
            LivingEntity target
    ) {
        AftershockEntity aftershock =
                ModEntities.AFTERSHOCK.get().create(
                        attacker.level()
                );

        if (aftershock == null) return false;

        aftershock.moveTo(
                target.getX(),
                target.getY(),
                target.getZ(),
                0.0F,
                0.0F
        );

        aftershock.setOwner(attacker);

        attacker.level().addFreshEntity(aftershock);

        return true;
    }
}