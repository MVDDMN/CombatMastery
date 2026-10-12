package com.combatmastery.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class CloudParticle extends TextureSheetParticle {

    private static final int LIFETIME = 8;

    private static final float MIN_SCALE = 0.10F;

    private static final float MAX_SCALE = 0.30F;

    private static final float START_ALPHA = 0.90F;

    private static final int FADE_START_TICK = 5;

    private final SpriteSet sprites;

    protected CloudParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet sprites
    ) {
        super(
                level,
                x,
                y,
                z,
                xSpeed,
                ySpeed,
                zSpeed
        );

        this.sprites = sprites;

        lifetime = LIFETIME;
        gravity = 0.0F;
        friction = 1.0F;
        quadSize = MAX_SCALE;

        xd = xSpeed;
        yd = ySpeed;
        zd = zSpeed;

        alpha = START_ALPHA;

        setSprite(
                sprites.get(
                        0,
                        lifetime
                )
        );
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;

        if (age++ >= lifetime) {
            remove();
            return;
        }

        move(
                xd,
                yd,
                zd
        );

        float progress =
                age
                        / (float)
                        lifetime;

        quadSize =
                Mth.lerp(
                        progress,
                        MAX_SCALE,
                        MIN_SCALE
                );

        if (age < FADE_START_TICK) {
            alpha = START_ALPHA;
        } else {
            float fadeProgress =
                    (
                            age
                                    - FADE_START_TICK
                    )
                            / (float)
                            (
                                    lifetime
                                            - FADE_START_TICK
                            );

            alpha =
                    Mth.lerp(
                            fadeProgress,
                            START_ALPHA,
                            0.0F
                    );
        }

        setSprite(
                sprites.get(
                        age,
                        lifetime
                )
        );
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider
            implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(
                SpriteSet sprites
        ) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new CloudParticle(
                    level,
                    x,
                    y,
                    z,
                    xSpeed,
                    ySpeed,
                    zSpeed,
                    sprites
            );
        }
    }
}