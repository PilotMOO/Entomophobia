package mod.pilot.entomophobia.systems.screentextdisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class TextOverlay implements IGuiOverlay {
    public ArrayList<TextInstance> textInstances = new ArrayList<>();
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        for (TextInstance text : new ArrayList<>(textInstances)){
            guiGraphics.drawCenteredString(text.font, text.TextWithAppends(), text.x, text.y, text.color);
        }
    }
    public void TickAllInstances(){
        new ArrayList<>(textInstances).forEach(TextInstance::Tick);
    }

    public static class TextInstance{
        public @Nullable String prepend;
        public String text;
        public @Nullable String append;

        public int x;
        public int wantedX;
        public double XShiftDuration;
        public float XAge;
        public void ShiftX(int newX){
            ShiftX(newX, Math.abs(newX - x) / 200d);
        }
        public void ShiftX(int newX, double duration){
            wantedX = newX;
            XShiftDuration = duration;
            XAge = 0;
        }

        public int y;
        public int wantedY;
        public double YShiftDuration;
        private float YAge;
        public void ShiftY(int newY){
            ShiftY(newY, Math.abs(newY - x) / 200d);
        }
        public void ShiftY(int newY, double duration){
            wantedY = newY;
            YShiftDuration = duration;
            YAge = 0;
        }

        public int color;
        public int wantedColor;
        public double ColorShiftDuration;
        private float ColorAge;
        public void ShiftColor(int newColor){
            ShiftColor(newColor, Math.abs(newColor - x) / 200d);
        }
        public void ShiftColor(int newColor, double duration){
            wantedColor = newColor;
            ColorShiftDuration = duration;
            ColorAge = 0;
        }

        public Font font;

        public void ShiftPosition(int x, int y){
            ShiftPosition(x, y, -1);
        }
        public void ShiftPosition(int x, int y, double duration){
            ShiftPosition(x, y, duration, duration);
        }
        public void ShiftPosition(int x, int y, double XDuration, double YDuration){
            if (XDuration < 0) ShiftX(x);
            else ShiftX(x, XDuration);
            if (YDuration < 0) ShiftY(y);
            else ShiftY(y, YDuration);
        }

        public TextInstance(String text, @Nullable String prepend, @Nullable String append,
                            int x, int y, int color, Font font){
            this.text = text;
            this.prepend = prepend;
            this.append = append;
            this.x = x;
            this.wantedX = x;
            this.y = y;
            this.wantedY = y;
            this.color = color;
            this.font = font;
        }
        public TextInstance(String text, int x, int y, int color, Font font){
            this(text, null, null, x, y, color, font);
        }
        public TextInstance(String text, int x, int y, int color){
            this(text, x, y, color, Minecraft.getInstance().font);
        }
        public TextInstance(String text, int x, int y){
            this(text, x, y, Colors.WHITE.colorInt);
        }

        public void Tick(){
            Age();
            LerpValues();
        }
        public void LerpValues(){
            if (x != wantedX) {
                x = Lerp(x, wantedX, XAge);
            } else {
                XShiftDuration = 0;
            }
            if (y != wantedY){
                y = Lerp(y, wantedY, YAge);
            } else {
                YShiftDuration = 0;
            }
            if (color != wantedColor) {
                color = Lerp(color, wantedColor, ColorAge);
            } else {
                ColorShiftDuration = 0;
            }
        }
        public void Age() {
            if (XShiftDuration > 0) {
                XAge += 1 / (XShiftDuration * 20);
                if (XAge > 1) XAge = 1;
            } else XAge = 0;
            if (YShiftDuration > 0) {
                YAge += 1 / (YShiftDuration * 20);
                if (YAge > 1) YAge = 1;
            } else YAge = 0;
            if (ColorShiftDuration > 0){
                ColorAge += 1 / (ColorShiftDuration * 20);
                if (ColorAge > 1) XAge = 1;
            } else ColorAge = 0;
        }
        private static int Lerp(int from, int to, float partial){
            return (int)(from + (to - from) * partial);
        }
        public String TextWithAppends(){
            return (prepend != null ? prepend : "") + text + (append != null ? append : "");
        }
    }
    public enum Colors{
        WHITE(16777215);

        Colors(int colorInt){
            this.colorInt = colorInt;
        }
        public final int colorInt;
    }
}
