package mod.pilot.entomophobia.systems.nest.hiveheart.decisions;

import mod.pilot.entomophobia.systems.nest.hiveheart.HiveNervousSystem;
import mod.pilot.entomophobia.systems.nest.hiveheart.StimulantType;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

public abstract class Decision{
    public HiveNervousSystem nervousSystem;
    public StimulantType stimulantType;
    public Decision(HiveNervousSystem nervousSystem, StimulantType stimulantType){
        this.nervousSystem = nervousSystem;
        this.stimulantType = stimulantType;
    }
    public Decision(HiveNervousSystem nervousSystem){
        this(nervousSystem, StimulantType.Undefined);
    }
    public void replaceNervousSystem(HiveNervousSystem nervousSystem){
        this.nervousSystem = nervousSystem;
    }


    public void trigger(){
        if (condition()) activate();
    }
    public abstract boolean condition();
    public abstract void activate();

    public ServerLevel server(){
        return nervousSystem.serverLevel;
    }


    /**
     * Checks if the given Decision is Continuous by checking then casting if true, returning null if it does not extend Decision.Continuous
     * @return The given Decision as a Decision.Continuous Object IF it extends Decision.Continuous, otherwise null
     */
    public @Nullable Continuous continuous() {return (this instanceof Continuous c) ? c : null;}

    public static abstract class Continuous extends Decision{
        public Continuous(HiveNervousSystem brain, StimulantType stimulantType) {
            super(brain, stimulantType);
        }
        public Continuous(HiveNervousSystem brain) {
            super(brain);
        }
        public abstract boolean activeUntil();
        public abstract void lifecycle();
    }
}
