package mod.pilot.entomophobia.systems.screentextdisplay.keyframes;

import mod.pilot.entomophobia.systems.screentextdisplay.TextInstance;

import java.util.function.Predicate;

public class PositionalKeyframe extends TextKeyframe{
    public PositionalKeyframe(TextInstance instance, int age, float x, float y) {
        this(instance, age, x, y, -1, -1);
    }
    public PositionalKeyframe(TextInstance instance, int age, float x, float y, double xDuration, double yDuration) {
        super(instance, age);
        this.x = x;
        this.y = y;
        this.xDuration = xDuration;
        this.yDuration = yDuration;
    }
    public PositionalKeyframe(TextInstance instance, Predicate<TextInstance> trigger, float x, float y) {
        this(instance, trigger, x, y, -1, -1);
    }
    public PositionalKeyframe(TextInstance instance, Predicate<TextInstance> trigger, float x, float y, double xDuration, double yDuration) {
        super(instance, trigger);
        this.x = x;
        this.y = y;
        this.xDuration = xDuration;
        this.yDuration = yDuration;
    }

    public float x;
    public float y;
    public double xDuration;
    public double yDuration;

    @Override
    public void Fire() {
        boolean xValid = x >= 0;
        boolean yValid = y >= 0;
        if (xValid){
            if (yValid) instance.shiftPosition(x, y, xDuration, yDuration);
            else instance.shiftX(x, xDuration);
        } else if (yValid){
            instance.shiftY(y, yDuration);
        }
    }

    @Override
    public void PostFire() {

    }
}
