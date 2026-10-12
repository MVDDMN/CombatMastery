package com.combatmastery.client.particle;

import com.combatmastery.CombatMastery;
import com.combatmastery.particle.CloudParticle;
import com.combatmastery.particle.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = CombatMastery.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class CombatMasteryParticleClient {

    @SubscribeEvent
    public static void registerParticleProviders(
            RegisterParticleProvidersEvent event
    ) {
        event.registerSpriteSet(
                ModParticles.CM_CLOUD.get(),
                CloudParticle.Provider::new
        );
    }
}