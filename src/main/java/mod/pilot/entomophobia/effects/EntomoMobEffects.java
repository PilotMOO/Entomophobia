package mod.pilot.entomophobia.effects;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntomoMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Entomophobia.MOD_ID);

    public static final RegistryObject<MobEffect> HUNT = MOB_EFFECTS.register("hunt",
            PheromoneHunt::new);
    public static final RegistryObject<MobEffect> PREY = MOB_EFFECTS.register("prey",
            PheromonePrey::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
