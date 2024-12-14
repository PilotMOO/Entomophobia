package mod.pilot.entomophobia.sound;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntomoSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Entomophobia.MOD_ID);

    public static final RegistryObject<SoundEvent> MYIATIC_ZOMBIE_IDLE = registerSoundEvents("myiatic_zombie_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_COW_IDLE = registerSoundEvents("myiatic_cow_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_PIG_IDLE = registerSoundEvents("myiatic_pig_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_SHEEP_IDLE = registerSoundEvents("myiatic_sheep_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_FLYING = registerSoundEvents("myiatic_flying");

    public static final ForgeSoundType TEST_SOUND_TYPE = new ForgeSoundType(1f, 1f,
            null, null, null, null, null); //Replace Null with actual sounds
    public static final SoundType TWINED_FLESH_STYPE = new SoundType(1f, 1f,
            SoundEvents.HONEY_BLOCK_BREAK, SoundEvents.MUD_STEP,
            SoundEvents.MUD_PLACE, SoundEvents.MUD_PLACE, SoundEvents.MUD_FALL);
    public static final SoundType BLOODWAX_PROTRUSION_STYPE = new SoundType(1f, 1f,
            SoundEvents.HONEYCOMB_WAX_ON, SoundEvents.HONEY_BLOCK_STEP,
            SoundEvents.MUD_STEP, SoundEvents.HONEYCOMB_WAX_ON, SoundEvents.HONEY_BLOCK_SLIDE);
    public static final SoundType CONGEALED_BLOOD_STYPE = new SoundType(1f, 1f,
            SoundEvents.HONEY_BLOCK_BREAK, SoundEvents.MUD_STEP,
            SoundEvents.MUD_PLACE, SoundEvents.HONEY_BLOCK_SLIDE, SoundEvents.MUD_FALL);


    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Entomophobia.MOD_ID, name)));
    }
    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
