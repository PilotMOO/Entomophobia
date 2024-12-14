package mod.pilot.entomophobia.particles;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntomoParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Entomophobia.MOD_ID);

    public static final RegistryObject<SimpleParticleType> FLY_PARTICLE =
            PARTICLE_TYPES.register("fly_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_HANG_PARTICLE =
            PARTICLE_TYPES.register("blood_hang_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_FALL_PARTICLE =
            PARTICLE_TYPES.register("blood_fall_particle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> BLOOD_LAND_PARTICLE =
            PARTICLE_TYPES.register("blood_land_particle", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
