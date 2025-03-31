package mod.pilot.entomophobia.systems.screentextdisplay;

import mod.pilot.entomophobia.systems.screentextdisplay.keyframes.TextKeyframe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class TextInstance{
    private static final Random random = new Random();
    public static TextInstance create(){
        return create("", null, null);
    }
    public static TextInstance create(String text){
        return create(text, null, null);
    }
    public static TextInstance create(String text, @Nullable String prepend, @Nullable String append){
        return new TextInstance(text, prepend, append);
    }
    protected TextInstance(String text, @Nullable String prepend, @Nullable String append){
        this.text = text;
        this.prepend = prepend;
        this.append = append;
        this.age = 0;
    }

    public @Nullable String prepend;
    public String text;
    public @Nullable String append;
    public String TextWithAppends(){
        return (prepend != null ? prepend : "") + text + (append != null ? append : "");
    }

    public TextInstance at(float x, float y){
        this.x = this.oldX = wantedX = x;
        this.y = this.oldY = wantedY = y;
        return this;
    }
    public float oldX;
    public float x;
    public float wantedX;
    public double XShiftDuration;
    public float XAge;
    public float getXForRendering(float partial){
        float x1 = (TextInstance.Lerp(oldX, x, partial) / size);
        if (shaking() && XShake) x1 += random.nextInt(-shakingStrength, shakingStrength + 1);
        x1 -= (font.width(TextWithAppends()) / 2f);
        return x1;
    }
    public TextInstance shiftX(float newX){
        return shiftX(newX, Math.abs(newX - x) / 20d);
    }
    public TextInstance shiftX(float newX, double duration){
        wantedX = newX;
        XShiftDuration = duration;
        XAge = 0;

        return this;
    }

    public float oldY;
    public float y;
    public float wantedY;
    public double YShiftDuration;
    private float YAge;
    public float getYForRendering(float partial){
        float y1 = (TextInstance.Lerp(oldY, y, partial) / size);
        if (shaking() && YShake) y1 += random.nextInt(-shakingStrength, shakingStrength + 1);
        y1 -= (font.lineHeight / 2f);
        return y1;
    }
    public TextInstance shiftY(float newY){
        return shiftY(newY, Math.abs(newY - x) / 20d);
    }
    public TextInstance shiftY(float newY, double duration){
        wantedY = newY;
        YShiftDuration = duration;
        YAge = 0;
        return this;
    }

    public TextInstance shiftPosition(float x, float y){
        return shiftPosition(x, y, false);
    }
    public TextInstance shiftPosition(float x, float y, boolean calculateSeparate){
        return shiftPosition(x, y, calculateSeparate ? -1 : getAverageBetweenDifferences(this.x, x, this.y, y) / 20d);
    }
    public TextInstance shiftPosition(float x, float y, double duration){
        return shiftPosition(x, y, duration, duration);
    }
    public TextInstance shiftPosition(float x, float y, double XDuration, double YDuration){
        if (XDuration < 0) shiftX(x);
        else shiftX(x, XDuration);
        if (YDuration < 0) shiftY(y);
        else shiftY(y, YDuration);

        return this;
    }

    public TextInstance withColor(Color color){
        this.color = this.oldColor = this.wantedColor = color;
        return this;
    }
    public Color oldColor = Color.WHITE;
    public Color color = Color.WHITE;
    public Color wantedColor = Color.WHITE;
    public double ColorShiftDuration;
    private float ColorAge;
    public Color getColorForRendering(float partial){
        return LerpColors(oldColor, color, partial);
    }
    public int getRGBAForRendering(float partial){
        return getColorForRendering(partial).getRGB();
    }
    public TextInstance shiftColor(int newColor, boolean hasAlpha, double duration){
        return shiftColor(new Color(newColor, hasAlpha), duration);
    }
    public TextInstance shiftColor(Color newColor, double duration){
        wantedColor = newColor;
        ColorShiftDuration = duration;
        ColorAge = 0;

        return this;
    }

    public TextInstance withFont(Font font){
        this.font = font;
        return this;
    }
    public Font font = Minecraft.getInstance().font;

    public TextInstance shadowed(boolean shadow){
        this.shadow = shadow;
        return this;
    }
    public boolean shadow = true;
    public TextInstance ofSize(float size){
        this.size = size;
        return this;
    }
    public float size = 1.0f;
    public TextInstance withShaking(int strength){
        this.shakingStrength = strength;
        return this;
    }
    public boolean shaking() {return shakingStrength > 0;}
    public int shakingStrength = -1;
    public boolean XShake = true;
    public boolean YShake = true;

    public TextInstance aged(int maxAge){
        this.maxAge = maxAge;
        return this;
    }
    public TextInstance delayOf(int ticks){
        age = -ticks;
        return this;
    }
    public int maxAge = -1;
    public int age = 0;
    boolean remove = false;
    public boolean shouldBeRemoved(){
        return remove || altRemoveCheck();
    }
    protected boolean altRemoveCheck(){
        return maxAge != -1 && maxAge < age;
    }

    public TextInstance addKeyframe(TextKeyframe kFrame){
        this.keyframes.add(kFrame);
        return this;
    }
    public TextInstance discardKeyframe(TextKeyframe kFrame){
        if (keyframes.contains(kFrame)) discardedKeyframes.add(kFrame);
        return this;
    }
    protected ArrayList<TextKeyframe> keyframes = new ArrayList<>();
    protected ArrayList<TextKeyframe> discardedKeyframes = new ArrayList<>();
    protected final ArrayList<TextKeyframe> volatileKeyframes = new ArrayList<>();
    protected void updateVolatileList(){
        keyframes.removeAll(discardedKeyframes);
        volatileKeyframes.clear();
        volatileKeyframes.addAll(keyframes);
    }


    public void Render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight){
        Color renderColor;
        if (age <= 0 || (renderColor = getColorForRendering(partialTick)).getAlpha() <= 4) return;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(size, size, size);
        font.drawInBatch(TextWithAppends(),
                getXForRendering(partialTick),
                getYForRendering(partialTick),
                renderColor.getRGB(),
                shadow, guiGraphics.pose().last().pose(),
                guiGraphics.bufferSource(), Font.DisplayMode.NORMAL,
                0x00FFFFFF, 15728880);
        guiGraphics.pose().popPose();
    }
    public void Tick(){
        Age();
        LerpAndSetValues();
        volatileKeyframes.forEach(TextKeyframe::Tick);
        updateVolatileList();
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
        age++;
    }
    public void LerpAndSetValues(){
        oldX = x;
        oldY = y;
        oldColor = color;
        if (x != wantedX) {
            x = Lerp(x, wantedX, XAge);
        } else if (XShiftDuration != 0) {
            onReachingX();
            onReachingXOrY(true, y == wantedY);
            XShiftDuration = 0;
        }
        if (y != wantedY){
            y = Lerp(y, wantedY, YAge);
        } else if (YShiftDuration != 0) {
            onReachingY();
            onReachingXOrY(x == wantedX, true);
            YShiftDuration = 0;
        }
        if (!color.equals(wantedColor)) {
            color = LerpColors(color, wantedColor, ColorAge);
        } else if (ColorShiftDuration == 0) {
            onReachingColor();
            ColorShiftDuration = 0;
        }
    }
    public void onReachingX(){}
    public void onReachingY(){}
    public void onReachingXOrY(boolean x, boolean y){}
    public void onReachingColor(){}

    public static int Lerp(int from, int to, float partial){
        return (int)(from + (to - from) * partial);
    }
    public static float Lerp(float from, float to, float partial){
        return from + (to - from) * partial;
    }
    public static Color LerpColors(Color from, Color to, float partial){
        int r = Lerp(from.getRed(), to.getRed(), partial);
        int b = Lerp(from.getBlue(), to.getBlue(), partial);
        int g = Lerp(from.getGreen(), to.getGreen(), partial);
        int a = Lerp(from.getAlpha(), to.getAlpha(), partial);
        return new Color(r, g, b, a);
    }
    private double getAverageBetweenDifferences(float originX, float endX, float originY, float endY) {
        double absX = Math.abs(endX - originX);
        double absY = Math.abs(endY - originY);
        return (absX + absY) / 2;
    }
}