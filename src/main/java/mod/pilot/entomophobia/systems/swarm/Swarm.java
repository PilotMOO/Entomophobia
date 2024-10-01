package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.entity.AI.HuntSwarmGoal;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.festered.FesteredBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class Swarm {
    protected Swarm(byte type, MyiaticBase captain, @Nullable Nest parentNest, int maxRecruits){
        SwarmType = type;
        AssignNewCaptain(captain);
        if (getCaptain() == null){
            Disband();
            return;
        }
        if (parentNest == null){
            AssignClosestNest();
        }
        else{
            setNest(parentNest);
        }
        setMaxRecruits(maxRecruits);
        Enable();
    }
    protected Swarm(byte type, ArrayList<MyiaticBase> possibleCaptains, @Nullable Nest parentNest, int maxRecruits){
        SwarmType = type;
        AssignNewCaptain(DecideCaptain(possibleCaptains));
        if (getCaptain() == null){
            Disband();
            return;
        }
        if (parentNest == null){
            AssignClosestNest();
        }
        else{
            setNest(parentNest);
        }
        setMaxRecruits(maxRecruits);
        Enable();
    }

    protected enum SwarmStates{
        disbanded,
        idle,
        active,
        finished
    }
    @Override
    public String toString() {
        return "Swarm [type: " + SwarmManager.SwarmTypes.values()[getSwarmType()] + ", state: " +
                SwarmStates.values()[getSwarmState()] + ", has Captain: " + (getCaptain() != null) + ", has ParentNest: " +
                (getNest() != null) + "]";
    }

    private final byte SwarmType;
    public final byte getSwarmType(){
        return SwarmType;
    }
    public Swarm ConvertSwarmType(SwarmManager.SwarmTypes newType, boolean AssignNewCaptain, boolean clearPrimary){
        Swarm toReturn;
        MyiaticBase newCaptain = AssignNewCaptain ? DecideCaptain(getUnits()) : getCaptain();
        switch (newType.ordinal()){
            default -> toReturn = null;
            case 0 -> toReturn = new AimlessSwarm(newCaptain, getNest(), getMaxRecruits());
            case 1 -> toReturn = new HuntSwarm(newCaptain, getNest(), getMaxRecruits());
        }
        if (toReturn == null) return null;

        toReturn.setSwarmState(getSwarmState());
        toReturn.CopyUnits(this, false);
        toReturn.CopyOrders(this, false);

        return toReturn;
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
        System.out.println("Current State: " + getSwarmState());
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
    public void AssignNewCaptain(@Nullable MyiaticBase newCaptain){
        if (getCaptain() != null){
            getCaptain().LeaveSwarm(false);
        }
        if (newCaptain != null){
            newCaptain.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200)); //THIS IS TEMPORARY
            newCaptain.ForceJoin(this, true);
        }
        Captain = newCaptain;
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


    public @Nullable Vec3 getSwarmPosition(){
        if (getCaptain() == null) return null;
        return getCaptain().position();
    }
    public @Nullable Vec3 getNestPosition(){
        if (getNest() == null) return null;
        return getNest().origin;
    }
    public double getDistanceToParentNest(){
        Vec3 swarmPos = getSwarmPosition();
        Vec3 nestPos = getNestPosition();
        if (nestPos == null || swarmPos == null) return -1;
        return swarmPos.distanceTo(nestPos);
    }

    @Nullable
    private Nest ParentNest;
    public @Nullable Nest getNest(){
        return ParentNest;
    }
    protected void setNest(Nest nest){
        ParentNest = nest;
    }
    public boolean AssignClosestNest(){
        Vec3 pos = getSwarmPosition();
        if (pos == null) return false;

        ArrayList<Nest> nests = NestManager.getActiveNests();

        Nest closest = null;
        double distance = Double.MAX_VALUE;
        for (Nest nest : nests){
            if (closest == null){
                closest = nest;
                distance = pos.distanceTo(nest.origin);
                continue;
            }
            double newDistance = pos.distanceTo(nest.origin);
            if (newDistance < distance){
                closest = nest;
                distance = newDistance;
            }
        }
        if (closest != null){
            setNest(closest);
            return true;
        }
        return false;
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
    public void CopyUnits(Swarm toCopy, boolean clearOld){
        if (clearOld) this.units.clear();
        for (MyiaticBase M : toCopy.getUnits()){
            M.SwitchSwarm(this, true);
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
            RemoveAllOrdersFor(M, true);
        }
    }
    public int AmountOfRecruits(){
        return units.size();
    }

    private ISwarmOrder PrimaryOrder;
    protected abstract void GeneratePrimaryOrder(@NotNull MyiaticBase captain);
    public void setPrimaryOrder(ISwarmOrder order){
        PrimaryOrder = order;
    }
    @Nullable
    public ISwarmOrder getPrimaryOrderRaw(){
        return PrimaryOrder;
    }
    @Nullable
    public ISwarmOrder getPrimaryOrderFor(MyiaticBase M){
        if (PrimaryOrder == null) return null;
        return (ISwarmOrder)PrimaryOrder.Relay(M);
    }
    @Nullable
    public ISwarmOrder getPrimaryOrderWithNewCaptain(MyiaticBase newCaptain){
        if (PrimaryOrder == null) return null;
        return (ISwarmOrder)PrimaryOrder.ReplaceCaptain(newCaptain);
    }
    @Nullable
    public ISwarmOrder getPrimaryOrderWithNewCaptain(MyiaticBase newCaptain, MyiaticBase recruit){
        ISwarmOrder newCaptainOrder = getPrimaryOrderWithNewCaptain(newCaptain);
        if (newCaptainOrder == null) return null;
        return (ISwarmOrder)newCaptainOrder.Relay(recruit);
    }
    private final ArrayList<ISwarmOrder> ActiveOrders = new ArrayList<>();
    public ArrayList<ISwarmOrder> getOrders(){
        return new ArrayList<>(ActiveOrders);
    }
    public void CopyOrders(Swarm toCopy, boolean clearOld){
        if (clearOld) this.DiscardAllSwarmOrders(true);
        for (ISwarmOrder order : toCopy.getOrders()){
            ISwarmOrder newOrder = (ISwarmOrder)order.ReplaceCaptain(getCaptain());
            RelayOrder(newOrder, clearOld);
        }
    }
    protected void AddSwarmOrder(ISwarmOrder order){
        ActiveOrders.add(order);
    }
    protected boolean RemoveSwarmOrder(ISwarmOrder order){
        return ActiveOrders.remove(order);
    }
    public void RelayOrder(ISwarmOrder order, boolean override){
        if (override){
            for (ISwarmOrder currentOrders : getOrders()){
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
    public boolean DiscardSwarmOrder(ISwarmOrder order){
        boolean flag = RemoveSwarmOrder(order);
        if (flag){
            for (MyiaticBase M : getUnits()){
                M.goalSelector.removeGoal(order.Relay(M));
            }
        }
        return flag;
    }
    public void DiscardPrimary() {
        for (MyiaticBase M : getUnits()){
            M.goalSelector.removeGoal((Goal)getPrimaryOrderFor(M));
        }
        setPrimaryOrder(null);
    }
    public boolean DiscardAllSwarmOrders(boolean removePrimary){
        boolean flag = true;
        for (ISwarmOrder order : getOrders()){
            boolean flag1 = DiscardSwarmOrder(order);
            if (flag) flag = flag1;
        }
        if (removePrimary){
            DiscardPrimary();
        }
        return flag;
    }


    public void AssignAllOrdersFor(MyiaticBase M){
        for (ISwarmOrder order : getOrders()){
            M.goalSelector.addGoal(order.getPriority(), order.Relay(M));
        }
        ISwarmOrder primary = getPrimaryOrderFor(M);
        if (primary != null){
            M.goalSelector.addGoal(primary.getPriority(), (Goal)primary);
        }
    }
    public void RemoveAllOrdersFor(MyiaticBase M, boolean removePrimary){
        for (ISwarmOrder order : getOrders()){
            M.goalSelector.removeGoal(order.Relay(M));
        }
        if (removePrimary) M.goalSelector.removeGoal((Goal)getPrimaryOrderFor(M));
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
            toRecruit.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
            return true;
        }
        return false;
    }

    public void RecruitNearby(){
        AABB nearby = new AABB(getCaptain().blockPosition()).inflate(RecruitRange());
        for (MyiaticBase M : getCaptain().level().getEntitiesOfClass(MyiaticBase.class, nearby, (M) -> true)){
            M.TryToRecruit(this);
        }
    }

    public static class AimlessSwarm extends Swarm{
        private static final byte SwarmType = 0;
        public AimlessSwarm(MyiaticBase captain, @Nullable Nest parentNest, int maxRecruits){
            super(SwarmType, captain, parentNest, maxRecruits);
            Idle();
        }
        public AimlessSwarm(ArrayList<MyiaticBase> possibleCaptains, @Nullable Nest parentNest, int maxRecruits){
            super(SwarmType, possibleCaptains, parentNest, maxRecruits);
            Idle();
        }

        @Override
        protected void GeneratePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(null);
        }
    }
    public static class HuntSwarm extends Swarm{
        private static final byte SwarmType = 1;
        protected HuntSwarm(MyiaticBase captain, @Nullable Nest parentNest, int maxRecruits) {
            super(SwarmType, captain, parentNest, maxRecruits);
        }
        protected HuntSwarm(ArrayList<MyiaticBase> possibleCaptains, @Nullable Nest parentNest, int maxRecruits) {
            super(SwarmType, possibleCaptains, parentNest, maxRecruits);
        }

        @Override
        protected void GeneratePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(new HuntSwarmGoal(captain, captain, 2400, 1));
        }
    }
}
