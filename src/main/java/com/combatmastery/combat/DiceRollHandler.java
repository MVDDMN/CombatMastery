package com.combatmastery.combat;

import com.combatmastery.CombatMastery;
import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.DiceRollEntity;
import com.combatmastery.entity.ModEntities;
import com.combatmastery.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class DiceRollHandler {

    private static final float CAST_VOLUME = 0.5F;
    private static final float RESULT_VOLUME = 0.7F;

    @SubscribeEvent
    public static void onLivingHurt(
            LivingHurtEvent event
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

        LivingEntity caster =
                resolveCaster(sourceEntity);

        if (caster == null) {
            return;
        }

        if (caster == target) {
            return;
        }

        var attribute =
                caster.getAttribute(
                        ModAttributes.DICE_ROLL.get()
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
                Math.min(chance, 1.0D);

        if (caster.getRandom().nextDouble()
                >= chance) {
            return;
        }

        if (hasActiveDice(target)) {
            return;
        }

        int result =
                caster.getRandom().nextInt(6) + 1;

        float originalDamage =
                event.getAmount();

        spawnDice(
                caster,
                target,
                result,
                originalDamage
        );
    }

    private static LivingEntity resolveCaster(
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

    private static boolean hasActiveDice(
            LivingEntity target
    ) {

        return !target.level()
                .getEntitiesOfClass(
                        DiceRollEntity.class,
                        target.getBoundingBox()
                                .inflate(2.0D)
                )
                .isEmpty();
    }

    private static void spawnDice(
            LivingEntity caster,
            LivingEntity target,
            int result,
            float originalDamage
    ) {

        DiceRollEntity dice =
                ModEntities.DICE_ROLL.get()
                        .create(target.level());

        if (dice == null) {
            return;
        }

        dice.setCaster(caster);
        dice.setTarget(target);
        dice.setResult(result);
        dice.setOriginalDamage(originalDamage);

        float castPitch =
                0.95F
                        + target.level()
                        .getRandom()
                        .nextFloat()
                        * 0.10F;

        target.level().playSound(
                null,
                caster.getX(),
                caster.getY(),
                caster.getZ(),
                ModSounds.DICE_ROLL_CAST.get(),
                SoundSource.HOSTILE,
                CAST_VOLUME,
                castPitch
        );

        target.level()
                .addFreshEntity(dice);
    }

    public static void playResultSound(
            DiceRollEntity dice
    ) {

        Level level =
                dice.level();

        if (level.isClientSide) {
            return;
        }

        float pitch =
                0.95F
                        + level.getRandom().nextFloat()
                        * 0.10F;

        if (dice.getResult() <= 2) {

            level.playSound(
                    null,
                    dice.getX(),
                    dice.getY(),
                    dice.getZ(),
                    ModSounds.DICE_ROLL_BAD.get(),
                    SoundSource.HOSTILE,
                    RESULT_VOLUME,
                    pitch
            );

        } else if (dice.getResult() <= 4) {

            level.playSound(
                    null,
                    dice.getX(),
                    dice.getY(),
                    dice.getZ(),
                    ModSounds.DICE_ROLL_OK.get(),
                    SoundSource.HOSTILE,
                    RESULT_VOLUME,
                    pitch
            );

        } else {

            level.playSound(
                    null,
                    dice.getX(),
                    dice.getY(),
                    dice.getZ(),
                    ModSounds.DICE_ROLL_GOOD.get(),
                    SoundSource.HOSTILE,
                    RESULT_VOLUME,
                    pitch
            );
        }
    }

    public static void applyResult(
            DiceRollEntity dice
    ) {

        LivingEntity caster =
                dice.getCaster();

        LivingEntity target =
                dice.getTarget();

        if (caster == null
                || target == null
                || !caster.isAlive()
                || !target.isAlive()) {

            return;
        }

        if (!(caster instanceof Player player)) {
            return;
        }

        switch (dice.getResult()) {

            case 1 -> {

                caster.addEffect(
                        new MobEffectInstance(
                                MobEffects.MOVEMENT_SLOWDOWN,
                                60,
                                4
                        )
                );

                player.displayClientMessage(
                        Component.literal(
                                "ROLL 1 — BAD ROLL! The dice have cursed you with Slowness!"
                        ).withStyle(ChatFormatting.DARK_RED),
                        true
                );
            }

            case 2 -> {

                caster.addEffect(
                        new MobEffectInstance(
                                MobEffects.BLINDNESS,
                                40,
                                0
                        )
                );

                player.displayClientMessage(
                        Component.literal(
                                "ROLL 2 — BAD ROLL! Darkness consumes you. You are Blinded!"
                        ).withStyle(ChatFormatting.DARK_RED),
                        true
                );
            }

            case 3 -> {

                target.hurt(
                        target.damageSources().magic(),
                        1.0F
                );

                player.displayClientMessage(
                        Component.literal(
                                "ROLL 3 — AVERAGE ROLL! You dealt 1 damage to your target."
                        ).withStyle(ChatFormatting.WHITE),
                        true
                );
            }

            case 4 -> {

                BlockPos firePos =
                        target.blockPosition();

                if (target.level()
                        .getBlockState(firePos)
                        .isAir()) {

                    target.level().setBlock(
                            firePos,
                            Blocks.FIRE.defaultBlockState(),
                            3
                    );

                    player.displayClientMessage(
                            Component.literal(
                                    "ROLL 4 — AVERAGE ROLL! Your target has been set ablaze!"
                            ).withStyle(ChatFormatting.WHITE),
                            true
                    );

                } else {

                    player.displayClientMessage(
                            Component.literal(
                                    "ROLL 4 — AVERAGE ROLL! The flames failed to take hold."
                            ).withStyle(ChatFormatting.WHITE),
                            true
                    );
                }
            }

            case 5 -> {

                caster.addEffect(
                        new MobEffectInstance(
                                MobEffects.DAMAGE_RESISTANCE,
                                60,
                                2
                        )
                );

                player.displayClientMessage(
                        Component.literal(
                                "ROLL 5 — JACKPOT! Fortune smiles upon you. You gained Resistance!"
                        ).withStyle(ChatFormatting.GREEN),
                        true
                );
            }

            case 6 -> {

                float originalDamage =
                        dice.getOriginalDamage();

                if (originalDamage > 0.0F) {

                    float jackpotDamage =
                            originalDamage * 2.0F;

                    target.hurt(
                            player.damageSources().playerAttack(player),
                            jackpotDamage
                    );

                    player.displayClientMessage(
                            Component.literal(
                                    "ROLL 6 — JACKPOT! The dice demand blood. You dealt "
                                            + jackpotDamage
                                            + " additional physical damage!"
                            ).withStyle(ChatFormatting.GREEN),
                            true
                    );
                }
            }

            default -> {
            }
        }
    }
}