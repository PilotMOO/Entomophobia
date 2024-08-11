package mod.pilot.entomophobia;

import com.mojang.logging.LogUtils;
import mod.pilot.entomophobia.blocks.EntomoBlocks;
import mod.pilot.entomophobia.damagetypes.EntomoDamageTypes;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.items.EntomoCreativeTabs;
import mod.pilot.entomophobia.items.EntomoItems;
import mod.pilot.entomophobia.sound.EntomoSounds;
import mod.pilot.entomophobia.worlddata.WorldSaveData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

@Mod(Entomophobia.MOD_ID)
public class Entomophobia
{
    public static final String MOD_ID = "entomophobia";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static WorldSaveData activeData;

    public Entomophobia() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        EntomoItems.register(modEventBus);
        EntomoCreativeTabs.register(modEventBus);
        EntomoBlocks.register(modEventBus);
        EntomoSounds.register(modEventBus);
        EntomoEntities.register(modEventBus);
        EntomoMobEffects.register(modEventBus);

        System.out.println("Color: " + getIntFromColor(131, 35, 242));

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SERVER_SPEC);
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000;
        Green = (Green << 8) & 0x0000FF00;
        Blue = Blue & 0x000000FF;

        return 0xFF000000 | Red | Green | Blue;
    }
}
