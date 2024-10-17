package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.entity.AI.CaptainCommandGoal;
import mod.pilot.entomophobia.entity.AI.FollowCaptainGoal;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.AI.RecruitNearbyGoal;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.nest.Nest;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SwarmManager {
    public enum SwarmTypes{
        aimless,
        hunt,
        nest,
        attack,
        scout,
        intercept
    }
    private static final HashMap<Integer, String> SwarmNames = new HashMap<>();

    public static void PopulateNameHashmap(){
        SwarmNames.put(0, "Team Rocket");
        SwarmNames.put(1, "The United States of Bugtopia");
        SwarmNames.put(2, "Literally 1984");
        SwarmNames.put(3, "The Beetles");
        SwarmNames.put(4, "Republic of the Jar");
        SwarmNames.put(5, "Tilted Towers Enjoyers");
        SwarmNames.put(6, "Hotwheels Sisyphus Spiders");
        SwarmNames.put(7, "Lobotomy Crew");
        SwarmNames.put(8, "Mincerafters");
        SwarmNames.put(9, "Lunatic Cultists");
        SwarmNames.put(10, "John Fungal Stans");
        SwarmNames.put(11, "The Fembugs");
        SwarmNames.put(12, "Illiterate Jackasses");
        SwarmNames.put(13, "Mold Snorters");
    }
    public static String getNameFor(Swarm swarm){
        int index = getSwarms().indexOf(swarm);
        if (index == -1 || index > SwarmNames.size()) return "Unnamed Swarm";
        return SwarmNames.get(index);
    }

    private static final ArrayList<Swarm> ActiveSwarms = new ArrayList<>();
    public static ArrayList<Swarm> getSwarms(){
        CleanSwarms();
        return new ArrayList<>(ActiveSwarms);
    }
    public static @Nullable Swarm getClosestSwarm(Vec3 pos){
        Swarm toReturn = null;
        double distance = Double.MAX_VALUE;
        for (Swarm swarm : getSwarms()){
            if (swarm.getSwarmPosition() != null && swarm.getSwarmPosition().distanceTo(pos) < distance){
                toReturn = swarm;
                distance = swarm.getSwarmPosition().distanceTo(pos);
            }
        }
        return toReturn;
    }
    private static void CleanSwarms() {
        ArrayList<Swarm> toRemove = new ArrayList<>();
        for (Swarm swarm : ActiveSwarms){
            if (swarm.isDisbanded()){
                toRemove.add(swarm);
            }
        }
        ActiveSwarms.removeAll(toRemove);
    }
    public static void addToSwarms(Swarm swarm){
        ActiveSwarms.add(swarm);
    }

    private static final ArrayList<ISwarmOrder> defaultOrders = new ArrayList<>(Arrays.asList(
            new FollowCaptainGoal(null, 4, 48, 1),
            new RecruitNearbyGoal(null, 600, 1),
            new CaptainCommandGoal(null, 300, 3)
    ));
    public static Swarm CreateSwarm(SwarmTypes type, MyiaticBase captain, int maxUnits){
        switch (type){
            default -> {
                return null;
            }
            case aimless -> {
                return CreateAimlessSwarm(captain, maxUnits);
            }
            case hunt -> {
                return CreateHuntSwarm(captain, maxUnits);
            }
        }
    }
    public static Swarm CreateSwarm(SwarmTypes type, ArrayList<MyiaticBase> captain, int maxUnits){
        switch (type){
            default -> {
                return null;
            }
            case aimless -> {
                return CreateAimlessSwarm(captain, maxUnits);
            }
            case hunt -> {
                return CreateHuntSwarm(captain, maxUnits);
            }
        }
    }

    private static Swarm.AimlessSwarm CreateAimlessSwarm(MyiaticBase captain, int maxSwarms) {
        Swarm.AimlessSwarm aimless = new Swarm.AimlessSwarm(captain, maxSwarms);
        addToSwarms(aimless);
        aimless.GeneratePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            aimless.RelayOrder(order, true);
        }
        aimless.RecruitNearby();
        return aimless;
    }
    private static Swarm.AimlessSwarm CreateAimlessSwarm(ArrayList<MyiaticBase> captain, int maxSwarms) {
        Swarm.AimlessSwarm aimless = new Swarm.AimlessSwarm(captain, maxSwarms);
        addToSwarms(aimless);
        aimless.GeneratePrimaryOrder(aimless.getCaptain());
        for (ISwarmOrder order : defaultOrders){
            aimless.RelayOrder(order, true);
        }
        aimless.RecruitNearby();
        return aimless;
    }
    private static Swarm.HuntSwarm CreateHuntSwarm(MyiaticBase captain, int maxSwarms) {
        Swarm.HuntSwarm hunt = new Swarm.HuntSwarm(captain, maxSwarms);
        addToSwarms(hunt);
        hunt.GeneratePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            hunt.RelayOrder(order, true);
        }
        hunt.RecruitNearby();
        return hunt;
    }
    private static Swarm.HuntSwarm CreateHuntSwarm(ArrayList<MyiaticBase> captain, int maxSwarms) {
        Swarm.HuntSwarm hunt = new Swarm.HuntSwarm(captain, maxSwarms);
        addToSwarms(hunt);
        hunt.GeneratePrimaryOrder(hunt.getCaptain());
        for (ISwarmOrder order : defaultOrders){
            hunt.RelayOrder(order, true);
        }
        hunt.RecruitNearby();
        return hunt;
    }

    public static void setSwarmDetails(){
        BaseSwarmMaxSize = Config.SERVER.base_swarm_max_members.get();
    }

    private static int BaseSwarmMaxSize;
    public static int getBaseSwarmMaxSize(){
        return BaseSwarmMaxSize;
    }
}
