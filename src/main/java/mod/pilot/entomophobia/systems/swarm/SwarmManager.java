package mod.pilot.entomophobia.systems.swarm;

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

    private static ArrayList<Swarm> ActiveSwarms = new ArrayList<>();
    public static ArrayList<Swarm> getSwarms(){
        return new ArrayList<>(ActiveSwarms);
    }
    public static void addToSwarms(Swarm swarm){
        ActiveSwarms.add(swarm);
    }
}
