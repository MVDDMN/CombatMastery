package com.combatmastery.entity;

import com.combatmastery.CombatMastery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.combatmastery.entity.AftershockEntity;
import com.combatmastery.entity.ChainLightningEntity;
import com.combatmastery.entity.DiceRollEntity;
import com.combatmastery.entity.HomingShotEntity;
import com.combatmastery.entity.RocketShotEntity;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(
                    ForgeRegistries.ENTITY_TYPES,
                    CombatMastery.MOD_ID
            );

    public static final RegistryObject<EntityType<GreatSweepEntity>> GREAT_SWEEP =
            ENTITY_TYPES.register(
                    "great_sweep",
                    () -> EntityType.Builder
                            .of(GreatSweepEntity::new, MobCategory.MISC)
                            .sized(3.0F, 1.0F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("great_sweep")
            );

    public static final RegistryObject<EntityType<AftershockEntity>> AFTERSHOCK =
            ENTITY_TYPES.register(
                    "aftershock",
                    () -> EntityType.Builder
                            .<AftershockEntity>of(
                                    AftershockEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    3.0F,
                                    1.0F
                            )
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(
                                    CombatMastery.MOD_ID + ":aftershock"
                            )
            );

    public static final RegistryObject<EntityType<ChainLightningEntity>> CHAIN_LIGHTNING =
            ENTITY_TYPES.register(
                    "chain_lightning",
                    () -> EntityType.Builder
                            .<ChainLightningEntity>of(
                                    ChainLightningEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(5.0F, 5.0F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(
                                    CombatMastery.MOD_ID + ":chain_lightning"
                            )
            );

    public static final RegistryObject<EntityType<LightningStrikeEntity>> LIGHTNING_STRIKE =
            ENTITY_TYPES.register(
                    "lightning_strike",
                    () -> EntityType.Builder
                            .of(
                                    LightningStrikeEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    1.0F,
                                    0.1F
                            )
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(
                                    ResourceLocation
                                            .fromNamespaceAndPath(
                                                    CombatMastery.MOD_ID,
                                                    "lightning_strike"
                                            )
                                            .toString()
                            )
            );

    public static final RegistryObject<EntityType<DiceRollEntity>> DICE_ROLL =
            ENTITY_TYPES.register(
                    "dice_roll",
                    () -> EntityType.Builder
                            .<DiceRollEntity>of(
                                    DiceRollEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("dice_roll")
            );

    public static final RegistryObject<EntityType<HomingShotEntity>> HOMING_SHOT =
            ENTITY_TYPES.register(
                    "homing_shot",
                    () -> EntityType.Builder
                            .of(
                                    HomingShotEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    0.5F,
                                    0.5F
                            )
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build(
                                    "homing_shot"
                            )
            );

    public static final RegistryObject<EntityType<RocketShotEntity>> ROCKET_SHOT =
            ENTITY_TYPES.register(
                    "rocket_shot",
                    () -> EntityType.Builder
                            .of(
                                    RocketShotEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    0.5F,
                                    0.5F
                            )
                            .clientTrackingRange(
                                    64
                            )
                            .updateInterval(
                                    1
                            )
                            .build(
                                    "rocket_shot"
                            )
            );

    public static final RegistryObject<EntityType<TornadoEntity>> TORNADO =
            ENTITY_TYPES.register(
                    "tornado",
                    () -> EntityType.Builder
                            .of(
                                    TornadoEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    3.5F,
                                    3.0F
                            )
                            .clientTrackingRange(
                                    64
                            )
                            .updateInterval(
                                    1
                            )
                            .build(
                                    "tornado"
                            )
            );
}