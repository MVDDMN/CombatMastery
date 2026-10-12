package com.combatmastery.attribute;

import com.combatmastery.CombatMastery;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class MobAttributes {

    @SubscribeEvent
    public static void addMobAttributes(
            EntityAttributeModificationEvent event
    ) {

        // ============================================================
        // Great Sweep
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Iron Golem
        event.add(
                EntityType.IRON_GOLEM,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Wolf
        event.add(
                EntityType.WOLF,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Polar Bear
        event.add(
                EntityType.POLAR_BEAR,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        // Zoglin
        event.add(
                EntityType.ZOGLIN,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );


        // ============================================================
        // Aftershock
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.AFTERSHOCK.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.AFTERSHOCK.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.AFTERSHOCK.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.AFTERSHOCK.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.AFTERSHOCK.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.AFTERSHOCK.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.AFTERSHOCK.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.AFTERSHOCK.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.AFTERSHOCK.get()
        );

        // Iron Golem
        event.add(
                EntityType.IRON_GOLEM,
                ModAttributes.AFTERSHOCK.get()
        );

        // Wolf
        event.add(
                EntityType.WOLF,
                ModAttributes.AFTERSHOCK.get()
        );

        // Polar Bear
        event.add(
                EntityType.POLAR_BEAR,
                ModAttributes.AFTERSHOCK.get()
        );

        // Zoglin
        event.add(
                EntityType.ZOGLIN,
                ModAttributes.AFTERSHOCK.get()
        );


        // ============================================================
        // Chain Lightning
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Iron Golem
        event.add(
                EntityType.IRON_GOLEM,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Wolf
        event.add(
                EntityType.WOLF,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Polar Bear
        event.add(
                EntityType.POLAR_BEAR,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // Zoglin
        event.add(
                EntityType.ZOGLIN,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        // ============================================================
        // Lightning Strike
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Iron Golem
        event.add(
                EntityType.IRON_GOLEM,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Wolf
        event.add(
                EntityType.WOLF,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Polar Bear
        event.add(
                EntityType.POLAR_BEAR,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // Zoglin
        event.add(
                EntityType.ZOGLIN,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        // ============================================================
        // Dice Roll
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.DICE_ROLL.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.DICE_ROLL.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.DICE_ROLL.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.DICE_ROLL.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.DICE_ROLL.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.DICE_ROLL.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.DICE_ROLL.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.DICE_ROLL.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.DICE_ROLL.get()
        );

        // Iron Golem
        event.add(
                EntityType.IRON_GOLEM,
                ModAttributes.DICE_ROLL.get()
        );

        // Wolf
        event.add(
                EntityType.WOLF,
                ModAttributes.DICE_ROLL.get()
        );

        // Polar Bear
        event.add(
                EntityType.POLAR_BEAR,
                ModAttributes.DICE_ROLL.get()
        );

        // Zoglin
        event.add(
                EntityType.ZOGLIN,
                ModAttributes.DICE_ROLL.get()
        );

        // ============================================================
        // Homing Shot
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.HOMING_SHOT.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.HOMING_SHOT.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.HOMING_SHOT.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.HOMING_SHOT.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.HOMING_SHOT.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.HOMING_SHOT.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.HOMING_SHOT.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.HOMING_SHOT.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.HOMING_SHOT.get()
        );

        // ============================================================
        // Rocket Shot
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Iron Golem
        event.add(
                EntityType.IRON_GOLEM,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Wolf
        event.add(
                EntityType.WOLF,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Polar Bear
        event.add(
                EntityType.POLAR_BEAR,
                ModAttributes.ROCKET_SHOT.get()
        );

        // Zoglin
        event.add(
                EntityType.ZOGLIN,
                ModAttributes.ROCKET_SHOT.get()
        );

        // ============================================================
        // Tornado
        // ============================================================

        // Vindicator
        event.add(
                EntityType.VINDICATOR,
                ModAttributes.TORNADO.get()
        );

        // Zombie
        event.add(
                EntityType.ZOMBIE,
                ModAttributes.TORNADO.get()
        );

        // Husk
        event.add(
                EntityType.HUSK,
                ModAttributes.TORNADO.get()
        );

        // Drowned
        event.add(
                EntityType.DROWNED,
                ModAttributes.TORNADO.get()
        );

        // Zombified Piglin
        event.add(
                EntityType.ZOMBIFIED_PIGLIN,
                ModAttributes.TORNADO.get()
        );

        // Piglin
        event.add(
                EntityType.PIGLIN,
                ModAttributes.TORNADO.get()
        );

        // Piglin Brute
        event.add(
                EntityType.PIGLIN_BRUTE,
                ModAttributes.TORNADO.get()
        );

        // Wither Skeleton
        event.add(
                EntityType.WITHER_SKELETON,
                ModAttributes.TORNADO.get()
        );

        // Skeleton
        event.add(
                EntityType.SKELETON,
                ModAttributes.TORNADO.get()
        );

        // Iron Golem
        event.add(
                EntityType.IRON_GOLEM,
                ModAttributes.TORNADO.get()
        );

        // Wolf
        event.add(
                EntityType.WOLF,
                ModAttributes.TORNADO.get()
        );

        // Polar Bear
        event.add(
                EntityType.POLAR_BEAR,
                ModAttributes.TORNADO.get()
        );

        // Zoglin
        event.add(
                EntityType.ZOGLIN,
                ModAttributes.TORNADO.get()
        );
    }
}