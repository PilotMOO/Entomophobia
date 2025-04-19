package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.entity.AI.HuntSwarmCaptainGoal;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.AI.NestSwarmCaptainGoal;
import mod.pilot.entomophobia.entity.festered.FesteredBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class Swarm {
    protected Swarm(byte type, MyiaticBase captain, int maxRecruits, @Nullable Vec3 finalPos){
        SwarmType = type;
        AssignNewCaptain(captain);
        if (getCaptain() == null){
            Disband();
            return;
        }
        setMaxRecruits(maxRecruits);
        setDestination(finalPos);
        Enable();
    }
    protected Swarm(byte type, ArrayList<MyiaticBase> possibleCaptains, int maxRecruits, @Nullable Vec3 finalPos){
        SwarmType = type;
        AssignNewCaptain(DecideCaptain(possibleCaptains));
        if (getCaptain() == null){
            Disband();
            return;
        }
        setMaxRecruits(maxRecruits);
        setDestination(finalPos);
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
        return "Swarm " + "\"" + getName() + "\" "
                + "[type: " + SwarmManager.SwarmTypes.values()[getSwarmType()]
                + ", state: " + SwarmStates.values()[getSwarmState()]
                + ", has Captain: "+ (getCaptain() != null) + "]";
    }
    public String getName(){
        return SwarmManager.getNameFor(this);
    }

    private final byte SwarmType;
    public final byte getSwarmType(){
        return SwarmType;
    }
    public Swarm ConvertSwarmType(SwarmManager.SwarmTypes newType, boolean AssignNewCaptain){
        Swarm toReturn;
        MyiaticBase newCaptain = AssignNewCaptain ? DecideCaptain(getUnits()) : getCaptain();
        switch (newType.ordinal()){
            default -> toReturn = null;
            case 0 -> toReturn = new AimlessSwarm(newCaptain, getMaxRecruits(), getDestination());
            case 1 -> toReturn = new HuntSwarm(newCaptain, getMaxRecruits(), getDestination());
        }
        if (toReturn == null) return null;

        toReturn.setSwarmState(getSwarmState());
        toReturn.CopyUnits(this, false);
        toReturn.CopyOrders(this, false);

        this.Disband();

        return toReturn;
    }
    public boolean canMergeWith(Swarm swarm, boolean checkOther){
        return (swarm != this
                && (!swarm.isDisbanded() && !this.isDisbanded())
                && swarm.getSwarmType() == getSwarmType()) && (!checkOther || swarm.canMergeWith(this, false));
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
        OnFinish();
        Disband();
    }
    public void OnFinish(){}
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
            Captain = newCaptain;
            newCaptain.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200)); //THIS IS TEMPORARY
            newCaptain.ForceJoin(this, true);
        }
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
    public double distanceTo(Vec3 pos){
        Vec3 swarmPos = getSwarmPosition();
        if (swarmPos == null) return -1;
        return swarmPos.distanceTo(pos);
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
    public int getRecruitCount(){
        return units.size();
    }

    private @Nullable Vec3 FinalDestination;
    public @Nullable Vec3 getDestination(){
        return FinalDestination;
    }
    public void setDestination(Vec3 newDestination){
        FinalDestination = newDestination;
    }
    public double getDistanceToDestination(){
        return getDestination() != null && getSwarmPosition() != null ? getSwarmPosition().distanceTo(getDestination()) : -1;
    }

    private ISwarmOrder PrimaryOrder;
    protected abstract void GeneratePrimaryOrder(@NotNull MyiaticBase captain);
    public void setPrimaryOrder(ISwarmOrder order){
        PrimaryOrder = order;
    }
    public @Nullable ISwarmOrder getPrimaryOrderRaw(){
        return PrimaryOrder;
    }
    public @Nullable ISwarmOrder getPrimaryOrderFor(MyiaticBase M){
        if (PrimaryOrder == null) return null;
        return (ISwarmOrder)PrimaryOrder.Relay(M);
    }
    private final ArrayList<ISwarmOrder> ActiveOrders = new ArrayList<>();
    public ArrayList<ISwarmOrder> getOrders(){
        return new ArrayList<>(ActiveOrders);
    }
    public void CopyOrders(Swarm toCopy, boolean clearOld){
        if (clearOld) this.DiscardAllSwarmOrders(true);
        for (ISwarmOrder order : toCopy.getOrders()){
            RelayOrder(order, clearOld);
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
                    DiscardSwarmOrder(currentOrders);
                }
            }
        }
        AddSwarmOrder(order);
        if (order.CaptainOnly()){
            getCaptain().QueGoal(order.getPriority(), order.Relay(getCaptain()));
            return;
        }
        for (MyiaticBase M : getUnits()){
            M.QueGoal(order.getPriority(), order.Relay(M));
        }
    }
    public void DiscardSwarmOrder(ISwarmOrder order){
        boolean flag = RemoveSwarmOrder(order);
        if (flag){
            for (MyiaticBase M : getUnits()){
                M.QueRemoveGoal(order.Relay(M));
            }
        }
    }
    public void DiscardPrimary() {
        for (MyiaticBase M : getUnits()){
            M.QueRemoveGoal((Goal)getPrimaryOrderFor(M));
        }
        setPrimaryOrder(null);
    }
    public void DiscardAllSwarmOrders(boolean removePrimary){
        for (ISwarmOrder order : getOrders()) DiscardSwarmOrder(order);
        if (removePrimary) DiscardPrimary();
    }


    public void assignAllOrdersFor(MyiaticBase M){
        boolean captainFlag = M.amITheCaptain();
        for (ISwarmOrder order : getOrders()){
            if (order.CaptainOnly() && !captainFlag) continue;
            M.QueGoal(order.getPriority(), order.Relay(M));
        }

        ISwarmOrder primary = getPrimaryOrderFor(M);
        if (primary == null || primary.CaptainOnly() && !captainFlag) return;
        M.QueGoal(primary.getPriority(), (Goal)primary);
    }
    public void RemoveAllOrdersFor(MyiaticBase M, boolean removePrimary){
        for (ISwarmOrder order : getOrders()) M.QueRemoveGoal(order.Relay(M));
        if (removePrimary) M.QueRemoveGoal((Goal)getPrimaryOrderFor(M));
    }

    private int MaxRecruits;
    public int getMaxRecruits(){
        return MaxRecruits;
    }
    public void setMaxRecruits(int max){
        MaxRecruits = max;
    }

    public int recruitRange(){
        if (getCaptain() == null) return 0;
        return (int)getCaptain().getAttributeValue(Attributes.FOLLOW_RANGE);
    }
    protected boolean canRecruit(MyiaticBase recruit){
        return recruit.isAlive() && recruit.canSwarm() && !recruit.isInSwarm() && getRecruitCount() < getMaxRecruits()
                && this.distanceTo(recruit.position()) < recruitRange();
    }

    public boolean attemptToRecruit(MyiaticBase toRecruit){
        if (canRecruit(toRecruit)){
            addToUnits(toRecruit);
            assignAllOrdersFor(toRecruit);
            toRecruit.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
            return true;
        }
        return false;
    }

    public void RecruitNearby(){
        AABB nearby = getCaptain().getBoundingBox().inflate(recruitRange());
        for (MyiaticBase M : getCaptain().level().getEntitiesOfClass(MyiaticBase.class, nearby, (M) -> true)){
            M.tryToRecruit(this);
        }
    }

    public static class AimlessSwarm extends Swarm{
        private static final byte SwarmType = 0;
        protected AimlessSwarm(MyiaticBase captain, int maxRecruits, @Nullable Vec3 finalPos){
            super(SwarmType, captain, maxRecruits, finalPos);
            Idle();
        }
        protected AimlessSwarm(ArrayList<MyiaticBase> possibleCaptains, int maxRecruits, @Nullable Vec3 finalPos){
            super(SwarmType, possibleCaptains, maxRecruits, finalPos);
            Idle();
        }

        @Override
        protected void GeneratePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(null);
        }
    }
    public static class HuntSwarm extends Swarm{
        private static final byte SwarmType = 1;
        protected HuntSwarm(MyiaticBase captain, int maxRecruits, @Nullable Vec3 finalPos) {
            super(SwarmType, captain, maxRecruits, finalPos);
        }
        protected HuntSwarm(ArrayList<MyiaticBase> possibleCaptains, int maxRecruits, @Nullable Vec3 finalPos) {
            super(SwarmType, possibleCaptains, maxRecruits, finalPos);
        }

        @Override
        protected void GeneratePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(new HuntSwarmCaptainGoal(captain, 300, 2));
            RelayOrder(getPrimaryOrderRaw(), false);
        }
    }
    public static class NestSwarm extends Swarm{
        private static final byte SwarmType = 2;
        protected NestSwarm(MyiaticBase captain, int maxRecruits, @Nullable Vec3 finalPos) {
            super(SwarmType, captain, maxRecruits, finalPos);
        }
        protected NestSwarm(ArrayList<MyiaticBase> possibleCaptains, int maxRecruits, @Nullable Vec3 finalPos) {
            super(SwarmType, possibleCaptains, maxRecruits, finalPos);
        }

        @Override
        protected void GeneratePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(new NestSwarmCaptainGoal(captain, 300, 15, 3));
            RelayOrder(getPrimaryOrderRaw(), false);
        }

        @Override
        public void OnFinish() {
            for (MyiaticBase M : getUnits()){
                M.setEncouragedDespawn(true);
            }
        }
    }
}
