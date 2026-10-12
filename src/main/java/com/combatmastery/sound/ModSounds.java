package com.combatmastery.sound;

import com.combatmastery.CombatMastery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(
                    Registries.SOUND_EVENT,
                    CombatMastery.MOD_ID
            );

    public static final RegistryObject<SoundEvent> GREAT_SWEEP_CAST =
            SOUNDS.register(
                    "great_sweep_cast",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "great_sweep_cast"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> GREAT_SWEEP_FLY =
            SOUNDS.register(
                    "great_sweep_fly",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "great_sweep_fly"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> GREAT_SWEEP_IMPACT =
            SOUNDS.register(
                    "great_sweep_impact",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "great_sweep_impact"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> AFTERSHOCK =
            SOUNDS.register(
                    "aftershock",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "aftershock"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> CHAIN_LIGHTNING_CAST =
            SOUNDS.register(
                    "chain_lightning_cast",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "chain_lightning_cast"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> CHAIN_LIGHTNING_BOUNCE =
            SOUNDS.register(
                    "chain_lightning_bounce",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "chain_lightning_bounce"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> LIGHTNING_STRIKE_CAST =
            SOUNDS.register(
                    "lightning_strike_cast",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "lightning_strike_cast"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> LIGHTNING_STRIKE_PING =
            SOUNDS.register(
                    "lightning_strike_ping",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "lightning_strike_ping"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> DICE_ROLL_CAST =
            SOUNDS.register(
                    "dice_roll_cast",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "dice_roll_cast"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> DICE_ROLL_ROLL =
            SOUNDS.register(
                    "dice_roll_roll",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "dice_roll_roll"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> DICE_ROLL_BAD =
            SOUNDS.register(
                    "dice_roll_bad",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "dice_roll_bad"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> DICE_ROLL_OK =
            SOUNDS.register(
                    "dice_roll_ok",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "dice_roll_ok"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> DICE_ROLL_GOOD =
            SOUNDS.register(
                    "dice_roll_good",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "dice_roll_good"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> HOMING_SHOT_CAST =
            SOUNDS.register(
                    "homing_shot_cast",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "homing_shot_cast"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> HOMING_SHOT_FLY =
            SOUNDS.register(
                    "homing_shot_fly",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "homing_shot_fly"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> HOMING_SHOT_SUMMON =
            SOUNDS.register(
                    "homing_shot_summon",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "homing_shot_summon"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> TORNADO_CAST =
            SOUNDS.register(
                    "tornado_cast",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "tornado_cast"
                            )
                    )
            );

    public static final RegistryObject<SoundEvent> TORNADO_FLY =
            SOUNDS.register(
                    "tornado_fly",
                    () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(
                                    CombatMastery.MOD_ID,
                                    "tornado_fly"
                            )
                    )
            );

}