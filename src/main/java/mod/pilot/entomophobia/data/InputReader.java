package mod.pilot.entomophobia.data;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

//"Borrowed" from Mikeatron's Sculk Horde GitHub
//Expanded into a neat little class of shorthands, will be expanded as needed
public class InputReader {
    public static boolean leftShift(){
        return keyDown(GLFW.GLFW_KEY_LEFT_SHIFT);
    }
    public static boolean leftControl(){
        return keyDown(GLFW.GLFW_KEY_LEFT_CONTROL);
    }

    public static boolean keyDown(int glfw){
        return  InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), glfw);
    }
}
