package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.data.EntomoWorldManager;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.EntomoEntities;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class HuntSwarmCaptainGoal extends Goal implements ISwarmOrder {
    final MyiaticBase parent;
    final int Priority;
    final int NextAreaTimer;
    int NATTracker = 0;
    boolean traveling = false;
    Vec3 lastPos = null;
    public HuntSwarmCaptainGoal(MyiaticBase parent, int nextAreaTimer, int priority){
        this.parent = parent;
        this.NextAreaTimer = nextAreaTimer;
        this.Priority = priority;
    }

    @Override
    public boolean canUse() {
        return parent.isInSwarm() && parent.getTarget() == null;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        lastPos = parent.position();
    }

    @Override
    public void tick() {
        NATTracker++;
        if (NATTracker >= NextAreaTimer){
            System.out.println("The captain declares we need to move somewhere else!");
            Vec3 nextArea = null;
            ArrayList<LivingEntity> possiblePrey = parent.getValidTargets((int)(parent.getAttributeValue(Attributes.FOLLOW_RANGE) * 2));
            int cycleTracker = 0;
            while (nextArea == null && cycleTracker < 5){
                if (possiblePrey.size() > 0){
                    nextArea = DefaultRandomPos.getPosTowards(parent, 96, 32,
                            possiblePrey.get(parent.getRandom().nextInt(possiblePrey.size())).position(), 1.5);
                }
                else{
                    nextArea = DefaultRandomPos.getPosAway(parent, 96, 32, lastPos);
                }
                cycleTracker++;
            }
            NATTracker = 0;
            if (nextArea == null){
                System.out.println("Nvm");
                stop();
                return;
            }
            System.out.println("Let's head over to " + nextArea);
            lastPos = nextArea;
            parent.getNavigation().moveTo(nextArea.x, nextArea.y, nextArea.z, 0.75);
            traveling = true;
        }
        if (parent.getNavigation().isDone() && traveling){
            System.out.println("We've reached our destination! Let's see if there is anything good to eat nearby...");
            traveling = false;
            NATTracker = NextAreaTimer;

            List<LivingEntity> nearbyPrey = parent.getValidTargets();
            if (nearbyPrey.size() > 0){
                System.out.println("Yummers! We've found something!");
                if (!parent.isThereAPheromoneOfTypeXNearby(EntomoEntities.PREYHUNT.get())){
                    System.out.println("Deploying Prey pheromone...");
                    EntomoWorldManager.CreateNewEntityAt(EntomoEntities.PREYHUNT.get(), parent);
                }
            }
            else{
                System.out.println("Sadge, nothing here... let's move on");
                NATTracker /= 4;
            }
        }
    }

    @Override
    public Goal Relay(MyiaticBase M) {
        return new HuntSwarmCaptainGoal(M, NextAreaTimer, getPriority());
    }
    @Override
    public MyiaticBase getParent() {
        return parent;
    }
    @Override
    public int getPriority() {
        return Priority;
    }

    @Override
    public boolean CaptainOnly() {
        return true;
    }
}
