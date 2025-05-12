package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pathfinding.INestPathfinding;
import mod.pilot.entomophobia.systems.nest.Nest;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;

public class NestPatrolGoal extends Goal {
    public NestPatrolGoal(MyiaticBase parent, int timer, int timerRandomClamp){
        this(parent, timer, timerRandomClamp, 1, true);
    }
    public NestPatrolGoal(MyiaticBase parent, int timer, int timerRandomClamp, double speed){
        this(parent, timer, timerRandomClamp, speed, true);
    }
    public NestPatrolGoal(MyiaticBase parent, int timer, int timerRandomClamp, boolean assumeAtMainChamber){
        this(parent, timer, timerRandomClamp, 1, assumeAtMainChamber);
    }
    public NestPatrolGoal(MyiaticBase parent, int timer, int timerRandomClamp, double navSpeed, boolean assumeAtMainChamber){
        this.parent = parent;
        this.patrolTimerMax = timer;
        this.pTimerClamp = timerRandomClamp;
        this.patrolSpeed = navSpeed;
        this.PatrolTimer = 0;

        if (parent.getNavigation() instanceof INestPathfinding iNest){
            nestPathfinder = iNest;
        } else throw new RuntimeException(
                "ERROR! Attempted to create a NestPatrolGoal for an entity whose Pathfinder does NOT implement INestPathfinding!");

        if (timer - timerRandomClamp < 0){
            throw new RuntimeException("ERROR! argument timer minus argument timerRandomClamp (" +
                    timer + " - " + timerRandomClamp + " = " + (timer - timerRandomClamp) + ") is LESS THAN 0!" +
                    " Ensure that timer - timerRandomClamp is GREATER THAN or EQUAL TO 0");
        }

        boolean NestMapNullFlag = nestPathfinder.getNestMap() == null || nestPathfinder.getNestMap().currentOffshoot() == null;
        if (assumeAtMainChamber){
            if (NestMapNullFlag) nestPathfinder.setNestMap(INestPathfinding.NestMap.MapAtMain(nestPathfinder.getClosestNest()));
        } else {
            System.err.println("[NEST NAVIGATION MANAGER] WARNING! Assigned a new NestPatrolGoal to an entity without a valid NestMap!");
        }
    }

    private final MyiaticBase parent;
    private final int patrolTimerMax;
    private final int pTimerClamp;
    public int PatrolTimer;
    public double patrolSpeed;

    public final INestPathfinding nestPathfinder;
    protected boolean inNest;

    @Override
    public boolean canUse() {
        return nestPathfinder.getNavigation().isDone() && cycleTimer();
    }

    @Override
    public boolean canContinueToUse() {
        if (!inNest || parent.tickCount % 120 == 0){
            inNest = nestPathfinder.guesstimateIfImInANest();
        }
        return inNest;
    }

    @Override
    public void start() {
        boolean backtrack;
        INestPathfinding.NestMap map = nestPathfinder.getNestMap();
        Nest.Offshoot o = map.currentOffshoot();

        if (o.parent == null) backtrack = false;
        else if (o.parent.children == null) backtrack = true;
        else backtrack = getRandom().nextBoolean();

        if (backtrack) {
            o = o.parent;
        } else if (o.children != null){
            o = o.children.get(getRandom().nextInt(o.children.size()));
        } else return;

        nestPathfinder.headTo(o, backtrack, 1);
    }

    private boolean cycleTimer(){
        if (--PatrolTimer <= 0){
            PatrolTimer = patrolTimerMax + getRandom().nextInt(-pTimerClamp, pTimerClamp);
            return true;
        } else return false;
    }
    private RandomSource getRandom(){
        return parent.getRandom();
    }
}
