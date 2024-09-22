package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.entity.AI.Interfaces.SwarmOrder;
import mod.pilot.entomophobia.entity.festered.FesteredBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;

public abstract class Swarm {
    public Swarm(byte type, MyiaticBase captain){
        SwarmType = type;
        AssignNewCaptain(captain);
        if (getCaptain() == null){
            Disband();
            return;
        }
        RecruitNearby();
        Enable();
    }
    public Swarm(byte type, ArrayList<MyiaticBase> possibleCaptains){
        SwarmType = type;
        AssignNewCaptain(DecideCaptain(possibleCaptains));
        if (getCaptain() == null){
            Disband();
            return;
        }
        RecruitNearby();
        Enable();
    }

    protected enum SwarmState{
        disbanded,
        idle,
        active,
        finished
    }

    private final byte SwarmType;
    public final byte getSwarmType(){
        return SwarmType;
    }

    private byte SwarmState;
    public final byte getSwarmState(){
        return SwarmState;
    }
    public final void setSwarmState(byte state){
        SwarmState = state;
    }
    public void Disband(){
        setSwarmState((byte)0);
        AssignNewCaptain(null);
    }
    public boolean isDisbanded(){
        return Captain == null || getSwarmState() == 0;
    }
    public void Idle(){
        setSwarmState((byte)1);
    }
    public boolean isIdling(){
        return Captain != null && getSwarmState() == 1;
    }
    public void Enable(){
        setSwarmState((byte)2);
    }
    public boolean isActive(){
        return Captain != null && getSwarmState() == 2;
    }
    public void Finish() {
        setSwarmState((byte)3);
    }
    public boolean isFinished(){
        return getSwarmState() == 3;
    }

    private MyiaticBase Captain;
    public MyiaticBase getCaptain(){
        return Captain;
    }
    public void AssignNewCaptain(MyiaticBase newCaptain){
        Captain = newCaptain;
    }

    private final ArrayList<MyiaticBase> units = new ArrayList<>();
    public ArrayList<MyiaticBase> getUnits(){
        return new ArrayList<>(units);
    }
    public void addToUnits(MyiaticBase unit){
        units.add(unit);
    }
    public void addToUnits(ArrayList<MyiaticBase> units){
        for (MyiaticBase unit : units){
            addToUnits(unit);
        }
    }
    protected boolean RemoveFromUnits(MyiaticBase M){
        return units.remove(M);
    }
    public void DropMember(MyiaticBase M, boolean disbandIfCaptain){
        if (RemoveFromUnits(M)){
            if (M.amITheCaptain()){
                if (disbandIfCaptain) {
                    Disband();
                }
                else{
                    AssignNewCaptain(DecideCaptain(getUnits()));
                }
            }
            RemoveAllOrdersFor(M);
        }
    }
    public int AmountOfRecruits(){
        return units.size();
    }

    private final ArrayList<SwarmOrder> ActiveOrders = new ArrayList<>();
    public ArrayList<SwarmOrder> getOrders(){
        return new ArrayList<>(ActiveOrders);
    }
    protected void AddSwarmOrder(SwarmOrder order){
        ActiveOrders.add(order);
    }
    protected boolean RemoveSwarmOrder(SwarmOrder order){
        return ActiveOrders.remove(order);
    }
    public void RelayOrder(SwarmOrder order, boolean override){
        if (override){
            for (SwarmOrder currentOrders : getOrders()){
                if (currentOrders.getClass() == order.getClass()){
                    RemoveSwarmOrder(currentOrders);
                }
            }
        }
        AddSwarmOrder(order);
        for (MyiaticBase M : getUnits()){
            M.goalSelector.addGoal(order.getPriority(), order.Relay(M));
        }
    }
    public boolean DiscardSwarmOrder(SwarmOrder order){
        boolean flag = RemoveSwarmOrder(order);
        if (flag){
            for (MyiaticBase M : getUnits()){
                M.goalSelector.removeGoal(order.Relay(M));
            }
        }
        return flag;
    }
    public void AssignAllOrdersFor(MyiaticBase M){
        for (SwarmOrder order : getOrders()){
            M.goalSelector.addGoal(order.getPriority(), order.Relay(M));
        }
    }
    public void RemoveAllOrdersFor(MyiaticBase M){
        for (SwarmOrder order : getOrders()){
            M.goalSelector.removeGoal(order.Relay(M));
        }
    }

    private int MaxRecruits;
    public int getMaxRecruits(){
        return MaxRecruits;
    }
    public void setMaxRecruits(int max){
        MaxRecruits = max;
    }

    protected int RecruitRange(){
        if (getCaptain() == null) return 0;
        return (int)getCaptain().getAttributeValue(Attributes.FOLLOW_RANGE);
    }
    protected boolean CanRecruit(MyiaticBase recruit){
        return recruit.isAlive() && !recruit.isInSwarm() && AmountOfRecruits() < MaxRecruits;
    }

    public boolean AttemptToRecruit(MyiaticBase toRecruit){
        if (toRecruit.distanceTo(getCaptain()) < RecruitRange() && CanRecruit(toRecruit)){
            addToUnits(toRecruit);
            AssignAllOrdersFor(toRecruit);
            return true;
        }
        return false;
    }
    protected MyiaticBase DecideCaptain(ArrayList<MyiaticBase> possibleCaptains) {
        ArrayList<MyiaticBase> myiatics = new ArrayList<>();
        ArrayList<FesteredBase> festereds = new ArrayList<>();

        for (MyiaticBase M : possibleCaptains){
            if (M instanceof FesteredBase){
                festereds.add((FesteredBase)M);
            }
            else{
                myiatics.add(M);
            }
        }

        if (festereds.size() > 0){
            FesteredBase strongest = null;
            double hp = 0;
            for (FesteredBase F : festereds){
                if (strongest == null){
                    strongest = F;
                    hp = F.getAttributeValue(Attributes.MAX_HEALTH);
                }
                else{
                    if (F.getAttributeValue(Attributes.MAX_HEALTH) > hp){
                        strongest = F;
                        hp = F.getAttributeValue(Attributes.MAX_HEALTH);
                    }
                    else if (F.getAttributeValue(Attributes.MAX_HEALTH) == hp){
                        boolean flag = F.getRandom().nextBoolean();
                        strongest = flag ? F : strongest;
                        hp = flag ? F.getAttributeValue(Attributes.MAX_HEALTH) : hp;
                    }
                }
            }

            return strongest;
        }
        else{
            MyiaticBase strongest = null;
            double hp = 0;
            for (MyiaticBase M : myiatics){
                if (strongest == null){
                    strongest = M;
                    hp = M.getAttributeValue(Attributes.MAX_HEALTH);
                }
                else{
                    if (M.getAttributeValue(Attributes.MAX_HEALTH) > hp){
                        strongest = M;
                        hp = M.getAttributeValue(Attributes.MAX_HEALTH);
                    }
                    else if (M.getAttributeValue(Attributes.MAX_HEALTH) == hp){
                        boolean flag = M.getRandom().nextBoolean();
                        strongest = flag ? M : strongest;
                        hp = flag ? M.getAttributeValue(Attributes.MAX_HEALTH) : hp;
                    }
                }
            }

            return strongest;
        }
    }

    public void RecruitNearby(){
        AABB nearby = new AABB(getCaptain().blockPosition()).inflate(RecruitRange());
        for (MyiaticBase M : getCaptain().level().getEntitiesOfClass(MyiaticBase.class, nearby, (M) -> !M.isInSwarm())){
            M.TryToRecruit(this);
        }
    }
}
