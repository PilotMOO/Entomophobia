package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.data.worlddata.SwarmSaveData;
import mod.pilot.entomophobia.entity.AI.SwarmGoals.CaptainCommandGoal;
import mod.pilot.entomophobia.entity.AI.SwarmGoals.FollowCaptainGoal;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.AI.RecruitNearbyGoal;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SwarmManager {
    public enum SwarmTypes{
        aimless, //0
        hunt, //1
        nest, //2
        attack, //3
        scout, //4
        intercept //5
    }
    private static final HashMap<Integer, String> SwarmNames = new HashMap<>();

    public static void populateNameHashmap(){
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
    private static void CleanSwarms() {
        ArrayList<Swarm> toRemove = new ArrayList<>();
        for (Swarm swarm : ActiveSwarms){
            if (swarm.isDisbanded()){
                toRemove.add(swarm);
            }
        }
        ActiveSwarms.removeAll(toRemove);
        SwarmSaveData.Dirty();
    }
    public static void addToSwarms(Swarm swarm){
        ActiveSwarms.add(swarm);
        SwarmSaveData.Dirty();
    }
    public static void PurgeAllSwarms(){
        ActiveSwarms.clear();
    }

    public static @Nullable Swarm getClosestSwarm(Vec3 pos){
        Swarm toReturn = null;
        double distance = Double.MAX_VALUE;
        for (Swarm swarm : getSwarms()){
            if (swarm.getSwarmPosition() != null && swarm.distanceTo(pos) < distance){
                toReturn = swarm;
                distance = swarm.getSwarmPosition().distanceTo(pos);
            }
        }
        return toReturn;
    }
    public static double getDistanceToClosestSwarm(Vec3 pos){
        Swarm closest = getClosestSwarm(pos);
        return closest == null ? -1 : closest.distanceTo(pos);
    }

    private static final ArrayList<ISwarmOrder> defaultOrders = new ArrayList<>(Arrays.asList(
            new FollowCaptainGoal(null, 4, 16, 1),
            new RecruitNearbyGoal(null, 600, 1),
            new CaptainCommandGoal(null, 300, 3)
    ));
    @Deprecated
    public static Swarm createSwarm(SwarmTypes type, MyiaticBase captain, int maxUnits, @Nullable Vec3 finalPos){
        switch (type){
            default -> {
                return null;
            }
            case aimless -> {
                return createAimlessSwarm(captain, maxUnits, finalPos);
            }
            case hunt -> {
                return createHuntSwarm(captain, maxUnits, finalPos);
            }
            case nest -> {
                return createNestSwarm(captain, maxUnits, finalPos);
            }
        }
    }
    @Deprecated
    public static Swarm createSwarm(SwarmTypes type, ArrayList<MyiaticBase> captain, int maxUnits, @Nullable Vec3 finalPos){
        switch (type){
            default -> {
                return null;
            }
            case aimless -> {
                return createAimlessSwarm(captain, maxUnits, finalPos);
            }
            case hunt -> {
                return createHuntSwarm(captain, maxUnits, finalPos);
            }
            case nest -> {
                return createNestSwarm(captain, maxUnits, finalPos);
            }
        }
    }

    public static Swarm.AimlessSwarm createAimlessSwarm(MyiaticBase captain, int maxSwarms, @Nullable Vec3 finalPos) {
        Swarm.AimlessSwarm aimless = new Swarm.AimlessSwarm(captain, maxSwarms, finalPos);
        aimless.generatePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            aimless.relayOrder(order, true);
        }
        aimless.RecruitNearby();
        addToSwarms(aimless);
        return aimless;
    }
    public static Swarm.AimlessSwarm createAimlessSwarm(ArrayList<MyiaticBase> captain, int maxSwarms, @Nullable Vec3 finalPos) {
        Swarm.AimlessSwarm aimless = new Swarm.AimlessSwarm(captain, maxSwarms, finalPos);
        aimless.generatePrimaryOrder(aimless.getCaptain());
        for (ISwarmOrder order : defaultOrders){
            aimless.relayOrder(order, true);
        }
        aimless.RecruitNearby();
        addToSwarms(aimless);
        return aimless;
    }
    public static Swarm.HuntSwarm createHuntSwarm(MyiaticBase captain, int maxSwarms, @Nullable Vec3 finalPos) {
        Swarm.HuntSwarm hunt = new Swarm.HuntSwarm(captain, maxSwarms, finalPos);
        hunt.generatePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            hunt.relayOrder(order, true);
        }
        hunt.RecruitNearby();
        addToSwarms(hunt);
        return hunt;
    }
    public static Swarm.HuntSwarm createHuntSwarm(ArrayList<MyiaticBase> captain, int maxSwarms, @Nullable Vec3 finalPos) {
        Swarm.HuntSwarm hunt = new Swarm.HuntSwarm(captain, maxSwarms, finalPos);
        hunt.generatePrimaryOrder(hunt.getCaptain());
        for (ISwarmOrder order : defaultOrders){
            hunt.relayOrder(order, true);
        }
        hunt.RecruitNearby();
        addToSwarms(hunt);
        return hunt;
    }
    public static Swarm.NestSwarm createNestSwarm(MyiaticBase captain, int maxSwarms, @Nullable Vec3 finalPos) {
        Swarm.NestSwarm nest = new Swarm.NestSwarm(captain, maxSwarms, finalPos);
        nest.generatePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            nest.relayOrder(order, true);
        }
        nest.RecruitNearby();
        addToSwarms(nest);
        return nest;
    }
    public static Swarm.NestSwarm createNestSwarm(ArrayList<MyiaticBase> captain, int maxSwarms, @Nullable Vec3 finalPos) {
        Swarm.NestSwarm nest = new Swarm.NestSwarm(captain, maxSwarms, finalPos);
        nest.generatePrimaryOrder(nest.getCaptain());
        for (ISwarmOrder order : defaultOrders){
            nest.relayOrder(order, true);
        }
        nest.RecruitNearby();
        addToSwarms(nest);
        return nest;
    }
    public static Swarm.AttackSwarm createAttackSwarm(MyiaticBase captain, int maxSwarms, @NotNull Entity target) {
        Swarm.AttackSwarm nest = new Swarm.AttackSwarm(captain, maxSwarms, target);
        nest.generatePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            nest.relayOrder(order, true);
        }
        nest.RecruitNearby();
        addToSwarms(nest);
        return nest;
    }
    public static Swarm.AttackSwarm createAttackSwarm(ArrayList<MyiaticBase> captain, int maxSwarms, @NotNull Entity target) {
        Swarm.AttackSwarm nest = new Swarm.AttackSwarm(captain, maxSwarms, target);
        nest.generatePrimaryOrder(nest.getCaptain());
        for (ISwarmOrder order : defaultOrders){
            nest.relayOrder(order, true);
        }
        nest.RecruitNearby();
        addToSwarms(nest);
        return nest;
    }

    public static Swarm CreateSwarmFromBlueprint(MyiaticBase captain, byte type, byte state, @Nullable Vec3 finalPos, int maxUnits){
        System.out.println("Trying to create a swarm from Blueprint...");
        Swarm toReturn;
        if (captain == null || !captain.canSwarm() || captain.isInSwarm()) {
            System.out.println("There was an issue with the new captain, skipping this swarm...");
            System.out.println("------");
            System.out.println("Issue was:");
            if (captain == null) {
                System.out.println("Captain was null");
                System.out.println("------");
                return null;
            }
            if (!captain.canSwarm()) System.out.println("Captain cannot swarm");
            if (captain.isInSwarm()) System.out.println("Captain was already in a different swarm!");
            System.out.println("------");
            return null;
        }
        System.out.println("New swarm's captain is " + captain);
        switch (type){
            default -> {
                return null;
            }
            case 0 -> toReturn = new Swarm.AimlessSwarm(captain, maxUnits, finalPos);
            case 1 -> toReturn = new Swarm.HuntSwarm(captain, maxUnits, finalPos);
        }
        toReturn.setSwarmState(state);
        toReturn.setDestination(finalPos);

        toReturn.generatePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            toReturn.relayOrder(order, true);
        }

        System.out.println("Successfully created a swarm from Blueprint!");
        addToSwarms(toReturn);
        return toReturn;
    }


    public static void setSwarmDetails(){
        BaseSwarmMaxSize = Config.SERVER.base_swarm_max_members.get();
    }

    private static int BaseSwarmMaxSize;
    public static int getBaseSwarmMaxSize(){
        return BaseSwarmMaxSize;
    }
}
