package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.LightningStrikeEntity;
import com.combatmastery.entity.ModEntities;
import com.combatmastery.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class LightningStrikeHandler {

    private static final float CAST_VOLUME = 0.4F;

    private static final float CAST_PITCH = 1.0F;

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

        LivingEntity attacker =
                resolveAttacker(sourceEntity);

        if (attacker == null) {
            return;
        }

        if (attacker == target) {
            return;
        }

        var attribute =
                attacker.getAttribute(
                        ModAttributes.LIGHTNING_STRIKE.get()
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

        if (LightningStrikeEntity.hasActiveStrike(
                target
        )) {
            return;
        }

        spawnLightningStrike(
                attacker,
                target
        );
    }

    private static LivingEntity resolveAttacker(
            Entity sourceEntity
    ) {

        if (sourceEntity instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        if (sourceEntity instanceof Projectile projectile) {

            Entity owner =
                    projectile.getOwner();

            if (owner instanceof LivingEntity livingEntity) {
                return livingEntity;
            }
        }

        return null;
    }

    private static void spawnLightningStrike(
            LivingEntity attacker,
            LivingEntity target
    ) {

        LightningStrikeEntity strike =
                ModEntities.LIGHTNING_STRIKE
                        .get()
                        .create(
                                target.level()
                        );

        if (strike == null) {
            return;
        }

        strike.setOwner(
                attacker
        );

        strike.setTarget(
                target
        );

        strike.moveTo(
                target.getX(),
                target.getY(),
                target.getZ()
        );

        target.level().playSound(
                null,
                attacker.getX(),
                attacker.getY(),
                attacker.getZ(),
                ModSounds.LIGHTNING_STRIKE_CAST.get(),
                SoundSource.HOSTILE,
                CAST_VOLUME,
                CAST_PITCH
        );

        target.level().addFreshEntity(
                strike
        );
    }
}