package mod.pilot.entomophobia.systems.screentextdisplay.keyframes;

import mod.pilot.entomophobia.systems.screentextdisplay.TextInstance;

import java.awt.*;
import java.util.function.Predicate;

public class ColorKeyframe extends TextKeyframe{
    public ColorKeyframe(TextInstance instance, int age, Color color, double duration) {
        super(instance, age);
        this.color = color;
        this.duration = duration;
    }
    public ColorKeyframe(TextInstance instance, Predicate<TextInstance> trigger, Color color, double duration) {
        super(instance, trigger);
        this.color = color;
        this.duration = duration;
    }

    public Color color;
    public double duration;

    @Override
    public void Fire() {
        instance.shiftColor(color, duration);
    }

    @Override
    public void PostFire() {}
}
