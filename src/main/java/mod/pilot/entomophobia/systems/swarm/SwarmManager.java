package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.Config;
import mod.pilot.entomophobia.entity.AI.FollowCaptainGoal;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.nest.Nest;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class SwarmManager {
    public enum SwarmTypes{
        aimless,
        hunt,
        nest,
        attack,
        scout,
        intercept
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
    }
    public static void addToSwarms(Swarm swarm){
        ActiveSwarms.add(swarm);
    }

    private static final ArrayList<ISwarmOrder> defaultOrders = new ArrayList<>(Arrays.asList(
            new FollowCaptainGoal(null, null, 4, 48, 1)
    ));
    public static Swarm CreateSwarm(SwarmTypes type, MyiaticBase captain, @Nullable Nest parentNest, int maxSwarms){
        switch (type){
            default -> {
                return null;
            }
            case aimless -> {
                return CreateAimlessSwarm(captain, parentNest, maxSwarms);
            }
            case hunt -> {
                return CreateHuntSwarm(captain, parentNest, maxSwarms);
            }
        }
    }

    private static Swarm.AimlessSwarm CreateAimlessSwarm(MyiaticBase captain, Nest parentNest, int maxSwarms) {
        Swarm.AimlessSwarm aimless = new Swarm.AimlessSwarm(captain, parentNest, maxSwarms);
        aimless.GeneratePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            aimless.RelayOrder((ISwarmOrder)order.ReplaceCaptain(captain), true);
        }
        aimless.RecruitNearby();
        addToSwarms(aimless);
        return aimless;
    }
    private static Swarm.HuntSwarm CreateHuntSwarm(MyiaticBase captain, Nest parentNest, int maxSwarms) {
        Swarm.HuntSwarm hunt = new Swarm.HuntSwarm(captain, parentNest, maxSwarms);
        hunt.GeneratePrimaryOrder(captain);
        for (ISwarmOrder order : defaultOrders){
            hunt.RelayOrder((ISwarmOrder)order.ReplaceCaptain(captain), true);
        }
        hunt.RecruitNearby();
        addToSwarms(hunt);
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
