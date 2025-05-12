package mod.pilot.entomophobia.entity.celestial;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.data.clientsyncing.ArteryClientSyncer;
import mod.pilot.entomophobia.data.clientsyncing.HiveDataSyncer;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import mod.pilot.entomophobia.sound.EntomoSounds;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.StimulantType;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class HiveHeartEntity extends MyiaticBase {
    public HiveHeartEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticPigEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100d)
                .add(Attributes.ARMOR, 5)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }

    //Nervous System Management
    /**The Nervous System of the Hive Heart and associated nest--
     * manages all the A.I. and goals of the Hive Heart*/
    public HiveNervousSystem nervousSystem;

    /**Create a new Nervous System for the Nest and the Hive Heart */
    public void constructNervousSystem(Nest n){
        System.out.println("Creating a new nervous system for the fucking heart");
        UUID nestHH_UUID;
        if ((nestHH_UUID = n.mainChamber.getHiveHeartUUID()) != null && nestHH_UUID.equals(this.getUUID())){
            nervousSystem = new HiveNervousSystem(n, this);
            nervousSystem.populateDefaultDecisions();
        } else{
            System.err.println("[HIVE NERVOUS SYSTEM] Error! Attempted to construct a nervous system for a nest with a hive heart that is NOT related!");
            System.err.println("[HIVE NERVOUS SYSTEM] Info-- UUID of Hive Heart: ["
                    + this.getUUID() + "], UUID of Nest's Hive Heart: ["
                    + n.mainChamber.getHiveHeartUUID() + "]");
        }
    }

    public void stimulate(StimulantType stimulant, StimulantPackage sPackage){
        if (nervousSystem != null) nervousSystem.stimulate(stimulant, sPackage);
    }
    public void alertHive(StimulantPackage sPackage){
        stimulate(StimulantType.Alarm, sPackage);
    }
    /**/

    //HiveData access
    private HiveSaveData.Packet data = null;
    public void setData(HiveSaveData.Packet packet){
        this.data = packet;
    }
    public HiveSaveData.Packet accessData(){
        return this.accessData(false);
    }
    public HiveSaveData.Packet accessData(boolean quiet){
        if (data == null){
            data = HiveSaveData.retrieveData(this);
            if (data == null) data = HiveSaveData.createNewDataPacket(this.getUUID());
        }
        if (!quiet) data = HiveDataSyncer.checkMiddleMan(this);
        return data;
    }
    /**/

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (nervousSystem != null) return;
        Nest n = NestManager.getClosestNest(position());
        if (n != null) {
            HiveHeartEntity hh;
            if ((hh = n.accessHiveHeart()) != null && this.getUUID().equals(hh.getUUID())) {
                constructNervousSystem(n);
                System.out.println("Teehee");
                return;
            } else {
                for (Nest n1 : NestManager.getActiveNests()) {
                    if ((hh = n1.accessHiveHeart()) != null && this.getUUID().equals(hh.getUUID())) {
                        constructNervousSystem(n1);
                        System.out.println("Teehee");
                        return;
                    }
                }
            }
        }
        System.out.println("[HIVE HEART ENTITY] FAILED to locate parent nest, failed to create Hive Nervous System...");
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        Nest nest = NestManager.getClosestNest(position());
        HiveHeartEntity other;
        if (nest != null && (other = nest.accessHiveHeart()) != null && other.getUUID().equals(this.getUUID())){
            nest.kill(true);
        }
        else{
            for (Nest nest1 : NestManager.getActiveNests()){
                if ((other = nest1.accessHiveHeart()) != null && other.getUUID().equals(this.getUUID())){
                    nest1.kill(true);
                    return;
                }
            }
        }
    }

    @Override
    protected void registerGoals() {}
    @Override
    protected void registerBasicGoals() {}
    @Override
    public void checkDespawn() {}
    @Override
    protected boolean shouldDespawnInPeaceful() {return false;}
    @Override
    public boolean isNoGravity() {return true;}

    @Override
    public void push(@NotNull Entity entity) {alertHive(StimulantPackage.entity(entity));}
    @Override
    public void push(double pX, double pY, double pZ) {}

    @Override
    public boolean canSwarm() {return false;}


    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypes.IN_WALL)) return false;
        boolean flag = super.hurt(pSource, pAmount);
        if (flag) {
            Entity from;
            StimulantPackage sPackage;
            if ((from = pSource.getEntity()) != null) {
                sPackage = StimulantPackage.entity(from);
            } else{
                boolean serverSide = !this.level().isClientSide();
                Vec3 pos;
                if ((pos = pSource.getSourcePosition()) != null) {
                    sPackage = StimulantPackage.positional(pos, serverSide);
                } else sPackage = StimulantPackage.empty(serverSide);
            }
            stimulate(StimulantType.Pain, sPackage);
        }
        return flag;
    }

    //Artery rendering
    private ArrayList<Artery> arteryHooks;
    private static final ArrayList<Artery> EMPTY = new ArrayList<>();
    public void setArteries(ArrayList<Artery> a){
        this.arteryHooks = a;
    }
    public ArrayList<Artery> getOrCreateArteryHooks(){
        boolean client = level().isClientSide;
        if (client) ArteryClientSyncer.checkMiddleMan(this);
        if (!hasArteries()) {
            if (client){
                ArteryClientSyncer.request(this);
                return EMPTY;
            } else {
                return arteryHooks = createArteries();
            }
        }
        return arteryHooks;
    }
    public boolean hasArteries(){return arteryHooks != null && !arteryHooks.isEmpty();}
    public ArrayList<Artery> createArteries(){
        return createArteries(random.nextInt(6, 9),32, 10);
    }
    //This just returns an arraylist of positions, you need to actually assign it for it to work...
    //Should only be called on the server and synced via the ArteryClientSyncer
    public ArrayList<Artery> createArteries(int count, int maxRange, int maxTries){
        ArrayList<Artery> toReturn = new ArrayList<>();
        for (; count > 0; --count){
            Vec3 pos;
            Vec3 direction;
            int cycle = maxTries;
            boolean valid = false;
            do{
                pos = position();
                direction = new Vec3(0, 1, 0)
                        .xRot((float)Math.toRadians(random.nextIntBetweenInclusive(-180, 180)))
                        .yRot((float)Math.toRadians(random.nextIntBetweenInclusive(-180, 180)))
                        .zRot((float)Math.toRadians(random.nextIntBetweenInclusive(-180, 180)));
                int i = maxRange;
                boolean flag = true;
                BlockState bState;
                while (--i >= 0 && flag){
                    pos = pos.add(direction);
                    bState = level().getBlockState(BlockPos.containing(pos));
                    flag = bState.canBeReplaced();
                }
                if (!flag){
                    valid = true;
                    break;
                }
            }while(--cycle >= 0);
            if (valid){
                Pair<Float, Float> pair = randomArterySizePair();
                boolean flip = random.nextBoolean();
                float a = flip ? pair.getB() : pair.getA();
                float b = flip ? pair.getA() : pair.getB();
                toReturn.add(new Artery(blockPosCenterOf(pos), a, b));
            }
        }
        return toReturn;
    }

    public Pair<Float, Float> randomArterySizePair(){
        float a, b;
        switch (random.nextInt(6)){
            case 0 ->{
                a = 0.35f; b = 0.55f;
            }
            case 1 ->{
                a = 0.25f; b = 0.35f;
            }
            case 2 ->{
                a = 0.3f; b = 0.45f;
            }
            case 3 ->{
                a = 0.35f; b = 0.5f;
            }
            case 4 ->{
                a = 0.25f; b = 0.5f;
            }
            case 5 ->{
                a = 0.2f; b = 0.4f;
            }
            default ->{
                a = 0.25f; b = 0.25f;
            }
        }
        return new Pair<>(a, b);
    }

    /**
     * Floors the supplied Vec3 argument, adds 0.5 to the Y, then returns the new Vec3--
     *  rounding all of its positions to an integer before having 0.5 added to all values.
     *  Mimics the same result as BlockPos.containing(Vec3).getCenter(), while being less performance-hungry.
     * @param vec The Vec3 to mimic the blockCenter of
     * @return A Vec3 identical to if BlockPos.containing(Vec3).getCenter() was called on it instead.
     */
    private Vec3 blockPosCenterOf(Vec3 vec){
        double x, y, z;
        x = Math.floor(vec.x) + 0.5d;
        y = Math.floor(vec.y) + 0.5d;
        z = Math.floor(vec.z) + 0.5d;
        return new Vec3(x, y, z);
    }

    //Animation handling
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "HeartManager", 2, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop("beat")))
                .setSoundKeyframeHandler(event -> {
                    String soundID = event.getKeyframeData().getSound();
                    SoundEvent sound = null;
                    if (soundID.equals("beat1")) sound = EntomoSounds.BEAT1.get();
                    else if (soundID.equals("beat2")) sound = EntomoSounds.BEAT2.get();

                    if (sound != null){
                        for (Player p : level().getEntitiesOfClass(Player.class,
                                getBoundingBox().inflate(32))){
                            double dist = p.distanceTo(this);
                            p.playSound(sound, generateBeatVolume(dist), generateBeatPitch(dist));
                        }
                    }
                }));
    }
    private float generateBeatVolume(double dist) {
        float base = 3f;
        return (float)(base - (2f / 32) * dist);
    }
    private float generateBeatPitch(double dist) {
        float base = 1f;
        return (float)(base - (0.5f / 32) * dist);
    }

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public static class Artery{
        private static final Random random = new Random();

        public final Vec3 position;
        public final float baseThickness, tipThickness;
        public float getBaseThickness(){
            return baseThickness + baseBeat;
        }
        public float getTipThickness(){
            return tipThickness + tipBeat;
        }


        private float baseMagnitude;
        public float baseBeat, baseDuration, baseAge;
        public int baseCooldown;

        private float tipMagnitude;
        public float tipBeat, tipDuration, tipAge;
        public int tipCooldown;

        public void beat(float amount, float duration, boolean base){
            if (base){
                this.baseMagnitude = amount;
                this.baseDuration = duration;
                this.baseAge = 0;
            } else{
                this.tipMagnitude = amount;
                this.tipDuration = duration;
                this.tipAge = 0;
            }
        }
        public void tick(){
            baseBeat = lerp(baseBeat, baseMagnitude, baseAge);
            if (baseAge == 1){
                if (baseMagnitude != 0) {
                    beat(0, baseDuration, true);
                } else{
                    baseCooldown = random.nextInt(10, 81);
                    resetBase();
                }
            }

            tipBeat = lerp(tipBeat, tipMagnitude, tipAge);
            if (tipAge == 1){
                if (tipMagnitude != 0) {
                    beat(0, tipDuration, false);
                } else{
                    tipCooldown = random.nextInt(10, 81);
                    resetTip();
                }
            }

            age();
        }
        private void age(){
            if (baseDuration > 0) {
                if (baseCooldown == 0) {
                    baseAge += 1 / (baseDuration * 20);
                    if (baseAge > 1) baseAge = 1;
                } else --baseCooldown;
            }

            if (tipDuration > 0) {
                if (tipCooldown == 0) {
                    tipAge += 1 / (tipDuration * 20);
                    if (tipAge > 1) tipAge = 1;
                } else --tipCooldown;
            }
        }
        public boolean isInactive(){
            return baseDuration == 0 && tipDuration == 0;
        }
        public void resetBase(){
            this.baseMagnitude = 0;
            this.baseBeat = 0;
            this.baseDuration = 0;
            this.baseAge = 0;
        }
        public void resetTip(){
            this.tipMagnitude = 0;
            this.tipBeat = 0;
            this.tipDuration = 0;
            this.tipAge = 0;
        }
        public void reset(){
            resetBase(); resetTip();
        }
        public Artery(Vec3 position, float startThickness, float endThickness){
            this.position = position;
            this.baseThickness = startThickness;
            this.tipThickness = endThickness;

            this.baseBeat = 0;
            this.tipBeat = 0;
        }

        private static float lerp(float a, float b, float partial){
            return a + ((b - a) * partial);
        }

        @Override
        public String toString() {
            return "Artery-- {misc values [Position: " + position +
                    ", base and tip thickness: " + baseThickness + ", " + tipThickness +
                    "]-- base lerping values [Magnitude, Beat, Duration, Age, Cooldown: " + baseMagnitude +
                    ", " + baseBeat + ", " + baseDuration + ", " + baseAge + ", " + baseCooldown +
                    "]-- tip lerping values [Magnitude, Beat, Duration, Age, Cooldown: " + tipMagnitude +
                    ", " + tipBeat + ", " + tipDuration + ", " + tipAge + ", " + tipCooldown + "]}";
        }
    }
}
