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
public class PlayerAttributes {

    @SubscribeEvent
    public static void addPlayerAttributes(
            EntityAttributeModificationEvent event
    ) {

        event.add(
                EntityType.PLAYER,
                ModAttributes.GREAT_SWEEP_CHANCE.get()
        );

        event.add(
                EntityType.PLAYER,
                ModAttributes.AFTERSHOCK.get()
        );

        event.add(
                EntityType.PLAYER,
                ModAttributes.CHAIN_LIGHTNING.get()
        );

        event.add(
                EntityType.PLAYER,
                ModAttributes.LIGHTNING_STRIKE.get()
        );

        event.add(
                EntityType.PLAYER,
                ModAttributes.DICE_ROLL.get()
        );

        event.add(
                EntityType.PLAYER,
                ModAttributes.HOMING_SHOT.get()
        );

        event.add(
                EntityType.PLAYER,
                ModAttributes.ROCKET_SHOT.get()
        );

        event.add(
                EntityType.PLAYER,
                ModAttributes.TORNADO.get()
        );
    }
}