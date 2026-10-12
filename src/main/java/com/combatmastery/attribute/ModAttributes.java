package com.combatmastery.attribute;

import com.combatmastery.CombatMastery;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {

    public static final DeferredRegister<net.minecraft.world.entity.ai.attributes.Attribute> ATTRIBUTES =
            DeferredRegister.create(
                    ForgeRegistries.ATTRIBUTES,
                    CombatMastery.MOD_ID
            );

    public static final RegistryObject<net.minecraft.world.entity.ai.attributes.Attribute> GREAT_SWEEP_CHANCE =
            ATTRIBUTES.register(
                    "great_sweep_chance",
                    () -> new RangedAttribute(
                            "attribute.name.combat_mastery.great_sweep_chance",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );

    public static final RegistryObject<net.minecraft.world.entity.ai.attributes.Attribute> AFTERSHOCK =
            ATTRIBUTES.register(
                    "aftershock",
                    () -> new RangedAttribute(
                            "attribute.name.combat_mastery.aftershock",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );

    public static final RegistryObject<net.minecraft.world.entity.ai.attributes.Attribute> CHAIN_LIGHTNING =
            ATTRIBUTES.register(
                    "chain_lightning",
                    () -> new RangedAttribute(
                            "attribute.name.combat_mastery.chain_lightning",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );

    public static final RegistryObject<net.minecraft.world.entity.ai.attributes.Attribute> LIGHTNING_STRIKE =
            ATTRIBUTES.register(
                    "lightning_strike",
                    () -> new RangedAttribute(
                            "attribute.name.combat_mastery.lightning_strike",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );

    public static final RegistryObject<Attribute> DICE_ROLL =
            ATTRIBUTES.register(
                    "dice_roll",
                    () -> new RangedAttribute(
                            "attribute.combat_mastery.dice_roll",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );

    public static final RegistryObject<Attribute> HOMING_SHOT =
            ATTRIBUTES.register(
                    "homing_shot",
                    () -> new RangedAttribute(
                            "attribute.name.combat_mastery.homing_shot",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );

    public static final RegistryObject<Attribute> ROCKET_SHOT =
            ATTRIBUTES.register(
                    "rocket_shot",
                    () -> new RangedAttribute(
                            "attribute.name.combat_mastery.rocket_shot",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );

    public static final RegistryObject<Attribute> TORNADO =
            ATTRIBUTES.register(
                    "tornado",
                    () -> new RangedAttribute(
                            "attribute.name.combat_mastery.tornado",
                            0.0D,
                            0.0D,
                            1.0D
                    ).setSyncable(true)
            );
}