package mod.pilot.entomophobia.entity.pathfinding;

import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.util.EntomoTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public interface INestPathfinding {
    Mob getUser();
    PathNavigation getNavigation();
    NestMap getNestMap();
    void setNestMap(NestMap newMap);
    MoveDirections getMoveDirections();
    void setMoveDirections(MoveDirections directions);
    boolean Reorientating();
    default void setReorientating() {setReorientating(true);}
    void setReorientating(boolean flag);
    default boolean amIMovingSomewhereInTheNest(){
        return getNavigation().isInProgress() && getMoveDirections() != null && guesstimateIfImInANest();
    }
    default boolean amIHomeless(){
        return getNestMap() == null || getNestMap().currentNest() == null;
    }
    default boolean amILost(){
        return getNestMap().currentOffshoot == null;
    }
    boolean shouldIReorientateMyself();
    default void HeadBackToCenterToReorientateMyself(double speed){
        HeadTo(getNestMap().currentNest.MainChamber, speed);
        setReorientating();
    }
    default void ReorientateMyself(){
        Nest currentNest = Objects.requireNonNullElseGet(getNestMap().currentNest, () -> getClosestNest(getUser(), this));
        setNestMap(new NestMap(currentNest, currentNest.MainChamber));
    }
    default void HeadTo(Nest.Chamber chamber, double speed){
        setMoveDirections(new MoveDirections(chamber, false));

        Vec3 pos = chamber.getPosition();
        getNavigation().moveTo(pos.x, pos.y - (chamber.radius - chamber.thickness), pos.z, speed);
    }
    default void HeadTo(Nest.Corridor corridor, boolean end, double speed){
        setMoveDirections(new MoveDirections(corridor, end));
        Vec3 pos = end && corridor.end != null ? corridor.end : corridor.getStartDirect();
        getNavigation().moveTo(pos.x, pos.y - (((double)corridor.weight / 2) - corridor.thickness), pos.z, speed);
    }
    default void HeadTo(Nest.Offshoot offshoot, boolean ifCorridorHeadToEnd, double speed){
        if (offshoot instanceof Nest.Chamber c){
            HeadTo(c, speed);
            return;
        }
        if (offshoot instanceof Nest.Corridor c){
            HeadTo(c, ifCorridorHeadToEnd, speed);
            return;
        }
        System.err.println("[NEST NAVIGATION MANAGER] ERROR! Somehow, the inputted offshoot wasn't an instance of Chamber or Corridor." +
                " Did you add a new offshoot without updating the navigation? Method invoke HeadTo(offshoot (" +
                offshoot + "), boolean (" +
                ifCorridorHeadToEnd + "), double (" +
                speed + ")) did nothing");
    }
    default void HeadToMoveDirections(){
        HeadToMoveDirections(1);
    }
    default void HeadToMoveDirections(double speed){
        MoveDirections mD = getMoveDirections();
        if (mD.currentOffshoot() instanceof Nest.Chamber c) HeadTo(c, speed);
        if (mD.currentOffshoot() instanceof Nest.Corridor c) HeadTo(c, mD.headToEndIfCorridor(), speed);
    }
    default void UpdateNavigationAfterMovement(){
        setNestMap(getMoveDirections().UpdateMap(getNestMap()));
        setMoveDirections(null);
    }

    default double NestCheckDistance(){
        return 300;
    }
    default boolean guesstimateIfImInANest(){
        return guesstimateIfImInANest(8);
    }
    default boolean guesstimateIfImInANest(int wantedNestBlocks){
        Mob e = getUser();
        //for checking if entity is on a flesh block or in the air, ensure that's there's a nest in range, and there's enough flesh blocks nearby
        boolean flag1, flag2, flag3;
        flag1 = !e.onGround() || e.getFeetBlockState().is(EntomoTags.Blocks.MYIATIC_FLESH_BLOCKTAG);
        flag2 = getClosestNest(e, this) != null;

        AtomicInteger fBlockCount = new AtomicInteger();
        e.level().getBlockStates(e.getBoundingBox().inflate(8)).forEach((b) ->{
            if (b.is(EntomoTags.Blocks.MYIATIC_FLESH_BLOCKTAG)) fBlockCount.getAndIncrement();
        });
        flag3 = fBlockCount.get() >= wantedNestBlocks;

        return flag1 && flag2 && flag3;
    }

    static @Nullable Nest getClosestNest(Entity e, INestPathfinding pFinder){
        return getClosestNest(e.position(), pFinder.NestCheckDistance());
    }
    static @Nullable Nest getClosestNest(Entity e, double withinDistance){
        return getClosestNest(e.position(), withinDistance);
    }
    static @Nullable Nest getClosestNest(Vec3 pos, INestPathfinding pFinder){
        return getClosestNest(pos, pFinder.NestCheckDistance());
    }
    static @Nullable Nest getClosestNest(Vec3 pos, double withinDistance){
        return NestManager.getClosestNest(pos, withinDistance);
    }
    default @Nullable Nest getClosestNest(){
        return INestPathfinding.getClosestNest(getUser(), this);
    }

    record NestMap(Nest currentNest, Nest.Offshoot currentOffshoot){
        /**Returns a NestMap of the given nest, with the Entrance Corridor as the Offshoot*/
        public static NestMap MapOf(Nest nest){
            return new NestMap(nest, getLastEntranceCorridor(nest));
        }
        public static NestMap MapAtMain(Nest nest){
            return new NestMap(nest, nest.MainChamber);
        }
        public @Nullable Nest.Offshoot getParentOfCurrent(){
            return currentOffshoot != null ? currentOffshoot.parent : null;
        }
        public @Nullable Nest.Chamber getMainChamber(){
            return currentNest != null ? currentNest.MainChamber : null;
        }
        public static @NotNull ArrayList<Nest.Corridor> getEntranceCorridors(Nest from){
            ArrayList<Nest.Corridor> entrances = new ArrayList<>();

            Nest.Corridor current = null;
            for (Nest.Offshoot o : from.offshoots()){
                if (o instanceof Nest.Corridor c && c.isEntrance()) {
                    current = c;
                    break;
                }
            }
            if (current == null) return entrances;

            do{
                entrances.add(current);
                if (current.children != null){
                    for (Nest.Offshoot o : current.children){
                        if (o instanceof Nest.Corridor c && c.isEntrance()) {
                            current = c;
                            break;
                        }
                    }
                }
                else break;
            }while(true);
            return entrances;
        }
        public static @Nullable Nest.Corridor getFirstEntranceCorridor(Nest from){
            if (from.MainChamber == null) return null;
            Nest.Corridor toReturn = null;
            for (Nest.Offshoot o : from.offshoots()){
                if (o instanceof Nest.Corridor c && c.isEntrance()) {
                    toReturn = c;
                    break;
                }
            }
            return toReturn;
        }
        public static @Nullable Nest.Corridor getLastEntranceCorridor(Nest from){
            if (from == null || from.MainChamber == null) return null;
            Nest.Corridor current = null;
            for (Nest.Offshoot o : from.offshoots()){
                if (o instanceof Nest.Corridor c && c.isEntrance()) {
                    current = c;
                    break;
                }
            }
            if (current == null) return null;

            while (current.children != null){
                for (Nest.Offshoot o : current.children){
                    if (o instanceof Nest.Corridor c && c.isEntrance()){
                        current = c;
                        break;
                    }
                }
            }
            return current;
        }
    }
    record MoveDirections(Nest.Offshoot currentOffshoot, boolean headToEndIfCorridor){
        public NestMap UpdateMap(NestMap old){
            return new NestMap(old.currentNest, currentOffshoot);
        }
    }
}
