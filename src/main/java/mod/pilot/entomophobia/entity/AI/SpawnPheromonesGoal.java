package mod.pilot.entomophobia.entity.AI;

import mod.pilot.entomophobia.worlddata.EntomoWorldManager;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.pheromones.PheromonesEntityBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.function.Predicate;

public class SpawnPheromonesGoal extends Goal {
    final MyiaticBase parent;
    final EntityType<? extends PheromonesEntityBase> PheroType;
    final int SearchRange;
    final int MaxCD;
    int CD;
    final Predicate<MyiaticBase> SpawnParams;
    public SpawnPheromonesGoal(MyiaticBase parent, EntityType<? extends  PheromonesEntityBase> pheroType, int searchRange, int CD, Predicate<MyiaticBase> spawnParams){
        this.parent = parent;
        PheroType = pheroType;
        SearchRange = searchRange;
        MaxCD = CD;
        this.CD = 0;
        SpawnParams = spawnParams;
    }
    public SpawnPheromonesGoal(MyiaticBase parent, EntityType<? extends  PheromonesEntityBase> pheroType, int CD, Predicate<MyiaticBase> spawnParams){
        this.parent = parent;
        PheroType = pheroType;
        SearchRange = (int)parent.getAttributeValue(Attributes.FOLLOW_RANGE);
        MaxCD = CD;
        this.CD = 0;
        SpawnParams = spawnParams;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        if (CD > 0){
            CD--;
        }
        if (SpawnParams.test(parent) && CanSpawnPheromone()){
            SpawnPheromone();
        }
    }

    private boolean CanSpawnPheromone() {
        return CD <= 0 && !parent.isThereAPheromoneOfTypeXNearby(PheroType, SearchRange);
    }

    private void SpawnPheromone() {
        EntomoWorldManager.CreateNewEntityAt(PheroType, parent);
        CD = MaxCD;
    }
}