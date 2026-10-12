package com.combatmastery.particle;

import com.combatmastery.CombatMastery;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(
                    ForgeRegistries.PARTICLE_TYPES,
                    CombatMastery.MOD_ID
            );

    public static final RegistryObject<SimpleParticleType> CM_CLOUD =
            PARTICLE_TYPES.register(
                    "cm_cloud",
                    () -> new SimpleParticleType(
                            false
                    )
            );
}