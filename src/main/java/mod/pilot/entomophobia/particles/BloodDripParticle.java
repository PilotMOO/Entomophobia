package mod.pilot.entomophobia.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BloodDripParticle extends TextureSheetParticle {
    protected boolean isGlowing;

    BloodDripParticle(ClientLevel pLevel, double pX, double pY, double pZ, SpriteSet spriteSet) {
        super(pLevel, pX, pY, pZ);
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getLightColor(float pPartialTick) {
        return this.isGlowing ? 240 : super.getLightColor(pPartialTick);
    }

    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.preMoveUpdate();
        if (!this.removed) {
            this.yd -= this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.postMoveUpdate();
            if (!this.removed) {
                this.xd *= 0.98F;
                this.yd *= 0.98F;
                this.zd *= 0.98F;
            }
        }
    }
    protected void preMoveUpdate() {
        if (this.lifetime-- <= 0) {
            this.remove();
        }

    }
    protected void postMoveUpdate() {
    }

    @OnlyIn(Dist.CLIENT)
    static class HangParticle extends BloodDripParticle {
        private final ParticleOptions fallingParticle;

        HangParticle(ClientLevel pLevel, double pX, double pY, double pZ, ParticleOptions pFallingParticle, SpriteSet spriteSet) {
            super(pLevel, pX, pY, pZ, spriteSet);
            this.fallingParticle = pFallingParticle;
            this.gravity *= 0.02F;
            this.lifetime = 40;
        }

        protected void preMoveUpdate() {
            if (this.lifetime-- <= 0) {
                this.remove();
                this.level.addParticle(this.fallingParticle, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }

        }

        protected void postMoveUpdate() {
            this.xd *= 0.02D;
            this.yd *= 0.02D;
            this.zd *= 0.02D;
        }
    }
    @OnlyIn(Dist.CLIENT)
    static class FallingParticle extends BloodDripParticle {
        protected final ParticleOptions landParticle;
        FallingParticle(ClientLevel pLevel, double pX, double pY, double pZ, ParticleOptions land, SpriteSet spriteSet) {
            this(pLevel, pX, pY, pZ, (int)(64.0D / (Math.random() * 0.8D + 0.2D)), land, spriteSet);
        }

        FallingParticle(ClientLevel pLevel, double pX, double pY, double pZ, int pLifetime, ParticleOptions pLandParticle, SpriteSet spriteSet) {
            super(pLevel, pX, pY, pZ, spriteSet);
            this.lifetime = pLifetime;
            this.landParticle = pLandParticle;
        }

        protected void postMoveUpdate() {
            if (this.onGround) {
                this.remove();
                this.level.addParticle(this.landParticle, this.x, this.y, this.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    @OnlyIn(Dist.CLIENT)
    static class LandParticle extends BloodDripParticle {
        LandParticle(ClientLevel client, double x, double y, double z, SpriteSet spriteSet) {
            super(client, x, y, z, spriteSet);
            this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class HangProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public HangProvider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            HangParticle toReturn = new HangParticle(level, x, y, z, EntomoParticles.BLOOD_FALL_PARTICLE.get(), sprites);
            toReturn.setColor(0.25f, 0.03f, 0.03f);
            return toReturn;
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static class FallProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public FallProvider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            FallingParticle toReturn = new FallingParticle(level, x, y, z, EntomoParticles.BLOOD_LAND_PARTICLE.get(), sprites);
            toReturn.setColor(0.25f, 0.03f, 0.03f);
            return toReturn;
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static class LandProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public LandProvider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            LandParticle toReturn = new LandParticle(level, x, y, z, sprites);
            toReturn.setColor(0.25f, 0.03f, 0.03f);
            return toReturn;
        }
    }
}
