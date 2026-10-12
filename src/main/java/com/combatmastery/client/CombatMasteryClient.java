package com.combatmastery.client;

import com.combatmastery.CombatMastery;
import com.combatmastery.client.renderer.*;
import com.combatmastery.entity.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)

public class CombatMasteryClient {

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event
    ) {

        event.registerEntityRenderer(
                ModEntities.GREAT_SWEEP.get(),
                GreatSweepRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.AFTERSHOCK.get(),
                AftershockRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.CHAIN_LIGHTNING.get(),
                ChainLightningRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.LIGHTNING_STRIKE.get(),
                LightningStrikeRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.DICE_ROLL.get(),
                DiceRollRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.HOMING_SHOT.get(),
                HomingShotRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.ROCKET_SHOT.get(),
                RocketShotRenderer::new
        );

        event.registerEntityRenderer(
                ModEntities.TORNADO.get(),
                TornadoRenderer::new
        );
    }
}