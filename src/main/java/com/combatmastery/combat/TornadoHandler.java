package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.ModEntities;
import com.combatmastery.entity.TornadoEntity;
import com.combatmastery.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
public class TornadoHandler {

    private static final double MAX_CHANCE = 1.0D;

    private static final double SPAWN_DISTANCE = 1.0D;

    private static final float DAMAGE_MULTIPLIER = 0.10F;

    private static final int COOLDOWN_TICKS = 40;

    private static final float TORNADO_CAST_VOLUME = 0.3F;

    private static final float TORNADO_CAST_MIN_PITCH = 0.60F;

    private static final float TORNADO_CAST_MAX_PITCH = 1.10F;

    private static final Map<UUID, Long> COOLDOWNS =
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

        if (!(attackerEntity
                instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker == target) {
            return;
        }

        Entity directEntity =
                event.getSource()
                        .getDirectEntity();

        boolean meleeAttack =
                directEntity == attacker;

        if (!meleeAttack) {
            return;
        }

        var attribute =
                attacker.getAttribute(
                        ModAttributes.TORNADO.get()
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

        UUID attackerUUID =
                attacker.getUUID();

        long currentGameTime =
                level.getGameTime();

        Long cooldownEnd =
                COOLDOWNS.get(
                        attackerUUID
                );

        if (cooldownEnd != null
                && currentGameTime < cooldownEnd) {
            return;
        }

        if (attacker.getRandom()
                .nextDouble() >= chance) {
            return;
        }

        COOLDOWNS.put(
                attackerUUID,
                currentGameTime
                        + COOLDOWN_TICKS
        );

        spawnTornado(
                attacker,
                target,
                event.getAmount()
        );
    }

    private static void spawnTornado(
            LivingEntity attacker,
            LivingEntity target,
            float attackDamage
    ) {
        Level level =
                attacker.level();

        TornadoEntity tornado =
                ModEntities.TORNADO
                        .get()
                        .create(level);

        if (tornado == null) {
            return;
        }

        Vec3 forward =
                attacker.getLookAngle()
                        .normalize();

        forward =
                new Vec3(
                        forward.x,
                        0.0D,
                        forward.z
                ).normalize();

        Vec3 spawnPosition =
                target.position()
                        .add(
                                forward.scale(
                                        SPAWN_DISTANCE
                                )
                        );

        tornado.setPos(
                spawnPosition.x,
                target.getY(),
                spawnPosition.z
        );

        tornado.setYRot(
                attacker.getYRot()
        );

        tornado.setXRot(
                0.0F
        );

        tornado.setOwner(
                attacker
        );

        float tornadoDamage =
                attackDamage
                        * DAMAGE_MULTIPLIER;

        tornado.setDamage(
                tornadoDamage
        );

        level.addFreshEntity(
                tornado
        );

        float pitch =
                TORNADO_CAST_MIN_PITCH
                        + attacker.getRandom().nextFloat()
                        * (
                        TORNADO_CAST_MAX_PITCH
                                - TORNADO_CAST_MIN_PITCH
                );

        level.playSound(
                null,
                tornado.getX(),
                tornado.getY(),
                tornado.getZ(),
                ModSounds.TORNADO_CAST.get(),
                SoundSource.PLAYERS,
                TORNADO_CAST_VOLUME,
                pitch
        );
    }
}