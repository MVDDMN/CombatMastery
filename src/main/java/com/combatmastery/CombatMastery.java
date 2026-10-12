package com.combatmastery;

import com.combatmastery.attribute.ModAttributes;
import com.combatmastery.entity.ModEntities;
import com.combatmastery.sound.ModSounds;
import com.combatmastery.particle.ModParticles;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CombatMastery.MOD_ID)
public class CombatMastery {

    public static final String MOD_ID = "combat_mastery";

    public CombatMastery(
            FMLJavaModLoadingContext context
    ) {

        ModAttributes.ATTRIBUTES.register(
                context.getModEventBus()
        );

        ModEntities.ENTITY_TYPES.register(
                context.getModEventBus()
        );

        ModSounds.SOUNDS.register(
                context.getModEventBus()
        );

        ModParticles.PARTICLE_TYPES.register(
                context.getModEventBus()
        );

        System.out.println(
                "Combat Mastery has loaded successfully!"
        );
    }
}