package mod.pilot.entomophobia.systems.screentextdisplay;

import mod.pilot.entomophobia.Entomophobia;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;

@OnlyIn(Dist.CLIENT)
public class TextDisplayManager {
    private static final String OverlayID = Entomophobia.MOD_ID + ":TextOverlay";
    private TextDisplayManager(){}
    public static void Setup(){
        MinecraftForge.EVENT_BUS.addListener(TextDisplayManager::RegisterTextOverlay);
        System.out.println("[TEXT DISPLAY MANAGER] Setup has been invoked!");
    }

    public static TextOverlay textOverlay;
    public static void RegisterTextOverlay(RegisterGuiOverlaysEvent event){
        System.out.println("[TEXT DISPLAY MANAGER] Attempting to register TextOverlay...");
        textOverlay = new TextOverlay();
        event.registerAboveAll(OverlayID, textOverlay);
        if (textOverlay != null) System.out.println("[TEXT DISPLAY MANAGER] textOverlay is NOT NULL");
        else System.out.println("[TEXT DISPLAY MANAGER] textOverlay is NULL");
    }
}
