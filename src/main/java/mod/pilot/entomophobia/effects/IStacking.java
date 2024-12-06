package mod.pilot.entomophobia.effects;

public interface IStacking {
    int getWrapAroundThreshold();
    int getMinimumWrapDuration();
    default int getMaxCap(){
        return -1;
    }
    default boolean hasCap(){
        return getMaxCap() != -1;
    }
    default boolean canDurationExtendIfCapped(){
        return true;
    }
    default int getDegradeDuration(){
        return getWrapAroundThreshold();
    }
    default boolean isDegradable(){
        return false;
    }
}
