package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.Config;

import java.util.ArrayList;

public class SwarmManager {
    public enum SwarmTypes{
        empty,
        hunt,
        nest,
        attack,
        scout,
        intercept
    }

    private static final ArrayList<Swarm> ActiveSwarms = new ArrayList<>();
    public static ArrayList<Swarm> getSwarms(){
        return new ArrayList<>(ActiveSwarms);
    }
    public static void addToSwarms(Swarm swarm){
        ActiveSwarms.add(swarm);
    }

    public static void setSwarmDetails(){
        BaseSwarmMaxSize = Config.SERVER.base_swarm_max_members.get();
    }

    private static int BaseSwarmMaxSize;
    public static int getBaseSwarmMaxSize(){
        return BaseSwarmMaxSize;
    }
}
