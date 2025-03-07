package mod.pilot.entomophobia.entity.celestial;

import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.pilot.entomophobia.effects.EntomoMobEffects;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.myiatic.MyiaticPigEntity;
import mod.pilot.entomophobia.systems.screentextdisplay.TextInstance;
import mod.pilot.entomophobia.systems.screentextdisplay.TextOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;

public class CelestialCarrionEntity extends MyiaticBase {
    public CelestialCarrionEntity(EntityType<? extends Monster> pEntityType, Level level) {
        super(pEntityType, level);
    }

    public enum ModelType{
        empty,
        base,
        tendril,
        eye,
        claw;
        public static final int totalCount = values().length;
        public static ModelType fromInt(int index){
            return ModelType.values()[index % totalCount];
        }
    }
    public static final EntityDataAccessor<Integer> MType = SynchedEntityData.defineId(CelestialCarrionEntity.class, EntityDataSerializers.INT);
    public ModelType getMType(){return ModelType.fromInt(entityData.get(MType));}
    public int getMTypeRaw(){return entityData.get(MType);}
    public void setMType(int type) {
        entityData.set(MType, type);
    }
    public void setMType(ModelType ordinal) {setMType(ordinal.ordinal());}
    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MType", entityData.get(MType));
    }
    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setMType(tag.getInt("MType"));
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MType, 0);
    }

    private final EntityDimensions baseDimensions = new EntityDimensions(1f, 1f, false);
    private final EntityDimensions tendrilDimensions = new EntityDimensions(1.25f, 0.75f, false);
    private final EntityDimensions eyeDimensions = new EntityDimensions(0.8f, 1.9f, false);
    private final EntityDimensions clawDimensions = new EntityDimensions(1f, 0.9f, false);
    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return switch (getMType()){
            case empty -> super.getDimensions(pose);
            case base -> baseDimensions;
            case tendril -> tendrilDimensions;
            case eye -> eyeDimensions;
            case claw -> clawDimensions;
        };
    }
    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
        if (MType.equals(pKey)){
            refreshDimensions();
        }
        super.onSyncedDataUpdated(pKey);
    }

    public static AttributeSupplier.Builder createAttributes(){
        return MyiaticPigEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20d)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
    }
    @Override
    protected void registerGoals() {
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (level() instanceof ServerLevel && getMTypeRaw() == 0) {
            setMType(random.nextInt(1, ModelType.totalCount));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide()) SpreadEffects();
        else {
            CallOutToPlayers();
        }
    }

    private static final int InfatuationSpread = 300;
    private static final int InfatuationSpreadAOE = 64;
    private static final int MyiasisSpread = 400;
    private static final int MyiasisSpreadAOE = 12;
    public void SpreadEffects(){
        if (tickCount % InfatuationSpread == 0){
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                    AABB.ofSize(position(), InfatuationSpreadAOE, InfatuationSpreadAOE, InfatuationSpreadAOE))){
                if (e instanceof MyiaticBase) continue;
                e.addEffect(new MobEffectInstance(EntomoMobEffects.INFATUATION.get(), 1200));
            }
        }
        if (tickCount % MyiasisSpread == 0){
            for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                    AABB.ofSize(position(), MyiasisSpreadAOE, MyiasisSpreadAOE, MyiasisSpreadAOE))){
                if (e instanceof MyiaticBase) continue;
                e.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 600));
            }
        }
    }

    @Override
    public void push(@NotNull Entity entity) {
        super.push(entity);
        if (entity instanceof LivingEntity le){
            boolean flag = !le.hasEffect(EntomoMobEffects.MYIASIS.get()) || le.getEffect(EntomoMobEffects.MYIASIS.get()).getAmplifier() < 4;
            if (flag) le.addEffect(new MobEffectInstance(EntomoMobEffects.MYIASIS.get(), 1200, 4));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "IdleManager", 2, event ->
                event.setAndContinue(RawAnimation.begin().thenLoop("idle"))));
    }

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean canSwarm() {
        return false;
    }

    public void CallOutToPlayers(){
        Entity cameraEntity;
        if (!level().isClientSide() || (cameraEntity = Minecraft.getInstance().getCameraEntity()) == null) return;
        for (Entity e : level().getEntities(this, AABB.ofSize(position(),
                Threshold1VoiceRange * 2, Threshold1VoiceRange * 2, Threshold1VoiceRange * 2))){
            if (!e.is(cameraEntity)) continue;

            double dist = this.distanceTo(e);
            if (_shouldGenerateTextAtDistance(dist)){
                String text = getRandomVoiceAtDistance(dist);
                String prepend = _generatePrependText(dist);
                String append = /*Might not be needed, but maybe generate different punctuations? E.G. '...', '.', '!', etc.*/ "";
                int widthPoint9 = (int)(TextOverlay.width * 0.1);
                int heightPoint9 = (int)(TextOverlay.height * 0.1);
                int x = random.nextInt(widthPoint9, TextOverlay.width - widthPoint9); //x and y positions
                int y = random.nextInt(heightPoint9, TextOverlay.width - heightPoint9); // ditto
                Color color = /*Make a color!!! very dark grey at start until white at like 16~, then turn slightly red as you approach 0*/
                        Color.WHITE;
                Color shiftColor = /*this is the color that it will shift to, usually the same color but with a higher alpha...0*/
                        Color.BLACK;
                int shiftSpeed = /*how fast the color shifts. Slower at first but faster as dist decreases*/ 30;
                /*Fuck I need to make an option to have it to bleed out alpha as its age approaches the end... bwahhh*/
                /*P.S. make the shift alpha start at like 10~ and slowly increase to 255 as you get within 4 blocks*/
                /*Starting color alpha NEEDS to be 0 so it's not too sudden...*/
                //no changes to font or shadowing
                float size = /*start at like... 0.2f size at first then slowly scale to 1.5f-2.0f as you get to around 6*/ 1f;
                int shaking = /*No shaking until like... 12? 14? and very minor shaking (like strength 1)*/ 0;
                int maxAge = /*max age sitting around... uhhh 40? at max dist. then up to 120 or so at 8 dist.*/ 80;
                boolean phantom = /*Should this text instance be a phantom?
                 make it a pretty low chance, slightly increasing with lowered distance*/ false;
                if (phantom){
                    //Manage the parameters if it's a phantom...
                } else {
                    //Otherwise, just make a normal one
                    TextOverlay.instance.textInstances.add(
                            TextInstance.create(text, prepend, append)
                                    .at(x, y)
                                    .withColor(color).ShiftColor(shiftColor, shiftSpeed)
                                    .ofSize(size)
                                    .withShaking(shaking)
                                    .aged(maxAge)
                    );
                }
            }

            if (e instanceof Player player) {
                player.displayClientMessage(Component.literal(String.valueOf((int)this.distanceTo(player))), true);
            }
        }
    }



    /*generate prepend based on distance, E.G. adding obfuscation if greater than 12 or smth, etc.*/
    private @Nullable String _generatePrependText(double dist) {
        if (dist > Threshold1VoiceRange) return null;
        if (random.nextDouble() < 1 - (1d / (dist / 2))) return "§k";
        return null;
    }
    private boolean _shouldGenerateTextAtDistance(double dist) {
        if (dist > Threshold1VoiceRange) return false;
        double chance = 1d / dist;
        return random.nextDouble() < chance;
    }

    public static final int Threshold1VoiceRange = 64;
    public static final int Threshold1VoiceRangeMin = 24;
    public static final int Threshold2VoiceRange = 32;
    public static final int Threshold2VoiceRangeMin = 12;
    public static final int Threshold3VoiceRange = 16;
    public static final ArrayList<ArrayList<String>> AstralVoices = new ArrayList<>();
    public static void CreateVoices(){
        ArrayList<String> threshold1 = new ArrayList<>();
        threshold1.add("Closer...");
        threshold1.add("Come...");

        ArrayList<String> threshold2 = new ArrayList<>();
        threshold2.add("Free me...");
        threshold2.add("Join us...");
        threshold2.add("Fallen.");
        threshold2.add("Defeat.");
        threshold2.add("Pain.");

        ArrayList<String> threshold3 = new ArrayList<>();
        threshold3.add("They know my name.");
        threshold3.add("They watched me BLEED");
        threshold3.add("Reclaim me...");
        threshold3.add("Salvation");
        threshold3.add("Revive");
        threshold3.add("FIX US");

        ArrayList<String> all = new ArrayList<>();
        all.addAll(threshold1);
        all.addAll(threshold2);
        all.addAll(threshold3);

        AstralVoices.add(all);
        AstralVoices.add(threshold1);
        AstralVoices.add(threshold2);
        AstralVoices.add(threshold3);
    }
    public static ArrayList<String> getAllVoicesAtDistance(double distance){
        if (distance < 0) return AstralVoices.get(0);

        ArrayList<String> toReturn = new ArrayList<>();
        if (distance > Threshold1VoiceRange + 1) return toReturn;

        if (distance >= Threshold1VoiceRangeMin){
            toReturn.addAll(AstralVoices.get(1));
        }
        if (distance <= Threshold2VoiceRange && distance >= Threshold2VoiceRangeMin){
            toReturn.addAll(AstralVoices.get(2));
        }
        if (distance <= Threshold3VoiceRange){
            toReturn.addAll(AstralVoices.get(3));
        }
        return toReturn;
    }
    public @Nullable String getRandomVoiceAtDistance(double distance){
        ArrayList<String> voices = getAllVoicesAtDistance(distance);
        if (voices == null || voices.isEmpty()) return null;
        else return voices.get(random.nextInt(voices.size()));
    }
    //ToDo: set up the rest of the values after testing
    public static boolean ObfuscateAt(double distance){
        return  false;
    }
}
