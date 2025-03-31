package mod.pilot.entomophobia.systems.screentextdisplay.keyframes;

import mod.pilot.entomophobia.systems.screentextdisplay.TextInstance;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public abstract class TextKeyframe {
    public TextKeyframe(TextInstance instance, int age){
        this.instance = instance;
        this.trigger = new Trigger.Aged(this, age);
    }
    public TextKeyframe(TextInstance instance, Predicate<TextInstance> trigger){
        this.instance = instance;
        this.trigger = new Trigger.Criteria(this, trigger);
    }
    public final TextInstance instance;
    public TextKeyframe Freeze(){
        this.active = false;
        return this;
    }
    public TextKeyframe Thaw(){
        this.active = true;
        return this;
    }
    public boolean isActive(){return active;}
    protected boolean active = true;

    public TextKeyframe Infinite(){
        this.finite = false;
        return this;
    }
    public TextKeyframe Finite(){
        this.finite = true;
        return this;
    }
    public boolean finite = true;

    public void Tick(){
        if (isActive() && trigger.SelfDestructiveCheck()) {
            Fire();
            if (finite) Freeze();
            PostFire();
        }
    }
    public abstract void Fire();
    public abstract void PostFire();

    public @NotNull Trigger trigger;
    public static abstract class Trigger {
        public Trigger(TextKeyframe keyFrame){
        this.keyFrame = keyFrame;
        }

        public final TextKeyframe keyFrame;
        public boolean fired = false;


        public final TextInstance instance(){return keyFrame.instance;}
        public boolean SelfDestructiveCheck(){
            if (keyFrame.finite) return !this.fired && (this.fired = CheckTrigger());
            else return CheckTrigger();
        }
        public abstract boolean CheckTrigger();

        //Instances
        public static class Aged extends Trigger {
            public int TriggerAge;
            public Aged(TextKeyframe keyFrame, int age){
                super(keyFrame);
                this.TriggerAge = age;
            }

            @Override
            public boolean CheckTrigger() {
                return instance().age % TriggerAge == 0;
            }
        }
        public static class Criteria extends Trigger {
            public Predicate<TextInstance> trigger;
            public Criteria(TextKeyframe keyFrame, Predicate<TextInstance> trigger) {
                super(keyFrame);
                this.trigger = trigger;
            }
            @Override
            public boolean CheckTrigger() {
                return trigger.test(instance());
            }
        }
    }
}
