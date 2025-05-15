package mod.pilot.entomophobia.systems.screentextdisplay;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;

public class TextOverlay implements IGuiOverlay {
    public static void setup(){
        MinecraftForge.EVENT_BUS.addListener(TextOverlay::tickAllInstances);
    }

    public static int width;
    public static int height;

    public static TextOverlay instance;
    public ArrayList<TextInstance> textInstances = new ArrayList<>();
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (width < screenWidth) width = screenWidth;
        if (height < screenHeight) height = screenHeight;

        new ArrayList<>(textInstances).forEach((textInstance -> textInstance.render(gui, guiGraphics, partialTick, screenWidth, screenHeight)));
    }
    public static void tickAllInstances(TickEvent.ClientTickEvent event){
        instance.textInstances.forEach(TextInstance::tick);
        instance.textInstances.removeIf(TextInstance::shouldBeRemoved);
    }
}
