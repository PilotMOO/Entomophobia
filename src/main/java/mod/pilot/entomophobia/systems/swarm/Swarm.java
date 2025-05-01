package mod.pilot.entomophobia.systems.swarm;

import mod.pilot.entomophobia.entity.AI.SwarmGoals.AttackSwarmCaptainGoal;
import mod.pilot.entomophobia.entity.AI.SwarmGoals.HuntSwarmCaptainGoal;
import mod.pilot.entomophobia.entity.AI.Interfaces.ISwarmOrder;
import mod.pilot.entomophobia.entity.AI.SwarmGoals.NestSwarmCaptainGoal;
import mod.pilot.entomophobia.entity.festered.FesteredBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class Swarm {
    protected Swarm(byte type, MyiaticBase captain, int maxRecruits, @Nullable Vec3 finalPos){
        swarmType = type;
        assignNewCaptain(captain);
        if (getCaptain() == null){
            disband();
            return;
        }
        setMaxRecruits(maxRecruits);
        setDestination(finalPos);
        enable();
    }
    protected Swarm(byte type, ArrayList<MyiaticBase> possibleCaptains, int maxRecruits, @Nullable Vec3 finalPos){
        swarmType = type;
        assignNewCaptain(decideCaptain(possibleCaptains));
        if (getCaptain() == null){
            disband();
            return;
        }
        setMaxRecruits(maxRecruits);
        setDestination(finalPos);
        enable();
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

    private final byte swarmType;
    public final byte getSwarmType(){
        return swarmType;
    }
    public Swarm convertSwarmType(SwarmManager.SwarmTypes newType, boolean AssignNewCaptain){
        Swarm toReturn;
        MyiaticBase newCaptain = AssignNewCaptain ? decideCaptain(getUnits()) : getCaptain();
        switch (newType.ordinal()){
            default -> toReturn = null;
            case 0 -> toReturn = new AimlessSwarm(newCaptain, getMaxRecruits(), getDestination());
            case 1 -> toReturn = new HuntSwarm(newCaptain, getMaxRecruits(), getDestination());
        }
        if (toReturn == null) return null;

        toReturn.setSwarmState(getSwarmState());
        toReturn.copyUnits(this, false);
        toReturn.copyOrders(this, false);

        this.disband();

        return toReturn;
    }
    public boolean canMergeWith(Swarm swarm, boolean checkOther){
        return (swarm != this
                && (!swarm.isDisbanded() && !this.isDisbanded())
                && swarm.getSwarmType() == getSwarmType()) && (!checkOther || swarm.canMergeWith(this, false));
    }

    private byte swarmState;
    public final byte getSwarmState(){
        return swarmState;
    }
    public final void setSwarmState(byte state){
        swarmState = state;
    }
    public void disband(){
        setSwarmState((byte)0);
        assignNewCaptain(null);
    }
    public boolean isDisbanded(){
        return captain == null || getSwarmState() == 0;
    }
    public void idle(){
        setSwarmState((byte)1);
    }
    public boolean isIdling(){
        return captain != null && getSwarmState() == 1;
    }
    public void enable(){
        setSwarmState((byte)2);
    }
    public boolean isActive(){
        return captain != null && getSwarmState() == 2;
    }
    public void finish() {
        setSwarmState((byte)3);
        onFinish();
        disband();
    }
    public void onFinish(){}
    public boolean isFinished(){
        return getSwarmState() == 3;
    }

    private MyiaticBase captain;
    public MyiaticBase getCaptain(){
        return captain;
    }
    public void assignNewCaptain(@Nullable MyiaticBase newCaptain){
        if (getCaptain() != null){
            getCaptain().LeaveSwarm(false);
        }
        if (newCaptain != null){
            captain = newCaptain;
            newCaptain.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200)); //THIS IS TEMPORARY
            newCaptain.ForceJoin(this, true);
        }
    }
    protected MyiaticBase decideCaptain(ArrayList<MyiaticBase> possibleCaptains) {
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
    public void copyUnits(Swarm toCopy, boolean clearOld){
        if (clearOld) this.units.clear();
        for (MyiaticBase M : toCopy.getUnits()){
            M.SwitchSwarm(this, true);
        }
    }
    protected boolean removeFromUnits(MyiaticBase M){
        return units.remove(M);
    }
    public void dropMember(MyiaticBase M, boolean disbandIfCaptain){
        if (removeFromUnits(M)){
            if (M.amITheCaptain()){
                if (disbandIfCaptain) {
                    disband();
                }
                else{
                    assignNewCaptain(decideCaptain(getUnits()));
                }
            }
            removeAllOrdersFor(M, true);
        }
    }
    public int getRecruitCount(){
        return units.size();
    }

    public @Nullable Vec3 finalDestination;
    public @Nullable Vec3 getDestination(){
        return finalDestination;
    }
    public void setDestination(Vec3 newDestination){
        finalDestination = newDestination;
    }
    public double getDistanceToDestination(){
        return getDestination() != null && getSwarmPosition() != null ? getSwarmPosition().distanceTo(getDestination()) : -1;
    }
    public @Nullable Entity target;
    public @Nullable Entity getSwarmTarget(){
        return target;
    }
    public void setSwarmTarget(Entity target){
        this.target = target;
        finalDestination = target.position();
    }
    public void updateTargetPosition(){
        if (getSwarmTarget() == null) return;
        setDestination(getSwarmTarget().position());
    }


    private ISwarmOrder primaryOrder;
    protected abstract void generatePrimaryOrder(@NotNull MyiaticBase captain);
    public void setPrimaryOrder(ISwarmOrder order){
        primaryOrder = order;
    }
    public @Nullable ISwarmOrder getPrimaryOrderRaw(){
        return primaryOrder;
    }
    public @Nullable ISwarmOrder getPrimaryOrderFor(MyiaticBase M){
        if (primaryOrder == null) return null;
        return (ISwarmOrder) primaryOrder.relay(M);
    }
    private final ArrayList<ISwarmOrder> activeOrders = new ArrayList<>();
    public ArrayList<ISwarmOrder> getOrders(){
        return new ArrayList<>(activeOrders);
    }
    public void copyOrders(Swarm toCopy, boolean clearOld){
        if (clearOld) this.discardAllSwarmOrders(true);
        for (ISwarmOrder order : toCopy.getOrders()){
            relayOrder(order, clearOld);
        }
    }
    protected void addSwarmOrder(ISwarmOrder order){
        activeOrders.add(order);
    }
    protected boolean removeSwarmOrder(ISwarmOrder order){
        return activeOrders.remove(order);
    }
    public void relayOrder(ISwarmOrder order, boolean override){
        if (override){
            for (ISwarmOrder currentOrders : getOrders()){
                if (currentOrders.getClass() == order.getClass()){
                    discardSwarmOrder(currentOrders);
                }
            }
        }
        addSwarmOrder(order);
        if (order.captainOnly()){
            getCaptain().queGoal(order.getPriority(), order.relay(getCaptain()));
            return;
        }
        for (MyiaticBase M : getUnits()){
            M.queGoal(order.getPriority(), order.relay(M));
        }
    }
    public void discardSwarmOrder(ISwarmOrder order){
        boolean flag = removeSwarmOrder(order);
        if (flag){
            for (MyiaticBase M : getUnits()){
                M.queRemoveGoal(order.relay(M));
            }
        }
    }
    public void discardPrimary() {
        for (MyiaticBase M : getUnits()){
            M.queRemoveGoal((Goal)getPrimaryOrderFor(M));
        }
        setPrimaryOrder(null);
    }
    public void discardAllSwarmOrders(boolean removePrimary){
        for (ISwarmOrder order : getOrders()) discardSwarmOrder(order);
        if (removePrimary) discardPrimary();
    }


    public void assignAllOrdersFor(MyiaticBase M){
        boolean captainFlag = M.amITheCaptain();
        for (ISwarmOrder order : getOrders()){
            if (order.captainOnly() && !captainFlag) continue;
            M.queGoal(order.getPriority(), order.relay(M));
        }

        ISwarmOrder primary = getPrimaryOrderFor(M);
        if (primary == null || primary.captainOnly() && !captainFlag) return;
        M.queGoal(primary.getPriority(), (Goal)primary);
    }
    public void removeAllOrdersFor(MyiaticBase M, boolean removePrimary){
        for (ISwarmOrder order : getOrders()) M.queRemoveGoal(order.relay(M));
        if (removePrimary) M.queRemoveGoal((Goal)getPrimaryOrderFor(M));
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
            idle();
        }
        protected AimlessSwarm(ArrayList<MyiaticBase> possibleCaptains, int maxRecruits, @Nullable Vec3 finalPos){
            super(SwarmType, possibleCaptains, maxRecruits, finalPos);
            idle();
        }

        @Override
        protected void generatePrimaryOrder(@NotNull MyiaticBase captain) {
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
        protected void generatePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(new HuntSwarmCaptainGoal(captain, 300, 2));
            relayOrder(getPrimaryOrderRaw(), false);
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
        protected void generatePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(new NestSwarmCaptainGoal(captain, 300, 15, 3));
            relayOrder(getPrimaryOrderRaw(), false);
        }

        @Override
        public void onFinish() {
            for (MyiaticBase M : getUnits()){
                M.setEncouragedDespawn(true);
            }
        }
    }
    public static class AttackSwarm extends Swarm{
        private static final byte SwarmType = 3;
        public AttackSwarm(MyiaticBase captain, int maxRecruits, @NotNull Entity target) {
            super(SwarmType, captain, maxRecruits, target.position());
            setSwarmTarget(target);
        }
        public AttackSwarm(ArrayList<MyiaticBase> possibleCaptains, int maxRecruits, @NotNull Entity target) {
            super(SwarmType, possibleCaptains, maxRecruits, target.position());
            setSwarmTarget(target);
        }

        @Override
        protected void generatePrimaryOrder(@NotNull MyiaticBase captain) {
            setPrimaryOrder(new AttackSwarmCaptainGoal(captain, 300, 2));
            relayOrder(getPrimaryOrderRaw(), false);
        }
    }
}
