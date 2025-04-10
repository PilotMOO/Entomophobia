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
import org.jetbrains.annotations.Nullable;

public class EntomoSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Entomophobia.MOD_ID);

    public static final RegistryObject<SoundEvent> MYIATIC_ZOMBIE_IDLE = registerSoundEvents("myiatic_zombie_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_COW_IDLE = registerSoundEvents("myiatic_cow_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_PIG_IDLE = registerSoundEvents("myiatic_pig_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_SHEEP_IDLE = registerSoundEvents("myiatic_sheep_idle");
    public static final RegistryObject<SoundEvent> MYIATIC_FLYING = registerSoundEvents("myiatic_flying");

    public static final RegistryObject<SoundEvent> CLOSER = registerSoundEvents("closer");
    public static final RegistryObject<SoundEvent> COME = registerSoundEvents("come");
    public static final RegistryObject<SoundEvent> DEFEAT = registerSoundEvents("defeat");
    public static final RegistryObject<SoundEvent> FALLEN = registerSoundEvents("fallen");
    public static final RegistryObject<SoundEvent> FIX_US = registerSoundEvents("fix_us");
    public static final RegistryObject<SoundEvent> FREE_ME = registerSoundEvents("free_me");
    public static final RegistryObject<SoundEvent> JOIN_US = registerSoundEvents("join_us");
    public static final RegistryObject<SoundEvent> PAIN = registerSoundEvents("pain");
    public static final RegistryObject<SoundEvent> RECLAIM_ME = registerSoundEvents("reclaim_me");
    public static final RegistryObject<SoundEvent> REVIVE = registerSoundEvents("revive");
    public static final RegistryObject<SoundEvent> SALVATION = registerSoundEvents("salvation");
    public static final RegistryObject<SoundEvent> THEY_KNOW_MY_NAME = registerSoundEvents("they_know_my_name");
    public static final RegistryObject<SoundEvent> THEY_WATCHED_ME_BLEED = registerSoundEvents("they_watched_me_bleed");

    public static final RegistryObject<SoundEvent> BEAT1 = registerSoundEvents("beat1");
    public static final RegistryObject<SoundEvent> BEAT2 = registerSoundEvents("beat2");

    public static @Nullable SoundEvent getVoice(String id){
        return switch (id) {
            default -> null;
            case "Closer" -> CLOSER.get();
            case "Come" -> COME.get();
            case "Defeat" -> DEFEAT.get();
            case "Fallen" -> FALLEN.get();
            case "FIX US" -> FIX_US.get();
            case "Free me" -> FREE_ME.get();
            case "Join us" -> JOIN_US.get();
            case "Pain" -> PAIN.get();
            case "Reclaim me" -> RECLAIM_ME.get();
            case "Revive" -> REVIVE.get();
            case "Salvation" -> SALVATION.get();
            case "They know my name" -> THEY_KNOW_MY_NAME.get();
            case "They watched me BLEED" -> THEY_WATCHED_ME_BLEED.get();
        };
    }

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
